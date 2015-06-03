package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.R;

import java.util.List;

/**
 * Created by Jibin_ism on 03-Jun-15.
 */
public class AboutUsAdapter extends BaseAdapter {

    List<String> options;
    Context mContext;
    LayoutInflater layoutInflater;


    public AboutUsAdapter(Context context,List<String> fields) {
        options=fields;
        mContext=context;
        layoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int position) {
        return options.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.listview_item_aboutus,null,false);
        }
        TextView tv=(TextView)convertView.findViewById(R.id.text1);
        tv.setText(options.get(position));
        return convertView;
    }
}
