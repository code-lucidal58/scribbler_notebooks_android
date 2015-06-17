package com.scribblernotebooks.scribblernotebooks.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jibin_ism on 14-Jun-15.
 */
public class UserHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="userDatabase";
    public static final int DATABASE_VERSION=1;
    public static final String DEAL_ID="dealId";

    SQLiteDatabase db;
    Context context;

    public static final String TABLE_NAME="userLikedDeals";
    public static final String SHARED_TABLE_NAME="userLikedDeals";
    final String CREATE_TABLE="CREATE TABLE  IF NOT EXISTS "+TABLE_NAME+" (" +
            DEAL_ID+" VARCHAR(255))";
    final String CREATE_SHARED_TABLE="CREATE TABLE  IF NOT EXISTS "+SHARED_TABLE_NAME+" (" +
            DEAL_ID+" VARCHAR(255))";

    public UserHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();

        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_SHARED_TABLE);

        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_SHARED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addDeal(String dealId){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DEAL_ID,dealId);
        db.insertOrThrow(TABLE_NAME,null,contentValues);
    }

    public void removeDeal(String dealId){
        db.delete(TABLE_NAME, DEAL_ID + "= ?", new String[]{dealId});
    }

    public boolean findDeal(String dealId){
        Cursor cursor=db.query(TABLE_NAME, new String[]{DEAL_ID},DEAL_ID+"=?",new String[]{dealId},null,null,null);
        Boolean b=cursor.moveToFirst();
        cursor.close();
        return b;
    }
    public void addSharedDeal(String dealId){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DEAL_ID,dealId);
        db.insertOrThrow(SHARED_TABLE_NAME,null,contentValues);
    }

    public boolean findSharedDeal(String dealId){
        Cursor cursor=db.query(SHARED_TABLE_NAME, new String[]{DEAL_ID},DEAL_ID+"=?",new String[]{dealId},null,null,null);
        Boolean b=cursor.moveToFirst();
        cursor.close();
        return b;
    }

}
