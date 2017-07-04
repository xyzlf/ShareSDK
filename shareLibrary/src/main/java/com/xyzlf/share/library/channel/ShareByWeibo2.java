package com.xyzlf.share.library.channel;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.xyzlf.com.share.library.R;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.OnShareListener;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.request.AbstractAsyncTask;
import com.xyzlf.share.library.request.BitmapAsyncTask;
import com.xyzlf.share.library.util.ChannelUtil;
import com.xyzlf.share.library.util.ShareUtil;
import com.xyzlf.share.library.util.ToastUtil;

import java.util.List;

/**
 * Created by zhanglifeng on 2016/6/18
 */
public class ShareByWeibo2 extends ShareBase {

    private ShareEntity data;
    private OnShareListener listener;

    public ShareByWeibo2(Context context) {
        super(context);
        this.context = context.getApplicationContext();
    }

    @Override
    public void share(final ShareEntity data, OnShareListener listener) {
        boolean isWeiboInstalled = ChannelUtil.isWeiboInstall(context);
        boolean isWeiboLiteInstalled = false;
        if (!isWeiboInstalled) {
            isWeiboLiteInstalled = ChannelUtil.isWeiboLiteInstall(context);
        }
        if (!isWeiboInstalled && !isWeiboLiteInstalled) {
            ToastUtil.showToast(context, R.string.share_no_weibo_client, true);
            listener.onShare(ShareConstant.SHARE_CHANNEL_SINA_WEIBO, ShareConstant.SHARE_STATUS_FAILED);
            return;
        }
        if (null == data) {
            listener.onShare(ShareConstant.SHARE_CHANNEL_SINA_WEIBO, ShareConstant.SHARE_STATUS_FAILED);
            return;
        }
        this.listener = listener;
        this.data = data;
        if (!TextUtils.isEmpty(data.getImgUrl())) {
            if (data.getImgUrl().startsWith("http")) {
                new BitmapAsyncTask(data.getImgUrl(), new BitmapAsyncTask.OnBitmapListener() {
                    @Override
                    public void onSuccess(final Bitmap bitmap) {
                        localSyncTask(bitmap);
                    }

                    @Override
                    public void onException(Exception exception) {
                        localSyncTask(null);
                    }
                }).execute();
            } else {
                localSyncTask();
            }
        } else if (data.getDrawableId() != 0) {
            BitmapDrawable drawable = null;
            try {
                drawable = (BitmapDrawable) ContextCompat.getDrawable(context, data.getDrawableId());
            } catch (Exception ignored) {
            }
            if (null != drawable) {
                localSyncTask(drawable.getBitmap());
            } else {
                localSyncTask(null);
            }
        } else {
            localSyncTask(null);
        }
    }

    private void localSyncTask(final Bitmap bitmap) {
        new AbstractAsyncTask<String>() {
            @Override
            protected String doLoadData() throws Exception {
                String imgPath;
                if (null != bitmap) {
                    imgPath = ShareUtil.saveBitmapToSDCard(context, bitmap);
                } else {
                    imgPath = ShareUtil.saveBitmapToSDCard(context, getDefaultBitmap(context));
                }
                weiboShare(imgPath);
                return null;
            }
        }.execute();
    }

    private void localSyncTask() {
        if (!TextUtils.isEmpty(data.getImgUrl()) && !data.getImgUrl().startsWith("http")) {
            new AbstractAsyncTask<String>() {
                @Override
                protected String doLoadData() throws Exception {
                    weiboShare(data.getImgUrl());
                    return null;
                }
            }.execute();
        } else {
            localSyncTask(null);
        }
    }

    private void weiboShare(final String imgPath) {
        Intent weiboIntent = new Intent(Intent.ACTION_SEND);
        weiboIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!TextUtils.isEmpty(imgPath)) {
            weiboIntent.setType("image/*");
        } else {
            weiboIntent.setType("text/plain");
        }
        try {
            String pkgName = "";
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> matches = pm.queryIntentActivities(weiboIntent, PackageManager.MATCH_DEFAULT_ONLY);
            ResolveInfo info = null;
            for (ResolveInfo each : matches) {
                pkgName = each.activityInfo.applicationInfo.packageName;
                if (ShareConstant.SINA_WEIBO_PACKAGE_NAME.equals(pkgName) || ShareConstant.SINA_WEIBO_LITE_PACKAGE_NAME.equals(pkgName)) {
                    info = each;
                    break;
                }
            }

            if (null != info) {
                weiboIntent.setClassName(pkgName, info.activityInfo.name);

                StringBuilder builder = new StringBuilder();
                if (!TextUtils.isEmpty(data.getContent())) {
                    builder.append(data.getContent());
                }
                if (!TextUtils.isEmpty(data.getUrl())) {
                    builder.append(" ").append(data.getUrl());
                }
                if (!TextUtils.isEmpty(builder.toString())) {
                    weiboIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                }

                if (!TextUtils.isEmpty(imgPath)) {
                    weiboIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imgPath));
                }
                context.startActivity(weiboIntent);
                listener.onShare(ShareConstant.SHARE_CHANNEL_SINA_WEIBO, ShareConstant.SHARE_STATUS_COMPLETE);
            } else {
                listener.onShare(ShareConstant.SHARE_CHANNEL_SINA_WEIBO, ShareConstant.SHARE_STATUS_FAILED);
            }
        } catch (Exception ignore) {
            listener.onShare(ShareConstant.SHARE_CHANNEL_SINA_WEIBO, ShareConstant.SHARE_STATUS_FAILED);
        }
    }

    private Bitmap getDefaultBitmap(Context context) {
        Drawable icon = null;
        try {
            icon = context.getPackageManager().getApplicationIcon(context.getPackageName());
        } catch (Exception e) {
        }
        if (null != icon && icon instanceof BitmapDrawable) {
            return ((BitmapDrawable) icon).getBitmap();
        }

        return BitmapFactory.decodeResource(context.getResources(), R.drawable.share_default);
    }


}
