package com.scribblernotebooks.scribblernotebooks.Fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.scribblernotebooks.scribblernotebooks.Activities.DealDetail;
import com.scribblernotebooks.scribblernotebooks.Adapters.RecyclerDealsAdapter;
import com.scribblernotebooks.scribblernotebooks.Handlers.DatabaseHandler;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.R;
import com.scribblernotebooks.scribblernotebooks.Services.ClaimedDealsRetriever;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClaimedDeals extends android.support.v4.app.Fragment {

    private static final String TITLE = "title";

    RecyclerView recyclerView;
    Context context;
    Toolbar appbar;
    DrawerLayout mDrawerLayout;
    RelativeLayout mDrawer;

    DatabaseHandler databaseHandler;

    private String title;


    /**
     * Setting statically the new fragment
     *
     * @return the Deal list fragment
     */
    public static ClaimedDeals newInstance(String title) {
        ClaimedDeals claimedDeals=new ClaimedDeals();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        claimedDeals.setArguments(args);
        return claimedDeals;
    }


    public ClaimedDeals() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context=getActivity().getApplicationContext();
        databaseHandler=new DatabaseHandler(context);
        final View v = inflater.inflate(R.layout.fragment_claimed_deals, container, false);

        databaseHandler.setListUpdateListener(new DatabaseHandler.ListUpdateListener() {
            @Override
            public void OnClaimedDealListUpdated() {
                updateList(v);
            }
        });
        context.startService(new Intent(context,ClaimedDealsRetriever.class));
        appbar = (Toolbar) v.findViewById(R.id.app_bar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(appbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().setTitle(title);

        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mDrawer = (RelativeLayout) getActivity().findViewById(R.id.left_drawer_relative);
        final ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, appbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });


        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        updateList(v);

        return v;
    }

    final int DEAL_DETAIL_REQUEST_CODE = 50;
    public void showDealDetail(int position, ArrayList<Deal> deals) {
        Intent i = new Intent(context, DealDetail.class);
        i.putParcelableArrayListExtra(Constants.PARCELABLE_DEAL_LIST_KEY, deals);
        i.putExtra(Constants.CURRENT_DEAL_INDEX, position);
        i.putExtra("IS_CLAIMED",true);
        startActivityForResult(i, DEAL_DETAIL_REQUEST_CODE);
    }

    void updateList(View v){
        ArrayList<Deal> deals=databaseHandler.getClaimedDealList();
        if (deals!=null){
            if(deals.size()>0){
                ((LinearLayout)v.findViewById(R.id.loadingProgress)).setVisibility(View.GONE);
            }
        }
        RecyclerDealsAdapter adapter=new RecyclerDealsAdapter(deals,context,true);

        adapter.setItemClickListener(new RecyclerDealsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, ArrayList<Deal> deals) {
                showDealDetail(position, deals);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        databaseHandler.close();
    }
}
