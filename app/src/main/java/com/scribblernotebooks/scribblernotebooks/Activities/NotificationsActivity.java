package com.scribblernotebooks.scribblernotebooks.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    NotificationDrawerListAdapter drawerListAdapter;
    ArrayList<Notifications> notificationList=new ArrayList<>();
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_right_drawer);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        notificationsListView = (ListView)findViewById(R.id.right_drawer);
        notificationList=retrieveNotifications();
        drawerListAdapter = new NotificationDrawerListAdapter(NotificationsActivity.this, notificationList);
        notificationsListView.setAdapter(drawerListAdapter);
    }

    void clearNotifications(){
        NotificationDataHandler handler=new NotificationDataHandler(NotificationsActivity.this);
        handler.open();
        handler.deleteTable(NotificationDataHandler.TABLE_NAME_DEFAULT);
        handler.close();
        Log.e("NotificationDrawer","Cliked Clear");
        notificationList=retrieveNotifications();
        drawerListAdapter = new NotificationDrawerListAdapter(NotificationsActivity.this, notificationList);
        notificationsListView.setAdapter(drawerListAdapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_notification,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.clear){
            clearNotifications();
        }else if(item.getItemId()==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
