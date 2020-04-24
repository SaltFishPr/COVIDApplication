package com.saltfishpr.covidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

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
            mEtAccount.setText(mSharedPreferences.getString("username", ""));
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
                // 登录行为
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
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
        mBtnRegister = findViewById(R.id.btn_register);
        mCbRemember = findViewById(R.id.cb_remember);
    }
}
