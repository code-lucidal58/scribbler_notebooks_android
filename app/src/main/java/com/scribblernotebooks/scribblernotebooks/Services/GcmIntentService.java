package com.scribblernotebooks.scribblernotebooks.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.scribblernotebooks.scribblernotebooks.Activities.NavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.BroadcastRecievers.GcmBroadcastReceiver;
import com.scribblernotebooks.scribblernotebooks.Handlers.NotificationDataHandler;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    String TAG = "GCMIntent";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        if (extras == null)
            return;
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {


                // Post notification of received message.
                try {
                    if (extras.getString("message").isEmpty())
                        return;
                    try {
                        executeOperations(extras.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "Received: " + extras.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    private void executeOperations(String response) throws Exception {
        /**
         * {type: "notification", notification:{id:"Some Id",notificationTitle:"Some Title", notificationText: "Notification Text", imgurl: "Image Url"}}
         * {type: "survey", survey:{id: "Some Id", questionText: "Question Text", options:["Option 1","Option 2"...]}}
         * {type: "setting", preferenceName: "Preference Name", preference:" The Preference to be edited", value: "The preference value to be saved"}
         */

        JSONObject responseObject = new JSONObject(response);
        String type = responseObject.getString("type");
        switch (type.toLowerCase()) {
            case "notification":
                handleNotification(responseObject);
                break;
            case "survey":
                handleSurvey(responseObject);
                break;
            case "setting":
                handleSetting(responseObject);
                break;
        }

    }

    private void handleSetting(JSONObject responseObject) throws Exception{
        String pref=responseObject.optString("preferenceName");
        String prefValue=responseObject.optString("value");
        String prefName=responseObject.optString("preference");
        SharedPreferences sharedPreferences=getSharedPreferences(pref, MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(prefName, prefValue);
        editor.apply();
    }

    private void handleSurvey(JSONObject responseObject) throws Exception {
        JSONObject surveyObject = responseObject.getJSONObject("survey");
        String id = surveyObject.optString("id");
        String question = surveyObject.optString("questionText");
        JSONArray optionsArray = surveyObject.getJSONArray("options");
        String[] optionList = new String[optionsArray.length()];
        for (int i = 0; i < optionsArray.length(); i++) {
            optionList[i] = optionsArray.getString(i);
        }
        sendSurvey(id, question, optionList);
    }

    private void sendSurvey(String id, String question, String[] optionList) {
        SharedPreferences.Editor surveyPref=getSharedPreferences(Constants.SURVEY_PREF_NAME,MODE_PRIVATE).edit();
        surveyPref.putBoolean(Constants.PREF_SURVEY_EXISTS, true);
        surveyPref.putString(Constants.PREF_SURVEY_ID, id);
        surveyPref.putString(Constants.PREF_SURVEY_QUESTION, question);
        Set<String> options=new HashSet<>(Arrays.asList(optionList));
        surveyPref.putStringSet(Constants.PREF_SURVEY_OPTIONS, options);
        surveyPref.apply();
    }


    private void handleNotification(JSONObject responseObject) throws Exception {
        JSONObject notificationObject = responseObject.getJSONObject("notification");
        Log.e("ResponseObject", notificationObject.toString());
        String id = notificationObject.optString("id");
        String title=notificationObject.optString("notificationTitle");
        String text = notificationObject.optString("notificationText");
        String imgurl = notificationObject.optString("imgurl");
        addNotification(id,title, text, imgurl);
    }

    private void addNotification(String id,String title, String text, String url) {
        Log.e("Notification", id +" text: "+text +" url: "+ url);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationDataHandler notifHandler = new NotificationDataHandler(this);

        notifHandler.open();
        notifHandler.insertData(NotificationDataHandler.TABLE_NAME_DEFAULT, id, text, url);
        notifHandler.insertData(NotificationDataHandler.TABLE_NAME_ALL, id, text, url);
        notifHandler.close();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.n4)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(text))
                        .setContentText(text)
                .setOnlyAlertOnce(true);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NavigationDrawer.class), 0);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }
}
