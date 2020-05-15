package com.saltfishpr.covidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsActivity extends AppCompatActivity {
    private Button mBtnFriends;
    private TextView mTvFriends;
    private EditText mEtName;
    private EditText mEtPhone;
    private EditText mEtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mBtnFriends = findViewById(R.id.btn_show_friends);
        mTvFriends = findViewById(R.id.tv_friends);
        mEtName = findViewById(R.id.et_name);
        mEtPhone = findViewById(R.id.et_phone_number);
        mEtEmail = findViewById(R.id.et_email);
        showContacts(mTvFriends);
        mBtnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEtName.getText().toString();
                String phone = mEtPhone.getText().toString();
                String email = mEtEmail.getText().toString();
                if (name.equals("") || phone.equals("") || email.equals("")) {
                    Toast.makeText(FriendsActivity.this, "请填入信息", Toast.LENGTH_SHORT).show();
                } else {
                    addContact(v, name, phone, email);
                }
                showContacts(v);
            }
        });
    }

    public void showContacts(View view) {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            mTvFriends.setText("显示联系人错误");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (cursor.moveToNext()) {
            stringBuilder.append("ID：");
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            stringBuilder.append(contactId);

            stringBuilder.append("\t\t");
            stringBuilder.append("名字：");
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            stringBuilder.append(contactName);

            // 根据联系人ID查询对应的电话号码
            Cursor phonesCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            // 取得电话号码(可能会存在多个号码)
            if (phonesCursor != null) {
                stringBuilder.append("\t\t");
                stringBuilder.append("号码：");
                while (phonesCursor.moveToNext()) {
                    String phoneNumber = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    stringBuilder.append(phoneNumber);
                    stringBuilder.append("\n");
                }
                phonesCursor.close();
            }
        }
        cursor.close();
        mTvFriends.setText(stringBuilder.toString());
    }

    public void addContact(View view, String name, String phone, String email) {
        ContentValues values = new ContentValues();

        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

        values.clear();
        values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

        values.clear();
        values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Email.DATA, email);
        values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
    }
}
