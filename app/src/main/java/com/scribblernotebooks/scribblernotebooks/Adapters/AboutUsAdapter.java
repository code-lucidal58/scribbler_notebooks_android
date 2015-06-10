package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.scribblernotebooks.scribblernotebooks.Activities.AboutScribblerNotebooks;
import com.scribblernotebooks.scribblernotebooks.Activities.FeedbackActivity;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.List;

/**
 * Created by Jibin_ism on 03-Jun-15.
 */
public class AboutUsAdapter extends RecyclerView.Adapter<AboutUsAdapter.ViewHolder> {

    List<String> options;
    Context mContext;
    LayoutInflater layoutInflater;


    public AboutUsAdapter(Context context,List<String> fields) {
        options=fields;
        mContext=context;
        layoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView=LayoutInflater.from(mContext)
                .inflate(R.layout.listview_item_aboutus,parent,false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.text.setText(options.get(position));
    }

    @Override
    public int getItemCount() {
        return options.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        MaterialRippleLayout ripple;

        public ViewHolder(View itemView) {
            super(itemView);
            ripple=(MaterialRippleLayout)itemView.findViewById(R.id.dealRipple);
            text=(TextView)itemView.findViewById(R.id.t1);
        }
    }

}
