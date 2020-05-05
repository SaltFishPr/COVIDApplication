package com.saltfishpr.covidapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;

import com.saltfishpr.covidapplication.data.MyContract;
import com.saltfishpr.covidapplication.data.MyDbHelper;

public class CustomActivity extends AppCompatActivity {
    private RecyclerView mRvInfo;
    private Button mBtnScanQR;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        Context mContext = this;
        mRvInfo = findViewById(R.id.rv_info);
        mBtnScanQR = findViewById(R.id.btn_scan);
        MyDbHelper dbHelper = new MyDbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();

        mRvInfo.setLayoutManager(new LinearLayoutManager(mContext));
        mRvInfo.setAdapter(new MyAdapter(mContext, getAllInfo()));

    }

    private Cursor getAllInfo()  {
        return mDb.query(
                MyContract.PassEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MyContract.PassEntry._ID
        );
    }

}
