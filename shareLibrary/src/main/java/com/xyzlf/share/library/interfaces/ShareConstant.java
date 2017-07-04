package com.xyzlf.share.library.interfaces;

/**
 * Created by zhanglifeng on 16/06/20
 */
public class ShareConstant {

    /// -------------------------- 分享渠道 --------------------------
    /**
     * 微信好友分享渠道
     */
    public static final int SHARE_CHANNEL_WEIXIN_FRIEND = 1; //微信好友
    /**
     * weixin circle
     */
    public static final int SHARE_CHANNEL_WEIXIN_CIRCLE = 1 << 1;
    /**
     * 新浪微博渠道
     */
    public static final int SHARE_CHANNEL_SINA_WEIBO = 1 << 2; //新浪微博
    /**
     * QQ分享渠道
     */
    public static final int SHARE_CHANNEL_QQ = 1 << 3; //QQ
    /**
     * QQ空间渠道
     */
    public static final int SHARE_CHANNEL_QZONE = 1 << 4;//qzone分享
    /**
     * 短信分享渠道
     */
    public static final int SHARE_CHANNEL_SMS = 1 << 5; //短信
    /**
     * 邮箱分享渠道
     */
    public static final int SHARE_CHANNEL_EMAIL = 1 << 6; //邮箱
    /**
     * 更多渠道
     */
    public static final int SHARE_CHANNEL_SYSTEM = 1 << 10; //更多
    /**
     * All channel
     */
    public static final int SHARE_CHANNEL_ALL = ~0;

    /// -------------------------- 分享渠道 --------------------------

    /// <<<< share status
    public static final String EXTRA_SHARE_DATA = "extra_share_data";
    public static final String EXTRA_SHARE_CHANNEL = "extra_show_channel";
    public static final String EXTRA_SHARE_STATUS = "extra_share_status";

    public static final int SHARE_STATUS_COMPLETE = 1;
    public static final int SHARE_STATUS_FAILED = 2;
    public static final int SHARE_STATUS_CANCEL = 3;
    public static final int SHARE_STATUS_ERROR = 4;

    public static final int SHARE_WEIBO_AUTH_COMPLETE = 20;
    public static final int SHARE_WEIBO_AUTH_CANCEL = 21;
    public static final int SHARE_WEIBO_AUTH_ERROR = 22;

    /// <<<< share status


    /// <<< 微信回调
    public static final String ACTION_WEIXIN_CALLBACK = "com.financial360.nicaifu.share.action.WEIXIN_CALLBACK";
    public static final String EXTRA_WEIXIN_RESULT = "weixin_result";
    /// <<< 微信回调

    public static final String WEIXIN_PACKAGE_NAME = "com.tencent.mm"; // 微信包名
    public static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq"; // QQ包名
    public static final String SINA_WEIBO_PACKAGE_NAME = "com.sina.weibo"; // 新浪微博
    public static final String SINA_WEIBO_LITE_PACKAGE_NAME = "com.sina.weibolite";

    /// <<<< request code
    public static final int REQUEST_CODE = 20112;
    /// <<<< request code

}
