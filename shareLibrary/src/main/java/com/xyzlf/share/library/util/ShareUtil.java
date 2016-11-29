package com.xyzlf.share.library.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;

import com.xyzlf.com.share.library.R;
import com.xyzlf.share.library.ShareDialogActivity;
import com.xyzlf.share.library.ShareHandlerActivity;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.ShareConstant;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zhanglifeng on 15/6/4.
 */
public class ShareUtil {

    /**
     * 分享数据
     * @param activity Activity
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
     * @param activity Activity
     * @param data {@link ShareEntity}
     * @param requestCode requestCode
     */
    public static void showShareDialog(Activity activity, ShareEntity data, int requestCode) {
        showShareDialog(activity, ShareConstant.SHARE_CHANNEL_ALL, data, requestCode);
    }

    /**
     * 调起ShareDialogActivity
     * @param activity Activity
     * @param data {@link ShareEntity}
     * @param channel {@link ShareConstant#SHARE_CHANNEL_ALL}
     * @param requestCode requestCode
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
     * data is sparsearray
     * @param activity {@link Activity}
     * @param data {@link ShareEntity}
     * @param requestCode requestCode
     */
    public static void showShareDialog(Activity activity, SparseArray<ShareEntity> data, int requestCode) {
        showShareDialog(activity, ShareConstant.SHARE_CHANNEL_ALL, data, requestCode);
    }

    /**
     * data is sparsearray
     * @param activity Activity
     * @param channel 渠道
     * @param data data
     * @param requestCode requestCode
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

    /**
     * save the Bitmap to SDCard
     * @param context context
     * @param bitmap bitmap
     * @return filePath
     */
    public static String saveBitmapToSDCard(Context context, Bitmap bitmap) {
        if (null == context) {
            return null;
        }
        if (null == bitmap) {
            ToastUtil.showToast(context, R.string.share_save_bitmap_failed, true);
            return null;
        }
        //SDCard is valid
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            ToastUtil.showToast(context, R.string.share_save_bitmap_no_sdcard, true);
            return null;
        }
        String filePath = null;
        File externalFilesDir = context.getExternalFilesDir(null);
        String dir = null;
        if (null != externalFilesDir) {
            dir = externalFilesDir.getAbsolutePath();
        }
        String packageName = context.getPackageName();
        if (!TextUtils.isEmpty(dir)) {
            if (!dir.endsWith(File.separator)) {
                filePath = dir + File.separator + packageName + "_share_pic.png";
            } else {
                filePath = dir + packageName + "_share_pic.png";
            }
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();

                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                ToastUtil.showToast(context, e.getMessage(), true);
            }
        }
        return filePath;
    }

}
