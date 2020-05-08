package com.saltfishpr.covidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEtAccount;
    private EditText mEtPassword;
    private EditText mEtPasswordConfirm;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindView();
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = mEtAccount.getText().toString();
                String password = mEtPassword.getText().toString();
                String passwordConfirm = mEtPasswordConfirm.getText().toString();
                // 网络操作，向服务器发送注册信息
                if (account.length() != 18) {
                    Toast.makeText(RegisterActivity.this, "身份证号码错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() <= 6) {
                    Toast.makeText(RegisterActivity.this, "密码太短", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(passwordConfirm)) {
                        RegisterTask registerTask = new RegisterTask();
                        registerTask.execute(account, password);
                    } else {
                        Toast.makeText(RegisterActivity.this, "两次输入的密码不相同", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void bindView() {
        mEtAccount = findViewById(R.id.et_account);
        mEtPassword = findViewById(R.id.et_password);
        mEtPasswordConfirm = findViewById(R.id.et_password_confirm);
        mBtnRegister = findViewById(R.id.btn_register);
    }

    private class RegisterTask extends AsyncTask<String, Void, Integer> {
        private String response_message;
        private int ret_code;

        @Override
        protected Integer doInBackground(String... strings) {
            String account = strings[0];
            String password = strings[1];
            String url = "http://49.235.19.174:5000/register";
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
            switch (ret_code) {
                case 1:
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case 2:
                    Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
}
