package com.example.wuyixiong.news.base;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;


/**
 * Created by WUYIXIONG on 2016-10-18.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    public ArrayList<T> data = new ArrayList<T>();
    public Context mContext;
    public LayoutInflater inflater;

    public MyBaseAdapter(ArrayList<T> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public MyBaseAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    /**
     * 设置数据
     * @param list
     */
    public void setData(ArrayList<T> list){
        this.data = list;
    }

    /**
     * 获取数据
     * @return
     */
    public ArrayList<T> getData(){
        return data;
    }


    /**
     * 增加单条数据
     * @param t
     */
    public void addNewData(T t){
        data.add(t);
    }

    /**
     * 添加多条数据
     * @param list
     */
    public void addNewList(ArrayList<T> list){
        data.addAll(list);
    }

    /**
     * 刷新适配器
     */
    public void updateAdapter(){
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (data==null)
            return 0;
        Log.i("tag", data.size()+"");
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
