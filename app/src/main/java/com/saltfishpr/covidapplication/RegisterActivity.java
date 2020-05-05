package com.saltfishpr.covidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.saltfishpr.covidapplication.server.ServerContract;
import com.saltfishpr.covidapplication.server.ServerDBHelper;
import com.saltfishpr.covidapplication.server.SimulateServer;

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
                // 网络操作，向服务器发送注册信息，这里简化为操作本地数据库
                SimulateServer simulateServer = new SimulateServer(RegisterActivity.this);
                String res = register(simulateServer, account, password, passwordConfirm);
                if (res == null) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, res, Toast.LENGTH_SHORT).show();
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

    public String register(SimulateServer server, String account, String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            return "两次输入的密码不相符";
        }
        if (server.queryAccountTable(account).getCount() != 0) {
            return "此用户已存在，请重新输入";
        }
        server.addAccountTable(account, password);
        return null;
    }
}
