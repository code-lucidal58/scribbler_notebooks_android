package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.content.Context;
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



    /**
     * http://someshit-akasantony.rhcloud.com/deal/:id
     * {"id":"1",
     * "title":"Dominos",
     * "category":"Restaurent",
     * "shortdescription":"some short description",
     * "longdescription":"some long desc",
     * "imgURL":"https://c402277.ssl.cf1.rackcdn.com/photos/1102/images/carousel_small/Gorillas_7.31.2012_Our_closest_cousins_HI_105193.jpg"}
     * @param jsonResponse the above response
     * @return the deal object
     */

    public static Deal parseSingleDeal(String jsonResponse){
        Deal deal=new Deal();
//        Log.e("JSON","jsonResponse "+jsonResponse);
        try {
            JSONObject jsonChild=new JSONObject(jsonResponse);
//            JSONArray json=jsonObject.optJSONArray("");
//            JSONObject jsonChild=json.getJSONObject(0);
            String id,title,category,sdesp,ldesp,imgurl;

            id=jsonChild.optString("id");
            title=jsonChild.optString("title");
            category=jsonChild.optString("category");
            sdesp=jsonChild.optString("shortdescription");
            ldesp=jsonChild.optString("longdescription");
            imgurl=jsonChild.optString("imgURL");

//            Log.e("JSON","Fields "+id+title+category+sdesp+ldesp+imgurl);

            deal.setId(id);
            deal.setTitle(title);
            deal.setCategory(category);
            deal.setImageUrl(imgurl);
            deal.setShortDescription(sdesp);
            deal.setLongDescription(ldesp);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deal;
    }
}
