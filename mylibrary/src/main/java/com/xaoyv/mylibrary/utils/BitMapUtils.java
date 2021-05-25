package com.xaoyv.mylibrary.utils;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>Project's name:Tech</p>
 * <p>说明:bitmap</p>
 *
 * @author Xaoyv
 * date 11/24/2020 8:44 AM
 */
public class BitMapUtils {
    public static void saveImg2Sdcard(Bitmap bitmap, Bitmap.CompressFormat format, String path) {
        //Bitmap.CompressFormat.JPEG format
        try {
            FileOutputStream fout = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            bitmap.compress(format, 100, bos);
            bos.flush();
            bos.close();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
