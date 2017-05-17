package com.example.android.thestreet;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.thestreet.data.StockContract;

public class AddUserActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private Button deleteButton;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        uri = getIntent().getData();
        Bundle bundle = getIntent().getExtras();
        editText = (EditText) findViewById(R.id.nameEditText);
        button = (Button) findViewById(R.id.addUserButton);
        deleteButton = (Button) findViewById(R.id.deleteUser);
        deleteButton.setVisibility(View.INVISIBLE);
        if (uri != null) {
            this.setTitle("Change User Details");
            button.setText("Update This");
            deleteButton.setVisibility(View.VISIBLE);
            if(bundle!=null){
                editText.setText(bundle.getString("user_name"));
            }
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertUser();
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
                finish();
            }
        });

    }
    private void deleteUser(){
        int rowsAffected = getContentResolver().delete(uri,null,null);
        if(rowsAffected!=0){
            Toast.makeText(this,"deleted",Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this,"Error while deleting..!!",Toast.LENGTH_SHORT).show();
    }

    private void insertUser() {
        String name = editText.getText().toString().trim();
        if(name.equals("")){
            editText.setError("This is required");
        }
        ContentValues values = new ContentValues();
        values.put(StockContract.UserProfile.COLUMN_USER_NAME,name);
        if(this.uri!=null){
            int rowsAffetcted = getContentResolver().update(this.uri, values, null, null);
            if (rowsAffetcted == 0) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with updating " + name, Toast.LENGTH_SHORT).show();
            }
        }else{
            Uri uri = getContentResolver().insert(StockContract.UserProfile.CONTENT_URI, values);
            long newRowId = ContentUris.parseId(uri);
            if (newRowId == -1) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with saving " + name, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
