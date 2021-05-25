package com.xaoyv.app.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xaoyv.app.R;
import com.xaoyv.app.base.BaseActivity;

import java.io.File;
import java.util.List;

public class HomeActivity extends BaseActivity {

    private Button btn_qqcatch, btn_qrcode, btn_xf_msc, btn_img_info, btn_getimg;


    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initView(View rootView) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        btn_qqcatch = findViewById(R.id.btn_qqcatch);
        btn_qrcode = findViewById(R.id.btn_qrcode);
        btn_xf_msc = findViewById(R.id.btn_xf_msc);
        btn_img_info = findViewById(R.id.btn_img_info);
        btn_getimg = findViewById(R.id.btn_getimg);
    }

    @Override
    public void initData() {
        //QQ闪照
        btn_qqcatch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, QQCatchActivity.class);
            startActivity(intent);
        });

        //二维码
        btn_qrcode.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, QrcodeActivity.class);
            startActivity(intent);
        });

        //讯飞语音
        btn_xf_msc.setOnClickListener(v -> {
//            Intent intent = new Intent(HomeActivity.this, .class);
//            startActivity(intent);
            Toast.makeText(this, "正在开发...", Toast.LENGTH_SHORT).show();
        });

        //获取图片信息
        btn_img_info.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ImageInfoActivity.class);
            startActivity(intent);
        });

        btn_getimg.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ShowqcimgsActivity.class);
            startActivity(intent);
        });

        btn_getimg.setOnLongClickListener(v -> {
            IndexActivity.start(this, true);
            return true;
        });

//        final PackageManager packageManager = getPackageManager();// 获取packagemanager
//        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
//        Log.d(TAG, "initData: " + new Gson().toJson(pinfo).length());
    }
}
