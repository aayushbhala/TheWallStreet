package com.example.android.thestreet;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MAHE on 14-May-17.
 */
public class SearchStockAdapter extends ArrayAdapter<SearchStock> {
    public SearchStockAdapter(Activity context,ArrayList<SearchStock> wadapter){
        super(context,0,wadapter);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView ==null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.search_adapter,parent,false);
        }

        SearchStock currentWord = (SearchStock) getItem(position);

        TextView idTextView = (TextView) listItemView.findViewById(R.id.stock_id);
        idTextView.setText(""+currentWord.getId());

        TextView nameTextView = (TextView) listItemView.findViewById(R.id.stock_name);
        nameTextView.setText(currentWord.getName());

        TextView nTextView = (TextView) listItemView.findViewById(R.id.text_circle);
        nTextView.setText(""+currentWord.getName().charAt(0));

        TextView indusTextView = (TextView) listItemView.findViewById(R.id.industry);
        indusTextView.setText(currentWord.getIndustry());

        return listItemView;
    }
}
