package com.xaoyv.delfile.base;

/**
 * Tag:BasePresenter
 *
 * @author Xaoyv
 * date 2021/7/7 11:33
 */
public class BasePresenter<V extends IView> {
    protected V mRootView;
    protected final String TAG = this.getClass().getSimpleName();

    void attachView(V view) {
        mRootView = view;
    }

    void detachView() {
        mRootView = null;
    }
}
