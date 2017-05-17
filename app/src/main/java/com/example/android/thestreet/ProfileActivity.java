package com.example.android.thestreet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import com.example.android.thestreet.data.StockContract;
import com.example.android.thestreet.data.StockDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    private SQLiteOpenHelper mdbHelper;
    private TextView mCurrentWorth;
    private TextView mMaxGainers;
    private TextView mMinGainers;
    private TextView mMaxName;
    private TextView mMinName;
    private TextView mDateTextView;
    private TextView mSmallTextView;
    private TextView mMonthTextView;
    private TextView mTimeTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mdbHelper = new StockDbHelper(this);
        mCurrentWorth = (TextView) findViewById(R.id.current_balance);
        mMaxGainers = (TextView) findViewById(R.id.max_gainer_value);
        mMinGainers = (TextView) findViewById(R.id.min_gainer_value);
        mMaxName = (TextView) findViewById(R.id.max_gainer);
        mMinName = (TextView) findViewById(R.id.min_gainer);
        mDateTextView = (TextView) findViewById(R.id.date);
        mSmallTextView = (TextView) findViewById(R.id.st_nd_rd_th);
        mMonthTextView = (TextView) findViewById(R.id.month);
        mTimeTextView = (TextView) findViewById(R.id.time);
        int id = Integer.parseInt(getIntent().getExtras().getString("user_id"));
        SQLiteDatabase db = mdbHelper.getReadableDatabase();
        String[] projections = {
                "SUM("+ StockContract.StockEntry.COLUMN_STOCK_PURCHASE_PRICE+"*"+StockContract.StockEntry.COLUMN_STOCK_VOLUME+")AS sum_pp",
                "SUM("+ StockContract.StockEntry.COLUMN_STOCK_CURRENT_PRICE+"*"+StockContract.StockEntry.COLUMN_STOCK_VOLUME+")AS sum_cp",
        };
        String selection = StockContract.StockEntry.COLUMN_STOCK_HOLDER+"=?";
        String[] selectionArgs = {
                ""+id
        };
        Cursor cursor = db.query(
                StockContract.StockEntry.TABLE_NAME,
                projections,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        double pp = cursor.getDouble(cursor.getColumnIndex("sum_pp"));
        Log.e("Profile",""+pp);
        double cp = cursor.getDouble(cursor.getColumnIndex("sum_cp"));
        Log.e("Profile",""+cp);
        double worth  = (cp-pp);
        mCurrentWorth.setText(String.format("%.2f", worth));
        if(worth>0){
            mCurrentWorth.setTextColor(Color.GREEN);
        }else{
            mCurrentWorth.setTextColor(Color.RED);
        }
        cursor.close();
        String[] projection2 = {
                StockContract.StockEntry.COLUMN_STOCK_NAME,
                "(("+StockContract.StockEntry.COLUMN_STOCK_CURRENT_PRICE+"-"+ StockContract.StockEntry.COLUMN_STOCK_PURCHASE_PRICE+")*"+
                        StockContract.StockEntry.COLUMN_STOCK_VOLUME+") AS gainers",
                StockContract.StockEntry.COLUMN_STOCK_UPDATED
        };
        Cursor cursor1 = db.query(
                StockContract.StockEntry.TABLE_NAME,
                projection2,
                selection,
                selectionArgs,
                null,
                null,
                "gainers DESC"
        );
        cursor1.moveToFirst();
        if(cursor1.getCount()!=0) {
            String date_time = cursor1.getString(cursor1.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_UPDATED));
            mMaxGainers.setText(String.format("%.2f", cursor1.getDouble(cursor1.getColumnIndex("gainers"))));
            if (cursor1.getDouble(cursor1.getColumnIndex("gainers")) > 0) {
                mMaxGainers.setTextColor(Color.GREEN);
            } else {
                mMaxGainers.setTextColor(Color.RED);
            }
            mMaxName.setText(cursor1.getString(cursor1.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_NAME)));
            cursor1.moveToLast();
            mMinGainers.setText(String.format("%.2f", cursor1.getDouble(cursor1.getColumnIndex("gainers"))));
            if (cursor1.getDouble(cursor1.getColumnIndex("gainers")) > 0) {
                mMinGainers.setTextColor(Color.GREEN);
            } else {
                mMinGainers.setTextColor(Color.RED);
            }
            mMinName.setText(cursor1.getString(cursor1.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_NAME)));
            cursor1.close();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,HH:mm");
            String day = "";
            String month = "";
            String time = "";
            try {
                Date date = simpleDateFormat.parse(date_time);
                day = (String) DateFormat.format("dd", date);
                month = (String) DateFormat.format("MMM", date);
                time = (String) DateFormat.format("HH:mm", date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDateTextView.setText(day);
            mMonthTextView.setText(month);
            mTimeTextView.setText(time);
            switch (Integer.parseInt(day)) {
                case 1:
                    mSmallTextView.setText("st");
                    break;
                case 2:
                    mSmallTextView.setText("nd");
                    break;
                case 3:
                    mSmallTextView.setText("rd");
                    break;
                default:
                    mSmallTextView.setText("th");
                    break;
            }
        }
    }
}