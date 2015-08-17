package com.scribblernotebooks.scribblernotebooks.Services;

import android.app.IntentService;
import android.content.Intent;

import com.scribblernotebooks.scribblernotebooks.Handlers.DatabaseHandler;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.DealListResponse;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ParseJson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ClaimedDealsRetriever extends IntentService {

    public ClaimedDealsRetriever(){
        super("ClaimedDealsRetriever");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            URL url = new URL(Constants.ServerUrls.claimDealList);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestProperty("Authorization", "Bearer " + Constants.getUser(this).getToken());


            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s=reader.readLine();
//            Log.e("Claimed Deal Response","Response: "+s);

            DealListResponse dealListResponse= ParseJson.parseClaimedDeals(s);

            if(dealListResponse==null){
//                Log.e("ClaimedDeals","No Deals Claimed Yet");
                return;
            }
            ArrayList<Deal> deals=dealListResponse.getDealList();
            DatabaseHandler handler=new DatabaseHandler(this);
            for(Deal deal:deals){
                handler.addClaimedDeal(deal);
            }
            handler.close();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
