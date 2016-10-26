package com.example.wuyixiong.news.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.wuyixiong.news.R;
import com.example.wuyixiong.news.base.MyBaseAdapter;
import com.example.wuyixiong.news.entity.NewsEntity;
import com.example.wuyixiong.news.util.HttpManager;
import com.example.wuyixiong.news.xlistview.XListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by WUYIXIONG on 2016-10-24.
 */

public class NewsAdapter extends MyBaseAdapter<NewsEntity> {
    HttpManager httpManager;
    private XListView xlv;
    private LruCache<String, Bitmap> cache;
    private File file;

    public NewsAdapter(ArrayList data, Context mContext, XListView xlv) {
        super(data, mContext);
        this.xlv = xlv;
        httpManager = new HttpManager(mContext);
        cache = new LruCache<>(1024 * 1024);
        file = mContext.getCacheDir();
    }

    public Bitmap getBitmap(final String url) {
        final String urlName = url.substring(url.lastIndexOf("/") + 1);
        Bitmap bit = null;

        //从缓存中找图片
        bit = cache.get(urlName);
        if (bit != null) {
            Log.i("tag", "缓存中读取");
            return bit;
        }
        //从文件中找图片
        bit = getBitmapInFile(urlName);
        if (bit != null) {
            Log.i("tag", "文件中读取");
            return bit;
        }
        //从网络获取图片
        httpManager.initIcon(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                ImageView iv = (ImageView) xlv.findViewWithTag(url);
                if (iv != null) {
                    //设置图片
                    iv.setImageBitmap(bitmap);
                    Log.i("tag", "网络中读取");

                    //将图片放入缓存
                    cache.put(urlName, bitmap);
                    //将图片放入文件
                    putBitmapInFile(urlName,bitmap);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        return null;

    }

    private Bitmap getBitmapInFile(String urlName) {
        if (file.exists()) {
            Bitmap b = null;
            File files[] = file.listFiles();
            for (File f : files) {
                if (urlName.equals(f.getName())) {
                    b = BitmapFactory.decodeFile(f.getAbsolutePath());
                    break;
                }
            }
            return b;
        } else {
            return null;
        }
    }

    private void putBitmapInFile(String urlName, Bitmap b) {
        file.mkdir();
        File f = new File(file, urlName);
        try {
            OutputStream output = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.JPEG, 100, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected View getMyView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        Bitmap bit = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_news, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_title.setText(data.get(position).getTitle());
        viewHolder.tv_stamp.setText(data.get(position).getStamp());

        bit = getBitmap(data.get(position).getIcon());
        if (bit != null) {
            viewHolder.iv_icon.setImageBitmap(bit);
        }
        viewHolder.iv_icon.setTag(data.get(position).getIcon());
        return convertView;
    }

    class ViewHolder {
        TextView tv_title;
        TextView tv_stamp;
        ImageView iv_icon;

        public ViewHolder(View view) {
            tv_title = (TextView) view.findViewById(R.id.tv_news_title);
            tv_stamp = (TextView) view.findViewById(R.id.tv_news_stamp);
            iv_icon = (ImageView) view.findViewById(R.id.iv_news_icon);
        }
    }
}
