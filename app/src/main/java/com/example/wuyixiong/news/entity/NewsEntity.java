package com.example.wuyixiong.news.entity;

import java.io.Serializable;

/**
 * 新闻的实体类
 * Created by WUYIXIONG on 2016-10-24.
 */

public class NewsEntity implements Serializable {

    private int type;//类型
    private int nid;//新闻id
    private String stamp;//时间
    private String icon;//图标路径
    private String title;//标题
    private String summary;//摘要
    private String link;//链接

    public NewsEntity(int type, int nid, String stamp, String icon, String title, String summary, String link) {
        this.type = type;
        this.nid = nid;
        this.stamp = stamp;
        this.icon = icon;
        this.title = title;
        this.summary = summary;
        this.link = link;
    }

    public NewsEntity() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
