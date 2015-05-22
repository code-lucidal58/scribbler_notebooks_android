package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Aanisha on 15-May-15.
 */
public class ProfileListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<String> Pfield,Pvalue;
    private static LayoutInflater inflater=null;
    TextView field,value;

    public ProfileListAdapter(Activity a, ArrayList<String> f, ArrayList<String> v) {
        activity=a;
        inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Pfield=f;
        Pvalue=v;
    }

    @Override
    public int getCount() {
        return Pfield.size();
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
            vi = inflater.inflate(R.layout.listview_item_profile, parent,false);

        field=(TextView)vi.findViewById(R.id.field);
        value=(TextView)vi.findViewById(R.id.field_value);

        String profileField=Pfield.get(position);
        String profileValue=Pvalue.get(position);

        field.setText(Pfield.get(position));
        value.setText(Pvalue.get(position));

        return vi;
    }
}
