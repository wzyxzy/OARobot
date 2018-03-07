package com.zgty.oarobot.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zy on 2018/2/1.
 * 身份证数据库
 */
@Entity
public class IdCard {
    @Id
    private String id;
    private String name;
    private String sex;//"M"为男，"W"为女
    private String id_card_num;
    @Generated(hash = 942377643)
    public IdCard(String id, String name, String sex, String id_card_num) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.id_card_num = id_card_num;
    }
    @Generated(hash = 1500073048)
    public IdCard() {
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
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getId_card_num() {
        return this.id_card_num;
    }
    public void setId_card_num(String id_card_num) {
        this.id_card_num = id_card_num;
    }


}
