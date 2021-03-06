package com.zgty.oarobot.bean;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ACCESS_TOKEN_WX".
*/
public class AccessTokenWXDao extends AbstractDao<AccessTokenWX, String> {

    public static final String TABLENAME = "ACCESS_TOKEN_WX";

    /**
     * Properties of entity AccessTokenWX.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Corpid = new Property(0, String.class, "corpid", true, "CORPID");
        public final static Property Token = new Property(1, String.class, "token", false, "TOKEN");
        public final static Property Time = new Property(2, long.class, "time", false, "TIME");
    }


    public AccessTokenWXDao(DaoConfig config) {
        super(config);
    }
    
    public AccessTokenWXDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACCESS_TOKEN_WX\" (" + //
                "\"CORPID\" TEXT PRIMARY KEY NOT NULL ," + // 0: corpid
                "\"TOKEN\" TEXT," + // 1: token
                "\"TIME\" INTEGER NOT NULL );"); // 2: time
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACCESS_TOKEN_WX\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AccessTokenWX entity) {
        stmt.clearBindings();
 
        String corpid = entity.getCorpid();
        if (corpid != null) {
            stmt.bindString(1, corpid);
        }
 
        String token = entity.getToken();
        if (token != null) {
            stmt.bindString(2, token);
        }
        stmt.bindLong(3, entity.getTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AccessTokenWX entity) {
        stmt.clearBindings();
 
        String corpid = entity.getCorpid();
        if (corpid != null) {
            stmt.bindString(1, corpid);
        }
 
        String token = entity.getToken();
        if (token != null) {
            stmt.bindString(2, token);
        }
        stmt.bindLong(3, entity.getTime());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public AccessTokenWX readEntity(Cursor cursor, int offset) {
        AccessTokenWX entity = new AccessTokenWX( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // corpid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // token
            cursor.getLong(offset + 2) // time
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AccessTokenWX entity, int offset) {
        entity.setCorpid(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setToken(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTime(cursor.getLong(offset + 2));
     }
    
    @Override
    protected final String updateKeyAfterInsert(AccessTokenWX entity, long rowId) {
        return entity.getCorpid();
    }
    
    @Override
    public String getKey(AccessTokenWX entity) {
        if(entity != null) {
            return entity.getCorpid();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AccessTokenWX entity) {
        return entity.getCorpid() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
