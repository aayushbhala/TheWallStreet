package com.example.android.thestreet;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.thestreet.data.StockContract.StockEntry;

import java.util.ArrayList;

public class StockEditorActivity extends AppCompatActivity {
    private EditText midEditText;
    private EditText mNameEditText;
    private EditText mpriceEditText;
    private EditText mVolumeEditText;
    private Button mInsertButton;
    private Uri uri;
    private StockData newData;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;

    private static final int CURSOR_LOADER_CALLBACK = 1;
    private static final int STOCK_PARSE_LOADER = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_editor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        uri = getIntent().getData();
        Bundle bundle = getIntent().getExtras();
        midEditText = (EditText) findViewById(R.id.input_idEditText);
        mNameEditText = (EditText) findViewById(R.id.input_nameEditText);
        mpriceEditText = (EditText) findViewById(R.id.input_purchase_priceEditText);
        mVolumeEditText = (EditText) findViewById(R.id.input_volumeEditText);
        mInsertButton = (Button) findViewById(R.id.insertButton);

        if (uri != null) {
            getLoaderManager().initLoader(CURSOR_LOADER_CALLBACK, null, cursorLoaderCallbacks);
            this.setTitle("Change Stock Details");
            mInsertButton.setText("Update This");
        } else {
            this.setTitle("Add a new Stock");
            mInsertButton.setText("Add This");
            if(bundle!=null){
                midEditText.setText(""+bundle.getString("ID"));
                mNameEditText.setText(bundle.getString("Name"));
            }
        }
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.INVISIBLE);
        linearLayout = (LinearLayout) findViewById(R.id.editorLayout);
        linearLayout.setVisibility(View.VISIBLE);

        mInsertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (midEditText.getText().toString().trim().equals("")) {
                    midEditText.setError("This is Required");
                    return;
                }
                if (mNameEditText.getText().toString().trim().equals("")) {
                    mNameEditText.setError("This is Required");
                    return;
                }
                if (mpriceEditText.getText().toString().trim().equals("")) {
                    mpriceEditText.setError("This is Required");
                    return;
                }
                if (mVolumeEditText.getText().toString().trim().equals("")) {
                    mVolumeEditText.setError("This is Required");
                    return;
                }
                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(isConnected){
                    insertStock();
                    progressBar.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.INVISIBLE);
                    getSupportLoaderManager().initLoader(STOCK_PARSE_LOADER, null, arrayListLoaderCallbacks);

                }else{
                    Toast.makeText(StockEditorActivity.this, "No Internet Connection Found", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
    private void insertStock() {
        String name = "";
        int volume;
        double price;
        int id;
        if (midEditText.getText().toString().trim().equals("")) {
            midEditText.setError("This is Required");
            return;
        } else {
            id = Integer.parseInt(midEditText.getText().toString().trim());
        }
        if (mNameEditText.getText().toString().trim().equals("")) {
            mNameEditText.setError("This is Required");
            return;
        } else {
            name = mNameEditText.getText().toString().trim();
        }

        if (mpriceEditText.getText().toString().trim().equals("")) {
            mpriceEditText.setError("This is Required");
            return;
        } else {
            price = Double.parseDouble(mpriceEditText.getText().toString().trim());
        }

        if (mVolumeEditText.getText().toString().trim().equals("")) {
            mVolumeEditText.setError("This is Required");
            return;
        } else {
            volume = Integer.parseInt(mVolumeEditText.getText().toString().trim());
        }
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_STOCK_ID, id);
        values.put(StockEntry.COLUMN_STOCK_NAME, name);
        values.put(StockEntry.COLUMN_STOCK_PURCHASE_PRICE, price);
        values.put(StockEntry.COLUMN_STOCK_VOLUME, volume);
        newData = new StockData(name,id,price,0.00,volume,"",0.00);

        if (this.uri == null) {
            Uri uri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
            long newRowId = ContentUris.parseId(uri);
            // Show a toast message depending on whether or not the insertion was successful
            if (newRowId == -1) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with saving " + name, Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
            }
        } else {
            int rowsAffetcted = getContentResolver().update(this.uri, values, null, null);
            if (rowsAffetcted == 0) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with updating " + name, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = {
                    StockEntry._ID,
                    StockEntry.COLUMN_STOCK_ID,
                    StockEntry.COLUMN_STOCK_NAME,
                    StockEntry.COLUMN_STOCK_PURCHASE_PRICE,
                    StockEntry.COLUMN_STOCK_VOLUME
            };
            return new CursorLoader(StockEditorActivity.this, uri, projection, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.moveToFirst()) {
                Log.e("Data", "Present" + data.getInt(data.getColumnIndex(StockEntry.COLUMN_STOCK_ID)));
                midEditText.setText("" + data.getInt(data.getColumnIndex(StockEntry.COLUMN_STOCK_ID)));
                mNameEditText.setText(data.getString(data.getColumnIndex(StockEntry.COLUMN_STOCK_NAME)));
                mpriceEditText.setText(String.valueOf(data.getDouble(data.getColumnIndex(StockEntry.COLUMN_STOCK_PURCHASE_PRICE))));
                mVolumeEditText.setText("" + data.getInt(data.getColumnIndex(StockEntry.COLUMN_STOCK_VOLUME)));
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            midEditText.setText("");
            mNameEditText.setText("");
            mpriceEditText.setText("");
            mVolumeEditText.setText("");
        }


    };

    private LoaderCallbacks<ArrayList<StockData>> arrayListLoaderCallbacks = new LoaderCallbacks<ArrayList<StockData>>() {
        @Override
        public android.support.v4.content.Loader<ArrayList<StockData>> onCreateLoader(int id, Bundle args) {
            ArrayList<StockData> stockData = new ArrayList<>();
            stockData.add(newData);
            return new StockParseAsyncLoader(StockEditorActivity.this,stockData);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<ArrayList<StockData>> loader, ArrayList<StockData> data) {
            if(data.size()!=0) {
                Uri uri = Uri.withAppendedPath(StockEntry.CONTENT_URI, "" + (data.get(0)).getStockID());
                ContentValues values = new ContentValues();
                values.put(StockEntry.COLUMN_STOCK_NAME, data.get(0).getName());
                values.put(StockEntry.COLUMN_STOCK_CURRENT_PRICE, data.get(0).getCurrentPrice());
                values.put(StockEntry.COLUMN_STOCK_CHANGE, data.get(0).getChange());
                values.put(StockEntry.COLUMN_STOCK_UPDATED, data.get(0).getDate());

                int rowsAffetcted = getContentResolver().update(uri, values, null, null);
                if (rowsAffetcted == 0) {
                    // If the row ID is -1, then there was an error with insertion.
                    Toast.makeText(StockEditorActivity.this, "Error with updating", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
                finish();
            }

        }


        @Override
        public void onLoaderReset(android.support.v4.content.Loader<ArrayList<StockData>> loader) {

        }
    };


}
