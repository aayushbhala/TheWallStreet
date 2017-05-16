package com.example.android.thestreet;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

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
        for(int i=0;i<stockData.size();i++) {
            String name_class = "";
            String price = "";
            String date = "";
            String temp = url+stockData.get(i).getStockID();
            try {
                Document doc = Jsoup.connect(temp).get();
                Element base_price = doc.getElementById("Bse_Prc_tick");
                price = base_price.text();
                Log.e("STOCK PARSE",""+i);
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
                StockData myData = new StockData(name_class,stockData.get(i).getStockID(), stockData.get(i).getPurchasePrice(), Double.parseDouble(price), stockData.get(i).getVolume(), date, db);
                this.result.add(myData);
            } catch (IOException e) {
                Log.d("ERROR IN DEBUG", "" + e);
            }

        }
        return result;
    }

    @Override
    public void deliverResult(ArrayList<StockData> data) {
        super.deliverResult(data);

    }
}
