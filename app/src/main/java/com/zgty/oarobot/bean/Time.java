package com.zgty.oarobot.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by zy on 2017/11/14.
 * 时间表
 */
@Entity
public class Time {
    @Id
    private String id;
    private String name;
    private String time;

    @Generated(hash = 1121533015)
    public Time(String id, String name, String time) {
        this.id = id;
        this.name = name;
        this.time = time;
    }

    @Generated(hash = 37380482)
    public Time() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
