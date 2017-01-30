package ru.ifmo.droid2016.vkdemo.GroupListDataBase;

/**
 * Created by Andrey on 21.12.2016.
 */

public class DBContract {

    public static final String TABLE = "groups22";
    public static final String TEMP_TABLE = "temptable";

    static final String ICON_URL = "icon_url";
    static final String HEADER = "header";
    static final String GROUP_ID = "group_id";
    static final String DB_ID = "id";
    static final String IGNORE = "ignore";

    static final String[] Fields = {
            DB_ID, GROUP_ID, ICON_URL, HEADER, IGNORE
    };


    static final String CREATE_TABLE = "CREATE TABLE " + TABLE + "(" +
            DB_ID + " INTEGER PRIMARY KEY, " +
            GROUP_ID + " INTEGER, " +
            ICON_URL + " TEXT, " +
            HEADER + " TEXT," +
            IGNORE + " INTEGER" +
            ")";

}
