package ru.ifmo.droid2016.vkdemo.GroupListDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import ru.ifmo.droid2016.vkdemo.model.GroupEntry;


/**
 * Created by Andrey on 27.12.2016.
 */

public class GroupListDB {

    private final Context context;
    private final int version;

    private final String LOG_TAG = "GroupListDB";

    public GroupListDB(Context context, int version) {
        this.context = context;
        this.version = version;
    }
    public List<GroupEntry> get() throws FileNotFoundException {


        GroupListDBHelper helper = GroupListDBHelper.getInstance(context, version);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<GroupEntry> list = new ArrayList<>();

        try {
            cursor = db.query(DBContract.TABLE, DBContract.Fields, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                    GroupEntry entry = new GroupEntry(
                            cursor.getInt(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getInt(4)
                    );
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

    public void setIgnore(int id,int ignore){
        GroupListDBHelper helper = GroupListDBHelper.getInstance(context, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement statement = null;
        ContentValues cv = new ContentValues();
        cv.put("ignore",ignore);
        int updCount = db.update(DBContract.TABLE, cv, "group_id = ?",
                new String[] { Integer.toString(id) });
        Cursor c = db.query(DBContract.TABLE, null, null, null, null, null, null);
        if(c.getCount()!=0){
            String[] columns = null;
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = null;
            Log.d("My",Integer.toString(id));
            selection = "ignore>-1";
            selectionArgs = new String[] { "id" };
            c = db.query(DBContract.TABLE, null, selection, null, null, null,
                    null);
            if (c != null) {
                if (c.moveToFirst()) {
                    String str;
                    do {
                        str = "";
                        for (String cn : c.getColumnNames()) {
                            str = str.concat(cn + " = "
                                    + c.getString(c.getColumnIndex(cn)) + "; ");
                        }
                        //Log.d("My", str);

                    } while (c.moveToNext());
                }
                c.close();
            }
            if(c.getCount()!=0) Log.d("My","NotBad)");
            Log.d("My","sad)");
        }
        Log.d("My", "updated rows count = " + updCount);
        Log.d("My","UPDATE " + DBContract.TABLE+" SET ignore = 1 WHERE group_id = "+Integer.toString(id));

        try {
            List<GroupEntry> g = get();
            for(int i=0;i<g.size();i++){
               // Log.d("My",Integer.toString(g.get(i).ignore));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void put(List<GroupEntry> list) {
        GroupListDBHelper helper = GroupListDBHelper.getInstance(context, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement statement = null;
        db.beginTransaction();
        try {
            StringBuilder args = new StringBuilder();
            for (int i = 0; i < DBContract.Fields.length; i++) {
                args.append(DBContract.Fields[i]);
                if (i != DBContract.Fields.length - 1) {
                    args.append(", ");
                }
            }
            statement = db.compileStatement("INSERT INTO " + DBContract.TABLE + " (" +
                    args.toString() + ") VALUES (?, ?, ?, ?, ?)");

            for (GroupEntry entry : list) {
                statement.bindLong(2, entry.id);
                statement.bindString(3, entry.icon_url);
                statement.bindString(4, entry.header);
                statement.bindLong(5, entry.ignore);
                statement.executeInsert();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void delete(List<GroupEntry> input) {
        GroupListDBHelper helper = GroupListDBHelper.getInstance(context, version);
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

}
