package com.saltfishpr.covidapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import com.saltfishpr.covidapplication.data.MyDbHelper;

public class CustomActivity extends AppCompatActivity {
    private RecyclerView mRvInfo;
    private Button mBtnScanQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        Context mContext = this;
        mRvInfo = findViewById(R.id.rv_info);
        mBtnScanQR = findViewById(R.id.btn_scan);
        MyDbHelper dbHelper = new MyDbHelper(this);

    }
}
