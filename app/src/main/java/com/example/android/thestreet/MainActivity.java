package com.example.android.thestreet;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.thestreet.data.StockContract.StockEntry;
import com.example.android.thestreet.data.StockDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements StockCursorAdapter.ListItemClickListener {
    private StockDbHelper stockDbHelper;
    private RecyclerView recyclerView;
    private StockCursorAdapter stockCursorAdapter;
    ArrayList<StockData> stockData;
    private static final int CURSOR_LOADER_CALLBACK = 1;
    private static final int STOCK_PARSE_LOADER = 2;
    private RelativeLayout relativeLayout;
    private SwipeRefreshLayout mSwipeRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,StockEditorActivity.class);
                startActivity(intent);
            }
        });*/
        stockDbHelper = new StockDbHelper(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        stockCursorAdapter = new StockCursorAdapter(this,stockData);
        recyclerView.setAdapter(stockCursorAdapter);
        getLoaderManager().initLoader(CURSOR_LOADER_CALLBACK, null, cursorLoaderCallbacks);
        relativeLayout = (RelativeLayout) findViewById(R.id.emptyView);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                int id = (int) viewHolder.itemView.getTag();
                deleteStock(id);


            }
        }).attachToRecyclerView(recyclerView);

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(!isConnected)
                    Toast.makeText(MainActivity.this, "No Internet Connection Found", Toast.LENGTH_SHORT).show();
                getSupportLoaderManager().initLoader(STOCK_PARSE_LOADER,null,arrayListLoaderCallbacks);
            }
        });

    }
    private void deleteStock(int id){
        Uri uri = Uri.withAppendedPath(StockEntry.CONTENT_URI, "" +id);
        int rowsAffected = getContentResolver().delete(uri,null,null);
        if(rowsAffected!=0){
            Toast.makeText(this,"deleted",Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this,"Error while deleting..!!",Toast.LENGTH_SHORT).show();
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
       SearchManager searchManager =
               (SearchManager) getSystemService(Context.SEARCH_SERVICE);
       SearchView searchView =
               (SearchView) menu.findItem(R.id.search).getActionView();
       searchView.setSearchableInfo(
               searchManager.getSearchableInfo(getComponentName()));
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.profile) {
            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.add_stock){
            Intent intent = new Intent(MainActivity.this,StockEditorActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[]  projections = {
                    StockEntry.COLUMN_STOCK_ID,
                    StockEntry.COLUMN_STOCK_NAME,
                    StockEntry.COLUMN_STOCK_CURRENT_PRICE,
                    StockEntry.COLUMN_STOCK_CHANGE,
                    StockEntry.COLUMN_STOCK_VOLUME,
                    StockEntry.COLUMN_STOCK_UPDATED,
                    StockEntry.COLUMN_STOCK_PURCHASE_PRICE
            };
            return new CursorLoader(MainActivity.this,StockEntry.CONTENT_URI,projections,null,null,null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            ArrayList<StockData> sData = stockCursorAdapter.swapData(data);
            if(sData==null){
                recyclerView.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }else{
                recyclerView.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.INVISIBLE);
            }
            StockCursorAdapter stockCursorAdapter = new StockCursorAdapter(MainActivity.this,sData);
            stockData = sData;
          //  getSupportLoaderManager().initLoader(STOCK_PARSE_LOADER,null,arrayListLoaderCallbacks);
            recyclerView.setAdapter(stockCursorAdapter);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            ArrayList<StockData> data = stockCursorAdapter.swapData(null);
            stockData = data;
            recyclerView.setAdapter(null);
        }
    };
    @Override
    public void onListItemClick(int clickedItemID) {
        Intent intent = new Intent(MainActivity.this,StockEditorActivity.class);
        intent.setData(Uri.withAppendedPath(StockEntry.CONTENT_URI, "" +(clickedItemID) ));
        Log.e("Hey There", "" + Uri.withAppendedPath(StockEntry.CONTENT_URI, "" + clickedItemID));
        startActivity(intent);
    }

    private LoaderCallbacks<ArrayList<StockData>> arrayListLoaderCallbacks = new LoaderCallbacks<ArrayList<StockData>>() {
        @Override
        public android.support.v4.content.Loader<ArrayList<StockData>> onCreateLoader(int id, Bundle args) {
            return new StockParseAsyncLoader(MainActivity.this,stockData);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<ArrayList<StockData>> loader, ArrayList<StockData> data) {
            updateStock(data);
            mSwipeRefresh.setRefreshing(false);
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<ArrayList<StockData>> loader) {

        }
    };
    private void updateStock(ArrayList<StockData> data){
        for(int i=0;i<data.size();i++){
            Log.e("mainact",""+data.size());
            Uri uri = Uri.withAppendedPath(StockEntry.CONTENT_URI, "" +(data.get(i)).getStockID() );
            ContentValues values = new ContentValues();
            values.put(StockEntry.COLUMN_STOCK_NAME,data.get(i).getName());
            values.put(StockEntry.COLUMN_STOCK_CURRENT_PRICE,data.get(i).getCurrentPrice());
            values.put(StockEntry.COLUMN_STOCK_CHANGE,data.get(i).getChange());
            values.put(StockEntry.COLUMN_STOCK_UPDATED, data.get(i).getDate());

            int rowsAffetcted = getContentResolver().update(uri, values, null,null);
            if (rowsAffetcted == 0) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with updating", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
