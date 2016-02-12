package com.khasang.vkphoto.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.khasang.vkphoto.database.tables.PhotoAlbumsTable;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MySQliteHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "vkphoto.db";
    public static final int DB_VERSION = 1;
    private static volatile MySQliteHelper instance;
    private static final Lock lock = new ReentrantLock();
    public static final String CREATE_TABLE = "CREATE TABLE %s ( %s);";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS %s";
    public static final String PRIMARY_KEY = " integer primary key autoincrement, ";

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
            db.execSQL(PhotoAlbumsTable.CREATE_PHOTOALBUMS_TABLE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(String.format(DROP_TABLE, PhotoAlbumsTable.TABLE_NAME));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
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
