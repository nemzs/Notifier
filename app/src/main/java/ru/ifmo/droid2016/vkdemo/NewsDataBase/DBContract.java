package ru.ifmo.droid2016.vkdemo.NewsDataBase;

/**
 * Created by Andrey on 27.12.2016.
 */

public class DBContract {
    public static final String TABLE = "posts234567";
    public static final String TEMP_TABLE = "temptable";

    static final String ICON_URL = "icon_url";
    static final String HEADER = "header";
    static final String DB_ID = "id";
    static final String TEXT = "text";
    static final String DATE = "date";
    static final String GROUP_ID = "group_id";
    static final String LIKES = "likes";
    static final String REPOSTS = "reposts";

    static final String[] Fields = {
            DB_ID, ICON_URL, HEADER, DATE, TEXT,GROUP_ID,LIKES,REPOSTS
    };


    static final String CREATE_TABLE = "CREATE TABLE " + TABLE + "(" +
            DB_ID + " INTEGER PRIMARY KEY, " +
            HEADER + " TEXT, " +
            ICON_URL + " TEXT, " +
            TEXT + " TEXT, " +
            DATE + " TEXT, " +
            GROUP_ID + " TEXT, " +
            LIKES + " INTEGER, " +
            REPOSTS + " INTEGER " +
            ")";

}
