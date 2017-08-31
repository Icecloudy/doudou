package com.doudou.passenger.ui.profile.message;

/**
 * Created by Administrator on 2017/4/18.
 *  id(int) 消息id,
 title(string) 消息标题,
 content(string) 内容,
 propellingtime(string) 发送时间
 */

public class NewsBean {
    private String title;
    private String content;

    private String propellingtime;
    private int id;



    public NewsBean(){

    }

    public String getPropellingtime() {
        return propellingtime;
    }

    public void setPropellingtime(String propellingtime) {
        this.propellingtime = propellingtime;
    }

    public NewsBean(String title, String content, String propellingtime) {
        this.title = title;
        this.content = content;
        this.propellingtime = propellingtime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
