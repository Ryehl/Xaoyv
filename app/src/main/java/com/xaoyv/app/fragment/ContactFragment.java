package com.xaoyv.app.fragment;

import android.view.View;

import com.xaoyv.app.R;
import com.xaoyv.app.base.BaseFragment;

public class ContactFragment extends BaseFragment {
    public static ContactFragment getInstance() {
        return new ContactFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    public void initView(View rootView) {

    }

    @Override
    public void initData() {

    }
}
