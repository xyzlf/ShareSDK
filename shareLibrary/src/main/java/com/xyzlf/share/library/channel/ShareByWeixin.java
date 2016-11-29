package com.xyzlf.share.library.channel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xyzlf.com.share.library.R;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.OnShareListener;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.request.BitmapAsyncTask;
import com.xyzlf.share.library.util.ManifestUtil;
import com.xyzlf.share.library.util.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by zhanglifeng
 */
public class ShareByWeixin extends ShareBase {

    private int channel;
    private ShareEntity data;
    private OnShareListener listener;
    private IWXAPI api;

    private WeixinShareReceiver receiver;

    public ShareByWeixin(Context context, int channel) {
        super(context);
        this.context = context.getApplicationContext();
        this.channel = channel;
        api = WXAPIFactory.createWXAPI(context.getApplicationContext(), ManifestUtil.getWeixinKey(this.context));
    }

    /**
     * 注册微信回调广播
     */
    public void registerWeixinReceiver() {
        receiver = new WeixinShareReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ShareConstant.ACTION_WEIXIN_CALLBACK);
        context.registerReceiver(receiver, intentFilter);
    }

    /**
     * unregister
     */
    public void unregisterWeixinReceiver() {
        if (null != context && null != receiver) {
            context.unregisterReceiver(receiver);
        }
    }

    @Override
    public void share(ShareEntity data, OnShareListener listener) {
        this.data = data;
        this.listener = listener;
        if (null == data) {
            return;
        }
        start();
    }

    /**
     * start share
     */
    private void start() {
        if (api.isWXAppInstalled()) {
            String imgUrl = data.getImgUrl();
            if (!TextUtils.isEmpty(imgUrl)) {
                // 网络图片
                if (imgUrl.startsWith("http")) {
                    new BitmapAsyncTask(imgUrl, new BitmapAsyncTask.OnBitmapListener() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            if (data.isShareBigImg()) {
                                shareImg(bitmap, listener);
                            } else {
                                send(bitmap);
                            }
                        }

                        @Override
                        public void onException(Exception exception) {
                            send();
                        }
                    }).execute();
                } else {
                    //本地图片
                    if (data.isShareBigImg()) {
                        shareImg(getLoacalBitmap(imgUrl), listener);
                    } else {
                        send(getLoacalBitmap(imgUrl));
                    }
                }
            } else if (data.getDrawableId() != 0) {
                BitmapDrawable drawable = null;
                try {
                    drawable = (BitmapDrawable) ContextCompat.getDrawable(context, data.getDrawableId());
                } catch (Exception ignored) {
                }
                if (null != drawable) {
                    send(drawable.getBitmap());
                } else {
                    send();
                }
            }/* else if (data.getBitmap() != null) {
                send(data.getBitmap());
            } */ else {
                send();
            }
        } else {
            if (null != listener) {
                listener.onShare(channel, ShareConstant.SHARE_STATUS_FAILED);
            }
            ToastUtil.showToast(context, R.string.share_no_weixin_client, true);
        }
    }

    public Bitmap getLoacalBitmap(String localPath) {
        File file = new File(localPath);
        if (file.exists()) {
            try {
                return BitmapFactory.decodeFile(localPath);
            } catch (OutOfMemoryError error) {
                error.printStackTrace();
            }
        }
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.share_default);
    }

    private void send() {
        this.send(null);
    }

    private void send(Bitmap bitmap) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = buildWXMediaMessage(bitmap);
        if (channel == ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else if (channel == ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        api.sendReq(req);
    }

    /**
     * 构建WXMediaMessage
     *
     * @param bitmap
     */
    private WXMediaMessage buildWXMediaMessage(Bitmap bitmap) {
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.title = data.getTitle();
        wxMediaMessage.description = data.getContent();
        if (TextUtils.isEmpty(data.getUrl())) {
            wxMediaMessage.mediaObject = new WXTextObject(data.getContent());
        } else {
            if (null != bitmap) {
                wxMediaMessage.setThumbImage(getWxShareBitmap(bitmap));
            } else {
                Bitmap defautlBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.share_default);
                if (null != defautlBitmap) {
                    wxMediaMessage.setThumbImage(getWxShareBitmap(defautlBitmap));
                }
            }
            wxMediaMessage.mediaObject = new WXWebpageObject(data.getUrl());
        }
        return wxMediaMessage;
    }

    /**
     * 微信分享图片不能超过64kb，特别坑...
     * @param targetBitmap bitmap
     * @return Bitmap
     */
    protected Bitmap getWxShareBitmap(Bitmap targetBitmap) {
        float scale = Math.min((float) 150 / targetBitmap.getWidth(), (float) 150 / targetBitmap.getHeight());
        Bitmap fixedBmp = Bitmap.createScaledBitmap(targetBitmap, (int) (scale * targetBitmap.getWidth()), (int) (scale * targetBitmap.getHeight()), false);
        return fixedBmp;
    }

    /**
     * 微信分享回调广播
     */
    private class WeixinShareReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(ShareConstant.EXTRA_WEIXIN_RESULT)) {
                int errCode = intent.getIntExtra(ShareConstant.EXTRA_WEIXIN_RESULT, BaseResp.ErrCode.ERR_USER_CANCEL);
                int resultCode = (errCode == BaseResp.ErrCode.ERR_OK) ? Activity.RESULT_OK : Activity.RESULT_CANCELED;
                if (resultCode == Activity.RESULT_OK) {
                    //分享成功
                    if (null != listener) {
                        listener.onShare(channel, ShareConstant.SHARE_STATUS_COMPLETE);
                    }
                    ToastUtil.showToast(context, R.string.share_success, true);
                } else {
                    //分享失败
                    if (null != listener) {
                        listener.onShare(channel, ShareConstant.SHARE_STATUS_FAILED);
                    }
                    // ToastUtil.showToast(context, R.string.share_failed, true);
                }
            }
        }
    }

    private static final int THUMB_SIZE = 250;

    public void shareImg(Bitmap bitmap, OnShareListener listener) {
        if (null == bitmap) {
            if (null != listener) {
                listener.onShare(channel, ShareConstant.SHARE_STATUS_FAILED);
            }
            return;
        }
        if (api.isWXAppInstalled()) {
            if (api.isWXAppSupportAPI()) {
                WXMediaMessage wxMediaMessage = new WXMediaMessage();
                wxMediaMessage.mediaObject = new WXImageObject(bitmap);

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                while (width * height > THUMB_SIZE * THUMB_SIZE) {
                    width /= 2;
                    height /= 2;
                }
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
                try {
                    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
                    thumbBmp.compress(Bitmap.CompressFormat.JPEG, 85, localByteArrayOutputStream);
                    wxMediaMessage.thumbData = localByteArrayOutputStream.toByteArray();
                    localByteArrayOutputStream.close();
                } catch (Exception e) {

                }

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = String.valueOf(System.currentTimeMillis());
                req.message = wxMediaMessage;
                if (ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND == channel) {
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                } else if (ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE == channel) {
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                }
                api.sendReq(req);
            }
        } else {
            if (null != listener) {
                listener.onShare(channel, ShareConstant.SHARE_STATUS_FAILED);
            }
            ToastUtil.showToast(context, R.string.share_no_weixin_client, true);
        }
    }

}
