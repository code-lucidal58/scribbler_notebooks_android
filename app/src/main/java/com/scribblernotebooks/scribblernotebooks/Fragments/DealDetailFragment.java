package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.R;

/**
 * Created by Jibin_ism on 09-Jun-15.
 */
public class DealDetailFragment extends Fragment {

    OnFragmentInteractionListener mListener;
    Deal deal;
    Context mContext;
    TextView title, category, description;
    ImageView image;
    Button claimDeal;
    CheckBox likeBox;
    RadioButton shareBox;

    public static DealDetailFragment newInstance(Deal deal){
        DealDetailFragment fragment=new DealDetailFragment();
        Bundle args=new Bundle();
        args.putParcelable(Constants.PARCELABLE_DEAL_KEY, deal);
        fragment.setArguments(args);
        return fragment;
    }


    public DealDetailFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        this.deal=args.getParcelable(Constants.PARCELABLE_DEAL_KEY);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContext=getActivity();
        View view=inflater.inflate(R.layout.fragment_deal_detail, container, false);

        //View Setup
        title=(TextView)view.findViewById(R.id.dealName);
        category=(TextView)view.findViewById(R.id.dealCategory);
        description=(TextView)view.findViewById(R.id.dealDescription);
        image=(ImageView)view.findViewById(R.id.dealIcon);
        claimDeal=(Button)view.findViewById(R.id.claimDeal);
        likeBox=(CheckBox)view.findViewById(R.id.likeBox);
        shareBox=(RadioButton)view.findViewById(R.id.shareBox);

        title.setText(deal.getTitle());
        category.setText(deal.getCategory());
        description.setText(Html.fromHtml(deal.getLongDescription()));
        likeBox.setChecked(deal.isFavorited());

        if(deal.isFavorited()){
            likeBox.setText("Liked");
        }

        likeBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    likeBox.setText("Liked");
                }
                else {
                    likeBox.setText("Like this deal");
                }
                deal.setIsFav(mContext,isChecked);
            }
        });

        shareBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deal.sendShareStatus(getActivity().getApplicationContext());
                Intent sharingIntent=new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Scribbler Deal");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,"Hi there, Just checkout this Scribbler Deal ");
                Intent starter=Intent.createChooser(sharingIntent,"Share Via");
                starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(starter);
            }
        });

//        ImageLoaderConfiguration loaderConfiguration=new ImageLoaderConfiguration.Builder(mContext).build();
//        ImageLoader.getInstance().init(loaderConfiguration);
//        ImageLoadingListener imageLoadingListener=new SimpleImageLoadingListener();
//        DisplayImageOptions displayImageOptions=new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.ic_launcher)
//                .showImageForEmptyUri(R.mipmap.ic_launcher)
//                .showImageOnFail(R.mipmap.ic_launcher)
//                .cacheOnDisk(true)
//                .cacheInMemory(true)
//                .considerExifParams(true)
//                .displayer(new SimpleBitmapDisplayer()).build();
//        ImageLoader.getInstance().displayImage(deal.getImageUrl(),image,displayImageOptions,imageLoadingListener);

        claimDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                claimThisDeal();
            }
        });

        return view;
    }


    private void claimThisDeal(){
        Toast.makeText(mContext,"Deal Claimed "+deal.getTitle(),Toast.LENGTH_LONG).show();
    }



    /**
     * Auto-generated methods
     *
     * @param uri
     */
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
