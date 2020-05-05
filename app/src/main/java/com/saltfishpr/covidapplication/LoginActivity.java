package com.saltfishpr.covidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.saltfishpr.covidapplication.data.MyValues;
import com.saltfishpr.covidapplication.server.ServerContract;
import com.saltfishpr.covidapplication.server.SimulateServer;

public class LoginActivity extends AppCompatActivity {

    private EditText mEtAccount;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private Button mBtnRegister;
    private CheckBox mCbRemember;
    private SharedPreferences.Editor mEditor;
    private boolean store = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences mSharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        bindView();

        store = mSharedPreferences.getBoolean("remember", false);
        if (store) {
            mEtAccount.setText(mSharedPreferences.getString("account", ""));
            mEtPassword.setText(mSharedPreferences.getString("password", ""));
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
                    mEditor.putBoolean("remember", store);
                    mEditor.apply();
                } else {
                    mEditor.putBoolean("remember", false);
                    mEditor.apply();
                }
                // 登录，向服务器发送登录请求
                SimulateServer server = new SimulateServer(LoginActivity.this);
                switch (login(server, account, password)) {
                    case 0:  // 登录成功
                        MyValues.account = account;
                        Intent intent = new Intent(LoginActivity.this, CustomActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    default:
                        Toast.makeText(LoginActivity.this, "账号或者密码错误", Toast.LENGTH_SHORT).show();
                        break;
                }
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

    private int login(SimulateServer server, String account, String tempPassword) {
        Cursor cursor = server.queryAccountTable(account);
        if (cursor.getCount() == 0) {
            return 1;
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex(ServerContract.ServerEntry.COLUMN_PASSWORD));
        if (!password.equals(tempPassword)) {
            return 2;
        } else {
            return 0;
        }
    }
}
