package com.scribblernotebooks.scribblernotebooks.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.scribblernotebooks.scribblernotebooks.Adapters.NotificationDrawerListAdapter;
import com.scribblernotebooks.scribblernotebooks.Handlers.NotificationDataHandler;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Notifications;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Aanisha on 08-Jul-15.
 */
public class NotificationsActivity extends AppCompatActivity {

    ListView notificationsListView;
    Button clearNotif;
    NotificationDrawerListAdapter drawerListAdapter;
    ArrayList<Notifications> notificationList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_right_drawer);

        notificationsListView = (ListView)findViewById(R.id.right_drawer);
        clearNotif=(Button)findViewById(R.id.clearNotifs);

        notificationList=retrieveNotifications();
        drawerListAdapter = new NotificationDrawerListAdapter(NotificationsActivity.this, notificationList);
        notificationsListView.setAdapter(drawerListAdapter);

        clearNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationDataHandler handler=new NotificationDataHandler(NotificationsActivity.this);
                handler.open();
                handler.deleteTable(NotificationDataHandler.TABLE_NAME_DEFAULT);
                handler.close();
                Log.e("NotificationDrawer","Cliked Clear");
                notificationList=retrieveNotifications();
                drawerListAdapter = new NotificationDrawerListAdapter(NotificationsActivity.this, notificationList);
                notificationsListView.setAdapter(drawerListAdapter);

            }
        });

    }

    protected ArrayList<Notifications> retrieveNotifications(){
        Log.e("NotificationDrawer", "Retrieving Notifications");
        ArrayList<Notifications> notificationsArrayList=new ArrayList<>();
        NotificationDataHandler notifHandler=new NotificationDataHandler(NotificationsActivity.this);
        notifHandler.open();
        notificationsArrayList = notifHandler.returnData(NotificationDataHandler.TABLE_NAME_DEFAULT);
        notifHandler.close();
        if(notificationsArrayList.isEmpty()){
            Log.e("NotificationDrawer","No notifications.");
            notificationsArrayList.add(new Notifications("0", "You do not have any notifications", ""));
        }
        return notificationsArrayList;
    }
}
