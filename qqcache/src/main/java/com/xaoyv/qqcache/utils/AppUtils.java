package com.xaoyv.qqcache.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Tag:
 *
 * @author Xaoyv
 * date 4/21/2021 12:03 PM
 */
public class AppUtils {

    private static String DEVICEID;

    /**
     * 得到DeviceId
     *
     * @param context 上下文
     * @return DeviceId
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        if(TextUtils.isEmpty(DEVICEID)){
            DEVICEID = Settings.System.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

//        if (!TextUtils.isEmpty(DEVICEID)) {
//            return DEVICEID;
//        }
//        if (!TextUtils.isEmpty(getAndroidId(context))) {
//            return getAndroidId(context);
//        }
//        if (!TextUtils.isEmpty(ZYApplication.oaid)) {
//            return ZYApplication.oaid;
//        }
//        if (!TextUtils.isEmpty(getUUID(context))) {
//            return getUUID(context);
//        }
        return DEVICEID;
    }
}
