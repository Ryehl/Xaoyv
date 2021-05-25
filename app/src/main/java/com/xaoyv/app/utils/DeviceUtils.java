package com.xaoyv.app.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.tencent.mmkv.MMKV;

import java.util.UUID;

public class DeviceUtils {
    private static String DEVICE_ID;
    private static String ANDROID_ID;

    public static String getDeviceId(Context context) {
        if (TextUtils.isEmpty(DEVICE_ID)) {
            DEVICE_ID = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return DEVICE_ID;
    }

    public static String getAndroidId(Context context) {
        if (ANDROID_ID == null) {
            ANDROID_ID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return ANDROID_ID;
    }

    /**
     * 得到PackgeName
     *
     * @param context 上下文
     * @return PackgeName
     */

    public static String getUUID(Context context) {
        String uuid = MMKV.defaultMMKV().decodeString("otherid_empty_uuid");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            MMKV.defaultMMKV().encode("otherid_empty_uuid", uuid);
        }
        return uuid;
    }
}
