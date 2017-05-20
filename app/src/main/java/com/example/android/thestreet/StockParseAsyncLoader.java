package com.example.android.thestreet;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.thestreet.data.StockContract;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by MAHE on 12-May-17.
 */
public class StockParseAsyncLoader extends AsyncTaskLoader<ArrayList<StockData>> {
    private ArrayList<StockData> stockData;
    private ArrayList<StockData> result;
    public StockParseAsyncLoader(Context context,ArrayList<StockData> data) {
        super(context);
        this.stockData = data;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<StockData> loadInBackground() {
        String url = "http://www.moneycontrol.com/stocks/cptmarket/compsearchnew.php?search_data=&cid=&mbsearch_str=&topsearch_type=1&search_str=";
        result = new ArrayList<>();
        if(stockData==null){
            return null;
        }
        for(int i=0;i<stockData.size();i++) {
            String name_class = "";
            String price = "";
            String date = "";
            String low = "";
            String high = "";
            String temp = url+stockData.get(i).getStockID();
          //  Log.e("STOCK PARSE",temp);
            try {
                Connection.Response response = Jsoup.connect(temp).userAgent("Mozilla").execute();
                String body = response.body();
                Document doc = Jsoup.parse(body);
                Element base_price = doc.getElementById("Bse_Prc_tick");
                price = base_price.text();
                Log.e("STOCK PARSE",price);
                Log.e("STOCK PARSE",""+i);
                Element today_low = doc.getElementById("b_low_sh");
                low = today_low.text();
                Log.e("STOCK PARSE",""+low);
                Element today_high = doc.getElementById("b_high_sh");
                high = today_high.text();
                Log.e("STOCK PARSE",""+high);
                Elements stock_name = doc.getElementsByClass("b_42");
                name_class = stock_name.get(0).text();
                Log.e("STOCK PARSE",name_class);
                date = doc.getElementById("bse_upd_time").text();
                Element change = doc.getElementById("b_changetext");
                String ch = change.text();
                if(ch.substring(0,3).equals("0.00")){
                    ch = ch.substring(0,3);
                }else{
                    ch = ch.substring(0,4);
                }
                Double db = Double.parseDouble(ch);
                Log.e("STOCK PARSE", "" + MainActivity.user_id);
                StockData myData = new StockData(name_class,stockData.get(i).getStockID(), stockData.get(i).getPurchasePrice(), Double.parseDouble(price), stockData.get(i).getVolume(), date, db,Double.parseDouble(high),Double.parseDouble(low),MainActivity.user_id);
                this.result.add(myData);
                Runnable r = new MyThread(myData);
                new Thread(r).start();

            } catch (IOException e) {
                Log.d("ERROR IN DEBUG", "" + e);
            }

        }

        return result;
    }

    public class MyThread implements Runnable{
        StockData myData;
        public MyThread(StockData data){
         myData = data;
        }
        @Override
        public void run() {
            Uri uri = Uri.withAppendedPath(StockContract.StockEntry.CONTENT_URI, "" +(myData).getStockID());
            ContentValues values = new ContentValues();
            values.put(StockContract.StockEntry.COLUMN_STOCK_NAME,myData.getName());
            values.put(StockContract.StockEntry.COLUMN_STOCK_CURRENT_PRICE, myData.getCurrentPrice());
            values.put(StockContract.StockEntry.COLUMN_STOCK_CHANGE, myData.getChange());
            values.put(StockContract.StockEntry.COLUMN_STOCK_UPDATED, myData.getDate());
            values.put(StockContract.StockEntry.COLUMN_STOCK_TODAYS_HIGH, myData.getHigh());
            values.put(StockContract.StockEntry.COLUMN_STOCK_TODAYS_LOW, myData.getLow());

            int rowsAffetcted = getContext().getContentResolver().update(uri, values, null, null);
            if (rowsAffetcted == 0) {
                // If the row ID is -1, then there was an error with insertion.
                //Toast.makeText(this, "Error with updating", Toast.LENGTH_SHORT).show();
                Log.e("StockParseupdate","error in updating");
            }
        }
    }
}
