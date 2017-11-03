package com.zgty.oarobot.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zgty.oarobot.bean.DaoMaster;
import com.zgty.oarobot.bean.DaoSession;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.bean.StaffDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by zy on 2017/11/3.
 */

public class StaffDaoUtils {
    private Context context;
    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;


    public StaffDaoUtils(Context context) {
        this.context = context;
        writableDatabase = DBManager.getInstance(context).getWritableDatabase();
        readableDatabase = DBManager.getInstance(context).getReadableDatabase();

    }


    /**
     * 插入一条记录
     *
     * @param staff
     */
    public void insertStaff(Staff staff) {

        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        StaffDao staffDao = daoSession.getStaffDao();
        staffDao.insert(staff);
    }

    /**
     * 插入用户集合
     *
     * @param staffs
     */
    public void insertStaffList(List<Staff> staffs) {
        if (staffs == null || staffs.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        StaffDao staffDao = daoSession.getStaffDao();
        staffDao.insertInTx(staffs);
    }

    /**
     * 删除一条记录
     *
     * @param staff
     */
    public void deleteStaff(Staff staff) {
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        StaffDao staffDao = daoSession.getStaffDao();
        staffDao.delete(staff);
    }

    /**
     * 更新一条记录
     *
     * @param staff
     */
    public void updateStaff(Staff staff) {
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        StaffDao staffDao = daoSession.getStaffDao();
        staffDao.update(staff);
    }

    /**
     * 查询用户列表
     */
    public List<Staff> queryStaffList() {
        DaoMaster daoMaster = new DaoMaster(readableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        StaffDao staffDao = daoSession.getStaffDao();
        QueryBuilder<Staff> qb = staffDao.queryBuilder();
        List<Staff> staffList = qb.list();
        return staffList;
    }

    /**
     * 查询用户列表
     */
    public List<Staff> queryStaffList(String id) {
        DaoMaster daoMaster = new DaoMaster(readableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        StaffDao staffDao = daoSession.getStaffDao();
        QueryBuilder<Staff> qb = staffDao.queryBuilder();
        qb.where(StaffDao.Properties.Id.gt(id));
        List<Staff> list = qb.list();
        return list;
    }
}
