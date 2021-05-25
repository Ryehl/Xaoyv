package com.luck.picture.lib.compress.gifcode;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class Utils {
    public static String toHex(int value, int length) {
        String hex = Integer.toHexString(value);
        hex = hex.toUpperCase();

        if (hex.length() < length) {
            while (hex.length() < length)
                hex = "0" + hex;
        } else if (hex.length() > length) {
            hex = hex.substring(hex.length() - length);
        }
        return hex;
    }

    public static byte[] streamToBytes(InputStream stream) throws IOException,
            OutOfMemoryError {
        byte[] buff = new byte[1024];
        int read;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        while ((read = stream.read(buff)) != -1) {
            bao.write(buff, 0, read);
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bao.toByteArray();
    }


    /**
     * 压缩gif图片
     *
     * @param desFile
     * @param MAX_SIZE
     * @param frames
     */
    public void compressGif(GifImageDecoder gifDecoder, File desFile, double MAX_SIZE, List<GifFrame> frames) {
        AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
        OutputStream os = null;
        try {
            os = new FileOutputStream(desFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        gifEncoder.start(os);
        for (int i = 0; i < frames.size(); i++) {
            Bitmap bitmap = imageZoom(frames.get(i).image, MAX_SIZE / frames.size());
            gifEncoder.addFrame(bitmap);
            gifEncoder.setDelay(gifDecoder.getDelay(i));
            gifEncoder.setRepeat(0);
        }
        gifEncoder.finish();
    }

    private Bitmap imageZoom(Bitmap bitMap, double maxSize) {
        maxSize = maxSize * 0.8;//解决压缩后图片会变大
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length / 1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
                    bitMap.getHeight() / Math.sqrt(i));
        }
        return bitMap;
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}