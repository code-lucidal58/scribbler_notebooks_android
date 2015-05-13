package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.scribblernotebooks.scribblernotebooks.Adapters.RecyclerCustomAdapter;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class DealList extends Fragment {

    private String dataUrl, listType,url;
    RecyclerView recyclerView;
    EditText search;
    ArrayList<HashMap<String, String>> dealsList = new ArrayList<HashMap<String, String>>();
    RecyclerCustomAdapter adapter;
    Context context;
    private OnFragmentInteractionListener mListener;

    public static DealList newInstance(String url, String listType) {
        DealList fragment = new DealList();
        Bundle args=new Bundle();
        args.putString(Constants.ARGUMENTS_URL,url);
        args.putString(Constants.ARGUMENTS_LISTTYPE,listType);
        fragment.setArguments(args);
        return fragment;
    }

    public DealList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dataUrl=getArguments().getString(Constants.ARGUMENTS_URL);
        this.listType=getArguments().getString(Constants.ARGUMENTS_LISTTYPE);
        this.context=getActivity().getApplicationContext();
        url=Constants.serverURL;
        if(listType.equals(Constants.LISTTYPE_TRENDING)){
            url=url+"featuredDeals";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_deal_list, container, false);

        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);

        new LongOperation().execute(url);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return v;
    }

    public class LongOperation extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {
            String response="";
            try{
                HttpClient client=new DefaultHttpClient();
                HttpGet httpGet=new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler=new BasicResponseHandler();
                response=client.execute(httpGet,responseHandler);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            dealsList.clear();
            try{
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.optJSONArray("deals");
                int lengthJsonArr= jsonArray.length();

                for(int i=0;i<lengthJsonArr;i++)
                {
                    JSONObject jsonChildNode = jsonArray.getJSONObject(i);

                    /****Fetch node values****/
                    String id = jsonChildNode.optString(Constants.TAG_ID);
                    String category = jsonChildNode.optString(Constants.TAG_CATEGORY);
                    String newhot = jsonChildNode.optString(Constants.TAG_NEW);
                    String dealName = jsonChildNode.optString(Constants.TAG_DEAL_NAME);
                    String image = jsonChildNode.optString(Constants.TAG_IMAGE_URL);
                    String shortDesc = jsonChildNode.optString(Constants.TAG_SHORT_DESCRIPTION);
                    String longDesc = jsonChildNode.optString(Constants.TAG_LONG_DESCRIPTION);

                    HashMap<String, String> deals = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    deals.put(Constants.TAG_ID, id);
                    deals.put(Constants.TAG_CATEGORY, category.replace(" ", "\n"));
                    deals.put(Constants.TAG_DEAL_NAME, dealName);
                    deals.put(Constants.TAG_IMAGE_URL, image);
                    deals.put(Constants.TAG_SHORT_DESCRIPTION, shortDesc);
                    deals.put(Constants.TAG_NEW, newhot);

                    dealsList.add(deals);
                }

                adapter=new RecyclerCustomAdapter(dealsList,context);
                recyclerView.setAdapter(adapter);

            }catch(JSONException e)
            {
                e.printStackTrace();
            }
        }
    }




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
