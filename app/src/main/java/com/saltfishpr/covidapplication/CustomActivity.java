package com.saltfishpr.covidapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.saltfishpr.covidapplication.data.MyContract;
import com.saltfishpr.covidapplication.data.MyValues;
import com.saltfishpr.covidapplication.services.MyJobService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CustomActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String CHANNEL_ID = "33333333";
    private RecyclerView mRvInfo;
    private Button mBtnScanQR;
    private Button mBtnRefresh;
    private Button mBtnTest;
    private MyAdapter myAdapter;
    private MyReceiver mReceiver;
    private int REQUEST_PERMISSION_CODE = 108;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        if (!checkPermission()) {  // 请求权限
            requestPermissions();
        }
        Context mContext = this;
        mRvInfo = findViewById(R.id.rv_info);
        mBtnScanQR = findViewById(R.id.btn_scan);
        mBtnRefresh = findViewById(R.id.btn_refresh);
        mBtnTest = findViewById(R.id.btn_test);

        mRvInfo.setLayoutManager(new LinearLayoutManager(mContext));
        myAdapter = new MyAdapter(mContext, getData());
        mRvInfo.setAdapter(myAdapter);

        mBtnScanQR.setOnClickListener(this);
        mBtnRefresh.setOnClickListener(this);
        mBtnTest.setOnClickListener(this);
        myAdapter.swapData(getData());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.activity_setting) {
            Toast.makeText(this, "功能尚未完成", Toast.LENGTH_SHORT).show();
//            Context context = CustomActivity.this;
//            Intent intent = new Intent(context, SettingActivity.class);
//            startActivity(intent);
        } else if (itemId == R.id.activity_friends) {
            Context context = CustomActivity.this;
            Intent intent = new Intent(context, FriendsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        // 注册广播接收器
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        // 注销广播接收器
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan: {
                IntentIntegrator intentIntegrator = new IntentIntegrator(CustomActivity.this);
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setCaptureActivity(ScanActivity.class);
                intentIntegrator.initiateScan();
                break;
            }
            case R.id.btn_refresh: {
                myAdapter.swapData(getData());
                break;
            }
            case R.id.btn_test: {

                break;
            }
            default:
                break;
        }
    }

    /**
     * 检查权限
     *
     * @return 是否授权
     */
    private boolean checkPermission() {
        boolean haveCameraPermission = ContextCompat.checkSelfPermission(CustomActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean haveVibratePermission = ContextCompat.checkSelfPermission(CustomActivity.this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED;
        boolean haveReadContactsPermission = ContextCompat.checkSelfPermission(CustomActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        boolean haveWriteContactsPermission = ContextCompat.checkSelfPermission(CustomActivity.this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        return haveCameraPermission && haveVibratePermission && haveReadContactsPermission && haveWriteContactsPermission;
    }

    /**
     * 请求权限
     */
    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.VIBRATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        };
        requestPermissions(permissions, REQUEST_PERMISSION_CODE);
    }

    private JSONArray getData() {
        GetRecordTask getRecordTask = new GetRecordTask();
        getRecordTask.execute(MyValues.account);
        return MyValues.data;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allowAllPermission = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {  // 被拒绝授权
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
                PostRecordTask postRecordTask = new PostRecordTask();
                postRecordTask.execute(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
                Toast.makeText(CustomActivity.this, "正在充电", Toast.LENGTH_SHORT).show();
            } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                Toast.makeText(CustomActivity.this, "停止充电", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class PostRecordTask extends AsyncTask<String, Void, Integer> {
        private String result;

        @Override
        protected Integer doInBackground(String... strings) {
            String gate;
            try {
                JSONObject jsonObject = new JSONObject(strings[0]);
                gate = jsonObject.getString("gate");
            } catch (JSONException e) {
                e.printStackTrace();
                return 2;
            }
            String url = MyContract.SERVER_URL + "post_record";

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("account", MyValues.account)
                    .add("gate", gate)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            String response_message;
            try {
                Response response = client.newCall(request).execute();
                response_message = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return 3;
            }
            int ret_code;
            try {
                JSONObject jsonObject = new JSONObject(response_message);
                result = jsonObject.getString("result");
                ret_code = jsonObject.getInt("ret_code");
            } catch (JSONException e) {
                e.printStackTrace();
                return 2;
            }
            return ret_code;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case 1:
                    if (result.equals("permit")) {
                        makeNotification();
                        // 设置JobService用于时间提醒
                        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                        JobInfo.Builder builder = new JobInfo.Builder(1,
                                new ComponentName(getPackageName(), MyJobService.class.getName()));
                        builder.setMinimumLatency(6300000L); //执行的最小延迟时间
                        builder.setOverrideDeadline(6300000L);  //执行的最长延时时间
                        builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
                        builder.setPersisted(true);  // 设置设备重启时，执行该任务
                        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
                        builder.setRequiresCharging(true); // 当插入充电器，执行该任务
                        JobInfo info = builder.build();
                        jobScheduler.schedule(info); //开始定时执行该系统任务
                    } else {
                        Toast.makeText(CustomActivity.this, "通行次数已使用", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(CustomActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(CustomActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    private class GetRecordTask extends AsyncTask<String, Void, Integer> {
        JSONArray data;

        @Override
        protected Integer doInBackground(String... strings) {
            String account = strings[0];
            String url = MyContract.SERVER_URL + "get_record/" + account;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            String response_message;
            try {
                Response response = client.newCall(request).execute();
                response_message = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return 3;
            }

            int ret_code;
            try {
                data = new JSONObject(response_message).getJSONArray("data");
                Log.i("data", String.valueOf(data));
                ret_code = new JSONObject(response_message).getInt("ret_code");
            } catch (JSONException e) {
                e.printStackTrace();
                return 2;
            }
            return ret_code;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case 1:
                    MyValues.data = data;
                    break;
                case 2:
                    Toast.makeText(CustomActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(CustomActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    public void makeNotification() {
        Context context = CustomActivity.this;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, CustomActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_account_circle_24px)
                .setContentTitle("准予出行")
                .setContentText("请在2小时内返回喔")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());
    }
}
