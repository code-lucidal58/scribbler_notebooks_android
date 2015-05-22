package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Aanisha on 12-May-15.
 */
public class NavigationListAdapter extends BaseAdapter {


    private ArrayList<Pair<Integer,String>> data;
    private static LayoutInflater inflater = null;

    public NavigationListAdapter(Context context, ArrayList<Pair<Integer,String>> d) {
        data = d;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
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
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.navigation_listview_item, parent, false);

        Pair<Integer, String> d=data.get(position);
        TextView navigationItem = (TextView) vi.findViewById(R.id.navigationItem);
        ImageView itemIcon=(ImageView)vi.findViewById(R.id.itemIcon);

        navigationItem.setText(d.second);
        itemIcon.setImageResource(d.first);

        return vi;
    }
}
