package com.xaoyv.app.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xaoyv.app.R;
import com.xaoyv.app.adapter.RecyAdap;
import com.xaoyv.app.base.BaseActivity;
import com.xaoyv.app.utils.BitMapUtils;
import com.xaoyv.app.utils.Constant;
import com.xaoyv.app.utils.NetUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class QQCatchActivity extends BaseActivity {
    private boolean haspre = false, isScan = false;
    private List<String> imgs = new ArrayList<>();
    private Button btn;
    private TextView status;
    private XRecyclerView recy;

    private synchronized void runScan() {
        if (!isScan)
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    isScan = true;
                    runOnUiThread(() -> btn.setText("正在扫描"));
                    getFile(new File(Environment.getExternalStorageDirectory() + Constant.path));
                    runOnUiThread(() -> {
                        btn.setText("重新扫描");
                        status.setText("扫描完成, 新增" + imgs.size() + "张图片，已保存至图库");
                        recy.setLayoutManager(new GridLayoutManager(QQCatchActivity.this, 3));
                        RecyAdap adapter = new RecyAdap(imgs);
                        recy.setAdapter(adapter);
                    });
                    isScan = false;
                }
            }.start();
    }

    /**
     * scan to get file list
     *
     * @param file file
     */
    private void getFile(File file) {
        if (file == null) {
            Log.d("TAG", "getFile : null.");
            return;
        }
        if (file.isFile()) {
            runOnUiThread(() -> status.setText("正在扫描" + file.getName()));
            Log.d("TAG", "getFile: " + file.getName());
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
        File f = new File(Environment.getExternalStorageDirectory() + "/Pictures/" + file.getName() + ".jpg");
        if (!f.exists()) {
            //save image
            BitMapUtils.saveImg2Sdcard(bitmap, Bitmap.CompressFormat.JPEG, Environment.getExternalStorageDirectory() + "/Pictures/" + file.getName() + ".jpg");
            //update gallery
            try {
                MediaStore.Images.Media.insertImage(this.getContentResolver(), f.getAbsolutePath(), f.getName(), null);
                imgs.add(f.getAbsolutePath());
            } catch (FileNotFoundException ignored) {
            }
            //update to cloud
            NetUtils.getNetUtils().uploadHd("qc/uploadimg", f);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.REQUESTCODE) {
            haspre = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (haspre) {
                status.setText("点击开始扫描");
                btn.setText("开始扫描");
            } else {
                status.setText("没有文件访问权限");
                btn.setText("点击授权");
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_qqcatch;
    }

    @Override
    public void initView(View rootView) {
        btn = findViewById(R.id.btn);
        status = findViewById(R.id.tv_status);
        recy = findViewById(R.id.recy);
    }

    @Override
    public void initData() {
        //get permission
        requestPermissions(Constant.FILE_PERMISSIONS, Constant.REQUESTCODE);

        //listener
        btn.setOnClickListener(v -> {
            imgs.clear();
            if (haspre)
                runScan();
            else
                requestPermissions(Constant.FILE_PERMISSIONS, Constant.REQUESTCODE);
        });
    }
}
