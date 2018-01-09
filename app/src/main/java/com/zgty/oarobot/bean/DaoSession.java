package com.zgty.oarobot.bean;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.zgty.oarobot.bean.AccessTokenWX;
import com.zgty.oarobot.bean.Account;
import com.zgty.oarobot.bean.Speaking;
import com.zgty.oarobot.bean.Staff;
import com.zgty.oarobot.bean.Time;
import com.zgty.oarobot.bean.WorkOnOff;
import com.zgty.oarobot.bean.Visitor;

import com.zgty.oarobot.bean.AccessTokenWXDao;
import com.zgty.oarobot.bean.AccountDao;
import com.zgty.oarobot.bean.SpeakingDao;
import com.zgty.oarobot.bean.StaffDao;
import com.zgty.oarobot.bean.TimeDao;
import com.zgty.oarobot.bean.WorkOnOffDao;
import com.zgty.oarobot.bean.VisitorDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig accessTokenWXDaoConfig;
    private final DaoConfig accountDaoConfig;
    private final DaoConfig speakingDaoConfig;
    private final DaoConfig staffDaoConfig;
    private final DaoConfig timeDaoConfig;
    private final DaoConfig workOnOffDaoConfig;
    private final DaoConfig visitorDaoConfig;

    private final AccessTokenWXDao accessTokenWXDao;
    private final AccountDao accountDao;
    private final SpeakingDao speakingDao;
    private final StaffDao staffDao;
    private final TimeDao timeDao;
    private final WorkOnOffDao workOnOffDao;
    private final VisitorDao visitorDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        accessTokenWXDaoConfig = daoConfigMap.get(AccessTokenWXDao.class).clone();
        accessTokenWXDaoConfig.initIdentityScope(type);

        accountDaoConfig = daoConfigMap.get(AccountDao.class).clone();
        accountDaoConfig.initIdentityScope(type);

        speakingDaoConfig = daoConfigMap.get(SpeakingDao.class).clone();
        speakingDaoConfig.initIdentityScope(type);

        staffDaoConfig = daoConfigMap.get(StaffDao.class).clone();
        staffDaoConfig.initIdentityScope(type);

        timeDaoConfig = daoConfigMap.get(TimeDao.class).clone();
        timeDaoConfig.initIdentityScope(type);

        workOnOffDaoConfig = daoConfigMap.get(WorkOnOffDao.class).clone();
        workOnOffDaoConfig.initIdentityScope(type);

        visitorDaoConfig = daoConfigMap.get(VisitorDao.class).clone();
        visitorDaoConfig.initIdentityScope(type);

        accessTokenWXDao = new AccessTokenWXDao(accessTokenWXDaoConfig, this);
        accountDao = new AccountDao(accountDaoConfig, this);
        speakingDao = new SpeakingDao(speakingDaoConfig, this);
        staffDao = new StaffDao(staffDaoConfig, this);
        timeDao = new TimeDao(timeDaoConfig, this);
        workOnOffDao = new WorkOnOffDao(workOnOffDaoConfig, this);
        visitorDao = new VisitorDao(visitorDaoConfig, this);

        registerDao(AccessTokenWX.class, accessTokenWXDao);
        registerDao(Account.class, accountDao);
        registerDao(Speaking.class, speakingDao);
        registerDao(Staff.class, staffDao);
        registerDao(Time.class, timeDao);
        registerDao(WorkOnOff.class, workOnOffDao);
        registerDao(Visitor.class, visitorDao);
    }
    
    public void clear() {
        accessTokenWXDaoConfig.clearIdentityScope();
        accountDaoConfig.clearIdentityScope();
        speakingDaoConfig.clearIdentityScope();
        staffDaoConfig.clearIdentityScope();
        timeDaoConfig.clearIdentityScope();
        workOnOffDaoConfig.clearIdentityScope();
        visitorDaoConfig.clearIdentityScope();
    }

    public AccessTokenWXDao getAccessTokenWXDao() {
        return accessTokenWXDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public SpeakingDao getSpeakingDao() {
        return speakingDao;
    }

    public StaffDao getStaffDao() {
        return staffDao;
    }

    public TimeDao getTimeDao() {
        return timeDao;
    }

    public WorkOnOffDao getWorkOnOffDao() {
        return workOnOffDao;
    }

    public VisitorDao getVisitorDao() {
        return visitorDao;
    }

}
