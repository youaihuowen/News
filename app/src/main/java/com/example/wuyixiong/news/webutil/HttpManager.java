package com.example.wuyixiong.news.webutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
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
     * 从网络获取图片
     * @param url 网址
     * @param listener 成功的监听
     * @param errorListener 失败的监听
     */
    public void initIcon(String url, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener){
        ImageRequest request = new ImageRequest(url,listener,0,0, Bitmap.Config.RGB_565,errorListener);
        queue.add(request);
    }

    /**
     * 以字符串请求的形式从网络获取数据
     * @param url 网址
     * @param listener 成功的监听
     * @param errorListener 失败的监听
     */
    public void getWebMessage(String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        StringRequest request = new StringRequest(url,listener,errorListener);
        queue.add(request);
    }

    /**
     * 判断是否有网
     * @param context 上下文
     * @return 有网返回true 没网返回false
     */
    public boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()){
            return false;
        }else{
            return true;
        }
    }
}
