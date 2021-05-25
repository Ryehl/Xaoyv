package com.xaoyv.app.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xaoyv.app.entity.event.EmptyEvent;
import com.xaoyv.app.interfaces.IView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Tag:BaseFragment
 *
 * @author Xaoyv
 * date 5/6/2021 11:24 AM
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IView<P> {

    protected Bundle bundle;
    protected Context mContext;
    protected P mPresenter;
    protected String TAG = getClass().getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View inflate = View.inflate(mContext, getLayoutId(), null);
        initView(inflate);
        initData();
        mPresenter = initPresenter();
        if (mPresenter != null)
            mPresenter.attachView(this);
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EmptyEvent event) {
    }
}
