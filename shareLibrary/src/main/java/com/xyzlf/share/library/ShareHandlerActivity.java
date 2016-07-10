package com.xyzlf.share.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.xyzlf.com.share.library.R;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.channel.ShareByEmail;
import com.xyzlf.share.library.channel.ShareByQQ;
import com.xyzlf.share.library.channel.ShareByQZone;
import com.xyzlf.share.library.channel.ShareBySms;
import com.xyzlf.share.library.channel.ShareBySystem;
import com.xyzlf.share.library.channel.ShareByWeibo;
import com.xyzlf.share.library.channel.ShareByWeixin;
import com.xyzlf.share.library.interfaces.OnShareListener;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.util.ToastUtil;

/**
 * Created by zhanglifeng on 16/06/20
 *
 * 分发Activity，主要对分享功能进行分发
 */
public class ShareHandlerActivity extends ShareBaseActivity implements IWeiboHandler.Response, WeiboAuthListener, OnShareListener {

    protected ShareEntity data;

    protected ShareByWeibo shareByWeibo;
    protected ShareByWeixin shareByWeixin;
    protected boolean isInit = true;

    protected boolean isWeiboAuthComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        Object object = getIntent().getParcelableExtra(ShareConstant.EXTRA_SHARE_DATA);
        if (null == object || !(object instanceof ShareEntity)) {
            finish();
            return;
        }
        data = (ShareEntity) object;

        if (savedInstanceState == null) {

            if (null != shareByWeixin) {
                shareByWeixin.unregisterWeixinReceiver();
                shareByWeixin = null;
            }
            switch (channel) {
                case ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE:
                    shareByWeixin = new ShareByWeixin(this, ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE);
                    shareByWeixin.registerWeixinReceiver();
                    shareByWeixin.share(data, this);
                    break;

                case ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND:
                    shareByWeixin = new ShareByWeixin(this, ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND);
                    shareByWeixin.registerWeixinReceiver();
                    shareByWeixin.share(data, this);
                    break;

                case ShareConstant.SHARE_CHANNEL_SINA_WEIBO:
                    shareByWeibo = new ShareByWeibo(this, this);
                    shareByWeibo.share(data, this);
                    break;

                case ShareConstant.SHARE_CHANNEL_QQ:
                    new ShareByQQ(this).share(data, this);
                    break;

                case ShareConstant.SHARE_CHANNEL_QZONE:
                    new ShareByQZone(this).share(data, this);
                    break;

                case ShareConstant.SHARE_CHANNEL_SMS:
                    new ShareBySms(this).share(data, this);
                    break;

                case ShareConstant.SHARE_CHANNEL_EMAIL:
                    new ShareByEmail(this).share(data, this);
                    break;

                case ShareConstant.SHARE_CHANNEL_SYSTEM:
                    new ShareBySystem(this).share(data, this);
                    break;

                default:
                    finishWithResult(channel, ShareConstant.SHARE_STATUS_ERROR);
                    break;
            }
        } else {
            // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
            // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
            // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
            // 失败返回 false，不调用上述回调
            if (null != shareByWeibo) {
                shareByWeibo.onNewIntent(getIntent(), this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInit && !isWeiboAuthComplete) {
            finishWithResult(channel, ShareConstant.SHARE_STATUS_ERROR);
        } else {
            isInit = false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != shareByWeibo) {
            shareByWeibo.onNewIntent(intent, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null != shareByWeibo) {
            shareByWeibo.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onShare(int channel, int status) {
        finishWithResult(channel, status);
    }

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseResp 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        if (baseResp != null) {
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    onShare(ShareConstant.SHARE_CHANNEL_SINA_WEIBO, ShareConstant.SHARE_STATUS_COMPLETE);
                    ToastUtil.showToast(this, R.string.share_success, true);
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    onShare(ShareConstant.SHARE_CHANNEL_SINA_WEIBO, ShareConstant.SHARE_STATUS_CANCEL);
                    ToastUtil.showToast(this, R.string.share_cancel, true);
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    onShare(ShareConstant.SHARE_CHANNEL_SINA_WEIBO, ShareConstant.SHARE_STATUS_FAILED);
                    ToastUtil.showToast(this, getString(R.string.share_failed) + "。Error Message: " + baseResp.errMsg, false);
                    break;
            }
        }
    }

    @Override
    public void onComplete(Bundle bundle) {
        isWeiboAuthComplete = true;
        shareByWeibo = new ShareByWeibo(this, this);
        shareByWeibo.share(data, this);
    }

    @Override
    public void onWeiboException(WeiboException e) {
        finishWithResult(channel, ShareConstant.SHARE_WEIBO_AUTH_CANCEL);
    }

    @Override
    public void onCancel() {
        finishWithResult(channel, ShareConstant.SHARE_WEIBO_AUTH_ERROR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != shareByWeixin) {
            shareByWeixin.unregisterWeixinReceiver();
        }
    }
}
