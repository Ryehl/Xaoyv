package com.xaoyv.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.xaoyv.app.R;
import com.xaoyv.app.base.BaseActivity;
import com.xaoyv.app.utils.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class QrcodeActivity extends BaseActivity {

    private EditText show;
    private ImageView img;
    private static Bitmap bmp;
    private String TAG = "MainActivity";
    private Button scan;
    private Button toImg;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String stringExtra = data.getStringExtra(CodeUtils.RESULT_STRING);
            show.setText(stringExtra);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_qrcode;
    }

    @Override
    public void initView(View rootView) {
        scan = findViewById(R.id.main_btn_scan);
        toImg = findViewById(R.id.main_btn_toImg);
        show = findViewById(R.id.main_et_show);
        img = findViewById(R.id.main_img_show);
    }

    @Override
    public void initData() {
        requestPermissions(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.VIBRATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 1);


        scan.setOnClickListener(view -> startActivityForResult(new Intent(this, CaptureActivity.class), 200));
        toImg.setOnClickListener(v -> {
            String text = show.getText().toString();
            bmp = CodeUtils.createImage(text, 3000, 3000, null);
            img.setImageBitmap(bmp);
        });
        img.setOnLongClickListener(view -> {
            if (bmp == null)
                return true;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有存储的权限，无法为您保存呢~", Toast.LENGTH_LONG).show();
                return true;
            }
            UUID uuid = UUID.randomUUID();
            File path = new File(Environment.getExternalStorageDirectory(), Constant.CHILDPATG + uuid.toString() + Constant.JPG);///sdcard/DCIM/Camera
            FileOutputStream fos;
            try {
                //path.createNewFile();
                fos = new FileOutputStream(path);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Log.i(TAG, uuid.toString());
                fos.close();
                Toast.makeText(this, "已经成功保存在相册了！", Toast.LENGTH_SHORT).show();
                MediaStore.Images.Media.insertImage(this.getContentResolver(), path.getAbsolutePath(), path.getName(), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        });
    }
}
