package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.scribblernotebooks.scribblernotebooks.CustomViews.LeftNavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Aanisha on 12-May-15.
 */
public class NavigationRecyclerAdapter extends RecyclerView.Adapter<NavigationRecyclerAdapter.ViewHolder> {

    private ArrayList<Pair<Integer,String>> data;
    Context context;
    LeftNavigationDrawer leftNavigationDrawer;

    public NavigationRecyclerAdapter(Context context, ArrayList<Pair<Integer, String>> d,LeftNavigationDrawer leftNavigationDrawer) {
        data = d;
        this.context=context;
        this.leftNavigationDrawer=leftNavigationDrawer;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listview_item_leftdrawer, viewGroup, false);

        // create ViewHolder

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Pair<Integer, String> d=data.get(position);
        viewHolder.navigationItem.setText(d.second);
        viewHolder.itemIcon.setImageResource(d.first);
        viewHolder.dealRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftNavigationDrawer.clickAction(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView navigationItem;
        ImageView itemIcon;
        MaterialRippleLayout dealRipple;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            dealRipple=(MaterialRippleLayout)itemLayoutView.findViewById(R.id.dealRipple);
            navigationItem = (TextView) itemLayoutView.findViewById(R.id.navigationItem);
            itemIcon=(ImageView)itemLayoutView.findViewById(R.id.itemIcon);

        }
    }
}
