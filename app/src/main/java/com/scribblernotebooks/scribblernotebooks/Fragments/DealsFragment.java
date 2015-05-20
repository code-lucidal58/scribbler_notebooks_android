package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scribblernotebooks.scribblernotebooks.Adapters.RecyclerCustomAdapter;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ParseJson;
import com.scribblernotebooks.scribblernotebooks.R;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;

public class DealsFragment extends android.support.v4.app.Fragment {
    private static final String ARG_PARAM1 = "url";

    RecyclerView recyclerView;
    ArrayList<Deal> dealsList = new ArrayList<>();
    RecyclerCustomAdapter adapter;
    Context context;

    // TODO: Rename and change types of parameters

    private String url;

    private OnFragmentInteractionListener mListener;

    /**
     * Setting statically the new fragment
     * @param url the url to be sent to server for getting deals
     * @return the Deal list fragment
     */
    public static DealsFragment newInstance(String url) {
        DealsFragment fragment = new DealsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, url);
        fragment.setArguments(args);
        return fragment;
    }

    /** Auto Generated */
    public DealsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflating Views
        View v = inflater.inflate(R.layout.fragment_deals, container, false);

        context = getActivity().getBaseContext();

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        //Get response from server
        new LongOperation().execute(url);

        //recyclerView setup
        recyclerView.setLayoutManager(new GridLayoutManager(context, getResources().getInteger(R.integer.dealListColoumnCount)));

        return v;
    }


    /**
     * To change to number of columns in the deal list. 1 when portrait and 2 when landscape
     * @param newConfig android config
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recyclerView.setLayoutManager(new GridLayoutManager(context, getResources().getInteger(R.integer.dealListColoumnCount)));
}



    /**
     * Async task to get the data from the server and process it
     */
    public class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = client.execute(httpGet, responseHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            dealsList.clear();
            dealsList = ParseJson.getParsedData(s);
            adapter = new RecyclerCustomAdapter(dealsList, context);
            recyclerView.setAdapter(adapter);
        }
    }




    /**
     * Auto-generated methods
     * @param uri
     */


    // TODO: Rename method, update argument and hook method into UI event
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
