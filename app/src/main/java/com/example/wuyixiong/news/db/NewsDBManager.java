package com.example.wuyixiong.news.db;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wuyixiong.news.entity.NewsEntity;

import java.util.ArrayList;

/**
 * Created by WUYIXIONG on 2016-10-26.
 */

public class NewsDBManager {

    private int nid;//新闻id
    private String stamp;//时间
    private String icon;//图标路径
    private String title;//标题
    private String link;//链接

    SQLiteDatabase db;

    public NewsDBManager() {
        db = SQLiteDatabase.openOrCreateDatabase("data/data/com.example.wuyixiong.news/database/news.db", null);
    }

    /**
     * 将新闻数据缓存入数据库
     * @param news 新闻数据的集合
     */
    public void insertNews(ArrayList<NewsEntity> news,int subid) {
        String sql = "insert into news (nid,stamp,icon,title,link,subid)values(?,?,?,?,?,?)";
        ArrayList<NewsEntity> news_db = selectNews(subid);
        if (news_db==null){
            for (NewsEntity message : news) {
                nid = message.getNid();
                stamp = message.getStamp();
                icon = message.getIcon();
                title = message.getTitle();
                link = message.getLink();
                db.execSQL(sql, new Object[]{nid, stamp, icon, title, link,subid});
            }
        }else{
            boolean isAdd=false;//是否要加入数据库 true 加入 false 不加入
            //存放数据库中数据的nid
            ArrayList<Integer> list = new ArrayList<>();
            for (int i=0;i<news_db.size();i++){
                list.add(news_db.get(i).getNid());
            }
            //判断与数据库的数据是否重复
            for (NewsEntity message : news) {
                nid = message.getNid();
                for (int i=0;i<list.size();i++){
                    if (nid == list.get(i)){
                        break;
                    }
                }
                if (isAdd){
                    stamp = message.getStamp();
                    icon = message.getIcon();
                    title = message.getTitle();
                    link = message.getLink();
                    db.execSQL(sql, new Object[]{nid, stamp, icon, title, link,subid});
                    isAdd = false;
                }

            }
        }

    }

    /**
     * 从数据库中读取新闻信息
     * @return 如果数据库中没有返回null 如果有数据返回新闻集合
     */
    public ArrayList<NewsEntity> selectNews(int subid){
        ArrayList<NewsEntity> news = new ArrayList<>();
        String sql = "select * from news where subid = ?";
        Cursor c = db.rawQuery(sql,new String[]{subid+""});
        if (c != null && c.getCount()>0){
            int index_nid = c.getColumnIndexOrThrow("nid");
            int index_stamp = c.getColumnIndexOrThrow("stamp");
            int index_icon = c.getColumnIndexOrThrow("icon");
            int index_title = c.getColumnIndexOrThrow("title");
            int index_link = c.getColumnIndexOrThrow("link");
            while (c.moveToNext()){
                NewsEntity message = new NewsEntity();
                message.setNid(c.getInt(index_nid));
                message.setStamp(c.getString(index_stamp));
                message.setIcon(c.getString(index_icon));
                message.setTitle(c.getString(index_title));
                message.setLink(c.getString(index_link));
                news.add(message);
            }
            c.close();
            return news;
        }else {
            return null;
        }
    }

    /**
     * 删除数据库中新闻表中的所有数据
     */
    public void clearNews(){
        String sql = "delete from news";
        db.execSQL(sql);
    }
}
