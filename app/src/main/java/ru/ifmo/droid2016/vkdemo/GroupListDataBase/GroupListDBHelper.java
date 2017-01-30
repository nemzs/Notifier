package ru.ifmo.droid2016.vkdemo.GroupListDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Andrey on 27.12.2016.
 */

public class GroupListDBHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG = "DBHelper";
    private static final String DB_FILE_NAME = "groups22.db";
    private static volatile GroupListDBHelper instance;
    private int version;

    private GroupListDBHelper(Context context, int version){
        super(context, DB_FILE_NAME, null, version);
        this.version = version;
    }

    public static GroupListDBHelper getInstance(Context context, int version){
        if(instance == null){
            synchronized (GroupListDBHelper.class){
                if(instance == null){
                    instance = new GroupListDBHelper(context, version);
                }
            }
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "on create");
        sqLiteDatabase.execSQL(DBContract.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
