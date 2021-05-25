package com.xaoyv.mylibrary.base;

import com.xaoyv.mylibrary.interfaces.IView;

/**
 * @author Xaoyv
 * date 2020/10/14 16:54
 */
public abstract class BasePresenter {

    protected IView iView;

    public void attachView(IView iView) {
        this.iView = iView;
    }

    public void detachView() {
        this.iView = null;
    }

}
