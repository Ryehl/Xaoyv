package com.xaoyv.app.application;

import android.app.Application;
import android.content.Context;

import com.tencent.mmkv.MMKV;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

public class XaoyvApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        MMKV.initialize(this);

        ZXingLibrary.initDisplayOpinion(this);
    }


    public static Context getAppContext(){
        return  context;
    }
}
