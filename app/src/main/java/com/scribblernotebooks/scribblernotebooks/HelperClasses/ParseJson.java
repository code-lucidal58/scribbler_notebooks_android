package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Aanisha on 11-May-15.
 */

public class ParseJson {

    public static ArrayList<Deal> getParsedData(String url,Context context, Boolean b){

//        DatabaseHandler db= new DatabaseHandler(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        ArrayList<Deal> dealsList=new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(url);
            JSONArray jsonArray = jsonObject.optJSONArray("deals");
            int lengthJsonArr= jsonArray.length();

            for(int i=0;i<lengthJsonArr;i++)
            {
                JSONObject jsonChildNode = jsonArray.getJSONObject(i);

                /****Fetch node values****/
                String id = jsonChildNode.optString(Constants.TAG_ID);
                String category = jsonChildNode.optString(Constants.TAG_CATEGORY);
                String dealName = jsonChildNode.optString(Constants.TAG_DEAL_NAME);
                String image = jsonChildNode.optString(Constants.TAG_IMAGE_URL);
                String shortDesc = jsonChildNode.optString(Constants.TAG_SHORT_DESCRIPTION);
                Log.e("row",b.toString());
                Deal deal = new Deal(id, dealName, category, shortDesc, image,"", false,b);
                Log.e("row",dealName+" is featured "+b+"   "+String.valueOf(b)+"   "+b.toString());
//                if(db.getDeal(id)!=null)
//                {
//                    db.updateDeal(deal);
//                }
//                else
//                {
//                    db.addDeal(deal);
//                }
                dealsList.add(deal);
            }

//            sdb.close();

        }catch(JSONException e)
        {
            e.printStackTrace();
        }
        return dealsList;
    }
}
