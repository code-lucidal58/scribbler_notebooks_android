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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.scribblernotebooks.scribblernotebooks.Activities.DealDetail;
import com.scribblernotebooks.scribblernotebooks.Activities.NotificationsActivity;
import com.scribblernotebooks.scribblernotebooks.Adapters.RecyclerDealsAdapter;
import com.scribblernotebooks.scribblernotebooks.Adapters.UnUsedDealsAdapter;
import com.scribblernotebooks.scribblernotebooks.CustomViews.JazzySwitchView;
import com.scribblernotebooks.scribblernotebooks.Handlers.DatabaseHandler;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.R;
import com.scribblernotebooks.scribblernotebooks.Services.ClaimedDealsRetriever;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClaimedDeals extends Fragment{

    private static final String TITLE = "title";

    RecyclerView recyclerView;
    Context context;
    Toolbar appbar;
    DrawerLayout mDrawerLayout;
    RelativeLayout mDrawer;
    JazzySwitchView jazzySwitchView;
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
                updateList(v, true);
            }
        });
        context.startService(new Intent(context, ClaimedDealsRetriever.class));
        appbar = (Toolbar) v.findViewById(R.id.app_bar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(appbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().setTitle(title);
        setHasOptionsMenu(true);

        jazzySwitchView=(JazzySwitchView)v.findViewById(R.id.jazzySwitch);
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

        jazzySwitchView.setSwitchToggleListener(new JazzySwitchView.SwitchToggledListener() {
            @Override
            public void onSwitchToggle(boolean checked) {
                Log.e("ClaimedDeals","Switch toggle: "+checked);
                if(checked){
                    updateList(v,true);
                }else{
                    updateList(v,false);
                }
            }
        });
        updateList(v, true);

        return v;
    }

    final int DEAL_DETAIL_REQUEST_CODE = 50;
    public void showDealDetail(int position, ArrayList<Deal> deals) {
        Intent i = new Intent(context, DealDetail.class);
        i.putParcelableArrayListExtra(Constants.PARCELABLE_DEAL_LIST_KEY, deals);
        i.putExtra(Constants.CURRENT_DEAL_INDEX, position);
        i.putExtra("IS_CLAIMED", true);
        startActivityForResult(i, DEAL_DETAIL_REQUEST_CODE);
    }

    ArrayList<Deal> deals;
    void updateList(View v, Boolean claimedDeals){
        if(claimedDeals) {
            deals = databaseHandler.getClaimedDealList();
            RecyclerDealsAdapter adapter=new RecyclerDealsAdapter(deals,context,true,getActivity());
            adapter.setItemClickListener(new RecyclerDealsAdapter.onItemClickListener() {
                @Override
                public void onItemClick(int position, ArrayList<Deal> deals) {
                    showDealDetail(position, deals);
                }
            });
            recyclerView.setAdapter(adapter);
        }
        else {
            deals = databaseHandler.getUnusedDealList();
            final UnUsedDealsAdapter adapter=new UnUsedDealsAdapter(deals,getActivity());
            adapter.setDealUsedListener(new UnUsedDealsAdapter.DealUsedListener() {
                @Override
                public void onDealUsed(int position, Deal deal) {
                    deals.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            recyclerView.setAdapter(adapter);

        }

        if (deals!=null){
            if(deals.size()>0){
                ((LinearLayout)v.findViewById(R.id.loadingProgress)).setVisibility(View.GONE);
            }
        }
//        Log.e("ClaimedDeals","Updating list "+claimedDeals);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        databaseHandler.close();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_navigation_drawer, menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            }
        }
        else if(item.getItemId() ==R.id.notification){
            startActivity(new Intent(getActivity(),NotificationsActivity.class));
        }
        return true;
    }
}
