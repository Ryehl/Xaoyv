package com.xaoyv.qqcache;

import android.app.Application;

import com.tencent.mmkv.MMKV;

public class QQCacheApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MMKV.initialize(this);
    }
}
