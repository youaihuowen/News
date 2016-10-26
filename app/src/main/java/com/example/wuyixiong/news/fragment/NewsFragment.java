package com.example.wuyixiong.news.fragment;


import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wuyixiong.news.R;
import com.example.wuyixiong.news.activity.MainActivity;
import com.example.wuyixiong.news.adapter.HLVAdapter;
import com.example.wuyixiong.news.adapter.NewsAdapter;
import com.example.wuyixiong.news.db.NewsDBManager;
import com.example.wuyixiong.news.entity.NewsEntity;
import com.example.wuyixiong.news.entity.NewsType;
import com.example.wuyixiong.news.util.HttpUtil;
import com.example.wuyixiong.news.view.HorizontalListView;
import com.example.wuyixiong.news.xlistview.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements AdapterView.OnItemClickListener {


    private HorizontalListView hlv;//页面上边类别
    private HLVAdapter adapter;
    private ArrayList<NewsType> list = new ArrayList<>();

    private XListView xlv;
    private NewsAdapter newsAdapter;
    private ArrayList<NewsEntity> data = new ArrayList<>();

    private MainActivity main;
    public NewsFragment() {
        // Required empty public constructor
    }

    public static final int REFRESH = 1;
    public static final int MORE = 2;

//    NewsDBManager dbManager = new NewsDBManager();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        hlv = (HorizontalListView) view.findViewById(R.id.hlv_main);
        xlv = (XListView) view.findViewById(R.id.xlv_frag_news);
        main = (MainActivity) getActivity();

        //初始化新闻类型的listview
        initType();
        //初始化新闻
        initNews(2);

        setListener();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hlv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.selected = position;
        adapter.notifyDataSetChanged();
        initNews(list.get(position).getSubid());
    }

    //设置监听
    private void setListener() {
        hlv.setOnItemClickListener(this);
        xlv.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                initNews(list.get(adapter.selected).getSubid());
                xlv.stopRefresh();
            }

            @Override
            public void onLoadMore() {
                loadMoreNews(list.get(adapter.selected).getSubid(), data.get(data.size() - 1).getNid());
                xlv.stopLoadMore();
            }
        });
    }

    /**
     * 使用volley获取新闻类型数据
     */
    private void initType() {
        final SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                "data/data/com.example.wuyixiong.news/database/news.db",
                null);
        String sql = "select * from type";
        final String sql_insert = "INSERT INTO type (subgroup,subid) VALUES (?,?)";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            if (MainActivity.isNetworkAvailable(getContext())) {
                String url = HttpUtil.URL + "news_sort?ver=1&imei=111111111111111";
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                StringRequest request = new StringRequest(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                //解析数据后得到一个集合
                                list = analysisType(s);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        db.execSQL(sql_insert,
                                                new Object[]{list.get(0).getSubgroup(), list.get(0).getSubid()});
                                        db.execSQL(sql_insert,
                                                new Object[]{list.get(1).getSubgroup(), list.get(1).getSubid()});
                                    }
                                }).start();
                                adapter = new HLVAdapter(list, getContext());
                                hlv.setAdapter(adapter);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.i("tag", "网络连接出错");
                            }
                        });
                queue.add(request);
            } else {
                Toast.makeText(getContext(), "没有网络", Toast.LENGTH_SHORT).show();
            }

        } else {
            cursor.moveToFirst();
            int index_type = cursor.getColumnIndexOrThrow("subgroup");
            int index_id = cursor.getColumnIndexOrThrow("subid");
            do {
                NewsType type = new NewsType();
                type.setSubgroup(cursor.getString(index_type));
                type.setSubid(cursor.getInt(index_id));
                list.add(type);
            } while (cursor.moveToNext());
            adapter = new HLVAdapter(list, getContext());
            hlv.setAdapter(adapter);
        }

    }

    /**
     * 解析新闻类型数据(第一次 返回两个)
     */
    private ArrayList<NewsType> analysisType(String s) {
        ArrayList<NewsType> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(s);
            if ("OK".equals(object.getString("message"))) {

                JSONArray array = object.getJSONArray("data");
                JSONObject object1 = array.getJSONObject(0);
                JSONArray array1 = object1.getJSONArray("subgrp");

                for (int j = 0; j < array1.length(); j++) {
                    JSONObject object2 = array1.getJSONObject(j);
                    String subgroup = object2.getString("subgroup");
                    int subid = object2.getInt("subid");
                    NewsType type = new NewsType();
                    type.setSubgroup(subgroup);
                    type.setSubid(subid);
                    list.add(type);
                }
            } else {
                Log.i("tag", object.getString("message") + "");
            }
            Log.i("tag", list.size() + "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 初始化新闻
     * @param subid
     */
    private void initNews(final int subid) {
        //显示加载的dialog
        main.showLoadingDialog(null,true);
        if (MainActivity.isNetworkAvailable(getContext())) {
            //获取当前时间
            SimpleDateFormat sp = new SimpleDateFormat("yyyyMMdd");
            String time = sp.format(new Date());

            String url = HttpUtil.URL + "news_list?ver=1&subid=" + subid + "&dir=1&nid=1&stamp=" + time + "&cnt=20";

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest request = new StringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            data.clear();
                            data = analysisNews(s);
                            newsAdapter = new NewsAdapter(data, getContext(),xlv);
                            xlv.setAdapter(newsAdapter);
                            xlv.setPullRefreshEnable(true);
                            xlv.setPullLoadEnable(true);
                            xlv.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                            //取消加载的dialog
                            main.cancelDialog();

//                            //将新闻数据缓存到数据库
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    dbManager.insertNews(data,subid);
//                                }
//                            }).start();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.i("tag", "网络连接出错");
                        }
                    });
            queue.add(request);
        } else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    data = dbManager.selectNews(subid);
//                    newsAdapter = new NewsAdapter(data, getContext());
//                    xlv.setAdapter(newsAdapter);
//                    xlv.setPullRefreshEnable(true);
//                    xlv.setPullLoadEnable(true);
//                    xlv.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
//                    //取消加载的dialog
//                    main.cancelDialog();
//                }
//            }).start();
            Toast.makeText(getContext(), "没有网络", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 上拉加载更多数据
     *
     * @param subid 类型的id
     * @param nid   xlistview显示的最后一条数据的id
     */
    private void loadMoreNews(int subid, int nid) {
        if (MainActivity.isNetworkAvailable(getContext())) {
            //获取当前时间
            SimpleDateFormat sp = new SimpleDateFormat("yyyyMMdd");
            String time = sp.format(new Date());

            String url = HttpUtil.URL + "news_list?ver=1&subid=" + subid + "&dir=2&nid=" + nid + "&stamp=" + time + "&cnt=20";

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest request = new StringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            data.addAll(analysisNews(s));
                            newsAdapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.i("tag", "网络连接出错");
                        }
                    });
            queue.add(request);
        } else {
            Toast.makeText(getContext(), "没有网络", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 解析新闻
     * @param s
     * @return
     */
    private ArrayList<NewsEntity> analysisNews(String s) {
        ArrayList<NewsEntity> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(s);
            String message = object.getString("message");
            int status = object.getInt("status");
            if ("OK".equals(message) && status == 0) {
                JSONArray array = object.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = array.getJSONObject(i);
                    String stamp = object1.getString("stamp");
                    String icon = object1.getString("icon");
                    String title = object1.getString("title");
                    int nid = object1.getInt("nid");
                    String link = object1.getString("link");
                    int type = object1.getInt("type");
                    NewsEntity news = new NewsEntity(type, nid, stamp, icon, title, null, link);
                    list.add(news);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }




}
