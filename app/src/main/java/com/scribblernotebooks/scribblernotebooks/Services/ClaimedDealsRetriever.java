package com.scribblernotebooks.scribblernotebooks.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.scribblernotebooks.scribblernotebooks.Handlers.DatabaseHandler;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.DealListResponse;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ParseJson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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

            int page=intent.getIntExtra("Page",1);
            Log.e("ClaimedDealsRetriever"," Page on start: "+page);
            URL url = new URL(Constants.ServerUrls.claimDealList+"?page="+page);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setChunkedStreamingMode(1024);
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestProperty("Authorization", "Bearer " + Constants.getUser(this).getToken());

            File ScribblerDirectory=new File(Environment.getExternalStorageDirectory(),"scribbler");
            InputStream inputStream=connection.getInputStream();
            ScribblerDirectory.mkdirs();
            if (inputStream != null) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(ScribblerDirectory, "tmpClaimed.tmp"));
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, bufferLength);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (FileNotFoundException fe) {
                    fe.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try{
                File in=new File(ScribblerDirectory,"tmpClaimed.tmp");
                BufferedReader reader=new BufferedReader(new FileReader(in));
                String line=null;
                String s="";
                while((line=reader.readLine())!=null){
                    s+=line;
                }
                reader.close();

                DealListResponse dealListResponse= ParseJson.parseClaimedDeals(s);

                if(dealListResponse==null){
                    return;
                }
                ArrayList<Deal> deals=dealListResponse.getDealList();
                DatabaseHandler handler=new DatabaseHandler(this);
                for(Deal deal:deals){
                    handler.addClaimedDeal(deal);
                }
                handler.close();
                Log.e("ClaimedDealsRetriever","Response object: "+dealListResponse.getPageCount()+" "+dealListResponse.getCurrentPage()+" "+dealListResponse.getDealCount());
                if (dealListResponse.getCurrentPage()<dealListResponse.getPageCount()){
                    Log.e("ClaimedDealsRetriever","Calling again");
                    Intent i=new Intent(this,ClaimedDealsRetriever.class);
                    i.putExtra("Page",dealListResponse.getCurrentPage()+1);
                    startService(i);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
//            Log.e("Claimed Deal Response", "Response: " + s);
//            Log.e("Claimed Deal Response", "Response: " + reader.readLine());



        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
