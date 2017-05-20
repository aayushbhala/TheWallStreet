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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.thestreet.data.StockContract.StockEntry;
import com.example.android.thestreet.data.StockContract.UserProfile;
import com.example.android.thestreet.data.StockDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements StockCursorAdapter.ListItemClickListener {
    private StockDbHelper stockDbHelper;
    private RecyclerView recyclerView;
    private Spinner spinner;
    private StockCursorAdapter stockCursorAdapter;
    ArrayList<StockData> stockData;
    private Bundle bundle;
    private static final int CURSOR_LOADER_CALLBACK = 1;
    private static final int STOCK_PARSE_LOADER = 2;
    private static final int USER_LOADER_CALLBACK = 3;
    private RelativeLayout relativeLayout;
    private ArrayList<User> arrayList;
    private ArrayList<String> userArrayList;
    private SwipeRefreshLayout mSwipeRefresh;
    public static String user_name = "";
    public static int user_id;
    private int postition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      /*  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.commit();
            Intent intent = new Intent(this,CreateUserActivity.class);
            startActivity(intent);
        }*/
        bundle = new Bundle();
        spinner = (Spinner) findViewById(R.id.spinner_user);
        setupSpinner();

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
       // getLoaderManager().initLoader(CURSOR_LOADER_CALLBACK, null, cursorLoaderCallbacks);
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
                getSupportLoaderManager().restartLoader(STOCK_PARSE_LOADER, null, arrayListLoaderCallbacks);
            }
        });
        getLoaderManager().restartLoader(USER_LOADER_CALLBACK,null,userCursorLoaderCallbacks);

    }

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        arrayList = new ArrayList<>();
        userArrayList = new ArrayList<>();
        getLoaderManager().initLoader(USER_LOADER_CALLBACK,null,userCursorLoaderCallbacks);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,userArrayList);

        // Specify dropdown layout style - simple list view with 1 item per line
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);

        // Set the integer mSelected to the constant values
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    for (int i = 0; i < userArrayList.size(); i++) {
                        if (selection.equals(userArrayList.get(i))) {
                          //  Log.e("MAIN ACTIVITY", selection);
                            user_name = selection;
                            user_id = arrayList.get(i).getUser_id();
                            getLoaderManager().restartLoader(CURSOR_LOADER_CALLBACK, null, cursorLoaderCallbacks);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                user_name = arrayList.get(0).getUser_name();
                user_id = arrayList.get(0).getUser_id();
                getLoaderManager().initLoader(CURSOR_LOADER_CALLBACK, null, cursorLoaderCallbacks);
            }
        });
    }
    private void deleteStock(int id){
        Uri uri = Uri.withAppendedPath(StockEntry.CONTENT_URI, "" +id);
        int rowsAffected = getContentResolver().delete(uri,null,null);
        if(rowsAffected!=0){
           // Log.e("MainAct",rowsAffected+" "+id);
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
            Bundle bundle = new Bundle();
            bundle.putString("user_id",""+user_id);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        if (id == R.id.add_stock){
            Intent intent = new Intent(MainActivity.this,StockEditorActivity.class);
            Bundle bundle2 = new Bundle();
            bundle2.putString("user_name",user_name);
            bundle2.putString("user_id",""+user_id);
            intent.putExtras(bundle2);
            startActivity(intent);
            return true;
        }
        if(id == R.id.user){
            Intent intent = new Intent(MainActivity.this,CreateUserActivity.class);
            Bundle bundle2 = new Bundle();
            intent.putExtras(bundle2);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private LoaderManager.LoaderCallbacks<Cursor> userCursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projections = {
                    UserProfile._ID,
                    UserProfile.COLUMN_USER_NAME
            };
            return new CursorLoader(MainActivity.this, UserProfile.CONTENT_URI,projections,null,null,null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            userArrayList.clear();
            arrayList.clear();
            if(cursor.getCount()==0){
                Toast.makeText(MainActivity.this,"No Account Found Create a new one",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,CreateUserActivity.class);
                startActivity(intent);
            }
            while(cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndex(UserProfile.COLUMN_USER_NAME));
                int id = cursor.getInt(cursor.getColumnIndex(UserProfile._ID));
                userArrayList.add(name);
                arrayList.add(new User(id,name));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,userArrayList);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spinner.setAdapter(adapter);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };
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
                    StockEntry.COLUMN_STOCK_PURCHASE_PRICE,
                    StockEntry.COLUMN_STOCK_TODAYS_HIGH,
                    StockEntry.COLUMN_STOCK_TODAYS_LOW,
                    StockEntry.COLUMN_STOCK_HOLDER,
            };
            String selection = StockEntry.TABLE_NAME+"."+ StockEntry.COLUMN_STOCK_HOLDER+"=?";
            String[] selectionArgs = {
                    ""+user_id
            };

            return new CursorLoader(MainActivity.this,StockEntry.CONTENT_URI,projections,selection,selectionArgs, StockEntry.COLUMN_STOCK_NAME+" ASC");
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
            stockCursorAdapter.notify(sData);
          //  stockCursorAdapter = new StockCursorAdapter(MainActivity.this,sData);
            Log.e("CursorAdapter","Here");
            stockData = sData;
          //getSupportLoaderManager().initLoader(STOCK_PARSE_LOADER,null,arrayListLoaderCallbacks);
           // recyclerView.setAdapter(stockCursorAdapter);
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
        intent.setData(Uri.withAppendedPath(StockEntry.CONTENT_URI, "" + (clickedItemID)));
        bundle.putString("user_id", "" + user_id);
        bundle.putString("user_name", user_name);
        intent.putExtras(bundle);
       // Log.e("Hey There", "" + Uri.withAppendedPath(StockEntry.CONTENT_URI, "" + clickedItemID));
        startActivity(intent);
    }

    private LoaderCallbacks<ArrayList<StockData>> arrayListLoaderCallbacks = new LoaderCallbacks<ArrayList<StockData>>() {
        @Override
        public android.support.v4.content.Loader<ArrayList<StockData>> onCreateLoader(int id, Bundle args) {
            return new StockParseAsyncLoader(MainActivity.this,stockData);
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<ArrayList<StockData>> loader, ArrayList<StockData> data) {
           // updateStock(data);
            mSwipeRefresh.setRefreshing(false);
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<ArrayList<StockData>> loader) {

        }
    };
    private void updateStock(ArrayList<StockData> data){
        if(data==null){
            return;
        }
        for(int i=0;i<data.size();i++){
          //  Log.e("mainact",""+data.size());
            Uri uri = Uri.withAppendedPath(StockEntry.CONTENT_URI, "" +(data.get(i)).getStockID());
            ContentValues values = new ContentValues();
            values.put(StockEntry.COLUMN_STOCK_NAME,data.get(i).getName());
            values.put(StockEntry.COLUMN_STOCK_CURRENT_PRICE,data.get(i).getCurrentPrice());
            values.put(StockEntry.COLUMN_STOCK_CHANGE,data.get(i).getChange());
            values.put(StockEntry.COLUMN_STOCK_UPDATED, data.get(i).getDate());
            values.put(StockEntry.COLUMN_STOCK_TODAYS_HIGH, data.get(i).getHigh());
            values.put(StockEntry.COLUMN_STOCK_TODAYS_LOW, data.get(i).getLow());

            int rowsAffetcted = getContentResolver().update(uri, values, null,null);
            if (rowsAffetcted == 0) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with updating", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
