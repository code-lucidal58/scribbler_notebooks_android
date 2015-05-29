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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Aanisha on 07-May-15.
 */
public class RecyclerDealsAdapter extends RecyclerView.Adapter<RecyclerDealsAdapter.ViewHolder> {

    ArrayList<Deal> dealsList;
    Context context;
    public DisplayImageOptions displayImageOptions;
    public ImageLoadingListener imageLoadingListener;
    public ImageLoaderConfiguration imageLoaderConfiguration;
    Boolean isClaimed=false;

    public RecyclerDealsAdapter(ArrayList<Deal> dealsList, Context context) {
        this.dealsList = dealsList;
        this.context = context;
        init();
    }

    public RecyclerDealsAdapter(ArrayList<Deal> dealsList,Context context,Boolean isClaimed){
        this.dealsList = dealsList;
        this.context = context;
        this.isClaimed=isClaimed;
        init();
    }

    public void init(){

        imageLoaderConfiguration=new ImageLoaderConfiguration.Builder(this.context).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
        imageLoadingListener=new SimpleImageLoadingListener();
        displayImageOptions=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20)).build();
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
                    .inflate(R.layout.listview_item_deals, viewGroup, false);

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
        if(!isClaimed) {
            viewHolder.txtViewDealDetails.setText(details);
            viewHolder.favoriteIcon.setChecked(isfavorited);
        }
        else
        {
            viewHolder.txtViewDealDetails.setText("Coupon Code: " + deal.getCouponCode());
        }

        ImageLoader.getInstance().displayImage(deal.getImageUrl(),viewHolder.imgViewIcon,displayImageOptions,imageLoadingListener);

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
                sharingIntent.putExtra(Intent.EXTRA_TEXT,"Hi there, Just checkout this Scribbler Deal ");
                Intent starter=Intent.createChooser(sharingIntent,"Share Via");
                starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(starter);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dealsList.size();
    }

}
