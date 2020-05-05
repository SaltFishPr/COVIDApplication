package com.saltfishpr.covidapplication.server;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.saltfishpr.covidapplication.data.MyContract;

public class ServerDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "server.db";
    private static final int DATABASE_VERSION = 1;

    public ServerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ACCOUNT_TABLE = "CREATE TABLE " + ServerContract.ServerEntry.TABLE_NAME + " (" +
                ServerContract.ServerEntry.COLUMN_ID_CARD + " TEXT PRIMARY KEY, " +
                ServerContract.ServerEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                ServerContract.ServerEntry.COLUMN_NAME + " TEXT, " +
                ServerContract.ServerEntry.COLUMN_UNIT + " INTEGER, " +
                ServerContract.ServerEntry.COLUMN_ROOM + " INTEGER, " +
                ServerContract.ServerEntry.COLUMN_PHONE + " INTEGER " +
                ");";
        db.execSQL(SQL_CREATE_ACCOUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ServerContract.ServerEntry.TABLE_NAME);
        onCreate(db);
    }
}
