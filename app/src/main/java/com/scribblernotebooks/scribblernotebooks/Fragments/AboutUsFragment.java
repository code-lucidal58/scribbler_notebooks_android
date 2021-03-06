package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.scribblernotebooks.scribblernotebooks.Activities.AboutScribblerNotebooks;
import com.scribblernotebooks.scribblernotebooks.Activities.FeedbackActivity;
import com.scribblernotebooks.scribblernotebooks.Adapters.AboutUsAdapter;
import com.scribblernotebooks.scribblernotebooks.CustomListeners.RecyclerItemClickListener;
import com.scribblernotebooks.scribblernotebooks.CustomViews.TermsPopup;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AboutUsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutUsFragment extends Fragment {

    /**
     * About Scribbler Notebooks
     * Terms and Condition
     * Privacy Policy
     * Feedback
     */
    String[] items = {"About Scribbler Notebooks", "Terms & Conditions", "Privacy Policy", "Feedback"};
    RecyclerView recyclerView;
    Context mContext;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AboutUsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutUsFragment newInstance() {
        AboutUsFragment fragment = new AboutUsFragment();
        return fragment;
    }

    public AboutUsFragment() {
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
        mContext = getActivity().getApplicationContext();
        View v = inflater.inflate(R.layout.fragment_about_us, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appBar);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);


        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.setSupportActionBar(toolbar);
        parentActivity.setTitle("About");
        parentActivity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        AboutUsAdapter adapter = new AboutUsAdapter(mContext, Arrays.asList(items));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(mContext, AboutScribblerNotebooks.class));
                        break;
                    case 1:
                        //TODO: terms and conditions
                        TermsPopup popup = new TermsPopup(getActivity());
                        popup.setUrl(Constants.ServerUrls.termsAndConditions);
                        popup.setTitle("Terms & Conditions");
                        popup.show();
                        break;
                    case 2:
                        //TODO: privacy policy
                        TermsPopup popup1 = new TermsPopup(getActivity());
                        popup1.setUrl(Constants.ServerUrls.privacyPolicy);
                        popup1.setTitle("Privacy Policy");
                        popup1.show();
                        break;
                    case 3:
                        startActivity(new Intent(mContext, FeedbackActivity.class));
                        break;
                    default:
                        break;
                }
            }
        }
        ));

        setHasOptionsMenu(true);

        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_no_item,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            getActivity().onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


}
