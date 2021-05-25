package com.xaoyv.qqcache;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.xaoyv.qqcache.adapter.RecyAdap;
import com.xaoyv.qqcache.utils.ActivityUtils;
import com.xaoyv.qqcache.utils.Constant;
import com.xaoyv.qqcache.utils.ScanUtil;

import java.io.File;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recy;
    private ArrayList<LocalMedia> localMedias;
    private ArrayList<String> imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initView();

        reqPer();

        initData();
    }

    private void initData() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            localMedias = new ArrayList<>();
            imgs = new ArrayList<>();

            scanFile(Constant.APP_SAVE_PATH + "/");
            scanFile(Constant.PICTURES_SAVE_PATH);

            if (imgs.size() == 0){
                Toast.makeText(this, "未找到历史图片", Toast.LENGTH_SHORT).show();
            }
            recy.setLayoutManager(new GridLayoutManager(HistoryActivity.this, 3));
            RecyAdap adapter = new RecyAdap(imgs);
            adapter.setListener((v, position) -> {
                PictureSelector.create(HistoryActivity.this)
                        .themeStyle(R.style.Picture_default_style)
                        .openExternalPreview(position, Environment.getExternalStorageDirectory() + Constant.PICTURES_SAVE_PATH + "shanzhao/", localMedias);
            });
            recy.setAdapter(adapter);

            ScanUtil scanUtil = new ScanUtil(this);
            scanUtil.type1 = false;
            scanUtil.type2 = false;
            scanUtil.uploadNext();
        } else {
            Toast.makeText(this, "请开启文件读写权限，否则无法破解闪照", Toast.LENGTH_LONG).show();
            Uri packageURI = Uri.parse(Constant.PACKAGE_URI);
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            startActivity(intent);
        }
    }

    private void scanFile(String path) {
        File[] listFiles = new File(path).listFiles();
        if (listFiles == null) {
            return;
        }
        for (File tempFile : listFiles) {
            if (tempFile.isFile() && tempFile.getName().endsWith("_fp.jpg")) {
                imgs.add(tempFile.getAbsolutePath());
                LocalMedia localMedia = new LocalMedia();
                localMedia.setPath(tempFile.getAbsolutePath());
                localMedias.add(localMedia);
            }
        }
    }

    private void reqPer() {
        requestPermissions(Constant.ALL_PERMISSIONS, Constant.REQUESTCODE);
    }

    private void initView() {
        ActivityUtils.setViewTransparent(this);
        ActivityUtils.changStatusIconCollor(this, true);
        recy = findViewById(R.id.recy);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
