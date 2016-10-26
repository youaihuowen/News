package com.example.wuyixiong.news.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;


/**
 * Created by WUYIXIONG on 2016-10-18.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    protected ArrayList<T> data = new ArrayList<T>();
    protected Context mContext;
    protected LayoutInflater inflater;

    public MyBaseAdapter(ArrayList<T> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (data==null)
            return 0;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        if (data!=null && data.size()==0)
            return null;
        if (position > data.size())
            return null;
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getMyView(position,convertView,parent);
    }

    protected abstract View getMyView(int position, View convertView, ViewGroup parent);
}
