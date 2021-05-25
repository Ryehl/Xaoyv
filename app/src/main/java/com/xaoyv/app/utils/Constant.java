package com.xaoyv.app.utils;

import android.Manifest;

/**
 * @author Xaoyv
 * date 12/16/2020 2:25 PM
 */
public class Constant {
    public static final String JPG = ".jpg";
    public static final String BASE_URL = "http://8.140.155.202:8082/";
    public static final String CHILDPATG = "/Pictures/";
    public static final String path = "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/chatpic/chatimg/";
//    public static final String path = "/Android/data/com.tencent.mobileqq/";
    public static final int REQUESTCODE = 0;
    public static String[] FILE_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
}
