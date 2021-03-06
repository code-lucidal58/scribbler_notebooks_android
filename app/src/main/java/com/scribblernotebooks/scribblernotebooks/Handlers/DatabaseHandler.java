package com.scribblernotebooks.scribblernotebooks.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;

import java.util.ArrayList;

/**
 * Created by Aanisha on 26-May-15.
 * Database based functions declared here
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG="DatabaseHandler";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dealsManager";
    private static final String TABLE_DEALS = "deals";
    private static final String TABLE_CLAIMED_DEALS="claimedDeals";
    private static final String TABLE_UNUSED_DEALS="unUsedDeals";
    ListUpdateListener listUpdateListener;
    SQLiteDatabase db;
    Context context;
    final String CREATE_DEALS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_DEALS + "("
            + Constants.TAG_ID + " VARCHAR(255) PRIMARY KEY," + Constants.TAG_DEAL_NAME + " VARCHAR(50),"
            + Constants.TAG_CATEGORY + " VARCHAR(50)," + Constants.TAG_SHORT_DESCRIPTION + " VARCHAR(100),"
            + Constants.TAG_IMAGE_URL + " VARCHAR(500)," + Constants.TAG_IF_FAVOURITE + " VARCHAR(10),"
            + Constants.TAG_IF_FEATURED + " VARCHAR(10)" + ")";

    final String CREATE_CLAIMED_DEALS_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_CLAIMED_DEALS+"("+
            Constants.TAG_ID+" VARCHAR(255) PRIMARY KEY,"+
            Constants.TAG_DEAL_NAME+" VARCHAR(50),"+
            Constants.TAG_CATEGORY+" VARCHAR(50),"+
            Constants.TAG_LONG_DESCRIPTION+" VARCHAR(500),"+
            Constants.TAG_IMAGE_URL+" VARCHAR(500),"+
            Constants.TAG_COUPON_CODE+" VARCHAR(50)"+
            ")";

    final String CREATE_UNUSED_DEALS_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_UNUSED_DEALS+"("+
            Constants.TAG_ID+" VARCHAR(255) PRIMARY KEY,"+
            Constants.TAG_DEAL_NAME+" VARCHAR(50),"+
            Constants.TAG_CATEGORY+" VARCHAR(50),"+
            Constants.TAG_LONG_DESCRIPTION+" VARCHAR(500),"+
            Constants.TAG_IMAGE_URL+" VARCHAR(500),"+
            Constants.TAG_COUPON_CODE+" VARCHAR(50)"+
            ")";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
        db.execSQL(CREATE_CLAIMED_DEALS_TABLE);
        db.execSQL(CREATE_DEALS_TABLE);
        db.execSQL(CREATE_UNUSED_DEALS_TABLE);
        this.context=context;
    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLAIMED_DEALS_TABLE);
        db.execSQL(CREATE_DEALS_TABLE);
        db.execSQL(CREATE_UNUSED_DEALS_TABLE);
    }

    //upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEALS);
        onCreate(db);
    }

    // Adding new deal
    public void addDeal(Deal deal) {
        ContentValues values = new ContentValues();
        values.put(Constants.TAG_ID, deal.getId());
        values.put(Constants.TAG_DEAL_NAME, deal.getTitle());
        values.put(Constants.TAG_CATEGORY, deal.getCategory());
        values.put(Constants.TAG_SHORT_DESCRIPTION, deal.getShortDescription());
        values.put(Constants.TAG_IMAGE_URL, deal.getImageUrl());
        values.put(Constants.TAG_IF_FAVOURITE, String.valueOf(deal.isFavorited()));
        values.put(Constants.TAG_IF_FEATURED, String.valueOf(deal.isFeatured()));

        db.insert(TABLE_DEALS, null, values);
    }

    // Getting single deal
    public Deal getDeal(String id) {
        String Query = "Select * from " + TABLE_DEALS + " where " + Constants.TAG_ID + " = " + "'"+ id +"'";
        Cursor cursor = db.rawQuery(Query, null);

        Deal deal = null;
        if (cursor.moveToFirst()) {
            deal = new Deal(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),"",
                    Boolean.parseBoolean(cursor.getString(5)), Boolean.parseBoolean(cursor.getString(6)));
        }
        cursor.close();
        return deal;
    }

    // Getting All Deals
    public ArrayList<Deal> getAllDeals() {
        ArrayList<Deal> dealList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_DEALS;

        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            do {
                Deal deal = new Deal();
                deal.setId(cursor.getString(0));
                deal.setTitle(cursor.getString(1));
                deal.setCategory(cursor.getString(2));
                deal.setImageUrl(cursor.getString(3));
                deal.setShortDescription(cursor.getString(4));
                deal.setIsFav(Boolean.parseBoolean(cursor.getString(5)));
                deal.setIsFeatured(Boolean.parseBoolean(cursor.getString(6)));

                dealList.add(deal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dealList;
    }

    // Getting deals Count
    public int getDealsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DEALS;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    // Updating single deal
    public int updateDeal(Deal deal) {
        ContentValues values = new ContentValues();
        values.put(Constants.TAG_ID, deal.getId());
        values.put(Constants.TAG_DEAL_NAME, deal.getTitle());
        values.put(Constants.TAG_CATEGORY, deal.getCategory());
        values.put(Constants.TAG_SHORT_DESCRIPTION, deal.getShortDescription());
        values.put(Constants.TAG_IMAGE_URL, deal.getImageUrl());
        values.put(Constants.TAG_IF_FAVOURITE, String.valueOf(deal.isFavorited()));
        values.put(Constants.TAG_IF_FEATURED, String.valueOf(deal.isFeatured()));

        return db.update(TABLE_DEALS, values, Constants.TAG_ID + " = '" + deal.getId() + "'", null);
    }

    // Deleting single deal
    public void deleteDeal(Deal deal) {
        db.delete(TABLE_DEALS, Constants.TAG_ID + "=" + deal.getId(), null);
    }

    //Deleting table
    public void dropDatabase() {
        context.deleteDatabase(DATABASE_NAME);
    }

    /**Add Deals**/
    public long addClaimedDeal(Deal deal){
       return addDeal(deal,TABLE_CLAIMED_DEALS);
    }
    public long addUnusedDeal(Deal deal){
        return addDeal(deal,TABLE_UNUSED_DEALS);
    }
    private long addDeal(Deal deal, String table){
        Log.e(TAG,"Adding deal in table "+table);
        Cursor c=db.query(table,null,Constants.TAG_ID+"=?", new String[]{deal.getId()}, null, null, null);
        if(c.moveToFirst()){
            return 0;
        }
        c.close();
        ContentValues contentValues=new ContentValues();
        contentValues.put(Constants.TAG_ID,deal.getId());
        contentValues.put(Constants.TAG_DEAL_NAME,deal.getTitle());
        contentValues.put(Constants.TAG_CATEGORY,deal.getCategory());
        contentValues.put(Constants.TAG_LONG_DESCRIPTION,deal.getLongDescription());
        contentValues.put(Constants.TAG_IMAGE_URL,deal.getImageUrl());
        contentValues.put(Constants.TAG_COUPON_CODE, deal.getCouponCode());

//        Log.e("Claimed Deal", "Inserted: "+deal.getId());
        long l=-1;
        try{
            l = db.insertOrThrow(table,null,contentValues);
        }catch (Exception e){
            Log.e(TAG,"Claiming same deal again. error:"+e );
        }
        if(listUpdateListener!=null){
            try{
                listUpdateListener.OnClaimedDealListUpdated();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Log.e(TAG,"Finished adding deal in table: "+table+" result: "+l);
        return l;
    }


    /**Delete Deals**/
    public void deleteDeal(Deal deal, String table){
        db.delete(table, Constants.TAG_ID + "=?", new String[]{deal.getId()});
    }
    public void deleteClaimedDeal(Deal deal){
        deleteDeal(deal, TABLE_CLAIMED_DEALS);
    }
    public void deleteUnusedDeal(Deal deal){
        deleteDeal(deal, TABLE_UNUSED_DEALS);
    }


    /**Get Deals**/
    private ArrayList<Deal> getDealList(String table){
        Log.e(TAG,"Getting deal list from table: "+table);
        ArrayList<Deal> dealArrayList=new ArrayList<>();
        Cursor cursor = db.query(table,null,null,null,null,null,null);

        if (cursor.moveToFirst()) {
            Log.e(TAG,"getDealList: "+table+" first move");
            do {
                Deal deal = new Deal();
                deal.setId(cursor.getString(0));
                deal.setTitle(cursor.getString(1));
                deal.setCategory(cursor.getString(2));
                deal.setImageUrl(cursor.getString(4));
                deal.setLongDescription(cursor.getString(3));
                deal.setCouponCode(cursor.getString(5));
                dealArrayList.add(deal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dealArrayList;
    }

    public ArrayList<Deal> getClaimedDealList(){
        return getDealList(TABLE_CLAIMED_DEALS);
    }

    public ArrayList<Deal> getUnusedDealList(){
        return getDealList(TABLE_UNUSED_DEALS);
    }

    public interface ListUpdateListener{
        void OnClaimedDealListUpdated();
    }

    public void setListUpdateListener(ListUpdateListener listUpdateListener){
        this.listUpdateListener=listUpdateListener;
    }

}
