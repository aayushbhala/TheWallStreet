package com.example.android.thestreet;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.android.thestreet.data.StockContract;

import java.util.ArrayList;

public class CreateUserActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private LinearLayout linearLayout;
    private ArrayList<String> userNameArrayList;
    private ArrayList<User> arrayList;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLoaderManager().initLoader(0, null, this);
        linearLayout = (LinearLayout) findViewById(R.id.addUser);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateUserActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });
        userNameArrayList = new ArrayList<>();
        arrayList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listUser);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNameArrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CreateUserActivity.this, AddUserActivity.class);
                Log.e("CreateUser","Here");
                Bundle bundle = new Bundle();
                bundle.putString("user_name", userNameArrayList.get(position));
                intent.putExtras(bundle);
                intent.setData(Uri.withAppendedPath(StockContract.UserProfile.CONTENT_URI, "" + arrayList.get(position).getUser_id()));
                startActivity(intent);
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection  = {
                StockContract.UserProfile._ID,
                StockContract.UserProfile.COLUMN_USER_NAME
        };
        return new CursorLoader(CreateUserActivity.this, StockContract.UserProfile.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        userNameArrayList.clear();
        arrayList.clear();
        while(data.moveToNext()){
            String name  = data.getString(data.getColumnIndex(StockContract.UserProfile.COLUMN_USER_NAME));
            int id   = data.getInt(data.getColumnIndex(StockContract.UserProfile._ID));
            userNameArrayList.add(name);
            arrayList.add(new User(id,name));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,userNameArrayList);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
