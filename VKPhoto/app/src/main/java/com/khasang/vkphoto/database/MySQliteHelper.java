package com.khasang.vkphoto.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.khasang.vkphoto.database.tables.CommentsTable;
import com.khasang.vkphoto.database.tables.PhotoAlbumsTable;
import com.khasang.vkphoto.database.tables.PhotosTable;
import com.khasang.vkphoto.database.tables.UsersTable;
import com.khasang.vkphoto.util.Logger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MySQliteHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "vkphoto.db";
    public static final int DB_VERSION = 1;
    private static volatile MySQliteHelper instance;
    private static final Lock lock = new ReentrantLock();
    public static final String CREATE_TABLE = "CREATE TABLE %s ( %s);";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS %s";
    public static final String PRIMARY_KEY = BaseColumns._ID + " integer primary key autoincrement, ";

    private MySQliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static MySQliteHelper getInstance(Context context) {
        MySQliteHelper localHelper = instance;
        if (localHelper == null) { // While we were waiting for the lock, another
            localHelper = instance;        // thread may have instantiated the object.
            lock.lock();
            if (localHelper == null) {
                localHelper = new MySQliteHelper(context.getApplicationContext());
                instance = localHelper;
            }
            lock.unlock();
        }
        return localHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(getCreateSql(PhotoAlbumsTable.TABLE_NAME, PhotoAlbumsTable.FIELDS));
            db.execSQL(getCreateSql(PhotosTable.TABLE_NAME, PhotosTable.FIELDS));
            db.execSQL(getCreateSql(CommentsTable.TABLE_NAME, CommentsTable.FIELDS));
            db.execSQL(getCreateSql(UsersTable.TABLE_NAME, UsersTable.FIELDS));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.d(e.toString());
        } finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(getDropSql(PhotoAlbumsTable.TABLE_NAME));
            db.execSQL(getDropSql(CommentsTable.TABLE_NAME));
            db.execSQL(getDropSql(PhotosTable.TABLE_NAME));
            db.execSQL(getDropSql(UsersTable.TABLE_NAME));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private String getDropSql(String tableName) {
        return String.format(DROP_TABLE, tableName);
    }

    private String getCreateSql(String tableName, String fields) {
        return String.format(MySQliteHelper.CREATE_TABLE, tableName, fields);
    }

//    db.beginTransaction(); все транзакции делать таким образом
//    try {
//        for (DBRow row : insertList) {
//            // your insert code
//            insertRow(row);
//            db.yieldIfContendedSafely();
//        }
//        db.setTransactionSuccessful();
//    } finally {
//        db.endTransaction();
//    }
}
