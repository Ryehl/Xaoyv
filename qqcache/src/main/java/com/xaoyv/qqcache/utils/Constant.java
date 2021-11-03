package com.xaoyv.qqcache.utils;

import android.Manifest;

public class Constant {
    public static final String JPG = ".jpg";
    public static final String CHILDPATG = "/DCIM/Camera/";
    public static final String KV_SET_UPED = "set_uped";
    public static final String PICTURES_SAVE_PATH = "/Pictures/";
    public static String APP_SAVE_PATH = "/storage/emulated/0/Android/data/com.xaoyv.qqcache/files";
    public static final String PACKAGE_URI = "package:com.xaoyv.qqcache";
    public static final String path = "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/chatpic/chatimg/";
    public static final int REQUESTCODE = 0;
    public static final String URL = "qc/uploadimg";
    static final String BASE_URL = "http://8.140.155.202:8082/";
    public static final String KV_NEED_DEL = "need_del";
    public static final String KV_DEL_AUTHOR = "del_author";
//    static final String BASE_URL = "http://192.168.1.51:8080/";
    public static String[] ALL_PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
}
