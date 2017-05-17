package com.example.android.thestreet.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MAHE on 13-May-17.
 */
public class StockContract {
    private StockContract(){}
    public static final String CONTENT_AUTHORITY = "com.example.android.thestreet";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_STOCK = "stocks";
    public static final String PATH_USER = "users";
    public static final String PATH_JOIN = "join";

    public static final class StockEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_STOCK);
        public static final String TABLE_NAME = "stocks";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_STOCK_ID = "bse_id";
        public static final String COLUMN_STOCK_NAME = "name";
        public static final String COLUMN_STOCK_PURCHASE_PRICE ="purchase_price";
        public static final String COLUMN_STOCK_CURRENT_PRICE = "current_price";
        public static final String COLUMN_STOCK_UPDATED = "last_updated";
        public static final String COLUMN_STOCK_VOLUME = "share_volume";
        public static final String COLUMN_STOCK_CHANGE = "change";
        public static final String COLUMN_STOCK_TODAYS_HIGH = "high";
        public static final String COLUMN_STOCK_TODAYS_LOW = "low";
        public static final String COLUMN_STOCK_HOLDER = "user_id";

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_STOCK;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_STOCK;
    }
    public static final class UserProfile implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_USER);
        public static final String TABLE_NAME = "users";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_USER_NAME = "user_name";

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_USER;
    }
}