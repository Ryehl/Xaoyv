package com.xaoyv.app.fragment;

import android.view.View;

import com.xaoyv.app.R;
import com.xaoyv.app.base.BaseFragment;

public class HomeFragment extends BaseFragment {
    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView(View rootView) {

    }

    @Override
    public void initData() {

    }
}
