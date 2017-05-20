package com.example.android.thestreet;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.thestreet.data.StockContract;

import java.util.ArrayList;

/**
 * Created by MAHE on 13-May-17.
 */
public class StockCursorAdapter extends RecyclerView.Adapter<StockCursorAdapter.ViewHolder> {
    TextView mNameTextView;
    TextView mCurrentPriceTextView;
    TextView mActualChangeTextView;
    TextView mRelativeChangeTextView;
    TextView mDateTextView;
    TextView mTodaysHighTextView;
    TextView mTodaysLowTextView;
    private final ListItemClickListener mOnClickListener;
    StockData stockData;
    ArrayList<StockData> extracted_data;
    public StockCursorAdapter(ListItemClickListener mOnClickListener,ArrayList<StockData> data) {
        this.mOnClickListener = mOnClickListener;
        extracted_data = data;
    }
    public void notify(ArrayList<StockData> list){
        this.extracted_data = list;
        notifyDataSetChanged();
    }

    public ArrayList<StockData> swapData(Cursor data) {
        ArrayList<StockData> stockData = new ArrayList<>();
        if(data==null){
            return null;
        }
        if(data.moveToFirst()) {
            do {
                StockData sdata = new StockData(data.getString(data.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_NAME)),
                        data.getInt(data.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_ID)),
                        data.getDouble(data.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_PURCHASE_PRICE)),
                        data.getDouble(data.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_CURRENT_PRICE)),
                        data.getInt(data.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_VOLUME)),
                        data.getString(data.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_UPDATED)),
                        data.getDouble(data.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_CHANGE)),
                        data.getDouble(data.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_TODAYS_HIGH)),
                        data.getDouble(data.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_TODAYS_LOW)),
                        data.getInt(data.getColumnIndex(StockContract.StockEntry.COLUMN_STOCK_HOLDER)));
                stockData.add(sdata);
            } while (data.moveToNext());
            return stockData;
        }
        return null;
    }

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemID);
    }

    @Override
    public StockCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.stockdisplayadapter;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StockCursorAdapter.ViewHolder holder, int position) {

        stockData = extracted_data.get(position);
        mNameTextView.setText(stockData.getName());
        mCurrentPriceTextView.setText("" + stockData.getCurrentPrice());
        mDateTextView.setText(stockData.getDate());
        mActualChangeTextView.setText(""+stockData.getChange());
        if(stockData.getChange()>0){
            mActualChangeTextView.setTextColor(Color.GREEN);
        }else{
            mActualChangeTextView.setTextColor(Color.RED);
        }
        double amt = (stockData.getCurrentPrice()-stockData.getPurchasePrice());
        if(amt>0){
            mRelativeChangeTextView.setTextColor(Color.GREEN);
        }else{
            mRelativeChangeTextView.setTextColor(Color.RED);
        }
        mRelativeChangeTextView.setText(String.format("%.2f", amt));
        mTodaysHighTextView.setText(""+stockData.getHigh());
        mTodaysLowTextView.setText(""+stockData.getLow());
        holder.itemView.setTag(extracted_data.get(position).getStockID());

    }

    @Override
    public int getItemCount() {
        if(extracted_data!=null){
            return extracted_data.size();
        }
        else{
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            mActualChangeTextView = (TextView) itemView.findViewById(R.id.actualchangeTextView);
            mRelativeChangeTextView = (TextView) itemView.findViewById(R.id.changeTextView);
            mDateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            mCurrentPriceTextView = (TextView) itemView.findViewById(R.id.currentPriceTextView);
            mNameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            mTodaysHighTextView = (TextView) itemView.findViewById(R.id.today_high);
            mTodaysLowTextView = (TextView) itemView.findViewById(R.id.today_low);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int clickedItemIndex = getAdapterPosition();
            mOnClickListener.onListItemClick(extracted_data.get(clickedItemIndex).getStockID());
        }
    }
}
