package com.saltfishpr.covidapplication.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SimulateServer {
    private ServerDBHelper mDbHelper;
    private SQLiteDatabase mDb;

    public SimulateServer(Context context) {
        mDbHelper = new ServerDBHelper(context);
        mDb = mDbHelper.getWritableDatabase();
    }

    public Cursor queryAccountTable(String id_card) {
        // TODO: 查询具有id_card的用户，返回游标
        return mDb.query(ServerContract.ServerEntry.TABLE_NAME, new String[]{"id_card"}, "id_card = ?", new String[]{id_card}, null, null, null);
    }

    public void addAccountTable(String idCard, String password) {
        // TODO: 添加用户
        ContentValues contentValues = new ContentValues();
        contentValues.put(ServerContract.ServerEntry.COLUMN_ID_CARD, idCard);
        contentValues.put(ServerContract.ServerEntry.COLUMN_PASSWORD, password);
        mDb.insertOrThrow(ServerContract.ServerEntry.TABLE_NAME, null, contentValues);
    }

    public void updateAccountTable(String idCard, String password, String name, int unit, int room, int phoneNum) {
        // TODO: 修改用户信息
        ContentValues contentValues = new ContentValues();
        contentValues.put(ServerContract.ServerEntry.COLUMN_PASSWORD, password);
        contentValues.put(ServerContract.ServerEntry.COLUMN_NAME, name);
        contentValues.put(ServerContract.ServerEntry.COLUMN_UNIT, unit);
        contentValues.put(ServerContract.ServerEntry.COLUMN_ROOM, room);
        contentValues.put(ServerContract.ServerEntry.COLUMN_PHONE, phoneNum);
        mDb.update(ServerContract.ServerEntry.TABLE_NAME, contentValues, "id_card = ?", new String[]{idCard});
    }
}
