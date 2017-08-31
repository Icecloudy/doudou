package com.doudou.passenger.ui.profile.settings;

/**
 * Created by Administrator on 2017/6/21.
 * {
 * "content": "豆豆",
 * "email": "lexing@lexing.com",
 * "hotline": "13918183255",
 * "id": 5,
 * "sina": "lexing@sina1",
 * "tid": "103",
 * "wechat": "lexing@weixin1"
 * }
 */

public class AboutBean {
    private String content;
    private String email;
    private String hotline;
    private String sina;
    private String wechat;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    public String getSina() {
        return sina;
    }

    public void setSina(String sina) {
        this.sina = sina;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }
}
