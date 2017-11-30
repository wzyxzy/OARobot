package com.zgty.oarobot.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zgty.oarobot.bean.AccessTokenWX;
import com.zgty.oarobot.bean.AccessTokenWXDao;
import com.zgty.oarobot.bean.DaoMaster;
import com.zgty.oarobot.bean.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by zy on 2017/11/3.
 * 微信token管理
 */

public class TokenDaoUtils {
    private AccessTokenWXDao accessTokenWXDao;


    public TokenDaoUtils(Context context) {
        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        accessTokenWXDao = daoSession.getAccessTokenWXDao();
    }


    /**
     * 插入token
     *
     * @param accessTokenWX tokrn
     */
    public void insertAccessToken(AccessTokenWX accessTokenWX) {
        if (accessTokenWX == null) {
            return;
        }

        accessTokenWXDao.insert(accessTokenWX);
    }

    /**
     * 更新token
     *
     * @param accessTokenWX 新token
     */
    public void updateAccessToken(AccessTokenWX accessTokenWX) {
        if (accessTokenWX == null) {
            return;
        }
        accessTokenWXDao.update(accessTokenWX);
    }


    /**
     * 查询token
     */
    public AccessTokenWX queryAccessToken(String corpid) {

        QueryBuilder<AccessTokenWX> qb = accessTokenWXDao.queryBuilder();
        qb.where(AccessTokenWXDao.Properties.Corpid.eq(corpid));
        if (qb.list() != null && qb.list().size() > 0) {
            return qb.list().get(0);
        } else {
            return null;
        }
    }

}
