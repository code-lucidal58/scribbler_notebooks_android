package com.scribblernotebooks.scribblernotebooks.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class TokenRetriever extends IntentService {
    public TokenRetriever() {
        super("TokenRetriever");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.e("TokenRetriever","Retrieving token");
            URL url=new URL(Constants.ServerUrls.regenerateToken);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            User user=Constants.getUser(getApplicationContext());
            HashMap<String, String> d=new HashMap<>();
            d.put("token",user.getToken());
            Log.e("TokenRetriever","Saved Token = "+user.getToken());

            OutputStream os=connection.getOutputStream();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            writer.write(Constants.getPostDataString(d));
            writer.flush();
            writer.close();
            os.close();

            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JSONObject object=new JSONObject(reader.readLine());
            String t=object.optString("token");
            if(!t.equals("null") || !t.isEmpty()){
                user.setToken(t);
                Log.e("TokenRetriever", "New token " + t);
                Constants.saveUserDetails(getApplicationContext(),user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
