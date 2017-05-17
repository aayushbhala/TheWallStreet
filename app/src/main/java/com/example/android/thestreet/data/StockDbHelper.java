package com.example.android.thestreet.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.thestreet.data.StockContract.StockEntry;
import com.example.android.thestreet.data.StockContract.UserProfile;

/**
 * Created by MAHE on 13-May-17.
 */
public class StockDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = StockDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "mystock.db";
    private static final int DATABASE_VERSION = 1;

    public StockDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_STOCK_TABLE = "CREATE TABLE "+ UserProfile.TABLE_NAME + "("
                + UserProfile._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UserProfile.COLUMN_USER_NAME+ " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_STOCK_TABLE);
        String SQL_CREATE_STOCK_TABLE1 = "CREATE TABLE "+ StockEntry.TABLE_NAME + "("
                +StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                +StockEntry.COLUMN_STOCK_ID + " INTEGER,"
                +StockEntry.COLUMN_STOCK_NAME+ " TEXT NOT NULL,"
                +StockEntry.COLUMN_STOCK_CURRENT_PRICE+" REAL,"
                +StockEntry.COLUMN_STOCK_PURCHASE_PRICE+" REAL DEFAULT 0,"
                +StockEntry.COLUMN_STOCK_CHANGE+" REAL,"
                +StockEntry.COLUMN_STOCK_VOLUME+ " INTEGER DEFAULT 0,"
                +StockEntry.COLUMN_STOCK_TODAYS_HIGH+ " REAL DEFAULT 0,"
                +StockEntry.COLUMN_STOCK_TODAYS_LOW+ " REAL DEFAULT 0,"
                +StockEntry.COLUMN_STOCK_UPDATED+ " TEXT,"
                +StockEntry.COLUMN_STOCK_HOLDER+" INTEGER NOT NULL,"
                +" FOREIGN KEY (" +StockEntry.COLUMN_STOCK_HOLDER+") REFERENCES "+ UserProfile.TABLE_NAME+"("+ UserProfile._ID+") ON DELETE CASCADE);";
        db.execSQL(SQL_CREATE_STOCK_TABLE1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
