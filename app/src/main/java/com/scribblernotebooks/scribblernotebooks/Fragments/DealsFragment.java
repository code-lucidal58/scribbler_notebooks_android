package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.scribblernotebooks.scribblernotebooks.Activities.ScannerActivity;
import com.scribblernotebooks.scribblernotebooks.Adapters.RecyclerDealsAdapter;
import com.scribblernotebooks.scribblernotebooks.Adapters.SearchListAdapter;
import com.scribblernotebooks.scribblernotebooks.CustomListeners.HidingScrollListener;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ParseJson;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ShakeEventManager;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DealsFragment extends android.support.v4.app.Fragment {

    private static final String URL_STRING = "url";
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
    ShakeEventManager shakeEventManager = null;

    private String url, title;

    private OnFragmentInteractionListener mListener;


    RecyclerView suggestions;
    LinearLayout originalLayout, replacedLayout;
    ImageView selectedIcon;
    TextView selectionIconName;
    EditText selectedIconQuery;
    Boolean isOptionOpened = false;
    SearchListAdapter searchListAdapter, querySearchListAdapter;

    /**
     * Setting statically the new fragment
     *
     * @param url the url to be sent to server for getting listview_item_deals
     * @return the Deal list fragment
     */
    public static DealsFragment newInstance(String url, String title) {
        DealsFragment fragment = new DealsFragment();
        Bundle args = new Bundle();
        args.putString(URL_STRING, url);
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
            url = getArguments().getString(URL_STRING);
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
         * Option Select Animation and toggling
         */
        View category = searchbar.findViewById(R.id.layoutCategory);
        View search = searchbar.findViewById(R.id.layoutSearch);
        View sort = searchbar.findViewById(R.id.layoutSort);
        View scan = searchbar.findViewById(R.id.layoutScan);

        suggestions = (RecyclerView) searchbar.findViewById(R.id.recyclerView);
        suggestions.setLayoutManager(new LinearLayoutManager(context));
        originalLayout = (LinearLayout) searchbar.findViewById(R.id.originalLinearLayout);
        replacedLayout = (LinearLayout) searchbar.findViewById(R.id.replacedLinearLayout);
        selectedIcon = (ImageView) searchbar.findViewById(R.id.selectedIcon);
        selectionIconName = (TextView) searchbar.findViewById(R.id.selectedIcon_name);
        selectedIconQuery = (EditText) searchbar.findViewById(R.id.selectedQuery);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideToolbarOptions("category");
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideToolbarOptions("search");
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideToolbarOptions("sort");
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ScannerActivity.class));
            }
        });

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

        /**Dynamically Changing the recycler view content*/
        recyclerView.addOnScrollListener(new HidingScrollListener(context) {

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
                reload = true;
                runAsyncTask();
            }
        });
        return v;
    }


    /**
     * To show the basic option of category, search, scan and sort
     */
    public void showToolbarOptions() {
        isOptionOpened = false;
        replacedLayout.setVisibility(View.GONE);
        originalLayout.setVisibility(View.VISIBLE);
        suggestions.setVisibility(View.GONE);
//        toolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    /**
     * Hide the category, search, scan and sort options and show the corresponding menu
     */
    public void hideToolbarOptions(String tag) {
        isOptionOpened = true;
//        toolbarContainer.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(2)).start();
        toolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        replacedLayout.setVisibility(View.VISIBLE);
        originalLayout.setVisibility(View.GONE);
        suggestions.setVisibility(View.VISIBLE);
        selectedIconQuery.setText("");
        ArrayList<String> suggestionList = new ArrayList<>();
        replacedLayout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolbarOptions();
            }
        });
        switch (tag) {
            case "category":
                selectedIcon.setImageDrawable(getResources().getDrawable(R.drawable.category));
                selectionIconName.setText("Category");
                selectedIconQuery.setHint("Enter Category...");
                suggestionList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.category_list)));
                break;
            case "search":
                selectedIcon.setImageDrawable(getResources().getDrawable(R.drawable.search));
                selectionIconName.setText("Search");
                selectedIconQuery.setHint("Name, Location, Content...");
                suggestionList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.search_list)));
                break;
            case "sort":
                selectedIcon.setImageDrawable(getResources().getDrawable(R.drawable.sort));
                selectionIconName.setText("Sort");
                selectedIconQuery.setHint("Sort by...");
                suggestionList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sort_list)));
                break;
            default:
                break;
        }
        searchListAdapter = new SearchListAdapter(suggestionList);
        suggestions.setAdapter(searchListAdapter);

        querySearchListAdapter = new SearchListAdapter(suggestionList);
        selectedIconQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> result = querySearchListAdapter.searchResult(s);
                searchListAdapter = new SearchListAdapter(result);
                suggestions.setAdapter(searchListAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                User user=Constants.getUser(context);
                URL url = new URL(Constants.ServerUrls.dealList+"?token="+user.getToken());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                return reader.readLine();

            } catch (Exception e) {
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
     */
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
        if (isOptionOpened) {
            showToolbarOptions();
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
        if (shakeEventManager != null) {
            shakeEventManager.resume();
        }
        reload = true;
        getNotificationStatus();
    }

    public void getNotificationStatus() {
        SharedPreferences sd = PreferenceManager.getDefaultSharedPreferences(context);
        boolean onoff = sd.getBoolean(Constants.PREF_NOTIFICATION_ON_OFF, true);
        boolean dealofday = sd.getBoolean(Constants.PREF_NOTIFICATION_DEAL_OF_DAY, true);
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
        public void onFragmentInteraction();
    }

    /**
     * Run AsyncTask Only after phone is connected to internet
     */
    public void runAsyncTask() {
        if (!Constants.isNetworkAvailable(getActivity())) {
            noConnectionText.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            noConnectionText.setVisibility(View.GONE);
            //Get response from server
            new LongOperation().execute(url);
        }
    }


}
