package com.zgty.oarobot.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zy on 2018/1/9.
 * 访客人脸录入
 */
@Entity
public class Visitor {
    @Id
    private String id;
    private String visit_id;
    private String time;
    private String infos;
    @Generated(hash = 503988036)
    public Visitor(String id, String visit_id, String time, String infos) {
        this.id = id;
        this.visit_id = visit_id;
        this.time = time;
        this.infos = infos;
    }
    @Generated(hash = 382853925)
    public Visitor() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getVisit_id() {
        return this.visit_id;
    }
    public void setVisit_id(String visit_id) {
        this.visit_id = visit_id;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getInfos() {
        return this.infos;
    }
    public void setInfos(String infos) {
        this.infos = infos;
    }
    
}
