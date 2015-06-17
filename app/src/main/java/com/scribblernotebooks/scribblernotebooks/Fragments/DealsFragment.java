package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.util.Log;
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

import com.scribblernotebooks.scribblernotebooks.Activities.DealDetail;
import com.scribblernotebooks.scribblernotebooks.Activities.ScannerActivity;
import com.scribblernotebooks.scribblernotebooks.Adapters.RecyclerDealsAdapter;
import com.scribblernotebooks.scribblernotebooks.Adapters.SearchListAdapter;
import com.scribblernotebooks.scribblernotebooks.CustomListeners.HidingScrollListener;
import com.scribblernotebooks.scribblernotebooks.CustomListeners.RecyclerItemClickListener;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ParseJson;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ShakeEventManager;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DealsFragment extends android.support.v4.app.Fragment {

    private static final String URL_STRING = "url";
    private static final String TITLE = "title";
    int PAGE_LIMIT=5;

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

    Boolean finished=false;
    Boolean isFirst=true;

    String category = "", searchQuery = "";

    private String url, title;

    final int DEAL_DETAIL_REQUEST_CODE=50;

    Boolean parametersChanged=false;

    private OnFragmentInteractionListener mListener;


    RecyclerView suggestions;
    LinearLayout originalLayout, replacedLayout;
    ImageView selectedIcon;
    TextView selectionIconName;
    EditText selectedIconQuery;
    Boolean isOptionOpened = false;
    SearchListAdapter searchListAdapter, querySearchListAdapter;

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    String page = "1";

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

        recyclerView.computeScroll();

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
        try {
            shakeEventManager = new ShakeEventManager(context);
            shakeEventManager.setOnShakeListener(new ShakeEventManager.OnShakeListener() {
                @Override
                public void onShake() {
                    try {
                        Toast.makeText(context, "Shaken", Toast.LENGTH_SHORT).show();
                        reload = true;
                        runAsyncTask();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode==DEAL_DETAIL_REQUEST_CODE) {
                adapter = new RecyclerDealsAdapter(dealsList, context);
                setAdapterHolder();
                recyclerView.setAdapter(adapter);
                Log.e("DealFragment", "In Activity Result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * To show the basic option of category, search, scan and sort
     */
    public void showToolbarOptions() {
        isOptionOpened = false;
        if(parametersChanged){
            new LongOperation().execute(page, category, searchQuery);
        }
        replacedLayout.setVisibility(View.GONE);
        originalLayout.setVisibility(View.VISIBLE);
        suggestions.setVisibility(View.GONE);
//        toolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    /**
     * Hide the category, search, scan and sort options and show the corresponding menu
     */
    public void hideToolbarOptions(final String tag) {
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
        searchListAdapter = new SearchListAdapter(suggestionList,tag);
        suggestions.setAdapter(searchListAdapter);

        querySearchListAdapter = new SearchListAdapter(suggestionList,tag);
        selectedIconQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> result = querySearchListAdapter.searchResult(s);
                searchListAdapter = new SearchListAdapter(result,tag);
                suggestions.setAdapter(searchListAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        suggestions.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<String> list;
                switch (tag) {
                    case "category":
                        list = Arrays.asList(getResources().getStringArray(R.array.category_list));
                        category = list.get(position);
                        parametersChanged=true;
                        Log.e("DealFragment","Category set to "+category);
                        showToolbarOptions();
                        break;
                    case "search":
                        list = Arrays.asList(getResources().getStringArray(R.array.search_list));
                        searchQuery = list.get(position);
                        parametersChanged=true;
                        Log.e("DealFragment","SearchQuery set to "+searchQuery);
                        showToolbarOptions();
                        break;
                    case "sort":
                        parametersChanged=true;
                        break;
                    default:
                        break;
                }
            }
        }));


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

    public void LoadNextPage() {
        new LongOperation().execute(page, category, searchQuery);
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
        protected String doInBackground(String... queries) {

            String response = "";
            try {
                String page = queries[0];
                String searchQuery = queries[2];
                String category = queries[1];

                User user = Constants.getUser(context);

                HashMap<String, String> data = new HashMap<>();
                data.put("page", page);
                if(category!=null) {
                    if (!category.isEmpty())
                        data.put("category", category);
                }
                if(searchQuery!=null) {
                    if (!searchQuery.isEmpty())
                        data.put("searchQuery", searchQuery);
                }
                data.put("token", user.getToken());

                URL url = new URL(Constants.ServerUrls.dealList+"?token="+user.getToken()+"&page="+page+"&category="+category+"&searchQuery="+searchQuery);
                Log.e("DealFragment","Url: "+url.toString());
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
            Log.e("DealFragment","Response "+s);
            progressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
            JSONObject object = null;
            ArrayList<Deal> dealsList1=null;

            if(page.equals("1")){
                Log.e("DealFragment","Running for first time. Clearing original list");
                dealsList.clear();
            }

            try {
                object = new JSONObject(s);
                dealsList1 = ParseJson.getParsedData(s, context, isFeatured);
                if(dealsList1==null){
                    finished=true;
                    Log.e("DealFragment","Finished = true");
                }
                if(!finished) {
                    page = String.valueOf(Integer.parseInt(object.optString("page")) + 1);
                    Log.e("DealFragment","Finished =false, Page="+page);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                dealsList.addAll(dealsList1);
                Log.e("DealFragment","Deal list added to original" );
            }catch (Exception e){
                Log.e("DealFragment","Error adding deals to original list");
                e.printStackTrace();
            }

            if(page.equals("1")) {
                Log.e("Running","First Time");
                adapter = new RecyclerDealsAdapter(dealsList, context);
            }else {
                Log.e("Running","Not First Time");
                adapter = new RecyclerDealsAdapter(dealsList, context);
//                recyclerView.scrollToPosition(Integer.parseInt(page)*PAGE_LIMIT);
                adapter.notifyDataSetChanged();
            }
            setAdapterHolder();
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
        try {
            if (shakeEventManager != null) {
                shakeEventManager.resume();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        reload = true;
    }


    @Override
    public void onPause() {
        super.onPause();

        try {
            shakeEventManager.pause();
        }catch (Exception e){
            e.printStackTrace();
        }
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
            new LongOperation().execute(page, category, searchQuery);
        }
    }
    void setAdapterHolder(){
        adapter.setViewHolderListener(new RecyclerDealsAdapter.onViewHolderListener() {
            @Override
            public void onRequestedLastItem() {
                if (!finished) {
                    Log.e("DealFragment","Loading Next Page "+page);
                    LoadNextPage();

                }
            }
        });
        adapter.setItemClickListener(new RecyclerDealsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, ArrayList<Deal> deals) {
                showDealDetail(position, deals);
            }
        });
    }



    public void showDealDetail(int position, ArrayList<Deal> deals){
        Intent i=new Intent(context, DealDetail.class);
        i.putParcelableArrayListExtra(Constants.PARCELABLE_DEAL_LIST_KEY, deals);
        i.putExtra(Constants.CURRENT_DEAL_INDEX, position);
        startActivityForResult(i,DEAL_DETAIL_REQUEST_CODE);
    }

}
