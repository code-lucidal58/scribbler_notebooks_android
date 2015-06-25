package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.Activities.DealDetail;
import com.scribblernotebooks.scribblernotebooks.Activities.NavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.Activities.ScannerActivity;
import com.scribblernotebooks.scribblernotebooks.Adapters.CategoryListAdapter;
import com.scribblernotebooks.scribblernotebooks.Adapters.RecyclerDealsAdapter;
import com.scribblernotebooks.scribblernotebooks.Adapters.SearchListAdapter;
import com.scribblernotebooks.scribblernotebooks.CustomListeners.HidingScrollListener;
import com.scribblernotebooks.scribblernotebooks.CustomListeners.RecyclerItemClickListener;
import com.scribblernotebooks.scribblernotebooks.Handlers.UserHandler;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Categories;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.DealListResponse;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ParseJson;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ShakeEventManager;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONArray;
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

public class DealsFragment extends Fragment implements NavigationDrawer.OnNavKeyPressed {

    private static final String URL_STRING = "url";
    private static final String TITLE = "title";
    int PAGE_LIMIT = 5;

    public int ACTION_SEARCH = 1;
    public int ACTION_DEFAULT = 0;
    int action=0;
    ArrayList<Categories> categoryList = null;

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
    //    TextView noConnectionText;
    SwipeRefreshLayout swipeRefreshLayout;
    Boolean reload;
    ShakeEventManager shakeEventManager = null;

    Boolean finished = false;
    Boolean isFirst = true;

    String category = "", searchQuery = "", sort = "";

    private String url, title;

    final int DEAL_DETAIL_REQUEST_CODE = 50;

    Boolean parametersChanged = false;

    Boolean isloading = true;
    Boolean isEmpty = true;


    RecyclerView suggestions;
    LinearLayout originalLayout, replacedLayout;
    ImageView selectedIcon;
    TextView selectionIconName;
    AutoCompleteTextView selectedIconQuery;
    Boolean isOptionOpened = false;
    SearchListAdapter searchListAdapter, querySearchListAdapter;

    LinearLayout loadingProgressLayout;
    ImageView loadingCharacter;
    TextView loadingMessage;
    ProgressBar loadingBar;

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    int page = 1, dealCount = 0, pageCount = 0;

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
//        noConnectionText = (TextView) v.findViewById(R.id.noConnectionText);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        reload = true;


        //Retrieve Categories
        new CategoriesRetriever().execute();

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
        selectedIconQuery = (AutoCompleteTextView) searchbar.findViewById(R.id.selectedQuery);
        loadingProgressLayout = (LinearLayout) v.findViewById(R.id.loadingProgress);
        loadingCharacter = (ImageView) loadingProgressLayout.findViewById(R.id.loadingCharacter);
        loadingMessage = (TextView) loadingProgressLayout.findViewById(R.id.loadingMessage);
        loadingBar = (ProgressBar) loadingProgressLayout.findViewById(R.id.loadingBar);

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
                showToolbarOptions();
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
//                        Toast.makeText(context, "Shaken", Toast.LENGTH_SHORT).show();
                        reload = true;
                        runAsyncTask();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == DEAL_DETAIL_REQUEST_CODE) {
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
        String[] suggestions1 = new String[0];
        selectedIconQuery.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, suggestions1));
//        recyclerView.setPadding(swipeRefreshLayout.getPaddingLeft(), initialTopPadding, swipeRefreshLayout.getPaddingRight(), swipeRefreshLayout.getPaddingBottom());
        if (parametersChanged) {
            dealsList.clear();
            Log.e("DealFragment", "ShowToolbarOptions " + page + " " + category + " " + searchQuery + " " + sort);
            page = 1;
            new CategoriesRetriever().execute();
            if (!searchQuery.isEmpty()) {
                new LongOperation(ACTION_SEARCH).execute(String.valueOf(page), "", searchQuery, "");
            } else {
                new LongOperation(action).execute(String.valueOf(page), category, searchQuery, sort);
            }
            parametersChanged = false;
        }
        replacedLayout.setVisibility(View.GONE);
        originalLayout.setVisibility(View.VISIBLE);
        suggestions.setVisibility(View.GONE);
//        toolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    int initialTopPadding = 0;
    String tag;

    /**
     * Hide the category, search, scan and sort options and show the corresponding menu
     */
    public void hideToolbarOptions(String tag1) {
        Log.e("DealFragment", "Isloading: " + isloading + " isEmpty:" + isEmpty);
        if (isloading || isEmpty) {
            return;
        }
        isOptionOpened = true;
        tag = tag1;
        int height = replacedLayout.getHeight();

        toolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        replacedLayout.setVisibility(View.VISIBLE);
        //originalLayout.setVisibility(View.GONE);
        selectedIconQuery.setText("");
        if (tag.equalsIgnoreCase("search")) {
            UserHandler handler = new UserHandler(getActivity());
            String[] suggestions = handler.getSuggestions();
            handler.close();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, suggestions);
            selectedIconQuery.setAdapter(adapter);
            selectedIconQuery.setThreshold(1);
        }
        ArrayList<String> suggestionList = new ArrayList<>();
        replacedLayout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedIconQuery.getText().toString().isEmpty()) {
                    selectedIconQuery.setText("");
                }
                switch (tag) {
                    case "category":
                        category = "";
                        break;
                    case "search":
                        searchQuery = "";
                        break;
                    case "sort":
                        sort = "";
                        break;
                }
                Log.e("DealFragment", "Search Cleared " + page + " " + category + " " + searchQuery + " " + sort);
            }
        });

        replacedLayout.findViewById(R.id.searchIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText view = (EditText) replacedLayout.findViewById(R.id.selectedQuery);
                String text = view.getText().toString();
                switch (tag) {
                    case "category":
                        category = text;
                        Log.e("DealFragment", "Category Changed " + category);
                        break;
                    case "search":
                        searchQuery = text;
                        Log.e("DealFragment", "SearchQuery Changed " + searchQuery);
                        break;
                    case "sort":
                        sort = text;
                        Log.e("DealFragment", "Sort Changed " + sort);
                        break;
                }
                Log.e("DealFragment", "Search Clicked " + page + " " + category + " " + searchQuery + " " + sort);
                parametersChanged = true;
                showToolbarOptions();
            }
        });

        switch (tag) {
            case "category":
                selectedIcon.setImageDrawable(getResources().getDrawable(R.drawable.category));
                selectionIconName.setText("Category");
                selectedIconQuery.setHint("Enter Category...");

//                initialTopPadding=recyclerView.getPaddingTop();
//                recyclerView.setPadding(swipeRefreshLayout.getPaddingLeft(), 70 + 55 + 200, swipeRefreshLayout.getPaddingRight(), swipeRefreshLayout.getPaddingBottom());

                suggestions.setVisibility(View.VISIBLE);
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

//                initialTopPadding=recyclerView.getPaddingTop();
//                recyclerView.setPadding(swipeRefreshLayout.getPaddingLeft(), 70+55+200, swipeRefreshLayout.getPaddingRight(), swipeRefreshLayout.getPaddingBottom());
                suggestions.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        if (tag.equalsIgnoreCase("category")) {
            CategoryListAdapter categoryListAdapter = new CategoryListAdapter(categoryList, tag);
            suggestions.setAdapter(categoryListAdapter);
        } else {
            searchListAdapter = new SearchListAdapter(suggestionList, tag);
            suggestions.setAdapter(searchListAdapter);
        }

        querySearchListAdapter = new SearchListAdapter(suggestionList, tag);
        selectedIconQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> result = querySearchListAdapter.searchResult(s);
                searchListAdapter = new SearchListAdapter(result, tag);
                suggestions.setAdapter(searchListAdapter);
                switch (tag) {
                    case "category":
                        category = s.toString();
                        break;
                    case "search":
                        searchQuery = s.toString();
                        break;
                    case "sort":
                        sort = s.toString();
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        selectedIconQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    switch (tag) {
                        case "search":
                            searchQuery = v.getText().toString();
                            parametersChanged = true;
                            Log.e("DealFragment", "SearchQuery set to " + searchQuery);
                            showToolbarOptions();
                    }

                }
                return false;
            }
        });

        suggestions.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<String> list;
                switch (tag) {
                    case "category":
                        ArrayList<Categories> categories = categoryList;
                        category = categories.get(position).getId();
                        parametersChanged = true;
                        Log.e("DealFragment", "Category set to " + category);
                        showToolbarOptions();
                        break;
                    case "search":
//                        list = Arrays.asList(getResources().getStringArray(R.array.search_list));
//                        searchQuery = list.get(position);
//                        parametersChanged=true;
//                        Log.e("DealFragment","SearchQuery set to "+searchQuery);
//                        showToolbarOptions();
                        break;
                    case "sort":
                        list = Arrays.asList(getResources().getStringArray(R.array.sort_list));
                        sort = list.get(position);
                        parametersChanged = true;
                        Log.e("DealFragment", "Sort set to " + sort);
                        showToolbarOptions();
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
        new LongOperation(action).execute(String.valueOf(page), category, searchQuery,sort);
    }

    @Override
    public boolean onBackKeyPressed() {
        Log.e("Deal NavigationDrawer", "Back key passed");
        if (isOptionOpened) {
            Log.e("DealavigationDrawer", "Back key passed" + true);
            showToolbarOptions();
            return true;
        }
        Log.e("Deal NavigationDrawer", "Back key passed " + false);
        return false;
    }


    /**
     * Async task to get the data from the server and process it
     */
    public class LongOperation extends AsyncTask<String, Void, String> {
        Boolean isFeatured = false;

        int action;

        public LongOperation() {
            this(0);
        }

        public LongOperation(int action) {
            this.action = action;
        }

        String search = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (reload) {
//                progressDialog.show();
                reload = false;
            }
            isloading = true;
            if (page == 1) {
                isEmpty = true;
                try {
                    recyclerView.setAdapter(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                loadingProgressLayout.setVisibility(View.VISIBLE);
                loadingMessage.setText(getResources().getString(R.string.dealListLoading));
                loadingCharacter.setImageResource(R.drawable.child_searching_happy);
                loadingBar.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected String doInBackground(String... queries) {

            String response = "";
            try {
                String page = queries[0];
                String searchQuery = queries[2];
                search = searchQuery;
                String category = queries[1];
                String sort = queries[3];

                User user = Constants.getUser(context);
                URL url;
                Log.e("DealsFragment", "Action: " + action);
                if (action == ACTION_DEFAULT)
                    url = new URL(Constants.ServerUrls.dealList + "?token=" + user.getToken() + "&page=" + page + "&category=" + category + "&searchQuery=" + searchQuery + "&sortBy=" + sort);
                else
                    url = new URL(Constants.ServerUrls.searchDeal + "?searchQuery=" + searchQuery + "&page=" + page);
                Log.e("DealFragment", "Url: " + url.toString());
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
            isloading = false;
            Log.e("DealFragment", "Response " + s);
//            progressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);

            DealListResponse dealListResponse = ParseJson.getParsedData(s);
            try {
                if (s.isEmpty() || dealListResponse==null) {
                    Log.e("DealsFragment", "Is Empty1: " + isEmpty);
                    if(dealsList.isEmpty())
                    {
                        isEmpty = true;
                    }
                    Log.e("DealsFragment", "Is Empty2: " + isEmpty);
                    loadingBar.setVisibility(View.GONE);
                    loadingCharacter.setImageResource(R.drawable.child_searching_slept);
                    loadingMessage.setText(getResources().getString(R.string.dealListLoadingError));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isEmpty = false;

            Log.e("DealsFragment", "Is Empty3: " + isEmpty);
            if (action == ACTION_SEARCH) {
                Log.e("Deal Fragment", "Adding Suggestion");
                UserHandler handler = new UserHandler(getActivity());
                handler.addSuggestions(search);
                handler.close();
            }

            loadingProgressLayout.setVisibility(View.GONE);

            JSONObject object = null;
            ArrayList<Deal> dealsList1 = null;

            isEmpty = false;

            Log.e("DealFragment", "Response: " + dealListResponse.getPageCount() + dealListResponse.getCurrentPage() + dealListResponse.getDealCount());
            dealsList1 = dealListResponse.getDealList();

            page = dealListResponse.getCurrentPage();
            if (page == 1) {
                Log.e("DealFragment", "Running for first time. Clearing original list");
                dealsList.clear();
            }
            if (dealListResponse.getCurrentPage() == dealListResponse.getPageCount()) {
                Log.e("DealFragment", "Finished");
                finished = true;
            }else{
                finished=false;
            }
            if (!finished) {
                page += 1;
                Log.e("DealFragment", "Finished =false, Page=" + page);
            }

            try {
                dealsList.addAll(dealsList1);
                Log.e("DealFragment", "Deal list added to original");
            } catch (Exception e) {
                Log.e("DealFragment", "Error adding deals to original list");
                e.printStackTrace();
            }

            if (page == 1) {
                Log.e("Running", "First Time");
                adapter = new RecyclerDealsAdapter(dealsList, context);
            } else {
                Log.e("Running", "Not First Time");
                adapter = new RecyclerDealsAdapter(dealsList, context);
                recyclerView.scrollToPosition((dealListResponse.getCurrentPage() - 1) * PAGE_LIMIT);
                adapter.notifyDataSetChanged();
            }
            setAdapterHolder();
            recyclerView.setAdapter(adapter);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            if (shakeEventManager != null) {
                shakeEventManager.resume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        reload = true;
    }


    @Override
    public void onPause() {
        super.onPause();

        try {
            shakeEventManager.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Run AsyncTask Only after phone is connected to internet
     */
    public void runAsyncTask() {
        new CategoriesRetriever().execute();
        new LongOperation(action).execute("1", category, searchQuery, sort);

    }

    void setAdapterHolder() {
        adapter.setItemClickListener(new RecyclerDealsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, ArrayList<Deal> deals) {
                showDealDetail(position, deals);
            }
        });


        if (adapter.getItemCount() < 5) {
            return;
        }


        adapter.setViewHolderListener(new RecyclerDealsAdapter.onViewHolderListener() {
            @Override
            public void onRequestedLastItem() {
                if (!finished) {
                    Log.e("DealFragment", "Loading Next Page " + page);
                    LoadNextPage();

                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NavigationDrawer) getActivity()).setKeyListener(this);
    }

    public void showDealDetail(int position, ArrayList<Deal> deals) {
        Intent i = new Intent(context, DealDetail.class);
        i.putParcelableArrayListExtra(Constants.PARCELABLE_DEAL_LIST_KEY, deals);
        i.putExtra(Constants.CURRENT_DEAL_INDEX, position);
        startActivityForResult(i, DEAL_DETAIL_REQUEST_CODE);
    }

    class CategoriesRetriever extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.ServerUrls.dealCategories);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(5000);
                connection.setDoInput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                return reader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                Log.e("Category", "Retrieving again");
                doInBackground();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (Boolean.parseBoolean(jsonObject.optString("success"))) {
                    JSONObject data = jsonObject.optJSONObject("data");
                    categoryList = new ArrayList<>();

                    JSONArray list = data.optJSONArray("dealcategories");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject categoryObject = list.optJSONObject(i);
//                        Log.e("CategoryList", categoryObject.optString("name"));
                        Categories categories = new Categories(categoryObject.optString("_id"), categoryObject.optString("name"));
                        categoryList.add(categories);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException n) {
                Log.e("Category", "Null reposne recieved and tried to parse");
            }
            super.onPostExecute(s);
        }
    }

}
