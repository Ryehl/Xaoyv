package com.xaoyv.app.interfaces;

import android.view.View;

import com.xaoyv.app.base.BasePresenter;

public interface IView<P extends BasePresenter> {

    int getLayoutId();

    //!!! activity's rootView is null
    void initView(View rootView);

    void initData();

    default P initPresenter() {
        return null;
    }
}
