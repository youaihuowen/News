package com.example.wuyixiong.news.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by WUYIXIONG on 2016-10-19.
 */

public class ViewPagerAdapter extends PagerAdapter {

    ArrayList<View> views =new ArrayList<View>();

    public ViewPagerAdapter(ArrayList<View> views) {
        this.views=views;
    }

    @Override
    public int getCount() {

        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
