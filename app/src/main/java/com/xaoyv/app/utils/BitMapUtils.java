package com.xaoyv.app.utils;

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
        //format = Bitmap.CompressFormat.JPEG;
        if (bitmap == null)
            return;
        try {
            FileOutputStream fos = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(format, 100, bos);
            bos.flush();
            bos.close();
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
