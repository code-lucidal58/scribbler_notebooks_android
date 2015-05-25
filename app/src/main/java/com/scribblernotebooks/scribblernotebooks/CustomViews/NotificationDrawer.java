package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.scribblernotebooks.scribblernotebooks.Adapters.NotificationDrawerListAdapter;
import com.scribblernotebooks.scribblernotebooks.R;

/**
 * Created by Jibin_ism on 17-May-15.
 */
public class NotificationDrawer {

    View mainView = null;
    ListView notificationsListView;
    NotificationDrawerListAdapter drawerListAdapter;
    Context mContext;


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

        //Get notifications code
        String[] notificationList = new String[1];
        notificationList[0]=mContext.getResources().getString(R.string.noNotificationText);

        //Adapter setup
        drawerListAdapter = new NotificationDrawerListAdapter(mContext, notificationList);
        notificationsListView.setAdapter(drawerListAdapter);


    }

}
