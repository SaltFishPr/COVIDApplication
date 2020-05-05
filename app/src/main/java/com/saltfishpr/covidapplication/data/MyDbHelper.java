package com.saltfishpr.covidapplication.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "covid.db";
    private static final int DATABASE_VERSION = 1;

    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PASS_TABLE = "CREATE TABLE " + MyContract.PassEntry.TABLE_NAME + " (" +
                MyContract.PassEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MyContract.PassEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                MyContract.PassEntry.COLUMN_GATE + " INTEGER NOT NULL, " +
                MyContract.PassEntry.COLUMN_DIR + " INTEGER NOT NULL " +
                ");";
        db.execSQL(SQL_CREATE_PASS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MyContract.PassEntry.TABLE_NAME);
        onCreate(db);
    }

}
