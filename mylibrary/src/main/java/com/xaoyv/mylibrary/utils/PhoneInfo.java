package com.xaoyv.mylibrary.utils;

import android.app.Activity;
import android.util.Log;

/**
 * <p>项目名称:维度商城</p>
 * <p>简述:获取手机信息</p>
 *
 * @author Xaoyv
 * date 2020/10/15 14:17
 */
public class PhoneInfo {
    /**
     * 获取手机版本信息
     * @return 手机版本号
     */
    public static int getAndroidSdkVersion(){
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取当前Activity状态栏高度
     * @param activity
     * @return
     */
    public static int getStatusBarHeightPx(Activity activity){
        int statusBarHeight = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        Log.d("CompatToolbar", "状态栏高度：" + statusBarHeight + "dp");
        return statusBarHeight;
    }
}
