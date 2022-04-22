package com.androidapp.convero.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DATABASE_NAME = "HistoryDB";
    private static final String TABLE_NAME = "historyTable";
    public static String KEY_ID = "id";
    public static String DATE = "date";
    public static String ITEM1 = "item1";
    public static String ITEM2 = "item2";
    public static String ITEM_NAME = "item_name";
    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+ DATE +" VARCHAR(500),"+ ITEM1 +" VARCHAR(500),"+ ITEM2 +" VARCHAR(500), "+ ITEM_NAME +" VARCHAR(500) );";

    private final Context context;
    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            db.execSQL(CREATE_TABLE);
        }
        catch (Exception e)
        {
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertData(String date, String item1, String item2, String item_name)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE,date);
        contentValues.put(ITEM1,item1);
        contentValues.put(ITEM2,item2);
        contentValues.put(ITEM_NAME,item_name);
        long rowId = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        return rowId;
    }

    public Cursor read_all_data() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "SELECT * FROM "+TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null,null);
        return cursor;
    }
    public void delete_all_data() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "DELETE FROM "+TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
    }

    public void updateData(String date, String item1, String item2, String item_name)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE,date);
        contentValues.put(ITEM1,item1);
        contentValues.put(ITEM2,item2);
        contentValues.put(ITEM_NAME,item_name);
        sqLiteDatabase.update(TABLE_NAME,contentValues, DATE +" = ?",new String[]{String.valueOf(date)});
    }

    public Cursor read_all_favorites() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "SELECT * FROM "+TABLE_NAME+ " WHERE "+ ITEM_NAME +"=1";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null,null);
        return cursor;
    }

}
