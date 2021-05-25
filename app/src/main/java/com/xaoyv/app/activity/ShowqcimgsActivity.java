package com.xaoyv.app.activity;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.xaoyv.app.R;
import com.xaoyv.app.adapter.RecyAdap;
import com.xaoyv.app.base.BaseActivity;
import com.xaoyv.app.entity.bean.FileEntity;
import com.xaoyv.app.utils.Constant;
import com.xaoyv.app.utils.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowqcimgsActivity extends BaseActivity {

    private RecyclerView recyclerView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_showqcimgs;
    }

    @Override
    public void initView(View rootView) {
        recyclerView = findViewById(R.id.recy);
    }

    @Override
    public void initData() {
        String url = "xaoyv/getimgs";
        NetUtils.getNetUtils().getInfo(url, new HashMap<>(), new NetUtils.RequestListener() {
            @Override
            public void success(String string) {
                FileEntity fileEntity = new Gson().fromJson(string, FileEntity.class);
                ArrayList<String> fileList = new ArrayList<>();
                ArrayList<LocalMedia> list = new ArrayList<>();
                for (String fileName : fileEntity.getFileNames()) {
                    if (fileName.contains("\\u003d")) {
                        fileName = fileName.replace("\\u003d", "=");
                    }
                    LocalMedia e = new LocalMedia();
                    e.setPath(fileName);
                    list.add(e);
                    fileList.add(fileName);
                }
                RecyAdap recyAdap = new RecyAdap(fileList);
                recyAdap.setListener((view, position) -> PictureSelector.create(ShowqcimgsActivity.this).themeStyle(R.style.Picture_default_style).openExternalPreview(position, Constant.CHILDPATG, list));
                recyclerView.setAdapter(recyAdap);
                recyclerView.setLayoutManager(new GridLayoutManager(ShowqcimgsActivity.this, 3));
            }

            @Override
            public void error(String message) {
            }
        });
    }
}
