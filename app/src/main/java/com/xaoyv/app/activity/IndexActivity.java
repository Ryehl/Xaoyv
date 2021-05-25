package com.xaoyv.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xaoyv.app.R;
import com.xaoyv.app.adapter.IndexViewPagerAdapter;
import com.xaoyv.app.base.BaseActivity;
import com.xaoyv.app.fragment.ContactFragment;
import com.xaoyv.app.fragment.HomeFragment;
import com.xaoyv.app.fragment.MessageFragment;

import java.util.ArrayList;

public class IndexActivity extends BaseActivity {

    private ViewPager viewpager;
    private TabLayout tabLayout;

    public static void start(Context context, boolean closePage) {
        Intent intent = new Intent(context, IndexActivity.class);
        context.startActivity(intent);
        if (closePage) ((Activity) context).finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_index;
    }

    @Override
    public void initView(View rootView) {
        viewpager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tab_buttom);
    }

    @Override
    public void initData() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(HomeFragment.getInstance());
        fragments.add(MessageFragment.getInstance());
        fragments.add(ContactFragment.getInstance());
        viewpager.setAdapter(new IndexViewPagerAdapter(getSupportFragmentManager(), fragments));
        viewpager.setOffscreenPageLimit(fragments.size());
        viewpager.setNestedScrollingEnabled(false);

        tabLayout.setupWithViewPager(viewpager);
        tabLayout.addTab(tabLayout.newTab().setText("首页").setIcon(R.drawable.icon_home));
        tabLayout.addTab(tabLayout.newTab().setText("信息").setIcon(R.drawable.icon_home));
        tabLayout.addTab(tabLayout.newTab().setText("联系人").setIcon(R.drawable.icon_home));
    }
}
