package com.zgty.oarobot.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zgty.oarobot.bean.DaoMaster;
import com.zgty.oarobot.bean.DaoSession;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.bean.StaffDao;
import com.zgty.oarobot.bean.Visitor;
import com.zgty.oarobot.bean.VisitorDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by zy on 2017/11/3.
 * 员工表管理
 */

public class VisitorDaoUtils {
    private VisitorDao visitorDao;
    private Context context;


    public VisitorDaoUtils(Context context) {
        this.context = context;
        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        visitorDao = daoSession.getVisitorDao();
    }


    /**
     * 插入一条记录
     *
     * @param visitor 访客
     */
    public void insertVisitor(Visitor visitor) {


        visitorDao.insert(visitor);
    }


    /**
     * 更新一条记录
     *
     * @param visitor 访客
     */
    public void updateVisitor(Visitor visitor) {
        visitorDao.update(visitor);
    }

    /**
     * 查询访客列表
     */
    public List<Visitor> queryVisitorList() {
        QueryBuilder<Visitor> qb = visitorDao.queryBuilder();
        return qb.list();
    }

    /**
     * 查询用户列表
     */
    public List<Visitor> queryVisitorList(String id) {
        QueryBuilder<Visitor> qb = visitorDao.queryBuilder();
        qb.where(VisitorDao.Properties.Id.eq(id));
        return qb.list();
    }

    /**
     * 查询访客编号
     */
    public int findVisitorNum() {
        int num = 0;
        if (queryVisitorList() != null && queryVisitorList().size() > 0) {
            num = queryVisitorList().size();
        }
        return num;
    }


}
