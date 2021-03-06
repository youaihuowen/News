package com.example.wuyixiong.news.webutil;

import android.util.Log;

import com.example.wuyixiong.news.entity.CommentEntity;
import com.example.wuyixiong.news.entity.NewsEntity;
import com.example.wuyixiong.news.entity.NewsType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by WUYIXIONG on 2016-10-26.
 */

public class Analysis {

    /**
     * 解析新闻类型数据(第一次 返回两个)
     */
    public ArrayList<NewsType> analysisType(String s) {
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
     * 解析新闻
     *
     * @param s
     * @return
     */
    public ArrayList<NewsEntity> analysisNews(String s) {
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

    /**
     * 解析评论的数量
     *
     * @param s
     * @return
     */
    public int analysisCount(String s) {
        int count = 0;
        try {
            JSONObject object = new JSONObject(s);
            int data = object.getInt("data");
            count = data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 解析评论
     *
     * @param s
     * @return
     */
    public ArrayList<CommentEntity> analysisComment(String s) {
        ArrayList<CommentEntity> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(s);
            String message = object.getString("message");
            int status = object.getInt("status");
            if ("OK".equals(message) && status == 0) {
                JSONArray array = object.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = (JSONObject) array.get(i);
                    CommentEntity comment = new CommentEntity();
                    comment.setUid(object1.getString("uid"));
                    comment.setContent(object1.getString("content"));
                    comment.setStamp(object1.getString("stamp"));
                    comment.setCid(object1.getInt("cid"));
                    comment.setPortrait(object1.getString("portrait"));
                    list.add(comment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 发表评论解析数据判断死否成功
     * @param s
     * @return
     */
    public Boolean analysisSend(String s) {
        String message = null;
        int status = -1;
        try {
            JSONObject object = new JSONObject(s);
            message = object.getString("message");
            status = object.getInt("status");

        } catch (Exception e) {
            e.printStackTrace();
        }
        if ("OK".equals(message) && status == 0) {
            return true;
        } else {
            return false;
        }
    }
}

