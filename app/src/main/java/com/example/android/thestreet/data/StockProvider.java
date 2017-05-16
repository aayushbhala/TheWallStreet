package com.example.android.thestreet.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.thestreet.data.StockContract.StockEntry;

/**
 * Created by MAHE on 13-May-17.
 */
public class StockProvider extends ContentProvider {
    public static final String LOG_TAG = StockProvider.class.getSimpleName();
    private static final int STOCK = 100;
    private static final int STOCK_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private StockDbHelper mDbHelper;
    static {
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY,"stocks",STOCK);
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY,"stocks/#",STOCK_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new StockDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case STOCK: cursor = db.query(StockEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                        break;
            case STOCK_ID: selection = StockEntry.COLUMN_STOCK_ID+"=?";
                           selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                            cursor = db.query(StockEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                            break;
            default:
                throw new IllegalArgumentException("Cannot query unnknown URI "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case STOCK: return StockEntry.CONTENT_LIST_TYPE;
            case STOCK_ID: return StockEntry.CONTENT_ITEM_TYPE;
            default:throw new IllegalArgumentException("Unknown Uri "+uri+" with match "+match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case STOCK: return insertStock(uri,values);
            default:throw new IllegalArgumentException("Cannot insert unknown URI "+uri);
        }
    }

    private Uri insertStock(Uri uri, ContentValues values) {
        SQLiteDatabase db  = mDbHelper.getWritableDatabase();
        String name = values.getAsString(StockEntry.COLUMN_STOCK_NAME);
        int stock_id  = values.getAsInteger(StockEntry.COLUMN_STOCK_ID);
        int volume = values.getAsInteger(StockEntry.COLUMN_STOCK_VOLUME);
        double purchase_price = values.getAsDouble(StockEntry.COLUMN_STOCK_PURCHASE_PRICE);
        if(name==null){
            throw new IllegalArgumentException("Stock requires a name ");
        }
        if(stock_id<=0){
            throw new IllegalArgumentException("Invalid ");
        }
        if(volume<0){
            throw new IllegalArgumentException("Invalid ");
        }
        if (purchase_price<0){
            throw new IllegalArgumentException("Invalid amount");
        }
        long newRowID  = db.insert(StockEntry.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,newRowID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case STOCK:return deleteStocks(uri, selection, selectionArgs);
            case STOCK_ID:selection = StockEntry.COLUMN_STOCK_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteStocks(uri, selection, selectionArgs);
            default:throw new IllegalArgumentException("Cannot delete uri "+uri);
        }

    }
    private int deleteStocks(Uri uri,String selection,String[] selectionArgs){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsdel = db.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
        if(rowsdel!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsdel;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        //Log.v(LOG_TAG,"here here "+match);
        switch (match){
            case STOCK: return updateStocks(uri, values, selection, selectionArgs);
            case STOCK_ID:selection = StockEntry.COLUMN_STOCK_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateStocks(uri, values, selection, selectionArgs);
            default:throw new IllegalArgumentException("Update is not supported "+ uri);
        }
    }
    private int updateStocks(Uri uri,ContentValues contentValues,String selection,String[] selectionArgs){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdate = db.update(StockEntry.TABLE_NAME,contentValues,selection,selectionArgs);
        if(rowsUpdate!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdate;
    }
}