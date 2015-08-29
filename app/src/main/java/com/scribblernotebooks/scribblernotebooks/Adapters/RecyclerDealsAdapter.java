package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.scribblernotebooks.scribblernotebooks.Handlers.DatabaseHandler;
import com.scribblernotebooks.scribblernotebooks.Handlers.UserHandler;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aanisha on 07-May-15.
 */
public class RecyclerDealsAdapter extends RecyclerView.Adapter<RecyclerDealsAdapter.ViewHolder> {

    public static int TYPE_GRID=0;
    public static int TYPE_LIST=1;

    ArrayList<Deal> dealsList;
    Context context;
    UserHandler handler;
    public DisplayImageOptions displayImageOptions;
    public ImageLoadingListener imageLoadingListener;
    public ImageLoaderConfiguration imageLoaderConfiguration;
    Boolean isClaimed=false;
    onViewHolderListener mListener;
    int type=1;
    onItemClickListener itemClickListener;
    Activity activity;

    public void setViewHolderListener(onViewHolderListener listener){
        mListener=listener;
    }
    public void setItemClickListener(onItemClickListener listener){
        itemClickListener=listener;
    }

    public interface onViewHolderListener{
        void onRequestedLastItem();
    }

    public RecyclerDealsAdapter(ArrayList<Deal> dealsList, Context context) {
        this.dealsList = dealsList;
        this.context = context;
        init();
    }
    public RecyclerDealsAdapter(ArrayList<Deal> dealsList, Context context, int type) {
        this.dealsList = dealsList;
        this.context = context;
        this.type=type;
        init();
    }

    public RecyclerDealsAdapter(ArrayList<Deal> dealsList,Context context,Boolean isClaimed, Activity activity){
        this.dealsList = dealsList;
        this.context = context;
        this.isClaimed=isClaimed;
        this.activity=activity;
        init();
    }

    public void init(){

        handler = new UserHandler(context);
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
                .displayer(new SimpleBitmapDisplayer()).build();
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
        MaterialRippleLayout rippleLayout;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            /** View Setup */
            rippleLayout=(MaterialRippleLayout)itemLayoutView.findViewById(R.id.dealRipple);
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
        int id;
        if(type==TYPE_GRID)
            id=R.layout.listview_item_deals_grid;
        else
            id=R.layout.listview_item_deals;
            View itemLayoutView = LayoutInflater.from(viewGroup.getContext()) .inflate(id, viewGroup, false);
//            View itemLayoutView = LayoutInflater.from(viewGroup.getContext()) .inflate(R.layout.listview_item_deals, viewGroup, false);

            // create ViewHolder

            return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        final Deal deal=dealsList.get(position);

        if(position==getItemCount()-1){
            if(mListener!=null)
                mListener.onRequestedLastItem();
        }

        /** Retrieving deal info  */
        final String id=deal.getId();
        final String title = deal.getTitle();
        final String category = deal.getCategory();
        String details = deal.getShortDescription();

        if(handler.findDeal(id)){
            Log.e("Deal Likes",id+"  "+deal.getTitle()+"   "+deal.isFavorited());
            deal.setIsFav(true);
            viewHolder.favoriteIcon.setChecked(true);
        }else{
            deal.setIsFav(false);
            viewHolder.favoriteIcon.setChecked(false);
        }



        /** Setting list View item details */
        viewHolder.txtViewTitle.setText(title);
        viewHolder.txtViewCategory.setText(category);
        try{
            URL url=new URL(deal.getImageUrl());
            ImageLoader.getInstance().displayImage(deal.getImageUrl(), viewHolder.imgViewIcon, displayImageOptions, imageLoadingListener);

        }catch (MalformedURLException e){
            setImage(viewHolder.imgViewIcon,deal.getCategory());

        }

//        if(deal.getImageUrl().isEmpty()){
//        }else {
//            ImageLoader.getInstance().displayImage(deal.getImageUrl(), viewHolder.imgViewIcon, displayImageOptions, imageLoadingListener);
//        }

        /** Saving favorite option to the deal **/
        viewHolder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isChecked = ((CheckBox) v).isChecked();
                deal.setIsFav(context, isChecked);
            }
        });


        /** Sharing deal **/
        viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deal.sendShareStatus(context);
                Intent sharingIntent=new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Scribbler Deal");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hi there, Just checkout this Scribbler Deal ");
                Intent starter=Intent.createChooser(sharingIntent,"Share Via");
                starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(starter);
            }
        });

        if(!isClaimed) {
            viewHolder.txtViewDealDetails.setText(Html.fromHtml(details));

        }
        else
        {
            viewHolder.txtViewDealDetails.setText("Coupon Code: " + deal.getCouponCode());
            viewHolder.shareButton.setVisibility(View.GONE);
            viewHolder.favoriteIcon.setVisibility(View.GONE);
            viewHolder.rippleLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dealsList.remove(position);
                            DatabaseHandler handler = new DatabaseHandler(context);
                            handler.deleteClaimedDeal(deal);
                            handler.close();
                            notifyItemRemoved(position);
                        }
                    });
                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setTitle("Confirm Delete");
                    dialog.setMessage("Are you sure you want to delete this deal from the list? This cannot be undone.");
                    dialog.show();
                    return true;
                }
            });
        }

        viewHolder.rippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(position, dealsList);
                //Mixpanel code
                MixpanelAPI mixpanelAPI=Constants.getMixPanelInstance(context);
                Calendar calendar=Calendar.getInstance();
                JSONObject props=new JSONObject();
                try {
                    props.put("id",id);
                    props.put("category",category);
                    props.put("dealName",title);
                    props.put("date", calendar.get(Calendar.DATE));
                    props.put("month",calendar.get(Calendar.MONTH));
                    props.put("year",calendar.get(Calendar.YEAR));
                    props.put("time",new Date()) ;
                    props.put("day", calendar.get(Calendar.DAY_OF_WEEK));
                    Log.e("check",props.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanelAPI.track("Views", props);
            }
        });


    }

    void setImage(ImageView imageView, String category){
//        Log.e("RecyclerDealAdapter","Category: "+category);
        int id;
        switch (category.toLowerCase()){
            case "food":
                id=R.drawable.food;
                break;
            case "education":
                id=R.drawable.education;
                break;
            case "travel":
                id=R.drawable.travel;
                break;
            case "automobiles":
                id=R.drawable.automobile;
                break;
            case "entertainment":
                id=R.drawable.entertainment;
                break;
            case "beauty & fitness":
                id=R.drawable.fitness;
                break;
            default:
                id=R.mipmap.ic_launcher;
                break;
        }
        imageView.setImageResource(id);
    }

    @Override
    public int getItemCount() {
        return dealsList==null?0:dealsList.size();
    }

    public interface onItemClickListener{
        void onItemClick(int position, ArrayList<Deal> deals);
    }

}
