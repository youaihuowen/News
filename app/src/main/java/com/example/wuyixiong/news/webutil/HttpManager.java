package com.example.wuyixiong.news.webutil;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by WUYIXIONG on 2016-10-26.
 */

public class HttpManager {
    private Context mContext;
    private RequestQueue queue;

    public HttpManager(Context context){
        mContext=context;
        queue = Volley.newRequestQueue(mContext);
    }


    /**
     * 从网络获取新闻的图片
     * @param url 网址
     * @param listener 成功的监听
     * @param errorListener 失败的监听
     */
    public void initIcon(String url, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener){
        ImageRequest request = new ImageRequest(url,listener,0,0, Bitmap.Config.RGB_565,errorListener);
        queue.add(request);
    }
}
