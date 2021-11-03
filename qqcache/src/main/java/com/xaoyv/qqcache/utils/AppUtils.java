package com.xaoyv.qqcache.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * Tag:
 *
 * @author Xaoyv
 * date 4/21/2021 12:03 PM
 */
public class AppUtils {

    private static String AndroidId;

    /**
     * 得到DeviceId
     *
     * @param context 上下文
     * @return DeviceId
     */
    public static String getDeviceId(Context context) {
        if (TextUtils.isEmpty(AndroidId)) {
            AndroidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return AndroidId;
    }
}
