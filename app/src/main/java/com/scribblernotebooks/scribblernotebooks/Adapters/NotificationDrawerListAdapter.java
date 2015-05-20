package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.R;

/**
 * Created by Jibin on 12-May-15.
 */
public class NotificationDrawerListAdapter extends BaseAdapter {
    private String[] data;
    private static LayoutInflater inflater=null;
    ImageView notificationImage;
    TextView notificationText;

    public NotificationDrawerListAdapter(Context context, String[] data) {
        this.data=data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.notification_layout, parent,false);

        notificationImage=(ImageView)vi.findViewById(R.id.notificationIcon);
        notificationText=(TextView)vi.findViewById(R.id.notificationText);

        String item;
        item = data[position];

        Log.e("Data","data "+item);
        // Setting all values in listview
        notificationText.setText(item);

        return vi;
    }
}
