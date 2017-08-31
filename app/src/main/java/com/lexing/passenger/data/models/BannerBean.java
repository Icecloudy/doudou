package com.lexing.passenger.data.models;

/**
 * Created by Administrator on 2017/5/12.
 *  "id": 2,
 "picture": "http://lexing.51edn.com/Public/Uploads/20170512/5915aca548486.jpg",
 "url": "http://",
 "text": "12"
 */

public class BannerBean {
    private int id;
    private String picture;
    private String url;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
