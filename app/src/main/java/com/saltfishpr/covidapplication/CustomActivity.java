package com.saltfishpr.covidapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.saltfishpr.covidapplication.data.MyContract;
import com.saltfishpr.covidapplication.data.MyDbHelper;

public class CustomActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRvInfo;
    private Button mBtnScanQR;
    private SQLiteDatabase mDb;
    private int REQUEST_PERMISSION_CODE = 108;

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

        mBtnScanQR.setOnClickListener(this);

    }

    private Cursor getAllInfo() {
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_scan) {
            if (!checkPermission()) {  // 请求权限
                requestPermissions();
            }
            IntentIntegrator intentIntegrator = new IntentIntegrator(CustomActivity.this);
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.setCaptureActivity(ScanActivity.class);
            intentIntegrator.initiateScan();
        }
    }

    //检查权限
    private boolean checkPermission() {
        boolean haveCameraPermission = ContextCompat.checkSelfPermission(CustomActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean haveVibratePermission = ContextCompat.checkSelfPermission(CustomActivity.this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED;
        return haveCameraPermission && haveVibratePermission;
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.VIBRATE
        };
        requestPermissions(permissions, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allowAllPermission = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {//被拒绝授权
                    allowAllPermission = false;
                    break;
                }
            }
            if (!allowAllPermission) {
                Toast.makeText(this, "该功能需要授权方可使用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
//                testResult.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
