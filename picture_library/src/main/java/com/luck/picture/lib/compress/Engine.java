package com.luck.picture.lib.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.luck.picture.lib.compress.gifcode.GifFrame;
import com.luck.picture.lib.compress.gifcode.GifImageDecoder;
import com.luck.picture.lib.compress.gifcode.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Responsible for starting compress and managing active and cached resources.
 */
class Engine {
    private ExifInterface srcExif;
    private String srcImg;
    private File tagImg;
    private int srcWidth;
    private int srcHeight;
    private int mLeastCompressSize;

    Engine(int leastCompressSize, String srcImg, File tagImg) throws IOException {
        if (Checker.isJPG(srcImg)) {
            this.srcExif = new ExifInterface(srcImg);
        }
        this.tagImg = tagImg;
        this.srcImg = srcImg;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeFile(srcImg, options);
        this.srcWidth = options.outWidth;
        this.srcHeight = options.outHeight;
        this.mLeastCompressSize = leastCompressSize;
    }

    private int computeSize() {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide >= 1664 && longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    private Bitmap rotatingImage(Bitmap bitmap) {
        if (srcExif == null) return bitmap;

        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
        }

        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    File compress() throws IOException {
        if (srcImg.toLowerCase().endsWith(".gif")) {
            GifImageDecoder gifDecoder = new GifImageDecoder();
            InputStream is = null;
            try {
                is = new FileInputStream(srcImg);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                gifDecoder.read(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<GifFrame> listFrames = new ArrayList<>();
            listFrames.clear();
            for (int i = 0; i < gifDecoder.getFrameCount(); i++) {
                listFrames.add(gifDecoder.getmGifFrames().get(i));
            }
            new Utils().compressGif(gifDecoder, tagImg, mLeastCompressSize, listFrames);
            return tagImg;
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = computeSize();

            Bitmap tagBitmap = BitmapFactory.decodeFile(srcImg, options);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            tagBitmap = rotatingImage(tagBitmap);

            //////
            tagBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int qualit = 100;
            while (stream.toByteArray().length / 1024 > mLeastCompressSize) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                stream.reset();//重置baos即清空baos
                tagBitmap.compress(Bitmap.CompressFormat.JPEG, qualit, stream);//这里压缩options%，把压缩后的数据存放到baos中
                qualit -= 10;//每次都减少10
            }
            //////
            tagBitmap.recycle();
            FileOutputStream fos = new FileOutputStream(tagImg);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();
            stream.close();

            return tagImg;
        }
    }

}