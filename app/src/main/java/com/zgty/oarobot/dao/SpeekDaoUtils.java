package com.zgty.oarobot.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zgty.oarobot.bean.DaoMaster;
import com.zgty.oarobot.bean.DaoSession;
import com.zgty.oarobot.bean.Speaking;
import com.zgty.oarobot.bean.SpeakingDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by zy on 2017/11/3.
 * 会话表管理
 */

public class SpeekDaoUtils {
    private SpeakingDao speakingDao;


    public SpeekDaoUtils(Context context) {
        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        speakingDao = daoSession.getSpeakingDao();

    }


    /**
     * 插入会话集合
     *
     * @param speakings 会话集合
     */
    public void insertSpeekList(List<Speaking> speakings) {
        if (speakings == null || speakings.isEmpty()) {
            return;
        }

        speakingDao.insertInTx(speakings);
    }

    /**
     * 更新会话集合
     *
     * @param speakings 会话集合
     */
    public void updateSpeeking(List<Speaking> speakings) {
        if (speakings == null || speakings.isEmpty()) {
            return;
        }
        speakingDao.updateInTx(speakings);
    }

    /**
     * 查询会话列表
     */
    public List<Speaking> querySpeekList() {
        QueryBuilder<Speaking> qb = speakingDao.queryBuilder();
        return qb.list();
    }

    /**
     * 查询会话
     */
    public String querySpeekingText(String id) {
        QueryBuilder<Speaking> qb = speakingDao.queryBuilder();
        qb.where(SpeakingDao.Properties.Id.eq(id));
        return qb.list().get(0).getText();
    }
}
