package com.zgty.oarobot.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zgty.oarobot.bean.Account;
import com.zgty.oarobot.bean.AccountDao;
import com.zgty.oarobot.bean.DaoMaster;
import com.zgty.oarobot.bean.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by zy on 2017/11/3.
 * 管理员账户表管理
 */

public class AccountDaoUtils {
    private AccountDao accountDao;


    public AccountDaoUtils(Context context) {
        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        accountDao = daoSession.getAccountDao();
    }


    /**
     * 插入用户
     *
     * @param account 账户
     */
    public void insertAccountList(Account account) {
        if (account == null) {
            return;
        }

        accountDao.insert(account);
    }

    /**
     * 更新账户
     *
     * @param account 用户账户
     */
    public void updateAccount(Account account) {
        if (account == null) {
            return;
        }
        accountDao.update(account);
    }


    /**
     * 查询用户密码
     */
    public String queryAccountPass(String account) {

        QueryBuilder<Account> qb = accountDao.queryBuilder();
        qb.where(AccountDao.Properties.Account.eq(account));
        return qb.list().get(0).getPassword();
    }

    /**
     * 查询时间列表
     */
    public int queryAccountSize() {
        QueryBuilder<Account> qb = accountDao.queryBuilder();
        return qb.list().size();
    }


}
