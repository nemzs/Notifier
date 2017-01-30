package ru.ifmo.droid2016.vkdemo.NewsDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by Andrey on 28.12.2016.
 */

public class NewsDBHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG = "DBHelper";
    private static final String DB_FILE_NAME = "posts234567.db";
    private static volatile NewsDBHelper instance;
    private int version;

    private NewsDBHelper(Context context, int version){
        super(context, DB_FILE_NAME, null, version);
        this.version = version;
    }

    public static NewsDBHelper getInstance(Context context, int version){
        if(instance == null){
            synchronized (NewsDBHelper.class){
                if(instance == null){
                    instance = new NewsDBHelper(context, version);
                }
            }
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "on create");
        sqLiteDatabase.execSQL(ru.ifmo.droid2016.vkdemo.NewsDataBase.DBContract.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
