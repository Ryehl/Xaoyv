package com.xaoyv.qqcache.utils;

import android.text.TextUtils;

import com.tencent.mmkv.MMKV;

import java.util.UUID;

/**
 * Tag:
 *
 * @author Xaoyv
 * date 2021/8/3 11:23
 */
public class UserTempInfoUtils {
    private static UserTempInfoUtils utils;

    private UserTempInfoUtils() {
    }

    public static UserTempInfoUtils getInstance() {
        if (utils == null) {
            utils = new UserTempInfoUtils();
        }
        return utils;
    }

    public String getAuthor() {
        String author = "";
        MMKV kv = MMKV.defaultMMKV();
        author = kv.decodeString("author");
        if (TextUtils.isEmpty(author)) {
            author = UUID.randomUUID().toString();
            kv.encode("author", author);
        }
        return author;
    }

    public void clearnAuthor() {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("author", "");
    }
}
