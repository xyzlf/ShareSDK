package com.xyzlf.share.library.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import com.xyzlf.share.library.ShareDialogActivity;
import com.xyzlf.share.library.ShareHandlerActivity;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.ShareConstant;

/**
 * Created by zhanglifeng on 15/6/4.
 */
public class ShareUtil {

    /**
     * 分享数据
     * @param activity
     * @param channel {@link ShareConstant}
     * @param data {@link ShareEntity}
     * @param requestCode {@link android.app.Activity#onActivityResult(int, int, Intent)}
     */
    public static void startShare(Activity activity, int channel, ShareEntity data, int requestCode) {
        if (null == activity || activity.isFinishing()) {
            return;
        }
        Intent intent = new Intent(activity, ShareHandlerActivity.class);
        intent.putExtra(ShareConstant.EXTRA_SHARE_CHANNEL, channel);
        intent.putExtra(ShareConstant.EXTRA_SHARE_DATA, data);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 调起ShareDialogActivity
     * @param activity
     * @param data {@link ShareEntity}
     * @param requestCode
     */
    public static void showShareDialog(Activity activity, ShareEntity data, int requestCode) {
        showShareDialog(activity, ShareConstant.SHARE_CHANNEL_ALL, data, requestCode);
    }

    /**
     * 调起ShareDialogActivity
     * @param activity
     * @param data {@link ShareEntity}
     * @param channel {@link ShareConstant#SHARE_CHANNEL_ALL}
     * @param requestCode
     */
    public static void showShareDialog(Activity activity, int channel, ShareEntity data, int requestCode) {
        if (null == activity || activity.isFinishing()) {
            return;
        }
        Intent intent = new Intent(activity, ShareDialogActivity.class);
        intent.putExtra(ShareConstant.EXTRA_SHARE_DATA, data);
        intent.putExtra(ShareConstant.EXTRA_SHARE_CHANNEL, channel);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 传递数组数据
     * @param activity {@link Activity}
     * @param data {@link ShareEntity}
     * @param requestCode
     */
    public static void showShareDialog(Activity activity, SparseArray<ShareEntity> data, int requestCode) {
        showShareDialog(activity, ShareConstant.SHARE_CHANNEL_ALL, data, requestCode);
    }

    /**
     * 传递数组数据
     * @param activity
     * @param channel
     * @param data
     * @param requestCode
     */
    public static void showShareDialog(Activity activity, int channel, SparseArray<ShareEntity> data, int requestCode) {
        if (null == activity || activity.isFinishing()) {
            return;
        }
        Intent intent = new Intent(activity, ShareDialogActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSparseParcelableArray(ShareConstant.EXTRA_SHARE_DATA, data);
        intent.putExtra(ShareConstant.EXTRA_SHARE_DATA, bundle);
        intent.putExtra(ShareConstant.EXTRA_SHARE_CHANNEL, channel);
        activity.startActivityForResult(intent, requestCode);
    }


    // 使用此方法打开外部activity,避免外部activity不存在而造成崩溃
    public static boolean startActivity(Context context, Class<?> c) {
        boolean result = true;
        try {
            Intent intent = new Intent(context, c);
            context.startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            result = false;
            exception.printStackTrace();
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    // 使用此方法打开外部activity,避免外部activity不存在而造成崩溃
    public static boolean startActivity(Context context, Intent intent) {
        boolean bResult = true;
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            bResult = false;
            e.printStackTrace();
        } catch (Exception e) {
            bResult = false;
            e.printStackTrace();
        }
        return bResult;
    }

}
