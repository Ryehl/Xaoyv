package com.xaoyv.mylibrary.interfaces;

/**
 * <p>项目名称:维度商城</p>
 * <p>简述:IView</p>
 *
 * @author Xaoyv
 * date 2020/10/14 16:56
 */
public interface IView<P> {

    void initView();

    void initData();

    int getLayout();

    P initPresenter();
}
