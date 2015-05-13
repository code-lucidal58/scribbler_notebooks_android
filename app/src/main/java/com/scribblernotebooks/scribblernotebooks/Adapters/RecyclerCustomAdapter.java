package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ImageLoader;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aanisha on 07-May-15.
 */
public class RecyclerCustomAdapter extends RecyclerView.Adapter<RecyclerCustomAdapter.ViewHolder> {

    ArrayList<HashMap<String,String>> dealsList,dealsListSearch;
    public ImageLoader imageLoader;
    Context context;

    public RecyclerCustomAdapter(ArrayList<HashMap<String, String>> dealsList,Context context){
        this.dealsList=dealsList;
        this.dealsListSearch=dealsList;
        this.context=context;
        imageLoader=new ImageLoader(context);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public TextView txtViewCategory;
        public TextView txtViewNewHot;
        public TextView txtViewDealDetails;
        public ImageView imgViewIcon;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.dealTitle);
            txtViewCategory = (TextView) itemLayoutView.findViewById(R.id.text_category);
            txtViewDealDetails = (TextView) itemLayoutView.findViewById(R.id.dealDetails);
            txtViewNewHot = (TextView) itemLayoutView.findViewById(R.id.newhot);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.deal_icon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //create new view
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.deals,null);

        // create ViewHolder
        ViewHolder viewHolder=new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        //replace the values in the view with that of dealsList
        String title = dealsList.get(position).get(Constants.TAG_DEAL_NAME);
        String category = dealsList.get(position).get(Constants.TAG_CATEGORY);
        String newHot = dealsList.get(position).get(Constants.TAG_NEW);
        String details = dealsList.get(position).get(Constants.TAG_SHORT_DESCRIPTION);

        viewHolder.txtViewTitle.setText(title);
        viewHolder.txtViewCategory.setText(category);
        viewHolder.txtViewNewHot.setText(newHot);
        viewHolder.txtViewDealDetails.setText(details);
        imageLoader.DisplayImage(dealsList.get(position).get(Constants.TAG_IMAGE_URL),R.mipmap.ic_launcher,viewHolder.imgViewIcon);
    }

    @Override
    public int getItemCount() {
        return dealsList.size();
    }

}
