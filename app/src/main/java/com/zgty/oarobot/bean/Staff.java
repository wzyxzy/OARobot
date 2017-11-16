package com.zgty.oarobot.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by zy on 2017/11/2.
 * 员工表
 */
@Entity
public class Staff {
    @Id
    private String id;
    @Property(nameInDb = "NAME")
    private String name_user;
    @Property(nameInDb = "ID_USER")
    private String id_user;
    @Property(nameInDb = "ID_CLERK")
    private String id_clerk;
    @Property(nameInDb = "NAME_PART")
    private String name_part;
    @Property(nameInDb = "NAME_POSITION")
    private String name_position;
    @Property(nameInDb = "CALL_NUM")
    private String call_num;
    @Property(nameInDb = "USER_TYPE")
    private String user_type;
    @Property(nameInDb = "HAS_FACE")
    private Boolean isRecordFace;
    @Generated(hash = 1034561132)
    public Staff(String id, String name_user, String id_user, String id_clerk,
            String name_part, String name_position, String call_num,
            String user_type, Boolean isRecordFace) {
        this.id = id;
        this.name_user = name_user;
        this.id_user = id_user;
        this.id_clerk = id_clerk;
        this.name_part = name_part;
        this.name_position = name_position;
        this.call_num = call_num;
        this.user_type = user_type;
        this.isRecordFace = isRecordFace;
    }
    @Generated(hash = 1774984890)
    public Staff() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName_user() {
        return this.name_user;
    }
    public void setName_user(String name_user) {
        this.name_user = name_user;
    }
    public String getId_user() {
        return this.id_user;
    }
    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
    public String getId_clerk() {
        return this.id_clerk;
    }
    public void setId_clerk(String id_clerk) {
        this.id_clerk = id_clerk;
    }
    public String getName_part() {
        return this.name_part;
    }
    public void setName_part(String name_part) {
        this.name_part = name_part;
    }
    public String getName_position() {
        return this.name_position;
    }
    public void setName_position(String name_position) {
        this.name_position = name_position;
    }
    public String getCall_num() {
        return this.call_num;
    }
    public void setCall_num(String call_num) {
        this.call_num = call_num;
    }
    public String getUser_type() {
        return this.user_type;
    }
    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public Boolean getRecordFace() {
        return isRecordFace;
    }

    public void setRecordFace(Boolean recordFace) {
        isRecordFace = recordFace;
    }
    public Boolean getIsRecordFace() {
        return this.isRecordFace;
    }
    public void setIsRecordFace(Boolean isRecordFace) {
        this.isRecordFace = isRecordFace;
    }
}
