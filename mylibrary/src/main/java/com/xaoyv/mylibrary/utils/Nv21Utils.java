package com.xaoyv.mylibrary.utils;

import android.graphics.Bitmap;

/**
 * <p>Project's name:Tech</p>
 * <p>说明:NV21</p>
 *
 * @author Xaoyv
 * date 11/24/2020 8:50 AM
 */
public class Nv21Utils {

    /**
     * 将Bitmap 转换成 nv21
     *
     * @param inputWidth  宽
     * @param inputHeight 高
     * @param scaled      图片
     * @return nv21
     * @throws Exception 异常
     */
    public static byte[] getNV21(int inputWidth, int inputHeight, Bitmap scaled) throws Exception {

        int[] argb = new int[inputWidth * inputHeight];

        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);
        scaled.recycle();
        return yuv;
    }

    public static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) throws Exception {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }

                index++;
            }
        }
    }

    /**
     * 左上角顶点坐标（left, top）和裁剪的宽高，都得是偶数
     *
     * @param src    原始数据
     * @param width  原始图像的width
     * @param height 原始图像height
     * @param left   裁剪区域左上角的x
     * @param top    裁剪区域左上角的y
     * @param clipW  裁剪的宽度
     * @param clipH  裁剪的高度
     * @return 裁剪后的图像数据
     */
    public static byte[] clipNV212(byte[] src, int width, int height, int left, int top, int clipW, int clipH) {
        // 目标区域取偶(YUV420SP要求图像高度是偶数)
//        long begin = System.nanoTime();
        if (left % 2 == 1) {
            left--;
        }
        if (top % 2 == 1) {
            top--;
        }
        int bottom = top + clipH;
        // 裁剪后的占用的大小
        int size = clipW * clipH * 3 / 2;
        byte[] data = new byte[size];
        // 按照YUV420SP格式，复制Y
        for (int i = top; i < bottom; i++) {
            System.arraycopy(src, left + i * width, data, (i - top) * clipW, clipW);
        }
        // 按照YUV420SP格式，复制UV
        int startH = height + top / 2;
        int endH = height + bottom / 2;
        for (int i = startH; i < endH; i++) {
            System.arraycopy(src,
                    left + i * width,
                    data,
                    (i - startH + clipH) * clipW,
                    clipW);
        }
//        long end = System.nanoTime();
//        Log.d("NV21Utils", "clip use: " + (end - begin) + "ns");
        return data;
    }
}
