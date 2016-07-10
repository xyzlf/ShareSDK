package com.xyzlf.share.library.interfaces;

/**
 * Created by zhanglifeng
 */
public interface OnShareListener {
    /**
     * @param channel {@link ShareConstant#SHARE_CHANNEL_ALL} 渠道
     * @param status {@link ShareConstant#SHARE_STATUS_COMPLETE} {@link ShareConstant#SHARE_STATUS_FAILED}
     * {@link ShareConstant#SHARE_STATUS_CANCEL}
     */
    void onShare(int channel, int status);
}
