package com.xaoyv.app.base;

import com.xaoyv.app.interfaces.IView;

public abstract class BasePresenter{
    public IView iView;
    public void attachView(IView iView){
        this.iView = iView;
    }
    public void detachView(){
        this.iView = null;
    }
}
