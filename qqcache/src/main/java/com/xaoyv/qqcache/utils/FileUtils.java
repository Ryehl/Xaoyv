package com.xaoyv.qqcache.utils;

import java.io.File;

public class FileUtils {
    public static void scanFile(File file, Listener listener){
        if(file == null || listener == null)return;

        if (listener.check(file)){
            listener.nextRun(file);
        }

        File[] listFiles = file.listFiles();
        if (listFiles != null){
            for (File f:listFiles) {
                scanFile(f, listener);
            }
        }
    }

    static interface Listener{
        boolean check(File file);

        void nextRun(File file);
    }
}
