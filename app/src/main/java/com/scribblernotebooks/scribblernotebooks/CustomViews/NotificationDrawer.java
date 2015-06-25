package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.scribblernotebooks.scribblernotebooks.Adapters.NotificationDrawerListAdapter;
import com.scribblernotebooks.scribblernotebooks.Handlers.NotificationDataHandler;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Notifications;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Jibin_ism on 17-May-15.
 */
public class NotificationDrawer {

    View mainView = null;
    Button clearNotif;
    ListView notificationsListView;
    NotificationDrawerListAdapter drawerListAdapter;
    Context mContext;
    ArrayList<Notifications> notificationList=new ArrayList<>();
    Boolean refreshed=false;

    /**
     * Default Constructor
     * @param activityView root view of the activity
     * @param context context of application
     */
    public NotificationDrawer(View activityView, Context context) {
        super();
        this.mainView = activityView;
        this.mContext = context;

        instantiate();
    }


    protected void instantiate() {
        //View setup
        notificationsListView = (ListView) mainView.findViewById(R.id.right_drawer);
        clearNotif=(Button)mainView.findViewById(R.id.clearNotifs);

        DrawerLayout mDrawerLayout=(DrawerLayout)mainView.findViewById(R.id.drawer_layout);
        final RelativeLayout mDrawer=(RelativeLayout)mainView.findViewById(R.id.notification_drawer);

        Log.e("NotificationDrawer","Initial List Retrieving");
        notificationList=retrieveNotifications();

        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if(slideOffset==1 || slideOffset==0){
                    refreshed=false;
                }
                if ((slideOffset != 0 )  && drawerView==mDrawer && !refreshed) {
                    Log.e("NotificationDrawer","Updating Notifications");
                    notificationList = retrieveNotifications();
                    drawerListAdapter = new NotificationDrawerListAdapter(mContext, notificationList);
                    notificationsListView.setAdapter(drawerListAdapter);
                }
            }
        });

        clearNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationDataHandler handler=new NotificationDataHandler(mContext);
                handler.open();
                handler.deleteTable(NotificationDataHandler.TABLE_NAME_DEFAULT);
                handler.close();
                Log.e("NotificationDrawer","Cliked Clear");
                notificationList=retrieveNotifications();
                drawerListAdapter = new NotificationDrawerListAdapter(mContext, notificationList);
                notificationsListView.setAdapter(drawerListAdapter);

            }
        });

        //Adapter setup
        drawerListAdapter = new NotificationDrawerListAdapter(mContext, notificationList);
        notificationsListView.setAdapter(drawerListAdapter);

    }

    protected ArrayList<Notifications> retrieveNotifications(){
        Log.e("NotificationDrawer","Retrieving Notifications");
        ArrayList<Notifications> notificationsArrayList=new ArrayList<>();
        NotificationDataHandler notifHandler=new NotificationDataHandler(mContext);
        notifHandler.open();
        notificationsArrayList = notifHandler.returnData(NotificationDataHandler.TABLE_NAME_DEFAULT);
        notifHandler.close();
        refreshed=true;
        if(notificationsArrayList.isEmpty()){
            Log.e("NotificationDrawer","No notifications.");
            notificationsArrayList.add(new Notifications("0", "You do not have any notifications", ""));
        }
        return notificationsArrayList;
    }



}
