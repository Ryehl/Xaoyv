package com.xaoyv.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;

/**
 * Tag:BaseFragment
 *
 * @author Xaoyv
 * date 2021/7/7 11:39
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IView<P> {
    @Nullable
    protected P mPresenter;
    protected Context mContext;
    protected final String TAG = this.getClass().getSimpleName();

    private boolean isHiddenChanged;
    private boolean isFirstVisible;
    private boolean isUserVisible;
    private boolean isViewNull;
    private boolean initFlag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void initVariable() {
        isFirstVisible = true;
        isUserVisible = false;
        isViewNull = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(getLayoutId(), null, false);
        mPresenter = initPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!initFlag) {
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    onFirstVisible();
                    return false;
                }
            });
            initFlag = true;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isViewNull) {
            return;
        }
        if (isVisibleToUser) {
            isUserVisible = true;
            if (isFirstVisible) {
                isFirstVisible = false;
                onFirstVisible();
            } else {
                onVisible();
            }
        } else if (isUserVisible) {
            isUserVisible = false;
            onInVisible();
        }
    }

    protected void onFirstVisible() {
    }

    protected void onVisible() {
    }

    protected void onInVisible() {
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        mPresenter = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Nullable
    @Override
    public P initPresenter() {
        return null;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (isHiddenChanged) {
                onInVisible();
            }
            isHiddenChanged = true;
        } else {
            onVisible();
        }
    }
}
