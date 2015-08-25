package com.scribblernotebooks.scribblernotebooks.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.scribblernotebooks.scribblernotebooks.Activities.NotificationsActivity;
import com.scribblernotebooks.scribblernotebooks.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PremiumDealsFragment extends Fragment {

    View v;
    Toolbar appbar;

    public PremiumDealsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_premium_deals, container, false);
        appbar = (Toolbar) v.findViewById(R.id.app_bar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(appbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().setTitle("Premium Deals");
        setHasOptionsMenu(true);
        return v;
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
