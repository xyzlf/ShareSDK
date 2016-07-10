package com.xyzlf.share.library.channel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.xyzlf.com.share.library.R;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.OnShareListener;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.util.ToastUtil;

import java.util.ArrayList;

/**
 * Created by zhanglifeng
 */
public class ShareByQZone extends ShareByQQ {

    public ShareByQZone(Context context) {
        super(context);
    }

    @Override
    public void share(ShareEntity data, final OnShareListener listener) {
        if (null == data) {
            return;
        }
        if (context == null || !(context instanceof Activity)) {
            return;
        }
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, data.getTitle());//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, data.getContent());//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, data.getUrl());//必填
        ArrayList<String> arrayList = new ArrayList<>();
        if (!TextUtils.isEmpty(data.getImgUrl())) {
            arrayList.add(data.getImgUrl());
        }
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrayList);
        mTencent.shareToQzone((Activity) context, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {
                if (null != listener) {
                    listener.onShare(ShareConstant.SHARE_CHANNEL_QZONE, ShareConstant.SHARE_STATUS_COMPLETE);
                }
                ToastUtil.showToast(context, R.string.share_success, true);
            }
            @Override
            public void onError(UiError uiError) {
                if (null != listener) {
                    listener.onShare(ShareConstant.SHARE_CHANNEL_QZONE, ShareConstant.SHARE_STATUS_FAILED);
                }
                if (null != uiError) {
                    ToastUtil.showToast(context, uiError.errorMessage, true);
                }
            }
            @Override
            public void onCancel() {
                if (null != listener) {
                    listener.onShare(ShareConstant.SHARE_CHANNEL_QZONE, ShareConstant.SHARE_STATUS_CANCEL);
                }
                ToastUtil.showToast(context, R.string.share_cancel, true);
            }
        });
    }
}
