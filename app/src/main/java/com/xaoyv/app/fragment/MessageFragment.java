package com.xaoyv.app.fragment;

import android.view.View;

import com.xaoyv.app.R;
import com.xaoyv.app.base.BaseFragment;

public class MessageFragment extends BaseFragment {
    public static MessageFragment getInstance() {
        return new MessageFragment();
    }
    @Override
    public int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    public void initView(View rootView) {

    }

    @Override
    public void initData() {

    }
}
