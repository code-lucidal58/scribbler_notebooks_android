package com.scribblernotebooks.scribblernotebooks.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.scribblernotebooks.scribblernotebooks.Activities.NavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.BroadcastRecievers.GcmBroadcastReceiver;
import com.scribblernotebooks.scribblernotebooks.Handlers.NotificationDataHandler;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONArray;
import org.json.JSONObject;


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
        if(extras==null)
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
                if(extras.getString("message").isEmpty())
                    return;
                sendNotification(extras.getString("message"));
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NavigationDrawer.class), 0);



        NotificationDataHandler notifHandler=new NotificationDataHandler(this);
        notifHandler.open();
        String text="";
        try {
            JSONObject jsonObject = new JSONObject(msg);
            JSONArray jsonArray = jsonObject.optJSONArray("notification");
            JSONObject jsonChild = jsonArray.optJSONObject(0);
            int id = Integer.parseInt(jsonChild.optString("id"));
            text = jsonChild.optString("text");
            String url = jsonChild.optString("imgurl");
            notifHandler.insertData(NotificationDataHandler.TABLE_NAME_DEFAULT, id, text, url);
            notifHandler.insertData(NotificationDataHandler.TABLE_NAME_ALL, id, text, url);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        notifHandler.close();


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.n1)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(text))
                        .setContentText(text);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());


    }
}
