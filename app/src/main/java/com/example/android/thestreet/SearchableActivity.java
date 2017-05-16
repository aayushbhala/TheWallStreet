package com.example.android.thestreet;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {
    private ArrayList<SearchStock> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        arrayList = new ArrayList<>();
        handleIntent(getIntent());
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.e("sra",query);
            ListView listView = (ListView) findViewById(R.id.list);
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.emptyView);
            listView.setEmptyView(layout);
            InputStream inputStream = getResources().openRawResource(R.raw.listofscripts);
            CSVFile csvFile = new CSVFile(inputStream,query);
            ArrayList<String[]> scoreList = csvFile.read();
            for(int i=0;i<scoreList.size();i++) {
                arrayList.add(new SearchStock(Integer.parseInt(scoreList.get(i)[0]),scoreList.get(i)[2],scoreList.get(i)[7]));
            }
            SearchStockAdapter adapter = new SearchStockAdapter(SearchableActivity.this,arrayList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent1 = new Intent(SearchableActivity.this, StockEditorActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", "" + arrayList.get(position).getId());
                    bundle.putString("Name", arrayList.get(position).getName());
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                    finish();

                }
            });


        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
    public class CSVFile {
        InputStream inputStream;
        private String key;

        public CSVFile(InputStream inputStream, String search) {
            this.inputStream = inputStream;
            key = search;
        }

        public ArrayList<String[]> read() {
            ArrayList<String[]> resultList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String csvLine;
                while ((csvLine = reader.readLine()) != null) {
                    String[] row = csvLine.split(",");
                    if (row[2].toLowerCase().contains(key.toLowerCase())) {
                        resultList.add(row);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException("Error in reading CSV file: " + ex);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Error while closing input stream: " + e);
                }
            }
            return resultList;
        }
    }
}
