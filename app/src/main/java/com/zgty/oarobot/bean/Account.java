package com.zgty.oarobot.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zy on 2017/11/16.
 * 管理员账户
 */
@Entity
public class Account {
    @Id
    private String account;
    private String password;
    @Generated(hash = 1326993273)
    public Account(String account, String password) {
        this.account = account;
        this.password = password;
    }
    @Generated(hash = 882125521)
    public Account() {
    }
    public String getAccount() {
        return this.account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
