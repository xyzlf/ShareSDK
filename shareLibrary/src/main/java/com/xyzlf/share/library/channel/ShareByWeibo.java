package com.xyzlf.share.library.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.xyzlf.com.share.library.R;
import com.xyzlf.share.library.AccessTokenKeeper;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.OnShareListener;
import com.xyzlf.share.library.request.BitmapAsyncTask;
import com.xyzlf.share.library.util.ManifestUtil;
import com.xyzlf.share.library.util.ToastUtil;


/**
 * Created by zhanglifeng on 2016/6/18
 */
public class ShareByWeibo extends ShareBase {

    public static final long HALF_HOUR = 30 * 60 * 1000L;
    /**
     * 微博微博分享接口实例
     */
    private IWeiboShareAPI mWeiboShareAPI = null;
    private AuthInfo mAuthInfo;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;
    private WeiboAuthListener authListener;

    private ShareEntity data;
    private String weiboToken;

    public ShareByWeibo(Context context, WeiboAuthListener authListener) {
        super(context);
        this.authListener = authListener;
        final String weiboKey = ManifestUtil.getSinaWeiboKey(context);
        final String redirect = ManifestUtil.getSinaWeiboRedirecturi(context);
        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context.getApplicationContext(), weiboKey);
        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();

        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(context, weiboKey, redirect, "");
        mSsoHandler = new SsoHandler((Activity) context, mAuthInfo);
    }

    public void onNewIntent(Intent intent, IWeiboHandler.Response response) {
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        if (null != mWeiboShareAPI) {
            mWeiboShareAPI.handleWeiboResponse(intent, response);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 32973) {
            //不要问为啥是32973不是常量，因为在微博的库里面是这样的：
            // private static final int REQUEST_CODE_SSO_AUTH = 32973;
            if (null != mSsoHandler) {
                // SSO 授权回调
                // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void share(ShareEntity data, OnShareListener listener) {
        if (null == data) {
            return;
        }
        this.data = data;
        if (isSessionValid()) {
            weiboShare();
        } else {
            mSsoHandler.authorize(new AuthListener());
        }
    }

    private void weiboShare() {
        if (!TextUtils.isEmpty(data.getImgUrl())) {
            new BitmapAsyncTask(data.getImgUrl(), new BitmapAsyncTask.OnBitmapListener() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    sendMultiMessage(bitmap);
                }

                @Override
                public void onException(Exception exception) {
                    sendMultiMessage();
                }
            }).execute();
        } else {
            sendMultiMessage();
        }
    }

    private boolean isSessionValid() {
        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(context);
        if (null != mAccessToken && mAccessToken.isSessionValid() && !isExpired(mAccessToken.getExpiresTime())) {
            return true;
        }
        return false;
    }
    /**
     * 是否过期；剩余时间不足半小时视为过期
     *
     * @return true过期，false未过期
     */
    public boolean isExpired(long expiredAt) {
        return System.currentTimeMillis() > (expiredAt - HALF_HOUR);
    }

    private void sendMultiMessage() {
        this.sendMultiMessage(null);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     */
    private void sendMultiMessage(Bitmap bitmap) {
        // 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj(data);

        if (null != bitmap) {
            weiboMessage.imageObject = getImageObj(bitmap);
        }

        // 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 发送请求消息到微博，唤起微博分享界面
        String token = "";
        if (!TextUtils.isEmpty(weiboToken)) {
            token = weiboToken;
        } else {
            Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(context);
            if (mAccessToken != null) {
                token = mAccessToken.getToken();
            }
        }
        mWeiboShareAPI.sendRequest((Activity) context, request, mAuthInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException arg0) {
                ToastUtil.showToast(context, R.string.share_failed, true);
            }

            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(context, newToken);
                ToastUtil.showToast(context, R.string.share_success, true);
            }

            @Override
            public void onCancel() {
                ToastUtil.showToast(context, R.string.share_cancel, true);
            }
        });
    }
    /**
     * 创建文本消息对象。
     * @return 文本消息对象。
     */
    private TextObject getTextObj(ShareEntity data) {
        TextObject textObject = new TextObject();
        if (null != data) {
            StringBuilder builder = new StringBuilder();
            builder.append(data.getContent()).append("  ").append(data.getUrl());
            textObject.text = builder.toString();
        }
        return textObject;
    }

    /**
     * 创建图片消息对象。
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        if (null != bitmap) {
            imageObject.setImageObject(getShareBitmap(bitmap));
        } else {
            Bitmap localBitmap = getShareBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.share_default));
            imageObject.setImageObject(localBitmap);
        }
        return imageObject;
    }

    /**
     * 微信分享图片不能超过32kb
     * @return
     */
    protected Bitmap getShareBitmap(Bitmap targetBitmap) {
        float scale = Math.min((float) 150 / targetBitmap.getWidth(), (float) 150 / targetBitmap.getHeight());
        Bitmap fixedBmp = Bitmap.createScaledBitmap(targetBitmap, (int) (scale * targetBitmap.getWidth()), (int) (scale * targetBitmap.getHeight()), false);
        return fixedBmp;
    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link # onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            /**
             * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
             */
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken.isSessionValid()) {
                weiboToken = accessToken.getToken();
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(context, accessToken);
                ToastUtil.showToast(context, R.string.share_weibosdk_auth_success, true);

                if (null != authListener) {
                    authListener.onComplete(values);
                } else {
                    onCancel();
                }
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = context.getString(R.string.share_weibosdk_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                ToastUtil.showToast(context, message, true);

                onCancel();
            }
        }

        @Override
        public void onCancel() {
            ToastUtil.showToast(context, R.string.share_weibosdk_auth_canceled, true);
            if (null != authListener) {
                authListener.onCancel();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ToastUtil.showToast(context,"Auth exception : " + e.getMessage(), true);
            if (null != authListener) {
                authListener.onWeiboException(e);
            }
        }
    }

}
