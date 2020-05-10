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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.saltfishpr.covidapplication.data.MyContract;
import com.saltfishpr.covidapplication.data.MyDbHelper;
import com.saltfishpr.covidapplication.data.MyValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();


//                testResult.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class LocationTask extends AsyncTask<String, Void, Integer> {
        private String location;
        private String response_message;
        private int ret_code;

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                JSONObject jsonObject = new JSONObject(strings[0]);
                location = (String) jsonObject.get("location");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = "http://49.235.19.174:5000/post";

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("account", MyValues.account)
                    .add("location", location)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                response_message = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject = new JSONObject(response_message);
                ret_code = (int) jsonObject.get("ret_code");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ret_code;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }

}
