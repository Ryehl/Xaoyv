package com.xaoyv.delfile.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Tag:BaseActivity
 *
 * @author Xaoyv
 * date 2021/7/7 11:32
 */
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements IView<P> {

    @Nullable
    protected P mPresenter;
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        mPresenter = initPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @Nullable
    @Override
    public P initPresenter() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
