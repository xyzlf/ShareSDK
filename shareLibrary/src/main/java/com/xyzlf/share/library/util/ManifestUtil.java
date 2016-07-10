package com.xyzlf.share.library.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by zhanglifeng.
 */
public class ManifestUtil {

    public static final String SINA_WEIBO_KEY = "sina_weibo_key";
    public static final String SINA_WEIBO_REDIRECTURI = "sina_weibo_redirecturi";
    public static final String WEIXIN_KEY = "weixin_key";
    public static final String WEIXIN_REDIRECTURI_KEY = "weixin_redirecturi";
    public static final String TENCENT_QQ_APPID = "tencent_qq_appid";

    /**
     * 读取新浪微博的Appkey
     * @param context context
     * @return String
     */
    public static String getSinaWeiboKey(Context context) {

        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if(null != appInfo) {
                Bundle bundle = appInfo.metaData;
                String key = null;
                if(null != bundle) {
                    key = String.valueOf(bundle.get(SINA_WEIBO_KEY));
                }
                if(!TextUtils.isEmpty(key)) {
                    return key;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 读取新浪微博的重定向URI
     * @param context context
     * @return String
     */
    public static String getSinaWeiboRedirecturi(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);

            if(null != appInfo) {
                Bundle bundle = appInfo.metaData;
                String url = null;
                if(null != bundle) {
                    url = bundle.getString(SINA_WEIBO_REDIRECTURI);
                }
                if(!TextUtils.isEmpty(url)) {
                    return url;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 读取微信的AppKey
     * @param context context
     * @return String
     */
    public static String getWeixinKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);

            if(null != appInfo) {
                Bundle bundle = appInfo.metaData;
                String key = null;
                if(null != bundle) {
                    key = String.valueOf(bundle.get(WEIXIN_KEY));
                }
                if(!TextUtils.isEmpty(key)) {
                    return key;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 读取微信的重定向URI
     * @param context context
     * @return String
     */
    public static String getWeixinRedirecturi(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if(null != appInfo) {
                Bundle bundle = appInfo.metaData;
                String url = null;
                if(null != bundle) {
                    url = bundle.getString(WEIXIN_REDIRECTURI_KEY);
                }

                if(!TextUtils.isEmpty(url)) {
                    return url;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * tencent Appid
     * @param context context
     * @return String
     */
    public static String getTencentQQAppId(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);

            if(null != appInfo) {
                Bundle bundle = appInfo.metaData;
                String key = null;
                if(null != bundle) {
                    key = String.valueOf(bundle.get(TENCENT_QQ_APPID));
                }

                if(!TextUtils.isEmpty(key)) {
                    return key;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "222222";
    }

}
