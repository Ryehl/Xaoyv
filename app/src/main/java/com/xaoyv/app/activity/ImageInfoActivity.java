package com.xaoyv.app.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xaoyv.app.R;
import com.xaoyv.app.base.BaseActivity;
import com.xaoyv.app.utils.GpsUtil;

import java.io.IOException;

public class ImageInfoActivity extends BaseActivity {

    private TextView mText;
    private Button btn;
    private static final int REQUEST_SYSTEM_PIC = 1;
    private static final String TAG = ImageInfoActivity.class.getSimpleName();

    /**
     * @param path 图片路径
     */
    private void getInfo(String path) {
        Log.d(TAG, "getInfo: " + path);
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            Log.d(TAG, "getInfo: " + new Gson().toJson(exifInterface));
            String guangquan = exifInterface.getAttribute(ExifInterface.TAG_APERTURE);
            String shijain = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            String baoguangshijian = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            String jiaoju = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            String chang = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            String kuan = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String moshi = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            String zhizaoshang = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            String iso = exifInterface.getAttribute(ExifInterface.TAG_ISO);
            String jiaodu = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            String baiph = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            String altitude_ref = exifInterface.getAttribute(ExifInterface
                    .TAG_GPS_ALTITUDE_REF);
            String altitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
            String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String latitude_ref = exifInterface.getAttribute(ExifInterface
                    .TAG_GPS_LATITUDE_REF);
            String longitude_ref = exifInterface.getAttribute(ExifInterface
                    .TAG_GPS_LONGITUDE_REF);
            String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String timestamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
            String processing_method = exifInterface.getAttribute(ExifInterface
                    .TAG_GPS_PROCESSING_METHOD);

            //转换经纬度格式
            double lat = score2dimensionality(latitude);
            double lon = score2dimensionality(longitude);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("光圈 = " + guangquan + "\n")
                    .append("时间 = " + shijain + "\n")
                    .append("曝光时长 = " + baoguangshijian + "\n")
                    .append("焦距 = " + jiaoju + "\n")
                    .append("长 = " + chang + "\n")
                    .append("宽 = " + kuan + "\n")
                    .append("型号 = " + moshi + "\n")
                    .append("制造商 = " + zhizaoshang + "\n")
                    .append("ISO = " + iso + "\n")
                    .append("角度 = " + jiaodu + "\n")
                    .append("白平衡 = " + baiph + "\n")
                    .append("海拔高度 = " + altitude_ref + "\n")
                    .append("GPS参考高度 = " + altitude + "\n")
                    .append("GPS时间戳 = " + timestamp + "\n")
                    .append("GPS定位类型 = " + processing_method + "\n")
                    .append("GPS参考经度 = " + latitude_ref + "\n")
                    .append("GPS参考纬度 = " + longitude_ref + "\n")
                    .append("GPS经度 = " + lat + "\n")
                    .append("GPS经度 = " + lon + "\n");

            //将获取的到的信息设置到TextView上
            mText.setText(stringBuilder.toString());

            /**
             * 将wgs坐标转换成百度坐标
             * 就可以用这个坐标通过百度SDK 去获取该经纬度的地址描述
             */
            double[] wgs2bd = GpsUtil.wgs2bd(lat, lon);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将 112/1,58/1,390971/10000 格式的经纬度转换成 112.99434397362694格式
     *
     * @param string 度分秒
     * @return 度
     */
    private double score2dimensionality(String string) {
        double dimensionality = 0.0;
        if (null == string) {
            return dimensionality;
        }

        //用 ，将数值分成3份
        String[] split = string.split(",");
        for (int i = 0; i < split.length; i++) {

            String[] s = split[i].split("/");
            //用112/1得到度分秒数值
            double v = Double.parseDouble(s[0]) / Double.parseDouble(s[1]);
            //将分秒分别除以60和3600得到度，并将度分秒相加
            dimensionality = dimensionality + v / Math.pow(60, i);
        }
        return dimensionality;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleImageOnKitkat(data);
    }

    private void handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是File类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        getInfo(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_info;
    }

    @Override
    public void initView(View rootView) {
        mText = findViewById(R.id.imginfo_show);
        btn = findViewById(R.id.imginfo_btn);
    }

    @Override
    public void initData() {
        requestPermissions(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        }, 0);

        btn.setOnClickListener(v -> {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_SYSTEM_PIC);//打开系统相册
        });
    }
}
