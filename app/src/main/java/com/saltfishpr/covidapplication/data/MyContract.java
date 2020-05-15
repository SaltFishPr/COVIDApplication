package com.saltfishpr.covidapplication.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MyContract {
    public static final String SERVER_URL = "http://49.235.19.174:5000/";
    public static final String CONTENT_AUTHORITY = "com.saltfishpr.covidapplication";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ACCOUNT_DATA = "account";

    public static final class AccountEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ACCOUNT_DATA)
                .build();

        public static final String TABLE_NAME = "accountinfo";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ID_CARD = "id_card";
        public static final String COLUMN_PASSWORD = "password";
    }
}
