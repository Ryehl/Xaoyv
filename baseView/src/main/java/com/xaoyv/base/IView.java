package com.xaoyv.base;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

/**
 * @author Xaoyv
 * date 2021/7/7 11:33
 */
public interface IView<P extends BasePresenter> {
    @LayoutRes
    int getLayoutId();

    void initData();

    @Nullable
    P initPresenter();
}
