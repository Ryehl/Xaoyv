package com.xaoyv.mylibrary.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xaoyv.mylibrary.interfaces.IView;


/**
 *
 * @author Xaoyv
 * date 2020/10/17 09:10
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IView<P> {

    protected P pre;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getContext(), getLayout(), null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        pre = initPresenter();
        if (pre!=null)
            pre.attachView(this);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pre!=null)
            pre.detachView();
        pre = null;
    }
}
