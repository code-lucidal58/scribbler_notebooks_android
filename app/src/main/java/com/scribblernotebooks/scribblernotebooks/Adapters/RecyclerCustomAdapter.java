package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ImageLoader;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Aanisha on 07-May-15.
 */
public class RecyclerCustomAdapter extends RecyclerView.Adapter<RecyclerCustomAdapter.ViewHolder> {

    ArrayList<Deal> dealsList;
    public ImageLoader imageLoader;
    Context context;

    public RecyclerCustomAdapter(ArrayList<Deal> dealsList, Context context) {
        this.dealsList = dealsList;
        this.context = context;
        imageLoader = new ImageLoader(context);
    }

    /**
     * View Holder Class to point to recycler view item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public TextView txtViewCategory;
        public TextView txtViewDealDetails;
        public ImageView imgViewIcon;
        public CheckBox favoriteIcon;
        public ImageButton shareButton;
        public Deal deal;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            /** View Setup */
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.dealTitle);
            txtViewCategory = (TextView) itemLayoutView.findViewById(R.id.text_category);
            txtViewDealDetails = (TextView) itemLayoutView.findViewById(R.id.dealDetails);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.deal_icon);
            favoriteIcon=(CheckBox)itemLayoutView.findViewById(R.id.favoriteIcon);
            shareButton=(ImageButton)itemLayoutView.findViewById(R.id.shareDeal);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //create new view
            View itemLayoutView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.deals, viewGroup, false);

            // create ViewHolder
            return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Deal deal=dealsList.get(position);


        /** Retrieving deal info  */
        String id=deal.getId();
        String title = deal.getTitle();
        String category = deal.getCategory();
        String details = deal.getShortDescription();
        Boolean isfavorited=deal.isFavorited();

        /** Setting list View item details */
        viewHolder.txtViewTitle.setText(title);
        viewHolder.txtViewCategory.setText(category);
        viewHolder.txtViewDealDetails.setText(details);
        imageLoader.DisplayImage(deal.getImageUrl(), R.mipmap.ic_launcher, viewHolder.imgViewIcon);
        viewHolder.favoriteIcon.setChecked(isfavorited);


        /** Saving favorite option to the deal **/
        viewHolder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isChecked = ((CheckBox) v).isChecked();
                deal.setIsFav(isChecked);
            }
        });


        /** Sharing deal **/
        viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deal.sendShareStatus();
                Intent sharingIntent=new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Scribbler Deal");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,"Hi there, I just checkout this Scribbler Deal ");
                context.startActivity(Intent.createChooser(sharingIntent,"Share Via"));
            }
        });


    }

    @Override
    public int getItemCount() {
        return dealsList.size();
    }

}
