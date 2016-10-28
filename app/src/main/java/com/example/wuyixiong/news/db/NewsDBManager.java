package com.example.wuyixiong.news.db;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wuyixiong.news.entity.NewsEntity;
import com.example.wuyixiong.news.entity.NewsType;

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

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public NewsDBManager(Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 将新闻数据缓存入数据库
     *
     * @param news 新闻数据的集合
     */
    public void insertNews(ArrayList<NewsEntity> news, int subid) {
        String sql = "insert into news (nid,stamp,icon,title,link,subid)values(?,?,?,?,?,?)";
        ArrayList<NewsEntity> news_db = selectNews(subid);
        if (news_db == null) {
            for (NewsEntity message : news) {
                nid = message.getNid();
                stamp = message.getStamp();
                icon = message.getIcon();
                title = message.getTitle();
                link = message.getLink();
                db.execSQL(sql, new Object[]{nid, stamp, icon, title, link, subid});
            }
        } else {
            boolean isAdd = true;//是否要加入数据库 true 加入 false 不加入
            //存放数据库中数据的nid
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < news_db.size(); i++) {
                list.add(news_db.get(i).getNid());
            }
            //判断与数据库的数据是否重复
            for (NewsEntity message : news) {
                nid = message.getNid();
                for (int i = 0; i < list.size(); i++) {
                    if (nid == list.get(i)) {
                        isAdd = false;
                        break;
                    }
                }
                if (isAdd) {
                    stamp = message.getStamp();
                    icon = message.getIcon();
                    title = message.getTitle();
                    link = message.getLink();
                    db.execSQL(sql, new Object[]{nid, stamp, icon, title, link, subid});
                    isAdd = true;
                }

            }
        }

    }

    /**
     * 从数据库中读取新闻信息
     *
     * @return 如果数据库中没有返回null 如果有数据返回新闻集合
     */
    public ArrayList<NewsEntity> selectNews(int subid) {
        ArrayList<NewsEntity> news = new ArrayList<>();
        String sql = "select * from news where subid = ?";
        Cursor c = db.rawQuery(sql, new String[]{subid + ""});
        if (c != null && c.getCount() > 0) {
            int index_nid = c.getColumnIndexOrThrow("nid");
            int index_stamp = c.getColumnIndexOrThrow("stamp");
            int index_icon = c.getColumnIndexOrThrow("icon");
            int index_title = c.getColumnIndexOrThrow("title");
            int index_link = c.getColumnIndexOrThrow("link");
            while (c.moveToNext()) {
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
        } else {
            return null;
        }
    }

    /**
     * 删除数据库中新闻表中的所有数据
     */
    public void clearNews() {
        String sql = "delete from news";
        db.execSQL(sql);
    }

    /**
     * 从数据库中查询类型
     *
     * @return
     */
    public ArrayList<NewsType> selectType() {
        String sql = "select * from newsType";
        ArrayList<NewsType> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            return null;
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
            cursor.close();
            return list;
        }
    }

    /**
     * 添加单条新闻类型
     *
     * @param type
     */
    public void insertType(NewsType type) {
        String sql_insert = "INSERT INTO newsType (subgroup,subid) VALUES (?,?)";
        db.execSQL(sql_insert, new Object[]{type.getSubgroup(), type.getSubid()});
    }

    /**
     * 查询数据库中收藏表中的数据的nid
     *
     * @return
     */
    public ArrayList<Integer> selectIsCollection() {
        ArrayList<Integer> list = new ArrayList<>();
        String sql = "select nid from favorite";
        Cursor c = db.rawQuery(sql, null);
        if (c == null && c.getCount() <= 0) {
            return null;
        } else {
            int index = c.getColumnIndexOrThrow("nid");
            while (c.moveToNext()) {
                list.add(c.getInt(index));
            }
            c.close();
            return list;
        }
    }

    /**
     * 将新闻添加到收藏的数据库
     *
     * @param message
     */
    public void insertCollection(NewsEntity message) {
        if (message != null) {
            nid = message.getNid();
            stamp = message.getStamp();
            icon = message.getIcon();
            title = message.getTitle();
            link = message.getLink();
            String sql = "insert into favorite (nid,stamp,icon,title,link,subid)values(?,?,?,?,?,?)";
            db.execSQL(sql, new Object[]{nid, stamp, icon, title, link});
        }
    }

    /**
     * 查询收藏数据库中的新闻
     * @return
     */
    public ArrayList<NewsEntity> selectCollection() {
        ArrayList<NewsEntity> list = new ArrayList<>();
        String sql = "select * from favorite";
        Cursor c = db.rawQuery(sql, null);
        if (c == null || c.getCount() <= 0) {
            return null;
        } else {
            int index_nid = c.getColumnIndexOrThrow("nid");
            int index_stamp = c.getColumnIndexOrThrow("stamp");
            int index_icon = c.getColumnIndexOrThrow("icon");
            int index_title = c.getColumnIndexOrThrow("title");
            int index_link = c.getColumnIndexOrThrow("link");
            while (c.moveToNext()) {
                NewsEntity message = new NewsEntity();
                message.setNid(c.getInt(index_nid));
                message.setStamp(c.getString(index_stamp));
                message.setIcon(c.getString(index_icon));
                message.setTitle(c.getString(index_title));
                message.setLink(c.getString(index_link));
                list.add(message);
            }
            c.close();
            return list;
        }
    }

    /**
     * 取消收藏
     *
     * @param message
     */
    public void deleteCollection(NewsEntity message) {
        nid = message.getNid();
        String sql = "delete from favorite where nid = ?";
        db.execSQL(sql, new Object[]{nid});
    }
}
