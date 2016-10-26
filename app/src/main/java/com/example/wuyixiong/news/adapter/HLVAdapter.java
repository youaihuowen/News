package com.example.wuyixiong.news.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wuyixiong.news.R;
import com.example.wuyixiong.news.base.MyBaseAdapter;
import com.example.wuyixiong.news.entity.NewsType;

import java.util.ArrayList;

/**
 * 横向listview的适配器
 * Created by WUYIXIONG on 2016-10-18.
 */

public class HLVAdapter extends MyBaseAdapter {


    public int selected = 0;

    public HLVAdapter(ArrayList data, Context mContext) {
        super(data, mContext);
    }

    @Override
    protected View getMyView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_horizontail_lv, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_main_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_type.setText(((NewsType) (data.get(position))).getSubgroup());
        if (position == selected) {
            viewHolder.tv_type.setTextColor(Color.rgb(255,0,0));
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_type;
    }
}
