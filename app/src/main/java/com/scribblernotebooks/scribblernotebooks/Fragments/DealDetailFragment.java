package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jibin_ism on 09-Jun-15.
 */
public class DealDetailFragment extends Fragment {

    Deal deal;
    Context mContext;
    TextView title, category, description;
    ImageView image;
    Button claimDeal;
    CheckBox likeBox;
    RadioButton shareBox;
    Boolean isClaimed=false;

    public static DealDetailFragment newInstance(Deal deal, Boolean isClaimed){
        DealDetailFragment fragment=new DealDetailFragment();
        Bundle args=new Bundle();
        args.putParcelable(Constants.PARCELABLE_DEAL_KEY, deal);
        args.putBoolean("IS_CLAIMED",isClaimed);
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
        this.isClaimed=args.getBoolean("IS_CLAIMED");
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

        if(isClaimed){
            claimDeal.setOnClickListener(null);
            claimDeal.setText(deal.getCouponCode());
        }else {
            claimDeal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    claimThisDeal();
                }
            });
        }

        return view;
    }


    private void claimThisDeal(){
        String s=deal.claimDeal(mContext);
        if(s.isEmpty()){
            claimDeal.setText("Error Claiming... Try Again");
        }else{
            claimDeal.setText(s);
            claimDeal.setOnClickListener(null);
            MixpanelAPI mixpanelAPI=Constants.getMixPanelInstance(getActivity());
            Calendar calendar=Calendar.getInstance();
            JSONObject props=new JSONObject();
            try {
                props.put("id",deal.getId());
                props.put("category",category);
                props.put("dealName",title);
                props.put("date", calendar.get(Calendar.DATE));
                props.put("month",calendar.get(Calendar.MONTH));
                props.put("year",calendar.get(Calendar.YEAR));
                props.put("time",new Date()) ;
                props.put("day", calendar.get(Calendar.DAY_OF_WEEK));
                Log.e("check", props.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mixpanelAPI.track("Claim", props);
        }
    }

}
