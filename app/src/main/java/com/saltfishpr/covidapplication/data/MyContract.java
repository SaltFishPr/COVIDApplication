package com.saltfishpr.covidapplication.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MyContract {

    public static final String CONTENT_AUTHORITY = "com.saltfishpr.covidapplication";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PASS_DATA = "pass";

    public static final class PassEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PASS_DATA)
                .build();

        public static final String TABLE_NAME = "passinfo";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_GATE = "gate_num";
        public static final String COLUMN_DIR = "direction";
    }
}
