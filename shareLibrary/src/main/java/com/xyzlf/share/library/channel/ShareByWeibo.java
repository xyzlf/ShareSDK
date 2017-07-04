//package com.xyzlf.share.library.channel;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.text.TextUtils;
//
//import com.sina.weibo.sdk.api.ImageObject;
//import com.sina.weibo.sdk.api.TextObject;
//import com.sina.weibo.sdk.api.WeiboMultiMessage;
//import com.sina.weibo.sdk.api.share.IWeiboHandler;
//import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
//import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
//import com.sina.weibo.sdk.api.share.WeiboShareSDK;
//import com.sina.weibo.sdk.auth.AuthInfo;
//import com.sina.weibo.sdk.auth.Oauth2AccessToken;
//import com.sina.weibo.sdk.auth.WeiboAuthListener;
//import com.sina.weibo.sdk.auth.sso.SsoHandler;
//import com.sina.weibo.sdk.exception.WeiboException;
//import com.xyzlf.com.share.library.R;
//import com.xyzlf.share.library.AccessTokenKeeper;
//import com.xyzlf.share.library.bean.ShareEntity;
//import com.xyzlf.share.library.interfaces.OnShareListener;
//import com.xyzlf.share.library.request.BitmapAsyncTask;
//import com.xyzlf.share.library.util.ManifestUtil;
//import com.xyzlf.share.library.util.ToastUtil;
//
//import java.io.File;
//
//
///**
// * Created by zhanglifeng on 2016/6/18
// */
//public class ShareByWeibo extends ShareBase {
//
//    public static final long HALF_HOUR = 30 * 60 * 1000L;
//    /**
//     * 微博微博分享接口实例
//     */
//    private IWeiboShareAPI mWeiboShareAPI = null;
//    private AuthInfo mAuthInfo;
//    /**
//     * if SsoHandler SDK support SSO
//     */
//    private SsoHandler mSsoHandler;
//    private WeiboAuthListener authListener;
//
//    private ShareEntity data;
//    private String weiboToken;
//
//    public ShareByWeibo(Context context, WeiboAuthListener authListener) {
//        super(context);
//        this.authListener = authListener;
//        final String weiboKey = ManifestUtil.getSinaWeiboKey(context);
//        final String redirect = ManifestUtil.getSinaWeiboRedirecturi(context);
//        // 创建微博分享接口实例
//        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context.getApplicationContext(), weiboKey);
//        mWeiboShareAPI.registerApp();
//
//        mAuthInfo = new AuthInfo(context, weiboKey, redirect, "");
//        mSsoHandler = new SsoHandler((Activity) context, mAuthInfo);
//    }
//
//    public void onNewIntent(Intent intent, IWeiboHandler.Response response) {
//        if (null != mWeiboShareAPI) {
//            mWeiboShareAPI.handleWeiboResponse(intent, response);
//        }
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 32973) {
//            // private static final int REQUEST_CODE_SSO_AUTH = 32973;
//            if (null != mSsoHandler) {
//                // SSO 授权回调
//                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//            }
//        }
//    }
//
//    @Override
//    public void share(ShareEntity data, OnShareListener listener) {
//        if (null == data) {
//            return;
//        }
//        this.data = data;
//        if (isSessionValid()) {
//            weiboShare();
//        } else {
//            mSsoHandler.authorize(new AuthListener());
//        }
//    }
//
//    private void weiboShare() {
//        String imgUrl = data.getImgUrl();
//        if (!TextUtils.isEmpty(imgUrl)) {
//            // 网络图片
//            if (imgUrl.startsWith("http")) {
//                new BitmapAsyncTask(imgUrl, new BitmapAsyncTask.OnBitmapListener() {
//                    @Override
//                    public void onSuccess(Bitmap bitmap) {
//                        sendMultiMessage(bitmap);
//                    }
//
//                    @Override
//                    public void onException(Exception exception) {
//                        sendMultiMessage();
//                    }
//                }).execute();
//            } else {
//                //本地图片
//                sendMultiMessage(getLoacalBitmap(imgUrl));
//            }
//        } else if (data.getDrawableId() != 0) {
//            BitmapDrawable drawable = null;
//            try {
//                drawable = (BitmapDrawable) ContextCompat.getDrawable(context, data.getDrawableId());
//            } catch (Exception ignored) {
//            }
//            if (null != drawable) {
//                sendMultiMessage(drawable.getBitmap());
//            } else {
//                sendMultiMessage();
//            }
//        } /*else if (data.getBitmap() != null) {
//            sendMultiMessage(data.getBitmap());
//        }*/ else {
//            sendMultiMessage();
//        }
//    }
//
//    private boolean isSessionValid() {
//        Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(context);
//        if (null != mAccessToken && mAccessToken.isSessionValid() && !isExpired(mAccessToken.getExpiresTime())) {
//            return true;
//        }
//        return false;
//    }
//
//    public Bitmap getLoacalBitmap(String localPath) {
//        File file = new File(localPath);
//        if (file.exists()) {
//            try {
//                return BitmapFactory.decodeFile(localPath);
//            } catch (OutOfMemoryError error) {
//                error.printStackTrace();
//            }
//        }
//        return BitmapFactory.decodeResource(context.getResources(), R.drawable.share_default);
//    }
//
//    /**
//     * 是否过期；剩余时间不足半小时视为过期
//     * @param expiredAt date
//     * @return boolean
//     */
//    public boolean isExpired(long expiredAt) {
//        return System.currentTimeMillis() > (expiredAt - HALF_HOUR);
//    }
//
//    private void sendMultiMessage() {
//        this.sendMultiMessage(null);
//    }
//
//    private void sendMultiMessage(Bitmap bitmap) {
//        // 初始化微博的分享消息
//        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//        weiboMessage.textObject = getTextObj(data);
//
//        if (null != bitmap) {
//            weiboMessage.imageObject = getImageObj(bitmap);
//        }
//
//        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.multiMessage = weiboMessage;
//
//        String token = "";
//        if (!TextUtils.isEmpty(weiboToken)) {
//            token = weiboToken;
//        } else {
//            Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(context);
//            if (mAccessToken != null) {
//                token = mAccessToken.getToken();
//            }
//        }
//        mWeiboShareAPI.sendRequest((Activity) context, request, mAuthInfo, token, new WeiboAuthListener() {
//
//            @Override
//            public void onWeiboException(WeiboException arg0) {
//                ToastUtil.showToast(context, R.string.share_failed, true);
//            }
//
//            @Override
//            public void onComplete(Bundle bundle) {
//                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
//                AccessTokenKeeper.writeAccessToken(context, newToken);
//                ToastUtil.showToast(context, R.string.share_success, true);
//            }
//
//            @Override
//            public void onCancel() {
//                ToastUtil.showToast(context, R.string.share_cancel, true);
//            }
//        });
//    }
//
//    private TextObject getTextObj(ShareEntity data) {
//        TextObject textObject = new TextObject();
//        if (null != data) {
//            StringBuilder builder = new StringBuilder();
//            if (!TextUtils.isEmpty(data.getContent())) {
//                builder.append(data.getContent());
//            }
//            if (!TextUtils.isEmpty(data.getUrl())) {
//                builder.append("  ").append(data.getUrl());
//            }
//            textObject.text = builder.toString();
//        }
//        return textObject;
//    }
//
//    private ImageObject getImageObj(Bitmap bitmap) {
//        ImageObject imageObject = new ImageObject();
//        if (null != bitmap) {
//            imageObject.setImageObject(/*getShareBitmap(bitmap)*/bitmap);
//        } else {
////            Bitmap localBitmap = getShareBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.share_default));
//            Bitmap localBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.share_default);
//            imageObject.setImageObject(localBitmap);
//        }
//        return imageObject;
//    }
//
////    /**
////     * 微信分享图片不能超过32kb
////     * @param targetBitmap bitmap
////     * @return Bitmap
////     */
////    protected Bitmap getShareBitmap(Bitmap targetBitmap) {
////        float scale = Math.min((float) 150 / targetBitmap.getWidth(), (float) 150 / targetBitmap.getHeight());
////        Bitmap fixedBmp = Bitmap.createScaledBitmap(targetBitmap, (int) (scale * targetBitmap.getWidth()), (int) (scale * targetBitmap.getHeight()), false);
////        return fixedBmp;
////    }
//
//    class AuthListener implements WeiboAuthListener {
//
//        @Override
//        public void onComplete(Bundle values) {
//            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
//            if (accessToken.isSessionValid()) {
//                weiboToken = accessToken.getToken();
//                AccessTokenKeeper.writeAccessToken(context, accessToken);
//                ToastUtil.showToast(context, R.string.share_weibosdk_auth_success, true);
//
//                if (null != authListener) {
//                    authListener.onComplete(values);
//                } else {
//                    onCancel();
//                }
//            } else {
//                String code = values.getString("code");
//                String message = context.getString(R.string.share_weibosdk_auth_failed);
//                if (!TextUtils.isEmpty(code)) {
//                    message = message + "\nObtained the code: " + code;
//                }
//                ToastUtil.showToast(context, message, true);
//
//                onCancel();
//            }
//        }
//
//        @Override
//        public void onCancel() {
//            ToastUtil.showToast(context, R.string.share_weibosdk_auth_canceled, true);
//            if (null != authListener) {
//                authListener.onCancel();
//            }
//        }
//
//        @Override
//        public void onWeiboException(WeiboException e) {
//            ToastUtil.showToast(context,"Auth exception : " + e.getMessage(), true);
//            if (null != authListener) {
//                authListener.onWeiboException(e);
//            }
//        }
//    }
//
//}
