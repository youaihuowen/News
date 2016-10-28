package com.example.wuyixiong.news.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.wuyixiong.news.R;
import com.example.wuyixiong.news.activity.DetailsActivity;
import com.example.wuyixiong.news.adapter.NewsAdapter;
import com.example.wuyixiong.news.db.NewsDBManager;
import com.example.wuyixiong.news.entity.NewsEntity;
import com.example.wuyixiong.news.xlistview.XListView;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {


    private XListView xlv_favorite;
    private NewsAdapter newsAdapter;
    private NewsDBManager dbManager;
    private ArrayList<NewsEntity> data = new ArrayList<>();

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        xlv_favorite = (XListView) view.findViewById(R.id.xlv_favorite);

        dbManager = new NewsDBManager(getContext());
        data = dbManager.selectCollection();

        newsAdapter = new NewsAdapter(getContext());
        newsAdapter.setData(data);
        xlv_favorite.setAdapter(newsAdapter);
        xlv_favorite.setPullRefreshEnable(true);
        xlv_favorite.setPullLoadEnable(true);

        setListener();
        return view;
    }

    private void setListener() {
        xlv_favorite.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                xlv_favorite.stopRefresh();
            }

            @Override
            public void onLoadMore() {
                xlv_favorite.stopLoadMore();
            }
        });
        xlv_favorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("tag", "onItemClick: "+position);
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("message", data.get(position - 1));
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data1) {
        if (requestCode == 1) {
            data = dbManager.selectCollection();
            newsAdapter.setData(data);
            newsAdapter.notifyDataSetChanged();
        }
    }
}
