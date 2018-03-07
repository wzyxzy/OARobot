package com.zgty.oarobot.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zgty.oarobot.bean.DaoMaster;
import com.zgty.oarobot.bean.DaoSession;
import com.zgty.oarobot.bean.IdCard;
import com.zgty.oarobot.bean.IdCardDao;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.bean.StaffDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by zy on 2017/11/3.
 * 员工表管理
 */

public class IdCardDaoUtils {
    private IdCardDao idCardDao;
    private Context context;


    public IdCardDaoUtils(Context context) {
        this.context = context;
        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        idCardDao = daoSession.getIdCardDao();
    }


    /**
     * 插入一条记录
     *
     * @param idCard 身份证
     */
    public void insertIdCard(IdCard idCard) {


        idCardDao.insert(idCard);
    }


    /**
     * 删除一条记录
     *
     * @param idCard 身份证记录
     */
    public void deleteIdCard(IdCard idCard) {

        idCardDao.delete(idCard);
    }

    public void deleteIdCard(String id) {
        QueryBuilder<IdCard> qb = idCardDao.queryBuilder();
        DeleteQuery<IdCard> bd = qb.where(IdCardDao.Properties.Id.eq(id)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }


    /**
     * 更新一条记录
     *
     * @param idCard 身份证
     */
    public void updateIdCard(IdCard idCard) {
        idCardDao.update(idCard);
    }

    /**
     * 查询用户列表
     */
    public List<IdCard> queryIdCardList() {
        QueryBuilder<IdCard> qb = idCardDao.queryBuilder();
        return qb.list();
    }

    /**
     * 查询用户列表
     */
    public List<IdCard> queryStaffList(String id) {
        QueryBuilder<IdCard> qb = idCardDao.queryBuilder();
        qb.where(IdCardDao.Properties.Id.eq(id));
        return qb.list();
    }


}
