package com.zgty.oarobot.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zgty.oarobot.bean.DaoMaster;
import com.zgty.oarobot.bean.DaoSession;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.bean.StaffDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by zy on 2017/11/3.
 * 员工表管理
 */

public class StaffDaoUtils {
    private StaffDao staffDao;


    public StaffDaoUtils(Context context) {
        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        staffDao = daoSession.getStaffDao();
    }


    /**
     * 插入一条记录
     *
     * @param staff 员工
     */
    public void insertStaff(Staff staff) {


        staffDao.insert(staff);
    }

    /**
     * 插入用户集合
     *
     * @param staffs 员工集合
     */
    public void insertStaffList(List<Staff> staffs) {
        if (staffs == null || staffs.isEmpty()) {
            return;
        }

        staffDao.insertInTx(staffs);
    }

    /**
     * 删除一条记录
     *
     * @param staff 员工
     */
    public void deleteStaff(Staff staff) {

        staffDao.delete(staff);
    }

    public void deleteUser(String userId) {
        QueryBuilder<Staff> qb = staffDao.queryBuilder();
        DeleteQuery<Staff> bd = qb.where(StaffDao.Properties.Id.eq(userId)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 更新一条记录
     *
     * @param staff 员工
     */
    public void updateStaff(Staff staff) {
        staffDao.update(staff);
    }

    /**
     * 查询用户列表
     */
    public List<Staff> queryStaffList() {
        QueryBuilder<Staff> qb = staffDao.queryBuilder();
        return qb.list();
    }

    /**
     * 查询用户列表
     */
    public List<Staff> queryStaffList(String id) {
        QueryBuilder<Staff> qb = staffDao.queryBuilder();
        qb.where(StaffDao.Properties.Id.eq(id));
        return qb.list();
    }
}
