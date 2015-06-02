package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.Adapters.HidingScrollListener;
import com.scribblernotebooks.scribblernotebooks.Adapters.RecyclerDealsAdapter;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ParseJson;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.SearchBarApplication;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ShakeEventManager;
import com.scribblernotebooks.scribblernotebooks.R;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;

public class DealsFragment extends android.support.v4.app.Fragment {

    private static final String URL = "url";
    private static final String TITLE = "title";

    RecyclerView recyclerView;
    ArrayList<Deal> dealsList = new ArrayList<>();
    RecyclerDealsAdapter adapter;
    Context context;
    ProgressDialog progressDialog;
    Toolbar appbar;
    View searchbar;
    LinearLayout toolbarContainer;
    int mToolbarHeight;
    DrawerLayout mDrawerLayout;
    RelativeLayout mDrawer;
    TextView noConnectionText;
    SwipeRefreshLayout swipeRefreshLayout;
    Boolean reload;
    ShakeEventManager shakeEventManager=null;

    // TODO: Rename and change types of parameters

    private String url, title;

    private OnFragmentInteractionListener mListener;


    /**
     * Setting statically the new fragment
     *
     * @param url the url to be sent to server for getting listview_item_deals
     * @return the Deal list fragment
     */
    public static DealsFragment newInstance(String url, String title) {
        DealsFragment fragment = new DealsFragment();
        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Auto Generated
     */
    public DealsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(URL);
            title = getArguments().getString(TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_deals, container, false);
        context = getActivity();
        noConnectionText = (TextView) v.findViewById(R.id.noConnectionText);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        reload = true;

        //Progress Dialog Setup
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading Deals...");
        progressDialog.setCancelable(false);

        //Setting toolbars
        toolbarContainer = (LinearLayout) v.findViewById(R.id.toolbar_container);
        appbar = (Toolbar) v.findViewById(R.id.app_bar);
        searchbar = v.findViewById(R.id.search_bar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(appbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().setTitle(title);

        /**
         * Calling functions for execution of the 4 functionality of search bar
         */
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        SearchBarApplication searchBarApplication = new SearchBarApplication(searchbar, container, context, fragmentManager);
        searchBarApplication.ImplementFunctions();

        /**
         * Navigation Drawer Hamburger Icon Setup
         */
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

        /**
         * Setting Deals List along with scrolling effects of toolbars
         */
        mToolbarHeight = getToolbarHeight(getActivity());
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        int paddingTop = getToolbarHeight(context) + getTabsHeight(context);
        recyclerView.setPadding(recyclerView.getPaddingLeft(), paddingTop, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setOnScrollListener(new HidingScrollListener(context) {

            @Override
            public void onMoved(int distance) {
                toolbarContainer.setTranslationY(-distance);
            }

            @Override
            public void onShow() {
                toolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void onHide() {
                toolbarContainer.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(2)).start();
            }

        });

        //Get response from server

        runAsyncTask();

        //recyclerView setup
        recyclerView.setLayoutManager(new GridLayoutManager(context, getResources().getInteger(R.integer.dealListColoumnCount)));

        /**
         * Swipe to refresh call
         */
        swipeRefreshLayout.setProgressViewOffset(true, 0, paddingTop);
        swipeRefreshLayout.setColorSchemeResources(R.color.yellow, R.color.green, R.color.red, R.color.darkBlue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload = false;
                runAsyncTask();
            }
        });

        /**
         * Shake to refresh
         */
        shakeEventManager = new ShakeEventManager(context);
        shakeEventManager.setOnShakeListener(new ShakeEventManager.OnShakeListener() {
            @Override
            public void onShake() {
                Toast.makeText(context, "Shaken", Toast.LENGTH_SHORT).show();
                reload=true;
                runAsyncTask();
            }
        });
        return v;
    }

    /**
     * Returning toolbar height for implementing animation
     *
     * @param context activity context
     * @return toolbarheight
     */
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.search);
    }


    /**
     * To change to number of columns in the deal list. 1 when portrait and 2 when landscape
     *
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
        Boolean isFeatured = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (reload) {
                progressDialog.show();
                reload = false;
            }
        }

        @Override
        protected String doInBackground(String... urls) {

            String response = "";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = client.execute(httpGet, responseHandler);
                //TODO: some codes relating to database is missing here
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
            dealsList.clear();
            dealsList = ParseJson.getParsedData(s, context, isFeatured);
            adapter = new RecyclerDealsAdapter(dealsList, context);
            recyclerView.setAdapter(adapter);
        }
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

    @Override
    public void onResume() {
        super.onResume();
        if(shakeEventManager!=null){
            shakeEventManager.resume();
        }
        reload = true;
        getNotificationStatus();
    }

    public void getNotificationStatus(){
        SharedPreferences sd= PreferenceManager.getDefaultSharedPreferences(context);
        boolean onoff=sd.getBoolean(Constants.PREF_NOTIFICATION_ON_OFF, true);
        boolean dealofday=sd.getBoolean(Constants.PREF_NOTIFICATION_DEAL_OF_DAY,true);
        Toast.makeText(context,Boolean.toString(onoff)+Boolean.toString(dealofday),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        shakeEventManager.pause();
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

    /**
     * Run AsyncTask Only after phone is connected to internet
     */
    public void runAsyncTask() {
        if (!Constants.isNetworkAvailable(getActivity())) {
            noConnectionText.setVisibility(View.VISIBLE);
        } else {
            noConnectionText.setVisibility(View.GONE);
            //Get response from server
            new LongOperation().execute(url);
        }
    }


}
