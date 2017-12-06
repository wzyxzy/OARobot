package com.zgty.oarobot.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.zgty.oarobot.bean.DaoMaster;
import com.zgty.oarobot.bean.DaoSession;
import com.zgty.oarobot.bean.StaffDao;
import com.zgty.oarobot.bean.WorkOnOff;
import com.zgty.oarobot.bean.WorkOnOffDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Calendar;
import java.util.List;

/**
 * Created by zy on 2017/11/3.
 * 员工表管理
 */

public class WorkOnOffDaoUtils {
    private WorkOnOffDao workOnOffDao;
    private Context context;


    public WorkOnOffDaoUtils(Context context) {
        this.context = context;
        SQLiteDatabase writableDatabase = DBManager.getInstance(context).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        DaoSession daoSession = daoMaster.newSession();
        workOnOffDao = daoSession.getWorkOnOffDao();
    }


    /**
     * 插入一条记录
     *
     * @param id 员工
     */
    public void insertWork(String id, String name, String id_clerk) {
        WorkOnOff workOnOff = new WorkOnOff();
        workOnOff.setId(id);
        workOnOff.setName(name);
        workOnOff.setId_clerk(id_clerk);
        workOnOffDao.insert(workOnOff);
    }

    /**
     * 清空全表数据
     */
    public void clearAll() {
        QueryBuilder<WorkOnOff> qb = workOnOffDao.queryBuilder();
        List<WorkOnOff> list = qb.list();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setWork_off1("");
            list.get(i).setWork_off2("");
            list.get(i).setWork_off3("");
            list.get(i).setWork_off4("");
            list.get(i).setWork_off5("");
            list.get(i).setWork_off6("");
            list.get(i).setWork_off7("");
            list.get(i).setWork_off8("");
            list.get(i).setWork_off9("");
            list.get(i).setWork_off10("");
            list.get(i).setWork_off11("");
            list.get(i).setWork_off12("");
            list.get(i).setWork_off13("");
            list.get(i).setWork_off14("");
            list.get(i).setWork_off15("");
            list.get(i).setWork_off16("");
            list.get(i).setWork_off17("");
            list.get(i).setWork_off18("");
            list.get(i).setWork_off19("");
            list.get(i).setWork_off20("");
            list.get(i).setWork_off21("");
            list.get(i).setWork_off22("");
            list.get(i).setWork_off23("");
            list.get(i).setWork_off24("");
            list.get(i).setWork_off25("");
            list.get(i).setWork_off26("");
            list.get(i).setWork_off27("");
            list.get(i).setWork_off28("");
            list.get(i).setWork_off29("");
            list.get(i).setWork_off30("");
            list.get(i).setWork_off31("");
            list.get(i).setWork_on1("");
            list.get(i).setWork_on2("");
            list.get(i).setWork_on3("");
            list.get(i).setWork_on4("");
            list.get(i).setWork_on5("");
            list.get(i).setWork_on6("");
            list.get(i).setWork_on7("");
            list.get(i).setWork_on8("");
            list.get(i).setWork_on9("");
            list.get(i).setWork_on10("");
            list.get(i).setWork_on11("");
            list.get(i).setWork_on12("");
            list.get(i).setWork_on13("");
            list.get(i).setWork_on14("");
            list.get(i).setWork_on15("");
            list.get(i).setWork_on16("");
            list.get(i).setWork_on17("");
            list.get(i).setWork_on18("");
            list.get(i).setWork_on19("");
            list.get(i).setWork_on20("");
            list.get(i).setWork_on21("");
            list.get(i).setWork_on22("");
            list.get(i).setWork_on23("");
            list.get(i).setWork_on24("");
            list.get(i).setWork_on25("");
            list.get(i).setWork_on26("");
            list.get(i).setWork_on27("");
            list.get(i).setWork_on28("");
            list.get(i).setWork_on29("");
            list.get(i).setWork_on30("");
            list.get(i).setWork_on31("");
            list.get(i).setMark_all("");
            workOnOffDao.update(list.get(i));
        }

    }

    /**
     * 删除一条记录
     *
     * @param userId 员工
     */
    public void deleteUser(String userId) {
        QueryBuilder<WorkOnOff> qb = workOnOffDao.queryBuilder();
        DeleteQuery<WorkOnOff> bd = qb.where(StaffDao.Properties.Id.eq(userId)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 上班打卡
     *
     * @param userid 打卡员工
     */
    public void updateWorkOn(String userid, String time) {
        QueryBuilder<WorkOnOff> qb = workOnOffDao.queryBuilder();
        WorkOnOff workOnOff = qb.where(StaffDao.Properties.Id.eq(userid)).list().get(0);
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        switch (day) {
            case 1:
                if (TextUtils.isEmpty(workOnOff.getWork_on1()))
                    workOnOff.setWork_on1(time);
                break;
            case 2:
                if (TextUtils.isEmpty(workOnOff.getWork_on2()))
                    workOnOff.setWork_on2(time);
                break;
            case 3:
                if (TextUtils.isEmpty(workOnOff.getWork_on3()))
                    workOnOff.setWork_on3(time);
                break;
            case 4:
                if (TextUtils.isEmpty(workOnOff.getWork_on4()))
                    workOnOff.setWork_on4(time);
                break;
            case 5:
                if (TextUtils.isEmpty(workOnOff.getWork_on5()))
                    workOnOff.setWork_on5(time);
                break;
            case 6:
                if (TextUtils.isEmpty(workOnOff.getWork_on6()))
                    workOnOff.setWork_on6(time);
                break;
            case 7:
                if (TextUtils.isEmpty(workOnOff.getWork_on7()))
                    workOnOff.setWork_on7(time);
                break;
            case 8:
                if (TextUtils.isEmpty(workOnOff.getWork_on8()))
                    workOnOff.setWork_on8(time);
                break;
            case 9:
                if (TextUtils.isEmpty(workOnOff.getWork_on9()))
                    workOnOff.setWork_on9(time);
                break;
            case 10:
                if (TextUtils.isEmpty(workOnOff.getWork_on10()))
                    workOnOff.setWork_on10(time);
                break;
            case 11:
                if (TextUtils.isEmpty(workOnOff.getWork_on11()))
                    workOnOff.setWork_on11(time);
                break;
            case 12:
                if (TextUtils.isEmpty(workOnOff.getWork_on12()))
                    workOnOff.setWork_on12(time);
                break;
            case 13:
                if (TextUtils.isEmpty(workOnOff.getWork_on13()))
                    workOnOff.setWork_on13(time);
                break;
            case 14:
                if (TextUtils.isEmpty(workOnOff.getWork_on14()))
                    workOnOff.setWork_on14(time);
                break;
            case 15:
                if (TextUtils.isEmpty(workOnOff.getWork_on15()))
                    workOnOff.setWork_on15(time);
                break;
            case 16:
                if (TextUtils.isEmpty(workOnOff.getWork_on16()))
                    workOnOff.setWork_on16(time);
                break;
            case 17:
                if (TextUtils.isEmpty(workOnOff.getWork_on17()))
                    workOnOff.setWork_on17(time);
                break;
            case 18:
                if (TextUtils.isEmpty(workOnOff.getWork_on18()))
                    workOnOff.setWork_on18(time);
                break;
            case 19:
                if (TextUtils.isEmpty(workOnOff.getWork_on19()))
                    workOnOff.setWork_on19(time);
                break;
            case 20:
                if (TextUtils.isEmpty(workOnOff.getWork_on20()))
                    workOnOff.setWork_on20(time);
                break;
            case 21:
                if (TextUtils.isEmpty(workOnOff.getWork_on21()))
                    workOnOff.setWork_on21(time);
                break;
            case 22:
                if (TextUtils.isEmpty(workOnOff.getWork_on22()))
                    workOnOff.setWork_on22(time);
                break;
            case 23:
                if (TextUtils.isEmpty(workOnOff.getWork_on23()))
                    workOnOff.setWork_on23(time);
                break;
            case 24:
                if (TextUtils.isEmpty(workOnOff.getWork_on24()))
                    workOnOff.setWork_on24(time);
                break;
            case 25:
                if (TextUtils.isEmpty(workOnOff.getWork_on25()))
                    workOnOff.setWork_on25(time);
                break;
            case 26:
                if (TextUtils.isEmpty(workOnOff.getWork_on26()))
                    workOnOff.setWork_on26(time);
                break;
            case 27:
                if (TextUtils.isEmpty(workOnOff.getWork_on27()))
                    workOnOff.setWork_on27(time);
                break;
            case 28:
                if (TextUtils.isEmpty(workOnOff.getWork_on28()))
                    workOnOff.setWork_on28(time);
                break;
            case 29:
                if (TextUtils.isEmpty(workOnOff.getWork_on29()))
                    workOnOff.setWork_on29(time);
                break;
            case 30:
                if (TextUtils.isEmpty(workOnOff.getWork_on30()))
                    workOnOff.setWork_on30(time);
                break;
            case 31:
                if (TextUtils.isEmpty(workOnOff.getWork_on31()))
                    workOnOff.setWork_on31(time);
                break;


        }
        workOnOffDao.update(workOnOff);
    }

    /**
     * 下班打卡
     *
     * @param userid 打卡员工
     */
    public void updateWorkOff(String userid, String time) {
        QueryBuilder<WorkOnOff> qb = workOnOffDao.queryBuilder();
        WorkOnOff workOnOff = qb.where(StaffDao.Properties.Id.eq(userid)).list().get(0);
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        switch (day) {
            case 1:
                workOnOff.setWork_off1(time);
                break;
            case 2:
                workOnOff.setWork_off2(time);
                break;
            case 3:
                workOnOff.setWork_off3(time);
                break;
            case 4:
                workOnOff.setWork_off4(time);
                break;
            case 5:
                workOnOff.setWork_off5(time);
                break;
            case 6:
                workOnOff.setWork_off6(time);
                break;
            case 7:
                workOnOff.setWork_off7(time);
                break;
            case 8:
                workOnOff.setWork_off8(time);
                break;
            case 9:
                workOnOff.setWork_off9(time);
                break;
            case 10:
                workOnOff.setWork_off10(time);
                break;
            case 11:
                workOnOff.setWork_off11(time);
                break;
            case 12:
                workOnOff.setWork_off12(time);
                break;
            case 13:
                workOnOff.setWork_off13(time);
                break;
            case 14:
                workOnOff.setWork_off14(time);
                break;
            case 15:
                workOnOff.setWork_off15(time);
                break;
            case 16:
                workOnOff.setWork_off16(time);
                break;
            case 17:
                workOnOff.setWork_off17(time);
                break;
            case 18:
                workOnOff.setWork_off18(time);
                break;
            case 19:
                workOnOff.setWork_off19(time);
                break;
            case 20:
                workOnOff.setWork_off20(time);
                break;
            case 21:
                workOnOff.setWork_off21(time);
                break;
            case 22:
                workOnOff.setWork_off22(time);
                break;
            case 23:
                workOnOff.setWork_off23(time);
                break;
            case 24:
                workOnOff.setWork_off24(time);
                break;
            case 25:
                workOnOff.setWork_off25(time);
                break;
            case 26:
                workOnOff.setWork_off26(time);
                break;
            case 27:
                workOnOff.setWork_off27(time);
                break;
            case 28:
                workOnOff.setWork_off28(time);
                break;
            case 29:
                workOnOff.setWork_off29(time);
                break;
            case 30:
                workOnOff.setWork_off30(time);
                break;
            case 31:
                workOnOff.setWork_off31(time);
                break;


        }
        workOnOffDao.update(workOnOff);
    }

    /**
     * 查询用户列表
     */
    public List<WorkOnOff> queryStaffList() {
        QueryBuilder<WorkOnOff> qb = workOnOffDao.queryBuilder();
        return qb.list();
    }


    /**
     * 导出cvs
     *
     * @return 返回一个Cursor来使用
     */
    public Cursor queryAll() {

        return workOnOffDao.queryBuilder().buildCursor().forCurrentThread().query();
    }
}
