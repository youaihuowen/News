package com.example.wuyixiong.news.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wuyixiong.news.R;
import com.example.wuyixiong.news.activity.DetailsActivity;
import com.example.wuyixiong.news.activity.MainActivity;
import com.example.wuyixiong.news.adapter.HLVAdapter;
import com.example.wuyixiong.news.adapter.NewsAdapter;
import com.example.wuyixiong.news.db.NewsDBManager;
import com.example.wuyixiong.news.entity.NewsEntity;
import com.example.wuyixiong.news.entity.NewsType;
import com.example.wuyixiong.news.webutil.Analysis;
import com.example.wuyixiong.news.webutil.HttpUtil;
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
public class NewsFragment extends Fragment {


    private HorizontalListView hlv;//页面上边类别
    private HLVAdapter adapter;
    private ArrayList<NewsType> list = new ArrayList<>();

    private XListView xlv;
    private NewsAdapter newsAdapter;
    private ArrayList<NewsEntity> data = new ArrayList<>();

    private MainActivity main;

    private Analysis analysis = new Analysis();

    public NewsFragment() {
        // Required empty public constructor
    }

    public static final int REFRESH = 1;
    public static final int MORE = 2;

    private NewsDBManager dbManager ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        dbManager = new NewsDBManager(getContext());

        hlv = (HorizontalListView) view.findViewById(R.id.hlv_main);
        xlv = (XListView) view.findViewById(R.id.xlv_frag_news);
        main = (MainActivity) getActivity();

        newsAdapter = new NewsAdapter(getActivity());
        xlv.setPullRefreshEnable(true);
        xlv.setPullLoadEnable(true);
        xlv.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

        //初始化新闻类型的listview
        initType();
        //初始化新闻
        initNews(2);
        setListener();
        return view;
    }


    //设置监听
    private void setListener() {
        hlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.selected = position;
                adapter.notifyDataSetChanged();
                initNews(list.get(position).getSubid());
            }
        });
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
        xlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                String link = data.get(position-1).getLink();
                intent.putExtra("link",link);
                startActivity(intent);
            }
        });
    }

    /**
     * 使用volley获取新闻类型数据
     */
    private void initType() {
        list = dbManager.selectType();
        if (list == null || list.size() <= 0) {
            if (MainActivity.isNetworkAvailable(getContext())) {
                String url = HttpUtil.URL + "news_sort?ver=1&imei=111111111111111";
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                StringRequest request = new StringRequest(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                //解析数据后得到一个集合
                                list = analysis.analysisType(s);
                                dbManager.insertType(list.get(0));
                                dbManager.insertType(list.get(1));
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
            adapter = new HLVAdapter(list, getContext());
            hlv.setAdapter(adapter);
        }
    }



    /**
     * 初始化新闻
     *
     * @param subid
     */
    private void initNews(final int subid) {
        //显示加载的dialog
        main.showLoadingDialog(null, true);
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
                            data = analysis.analysisNews(s);
                            newsAdapter.setData(data);
                            Log.i("tag", "AAAAAAAAAAA");
                            xlv.setAdapter(newsAdapter);

                            //取消加载的dialog
                            main.cancelDialog();

                            //将新闻数据缓存到数据库
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    dbManager.insertNews(data,subid);
                                }
                            }).start();
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

                    data = dbManager.selectNews(subid);
                    newsAdapter.setData(data);
                    xlv.setAdapter(newsAdapter);
                    //取消加载的dialog
                    main.cancelDialog();

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
                            newsAdapter.addNewList(analysis.analysisNews(s));
                            newsAdapter.updateAdapter();
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


}
