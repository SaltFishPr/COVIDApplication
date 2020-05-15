package com.saltfishpr.covidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.saltfishpr.covidapplication.data.MyContract;
import com.saltfishpr.covidapplication.data.MyValues;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText mEtAccount;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private Button mBtnRegister;
    private CheckBox mCbRemember;
    private SharedPreferences.Editor mEditor;
    private boolean store = false;
    private final int REQUEST_PERMISSION_CODE = 106;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences mSharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        bindView();

        if (!checkPermission()) {
            requestPermissions();
        }

        store = mSharedPreferences.getBoolean("remember", false);
        if (store) {
            mEtAccount.setText(mSharedPreferences.getString("account", ""));
            mEtPassword.setText(mSharedPreferences.getString("password", ""));
            mCbRemember.setChecked(true);
        }

        mCbRemember.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.getId() == R.id.cb_remember) {
                    store = isChecked;
                }
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = mEtAccount.getText().toString();
                String password = mEtPassword.getText().toString();
                if (store) {
                    mEditor.putString("account", account);
                    mEditor.putString("password", password);
                } else {
                    mEditor.clear();
                }
                mEditor.putBoolean("remember", store);
                mEditor.apply();

                // 登录，向服务器发送登录请求
                LoginTask loginTask = new LoginTask();
                loginTask.execute(account, password);
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void bindView() {
        mEtAccount = findViewById(R.id.et_account);
        mEtPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_go_register);
        mCbRemember = findViewById(R.id.cb_remember);
    }

    //检查权限
    private boolean checkPermission() {
        boolean haveInternetPermission = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        return haveInternetPermission;
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.INTERNET
        };
        requestPermissions(permissions, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allowAllPermission = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {//被拒绝授权
                    break;
                }
                allowAllPermission = true;
            }
            if (!allowAllPermission) {
                Toast.makeText(this, "该功能需要授权方可使用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LoginTask extends AsyncTask<String, Void, Integer> {
        private String response_message;
        private String tempAccount;
        private boolean passAuth = false;
        private int ret_code;

        @Override
        protected Integer doInBackground(@NotNull String... strings) {
            String account = strings[0];
            tempAccount = account;
            String password = strings[1];
            String url = MyContract.SERVER_URL + "login";

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("account", account)
                    .add("password", password)
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
                return 3;
            }
            try {
                JSONObject jsonObject = new JSONObject(response_message);
                passAuth = (boolean) jsonObject.get("data");
                ret_code = (int) jsonObject.get("ret_code");
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
                    if (passAuth) {
                        MyValues.account = tempAccount;
                        Intent intent = new Intent(LoginActivity.this, CustomActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "账号或者密码错误", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(LoginActivity.this, "连接错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
