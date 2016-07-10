package com.xyzlf.share;


import com.xyzlf.share.library.interfaces.ShareConstant;

/**
 * Created by zhanglifeng on 2016/6/22
 */
public class ShareCallBack {

    /**
     * @param channel
     * @param status
     */
    public void onShareCallback(int channel, int status) {
        switch (channel) {
            case ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND:
            case ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE:
                onWeixinCallBack(status);
                break;

            case ShareConstant.SHARE_CHANNEL_SINA_WEIBO:
                onWeiboCallBack(status);
                break;

            case ShareConstant.SHARE_CHANNEL_QQ:
                onQQCallBack(status);
                break;

            case ShareConstant.SHARE_CHANNEL_QZONE:
                onQZoneCallBack(status);
                break;

            case ShareConstant.SHARE_CHANNEL_SYSTEM:
                onSystemCallBack(status);
                break;
        }
    }

    /** 没有取消状态 **/
    private void onWeixinCallBack(int status) {
        switch (status) {
            /** 成功 **/
            case ShareConstant.SHARE_STATUS_COMPLETE:

                break;
            /** 失败 **/
            case ShareConstant.SHARE_STATUS_FAILED:

                break;
        }
    }

    /** 以下有成功，失败，取消三种回调结果 **/
    private void onWeiboCallBack(int status) {
        switch (status) {
            /** 成功 **/
            case ShareConstant.SHARE_STATUS_COMPLETE:

                break;
            /** 失败 **/
            case ShareConstant.SHARE_STATUS_FAILED:

                break;
            /** 取消 **/
            case ShareConstant.SHARE_STATUS_CANCEL:

                break;
        }
    }

    private void onQQCallBack(int status) {

    }

    private void onQZoneCallBack(int status) {
    }
    /** 以上有成功，失败，取消三种回调结果 **/


    /** 系统分享，回调结果没有实际意义，因为不能知道用户是否发送短信了，是否发送邮件了 **/
    private void onSystemCallBack(int status) {
    }
    /** 系统分享，回调结果没有实际意义，因为不能知道用户是否发送短信了，是否发送邮件了 **/

}
