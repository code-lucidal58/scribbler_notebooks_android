package com.scribblernotebooks.scribblernotebooks.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scribblernotebooks.scribblernotebooks.Adapters.RecyclerDealsAdapter;
import com.scribblernotebooks.scribblernotebooks.Handlers.DatabaseHandler;
import com.scribblernotebooks.scribblernotebooks.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClaimedDeals extends android.support.v4.app.Fragment {

    OnFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    Context context;


    /**
     * Setting statically the new fragment
     *
     * @return the Deal list fragment
     */
    public static ClaimedDeals newInstance() {
        ClaimedDeals claimedDeals=new ClaimedDeals();
        return claimedDeals;
    }


    public ClaimedDeals() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context=getActivity().getApplicationContext();
        View v = inflater.inflate(R.layout.fragment_claimed_deals, container, false);


        Toolbar appbar;
        appbar = (Toolbar) v.findViewById(R.id.app_bar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(appbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().setTitle("Claimed Deals");


        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        DatabaseHandler han=new DatabaseHandler(context);
        RecyclerDealsAdapter adapter=new RecyclerDealsAdapter(han.getClaimedDealList(),context,true);
        han.close();
        recyclerView.setAdapter(adapter);
        return v;
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
