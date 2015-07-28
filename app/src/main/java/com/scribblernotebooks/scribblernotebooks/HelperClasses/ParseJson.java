package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aanisha on 11-May-15.
 */

public class ParseJson {

    public static DealListResponse getParsedData(String response) {
        DealListResponse result = null;
        try {
            JSONObject jsonChild = new JSONObject(response);
            String success = jsonChild.optString("success");
            if (success.equalsIgnoreCase("false")) {
                return null;
            }
            JSONArray jsonArray;
            JSONObject data;
            int pageCount = 1, dealCount = 0, currentPage = 1;
            try {
                data = jsonChild.optJSONObject(Constants.TAG_DATA);
                jsonArray = data.optJSONArray(Constants.TAG_DEALS);
                pageCount = Integer.parseInt(data.optString("pages"));
                dealCount = Integer.parseInt(data.optString("count"));
                currentPage = Integer.parseInt(data.optString("currentPage"));
            } catch (Exception e) {
                jsonArray = jsonChild.optJSONArray(Constants.TAG_DATA);
            }

            ArrayList<Deal> dealsList = new ArrayList<>();
            int lengthJsonArr = jsonArray.length();

            for (int i = 0; i < lengthJsonArr; i++) {
                if (i == 0) {
                    dealsList = new ArrayList<>();
                }
                JSONObject jsonChildNode = jsonArray.getJSONObject(i);

                /****Fetch node values****/
                Deal deal = parseSingleDeal(jsonChildNode.toString());
                dealsList.add(deal);
            }


            Log.e("ParseJSON Response",pageCount+" "+dealCount+" "+currentPage+" "+dealsList.toString());
            result = new DealListResponse(dealsList, pageCount, dealCount, currentPage);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Deal parseSingleDeal(String jsonResponse) {
        Deal deal = new Deal();
        try {
            JSONObject jsonChild = new JSONObject(jsonResponse);
            String id, title, category, sdesp, ldesp, imgurl, code, couponCode;

            id = jsonChild.optString("_id");
            title = jsonChild.optString(Constants.TAG_DEAL_NAME);
            JSONObject categoryDetail = jsonChild.optJSONObject(Constants.TAG_CATEGORY);
            category = categoryDetail.optString("name");
            sdesp = jsonChild.optString(Constants.TAG_SHORT_DESCRIPTION);
            ldesp = jsonChild.optString(Constants.TAG_LONG_DESCRIPTION);
//            code = jsonChild.optString(Constants.TAG_CODE);
//            imgurl = "http://www.ucarecdn.com/" + jsonChild.optString(Constants.TAG_IMAGE_URL) + "/image.png";
            imgurl = jsonChild.optString(Constants.TAG_IMAGE_UUID);
            couponCode=jsonChild.optString(Constants.TAG_COUPON_CODE);
            deal.setId(id);
            deal.setTitle(title);
            deal.setCategory(category);
            deal.setImageUrl(imgurl);
            deal.setShortDescription(sdesp);
            deal.setLongDescription(ldesp);
            deal.setCouponCode(couponCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Log.e("Deal Object", deal.getId() + deal.getTitle() + deal.getCategory() + deal.getLongDescription() + deal.getShortDescription());
        return deal;
    }

    public static Deal parseSingleDealDetail(String jsonResponse) {
        Deal deal = new Deal();
        Log.e("JSON", "jsonResponse " + jsonResponse);
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject jsonChild = jsonObject.optJSONObject(Constants.TAG_DATA);
            String success = jsonObject.optString("success");

            String id, title, category, sdesp, ldesp, imgurl, couponCode;

            id = jsonChild.optString(Constants.TAG_ID);
            title = jsonChild.optString(Constants.TAG_DEAL_NAME);
            JSONObject categoryChild = jsonChild.optJSONObject(Constants.TAG_CATEGORY);
            category=categoryChild.optString("name");
            sdesp = jsonChild.optString(Constants.TAG_SHORT_DESCRIPTION);
            ldesp = jsonChild.optString(Constants.TAG_LONG_DESCRIPTION);
            // Image url http://www.ucarecdn.com/<image UUID>/image.png
//            if (jsonChild.optString(Constants.TAG_IMAGE_UUID).isEmpty() || jsonChild.optString(Constants.TAG_IMAGE_UUID) == null) {
//                imgurl = Constants.ServerUrls.websiteUrl + jsonChild.optString(Constants.TAG_IMAGE_URL);
//                imgurl = imgurl.replace("%5C", "/");
//                Log.e("Image Url",imgurl);
//            } else {
//                imgurl = "http://www.ucarecdn.com/" + jsonChild.optString(Constants.TAG_IMAGE_UUID) + "/image.png";
//            }
            imgurl = jsonChild.optString(Constants.TAG_IMAGE_UUID);
            couponCode=jsonChild.optString(Constants.TAG_COUPON_CODE);
            deal.setId(id);
            deal.setTitle(title);
            deal.setCategory(category);
            deal.setImageUrl(imgurl);
            deal.setShortDescription(sdesp);
            deal.setLongDescription(ldesp);
            deal.setCouponCode(couponCode);
            Log.e("Done", "Done");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("Deal Object", deal.getId() + deal.getTitle() + deal.getCategory() + deal.getLongDescription() + deal.getShortDescription());
        return deal;
    }

    public static HashMap<String, String> parseLoginResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            HashMap<String, String> parsedData = new HashMap<>();
            parsedData.put(Constants.POST_SUCCESS, object.optString(Constants.POST_SUCCESS));

            JSONObject data = object.optJSONObject(Constants.POST_DATA);
//            parsedData.put(Constants.POST_ERROR, object.optString(Constants.POST_ERROR));
//            parsedData.put(Constants.POST_MIXPANELID, object.optString(Constants.POST_MIXPANELID));
            parsedData.put(Constants.PREF_DATA_ID,data.getString("_id"));
            parsedData.put(Constants.POST_MIXPANELID, data.getString(Constants.POST_MIXPANELID));
            parsedData.put(Constants.POST_EMAIL, data.getString(Constants.POST_EMAIL));
            parsedData.put(Constants.POST_MOBILE, data.getString(Constants.POST_MOBILE));
            parsedData.put(Constants.POST_TOKEN, data.optString(Constants.POST_TOKEN));

            try {
                JSONObject collegeObject = data.getJSONObject(Constants.POST_COLLEGE);
                parsedData.put(Constants.POST_COLLEGE, collegeObject.optString("name"));
            }catch (Exception e){
                e.printStackTrace();
            }

            parsedData.put(Constants.POST_IS_NEW, data.optString(Constants.POST_IS_NEW));
            JSONObject name = data.getJSONObject(Constants.POST_NAME);

            try {
                JSONArray dealsLiked = data.getJSONArray("dealsLiked");
                String liked=dealsLiked.toString();
                parsedData.put("likedDeals",liked);
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                JSONArray sharedDeals = data.getJSONArray("dealsShared");
                String shared=sharedDeals.toString();
                parsedData.put("sharedDeals",shared);
            }catch (Exception e){
                e.printStackTrace();
            }


            parsedData.put(Constants.POST_NAME, name.getString(Constants.POST_NAME_FIRST) + " " + name.getString(Constants.POST_NAME_LAST));
            return parsedData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, String> parseSignupResponse(String response) {
        HashMap<String, String> parsedData=null;
        try {
            Log.e("sigup", "parsesignupresponse");
            JSONObject object = new JSONObject(response);
            parsedData = new HashMap<>();

            parsedData.put(Constants.POST_SUCCESS, object.optString(Constants.POST_SUCCESS));
            JSONObject data = object.optJSONObject(Constants.POST_DATA);

            parsedData.put(Constants.PREF_DATA_ID, data.getString("_id"));
            parsedData.put(Constants.POST_MIXPANELID, data.getString(Constants.POST_MIXPANELID));
            parsedData.put(Constants.POST_EMAIL, data.getString(Constants.POST_EMAIL));
            parsedData.put(Constants.POST_MOBILE, data.getString(Constants.POST_MOBILE));
            parsedData.put(Constants.POST_TOKEN, data.optString(Constants.POST_TOKEN));
            parsedData.put(Constants.POST_IS_NEW, data.optString(Constants.POST_IS_NEW));
            parsedData.put(Constants.POST_MOBILE_VERIFY,data.optString(Constants.POST_MOBILE_VERIFY));
            JSONObject name = data.getJSONObject(Constants.POST_NAME);
            try {
                parsedData.put(Constants.POST_NAME, name.getString(Constants.POST_NAME_FIRST) + " " + name.getString(Constants.POST_NAME_LAST));
            } catch (JSONException e) {
                parsedData.put(Constants.POST_NAME, name.getString(Constants.POST_NAME_FIRST));
            }

            return parsedData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parsedData;
    }
}
