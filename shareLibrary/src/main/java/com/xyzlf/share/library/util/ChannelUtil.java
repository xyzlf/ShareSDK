package com.xyzlf.share.library.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.xyzlf.share.library.interfaces.ShareConstant;

/**
 * Created by zhanglifeng on 2016/6/17
 */
public class ChannelUtil {
    /**
     * 判断App是否安装
     *
     * @param context context
     * @param pkgName pkgname
     * @return boolean
     */
    public static boolean isAppInstall(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(pkgName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
    /**
     * 微信是否安装
     * @param context context
     * @return boolean
     */
    public static boolean isWeixinInstall(Context context) {
        return isAppInstall(context, ShareConstant.WEIXIN_PACKAGE_NAME);
    }
    /**
     * QQ是否安装
     * @param context context
     * @return boolean
     */
    public static boolean isQQInstall(Context context) {
        return isAppInstall(context, ShareConstant.QQ_PACKAGE_NAME);
    }

    /**
     * 微博是否安装
     * @param context context
     * @return boolean
     */
    public static boolean isWeiboInstall(Context context) {
        return isAppInstall(context, ShareConstant.SINA_WEIBO_PACKAGE_NAME);
    }
    /**
     * @param context context
     * @return boolean
     */
    public static boolean isWeiboLiteInstall(Context context) {
        return isAppInstall(context, ShareConstant.SINA_WEIBO_LITE_PACKAGE_NAME);
    }

}
