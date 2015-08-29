package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.scribblernotebooks.scribblernotebooks.Handlers.DatabaseHandler;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Aanisha on 07-May-15.
 */
public class UnUsedDealsAdapter extends RecyclerView.Adapter<UnUsedDealsAdapter.ViewHolder> {

//    int color1= Color.parseColor("#ffffff");
//    int color2= Color.parseColor("#f0f0ff");
//    int[] colors={color1, color2};
    ArrayList<Deal> dealsList;
    public DisplayImageOptions displayImageOptions;
    public ImageLoadingListener imageLoadingListener;
    public ImageLoaderConfiguration imageLoaderConfiguration;
    onViewHolderListener mListener;
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

    public UnUsedDealsAdapter(ArrayList<Deal> dealsList,Activity activity){
        this.dealsList = dealsList;
        this.activity=activity;
        init();
    }

    public void init(){
        imageLoaderConfiguration=new ImageLoaderConfiguration.Builder(activity).build();
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
        public TextView txtViewDealDetails;
        public ImageView imgViewIcon;
        public EditText useCode;
        public Button useButton;
        public LinearLayout linearLayout;
        MaterialRippleLayout rippleLayout;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            /** View Setup */
            rippleLayout=(MaterialRippleLayout)itemLayoutView.findViewById(R.id.dealRipple);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.dealTitle);
            txtViewDealDetails = (TextView) itemLayoutView.findViewById(R.id.dealDetails);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.deal_icon);
            useCode=(EditText)itemLayoutView.findViewById(R.id.inputConfirmCode);
            useButton=(Button)itemLayoutView.findViewById(R.id.useButton);
            linearLayout=(LinearLayout)itemLayoutView.findViewById(R.id.listView_container);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //create new view
            View itemLayoutView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.listview_item_unuseddeals, viewGroup, false);

            // create ViewHolder

            return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final Deal deal=dealsList.get(position);

        if(position==getItemCount()-1){
            if(mListener!=null)
                mListener.onRequestedLastItem();
        }

        /** Retrieving deal info  */
        final String id=deal.getId();
        final String title = deal.getTitle();


        /** Setting list View item details */
        viewHolder.txtViewTitle.setText(title);
        ImageLoader.getInstance().displayImage(deal.getImageUrl(), viewHolder.imgViewIcon, displayImageOptions, imageLoadingListener);

        viewHolder.useButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deal.useDeal(activity, viewHolder.useCode.getText().toString());
            }
        });

//        viewHolder.linearLayout.setBackgroundColor(colors[position%2]);


        deal.setDealUseListener(new Deal.DealUseListener() {
            @Override
            public void onDealUsed(Boolean success) {
                if (success) {
                    dealUsedListener.onDealUsed(position, deal);
                }
            }
        });

            viewHolder.txtViewDealDetails.setText("Coupon Code: " + deal.getCouponCode());
            viewHolder.rippleLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dealsList.remove(position);
                            DatabaseHandler handler = new DatabaseHandler(activity);
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
                    dialog.setMessage("Are you sure you want to delete this deal from the list? You won't be able to use this deal.");
                    dialog.show();
                    return true;
                }
            });

        viewHolder.rippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(position, dealsList);
                //Mixpanel code
//                MixpanelAPI mixpanelAPI=Constants.getMixPanelInstance(context);
//                Calendar calendar=Calendar.getInstance();
//                JSONObject props=new JSONObject();
//                try {
//                    props.put("id",id);
//                    props.put("category",category);
//                    props.put("dealName",title);
//                    props.put("date", calendar.get(Calendar.DATE));
//                    props.put("month",calendar.get(Calendar.MONTH));
//                    props.put("year",calendar.get(Calendar.YEAR));
//                    props.put("time",new Date()) ;
//                    props.put("day", calendar.get(Calendar.DAY_OF_WEEK));
//                    Log.e("check",props.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                mixpanelAPI.track("Views", props);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dealsList==null?0:dealsList.size();
    }

    public interface onItemClickListener{
        void onItemClick(int position, ArrayList<Deal> deals);
    }

    DealUsedListener dealUsedListener;
    public interface DealUsedListener{
        void onDealUsed(int position, Deal deal);
    }
    public void setDealUsedListener(DealUsedListener dealUsedListener){
        this.dealUsedListener=dealUsedListener;
    }

}
