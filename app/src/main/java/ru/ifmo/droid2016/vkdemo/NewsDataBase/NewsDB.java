package ru.ifmo.droid2016.vkdemo.NewsDataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ifmo.droid2016.vkdemo.model.Post;

/**
 * Created by Andrey on 28.12.2016.
 */

public class NewsDB {


    private final Context context;
    private final int version;

    private final String LOG_TAG = "GroupListDB";

    public NewsDB(Context context, int version) {
        this.context = context;
        this.version = version;
    }

    public List<Post> get() throws FileNotFoundException {

        NewsDBHelper helper = NewsDBHelper.getInstance(context, version);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Post> list = new ArrayList<>();
        try {
            cursor = db.query(DBContract.TABLE, DBContract.Fields, null, null, null, null, "date DESC");
            if (cursor != null && cursor.moveToFirst()) {
                for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                    Post entry = new Post(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getInt(6),
                            cursor.getInt(7)
                    );
                    Log.d("My",entry.date);
                    list.add(entry);
                }
            } else throw new FileNotFoundException();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (list.isEmpty()) {
            throw new FileNotFoundException("DB is empty, couldn't return null");
        } else {
            return list;
        }

    }
    public void deleteGroup(int id) {
        NewsDBHelper helper = NewsDBHelper.getInstance(context, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement statement = null;
        db.beginTransaction();

        try{
            statement = db.compileStatement("DELETE FROM " + DBContract.TABLE+" WHERE group_id = "+Integer.toString(id));
            statement.executeUpdateDelete();
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
            if(statement != null){
                statement.close();
            }
        }

    }
    public void delete() {
        NewsDBHelper helper = NewsDBHelper.getInstance(context, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement statement = null;
        db.beginTransaction();

        try{
            statement = db.compileStatement("DELETE FROM " + DBContract.TABLE);
            statement.executeUpdateDelete();
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
            if(statement != null){
                statement.close();
            }
        }

    }
    public void createTable(){

        NewsDBHelper helper = NewsDBHelper.getInstance(context, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement statement = null;
        db.beginTransaction();
        try {
            statement = db.compileStatement("CREATE TABLE IF NOT EXISTS " + DBContract.TABLE + " (" +
                    "id " + "INTEGER PRIMARY KEY, " +
                    "icon_url " + "TEXT, " +
                    "header " + "TEXT, " +
                    "date " + "TEXT, " +
                    "text " + "TEXT, " +
                    "group_id " + "TEXT, " +
                    "likes " + " INTEGER, " +
                    "reposts " + " INTEGER " +
                    ")");
            statement.execute();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void put(List<Post> list,String group_id) {
        NewsDBHelper helper = NewsDBHelper.getInstance(context, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement statement = null;
       /* DELETE
        FROM _albums_info
        WHERE id NOT IN (
                SELECT id
        FROM _albums_info
        GROUP BY title||bid )*/
        db.beginTransaction();
        statement = db.compileStatement("DELETE FROM " + DBContract.TABLE+" WHERE group_id = '"+group_id+"'");
        statement.executeUpdateDelete();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.beginTransaction();
        long minLikes = 0;
        Collections.sort(list,Post.COMPARE_BY_LIKES);
        try {
            StringBuilder args = new StringBuilder();
            for (int i = 0; i < DBContract.Fields.length; i++) {
                args.append(DBContract.Fields[i]);
                if (i != DBContract.Fields.length - 1) {
                    args.append(", ");
                }
            }
            statement = db.compileStatement("INSERT INTO " + DBContract.TABLE + " (" +
                    args.toString() + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            long added = 0;
            for (Post entry : list) {
                if(added>10||entry.text.length()<15) break;
                try {
                    statement.bindLong(1, entry.id);
                    statement.bindString(2, entry.icon_url);
                    statement.bindString(3, entry.header);
                    statement.bindString(4, entry.date);
                    statement.bindString(5, entry.text);
                    statement.bindString(6, entry.group_id);
                    statement.bindLong(7, entry.likes);
                    statement.bindLong(8, entry.reposts);
                    statement.executeInsert();
                    added++;
                } catch (Error e){
                    Log.d("My","Failed bind long");
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            if (statement != null) {
                statement.close();
            }
        }
    }


}
