package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.R;

/**
 * Created by Aanisha on 12-May-15.
 */
public class NavigationListAdapter extends BaseAdapter {
    private Activity activity;
    private String[] data;
    private static LayoutInflater inflater=null;

    public NavigationListAdapter(Activity a,String[] d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            vi = inflater.inflate(R.layout.navigation_listview_item, parent,false);

        TextView navigationItem = (TextView)vi.findViewById(R.id.navigationItem);

        String item;
        item = data[position];

        // Setting all values in listview
        navigationItem.setText(item);

        return vi;
    }
}
