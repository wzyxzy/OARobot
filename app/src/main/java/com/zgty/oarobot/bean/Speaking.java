package com.zgty.oarobot.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zy on 2017/11/17.
 * 会话管理bean
 */
@Entity
public class Speaking {
    @Id
    private String id;
    private String name;
    private String text;
    @Generated(hash = 1191508440)
    public Speaking(String id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }
    @Generated(hash = 1161344253)
    public Speaking() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
