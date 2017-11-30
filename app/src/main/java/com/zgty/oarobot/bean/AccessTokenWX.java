package com.zgty.oarobot.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zy on 2017/11/28.
 * 发送微信需要先获取token
 */
@Entity
public class AccessTokenWX {
    @Id
    private String corpid;
    private String token;
    private long time;
    @Generated(hash = 1329278808)
    public AccessTokenWX(String corpid, String token, long time) {
        this.corpid = corpid;
        this.token = token;
        this.time = time;
    }
    @Generated(hash = 431977255)
    public AccessTokenWX() {
    }
    public String getCorpid() {
        return this.corpid;
    }
    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }
    public String getToken() {
        return this.token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
}
