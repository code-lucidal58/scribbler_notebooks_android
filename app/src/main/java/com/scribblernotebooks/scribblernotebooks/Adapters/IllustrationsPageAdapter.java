package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.Activities.LogIn;
import com.scribblernotebooks.scribblernotebooks.R;

import org.w3c.dom.Text;

/**
 * Created by Aanisha on 18-Jun-15.
 */
public class IllustrationsPageAdapter extends PagerAdapter {
    Activity activity;
    LayoutInflater mLayoutInflater;
    int[] imageId;
    String[] title, description;

    public IllustrationsPageAdapter(Activity activity, int[] a, String[] t, String[] d) {
        this.activity = activity;
        imageId = a;
        title = t;
        description = d;
        mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageId.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.layout_pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        TextView illusTitle = (TextView) itemView.findViewById(R.id.illusTitle);
        TextView illusDesc = (TextView) itemView.findViewById(R.id.illusDescription);
        Button getStarted = (Button) itemView.findViewById(R.id.getStarted);

        imageView.setImageResource(imageId[position]);
        illusTitle.setText(title[position]);
        illusDesc.setText(description[position]);

        if (position == (getCount() - 1)) {
            getStarted.setVisibility(View.VISIBLE);
            getStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getSharedPreferences("Illustrations",activity.MODE_PRIVATE).edit().putBoolean("firstTime",false);
                    activity.startActivity(new Intent(activity, LogIn.class));
                    activity.finish();
                }
            });
        } else {
            getStarted.setVisibility(View.GONE);
        }

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}

