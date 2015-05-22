package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.Adapters.SearchListAdapter;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchQueryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchQueryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchQueryFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_OPTION_IMAGE = "optionImage";
    private static final String ARG_OPTION_NAME = "optionName";

    // TODO: Rename and change types of parameters
    private Integer optionImageId;
    private String optionNameId;

    private OnFragmentInteractionListener mListener;

    ImageView cancel,option;
    EditText query;
    TextView optionName;
    RecyclerView recyclerView;
    SearchListAdapter searchListAdapter;
    Context context;
    ArrayList<String> suggestionList;


    public static SearchQueryFragment newInstance(int param1, String param2) {
        SearchQueryFragment fragment = new SearchQueryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_OPTION_IMAGE, param1);
        args.putString(ARG_OPTION_NAME, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchQueryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            optionImageId = getArguments().getInt(ARG_OPTION_IMAGE);
            optionNameId = getArguments().getString(ARG_OPTION_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_search_query, container, false);
        context=getActivity();
        //Initializing with ids
        cancel=(ImageView)view.findViewById(R.id.cancel);
        option=(ImageView)view.findViewById(R.id.option);
        optionName=(TextView)view.findViewById(R.id.option_name);
        query=(EditText)view.findViewById(R.id.query);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);

        /**
         * Setting of chosen option and implementing suggestions accordingly
         */
        option.setImageResource(optionImageId);
        optionName.setText(optionNameId);

        /**
         * RecyclerView setup for Suggestions List
         */
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        switch (optionNameId){
            case "Category":suggestionList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.category_list)));
                break;
            case "Search": suggestionList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.search_list)));
                break;
            case "Sort": suggestionList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sort_list)));
                break;
        }
        searchListAdapter=new SearchListAdapter(suggestionList);
        recyclerView.setAdapter(searchListAdapter);

        /**
         * Change in query detected, and suggestions displayed accordingly
         */
        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> result= searchListAdapter.searchResult(s);
                if(result.isEmpty())
                    result=suggestionList;
                searchListAdapter=new SearchListAdapter(result);
                recyclerView.setAdapter(searchListAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**
         * OnClickListener to cancel the query entry
         */
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}
