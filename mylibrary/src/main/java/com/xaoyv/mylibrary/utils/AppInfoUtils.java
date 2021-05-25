package com.xaoyv.mylibrary.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * <p>项目名称:维度商城</p>
 * <p>简述:app信息类</p>
 *
 * @author Xaoyv
 * date 2020/10/16 08:50
 */
public class AppInfoUtils {
    /**
     * 获取版本号
     *
     * @param activity act
     * @return -1
     */
    public static int getVersionCode(Activity activity) {
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
