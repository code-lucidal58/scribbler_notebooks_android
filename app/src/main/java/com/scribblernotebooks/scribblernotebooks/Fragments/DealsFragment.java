package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.Activities.DealDetail;
import com.scribblernotebooks.scribblernotebooks.Activities.NavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.Activities.NotificationsActivity;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DealsFragment extends Fragment implements NavigationDrawer.OnNavKeyPressed {

    private static final String URL_STRING = "url";
    private static final String TITLE = "title";
    public static final int CATEGORY = 1;
    public static final int SEARCH = 2;
    public static final int SORT = 3;
    public static final int NONE = 0;
    public static int OPEN_PARAMETER = NONE;

    int PAGE_LIMIT = 10;

    int type = RecyclerDealsAdapter.TYPE_GRID;

    boolean grid = true;
    boolean openingFirstTime = false;
    public int ACTION_SEARCH = 1;

    public int ACTION_DEFAULT = 0;
    int action = 0;

    ArrayList<Categories> categoryList = null;
    ArrayList<Deal> dealsList = new ArrayList<>();

    RecyclerDealsAdapter adapter;
    SearchListAdapter searchListAdapter;

    FloatingActionButton fab;
    RelativeLayout previousLayout, mDrawer;
    RecyclerView recyclerView, suggestions;
    LinearLayout toolbarContainer, originalLayout, replacedLayout, loadingProgressLayout;
    DrawerLayout mDrawerLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    View searchbar, categoryView, searchView, sortView;
    boolean firstTime = false;
    Context context;
    Toolbar appbar;
    int mToolbarHeight;
    ShakeEventManager shakeEventManager = null;
    Boolean finished = false,
            reload,
            parametersChanged = false,
            isloading = true,
            isEmpty = true,
            isOptionOpened = false,
            changeIndicator = false;

    String category = "", searchQuery = "", sort = "";

    private String url, title;

    final int DEAL_DETAIL_REQUEST_CODE = 50;

    AutoCompleteTextView selectedIconQuery;

    ImageView loadingCharacter;
    TextView loadingMessage;
    ProgressBar loadingBar;
    String categoryName = "";
    CoordinatorLayout coordinatorLayout;
    int page = 1;

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
     * Setting statically the new fragment
     *
     * @return the Deal list fragment
     */
    public static DealsFragment newInstance(String title) {
        DealsFragment fragment = new DealsFragment();
        Bundle args = new Bundle();
        args.putString(URL_STRING, "");
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
        firstTime = true;
        openingFirstTime = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_deals, container, false);
        context = getActivity();
//        noConnectionText = (TextView) v.findViewById(R.id.noConnectionText);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinatorLayout);
        reload = true;

        //Setting toolbars
        toolbarContainer = (LinearLayout) v.findViewById(R.id.toolbar_container);
        appbar = (Toolbar) v.findViewById(R.id.app_bar);
        searchbar = v.findViewById(R.id.search_bar);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        ((AppCompatActivity) getActivity()).setSupportActionBar(appbar);
        getActivity().setTitle(title);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeLayout();
//            }
//        });

        /**
         * Option Select Animation and toggling
         */
        categoryView = searchbar.findViewById(R.id.layoutCategory);
        searchView = searchbar.findViewById(R.id.layoutSearch);
        sortView = searchbar.findViewById(R.id.layoutSort);

        suggestions = (RecyclerView) searchbar.findViewById(R.id.recyclerView);
        suggestions.setLayoutManager(new LinearLayoutManager(context));
        originalLayout = (LinearLayout) searchbar.findViewById(R.id.originalLinearLayout);
        replacedLayout = (LinearLayout) searchbar.findViewById(R.id.replacedLinearLayout);
//        selectionIconName = (TextView) searchbar.findViewById(R.id.selectedIcon_name);
        selectedIconQuery = (AutoCompleteTextView) searchbar.findViewById(R.id.selectedQuery);
        loadingProgressLayout = (LinearLayout) v.findViewById(R.id.loadingProgress);
        loadingCharacter = (ImageView) loadingProgressLayout.findViewById(R.id.loadingCharacter);
        loadingMessage = (TextView) loadingProgressLayout.findViewById(R.id.loadingMessage);
        loadingBar = (ProgressBar) loadingProgressLayout.findViewById(R.id.loadingBar);

        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OPEN_PARAMETER == CATEGORY) {
                    boolean none = false;
                    if (previousLayout != null) {
                        previousLayout.getChildAt(1).setVisibility(View.GONE);
                        none = true;
                    }
                    changeIndicator = true;
                    OPEN_PARAMETER = NONE;
                    showToolbarOptions(none);
                    return;
                }
                OPEN_PARAMETER = CATEGORY;
                hideToolbarOptions("category", v);
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OPEN_PARAMETER == SEARCH) {
                    boolean none = false;
                    changeIndicator = true;
                    if (previousLayout != null) {
                        previousLayout.getChildAt(1).setVisibility(View.GONE);
                        none = true;
                    }
                    showToolbarOptions(none);
                    OPEN_PARAMETER = NONE;
                    return;
                }
                OPEN_PARAMETER = SEARCH;
                hideToolbarOptions("search", v);
            }
        });

        sortView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OPEN_PARAMETER == SORT) {
                    boolean none = false;
                    changeIndicator = true;
                    if (previousLayout != null) {
                        previousLayout.getChildAt(1).setVisibility(View.GONE);
                        none = true;
                    }
                    showToolbarOptions(none);
                    OPEN_PARAMETER = NONE;
                    return;
                }
                OPEN_PARAMETER = SORT;
                hideToolbarOptions("sort", v);
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
//        linearLayoutManager=new LinearLayoutManager(context);
//        recyclerView.setLayoutManager(new GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false));
//        recyclerView.setLayoutManager(linearLayoutManager);


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

        if (openingFirstTime) {
            //Get response from server
            runAsyncTask();
            openingFirstTime = false;
        }

        //recyclerView setup
//        recyclerView.setLayoutManager(new GridLayoutManager(context, getResources().getInteger(R.integer.dealListColoumnCount)));
        changeLayout(false);
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

        try{
            File file=new File(new File(Environment.getExternalStorageDirectory(),"scribbler"),"tmpCat.tmp");
            BufferedReader reader=new BufferedReader(new FileReader(file));
            String s="",line=null;
            while((line=reader.readLine())!=null){
                s+=line;
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(s);
            if (Boolean.parseBoolean(jsonObject.optString("success"))) {
                JSONObject data = jsonObject.optJSONObject("data");
                categoryList = new ArrayList<>();

                JSONArray list = data.optJSONArray("dealcategories");
                categoryList=new ArrayList<>();
                for (int i = 0; i < list.length(); i++) {
                    JSONObject categoryObject = list.optJSONObject(i);
//                        Log.e("CategoryList", categoryObject.optString("name"));
                    Categories categories = new Categories(categoryObject.optString("_id"), categoryObject.optString("name"));
                    categoryList.add(categories);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

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
        setHasOptionsMenu(true);
        return v;
    }

    void changeLayout() {
        changeLayout(true);
    }

    void changeLayout(Boolean changeStructure) {
        if (changeStructure) {
            grid = !grid;
        }
        if (grid) {
            type = RecyclerDealsAdapter.TYPE_LIST;
            adapter = new RecyclerDealsAdapter(dealsList, context, RecyclerDealsAdapter.TYPE_LIST);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            fab.setImageResource(R.drawable.ic_action_tiles_large);
        } else {
            type = RecyclerDealsAdapter.TYPE_GRID;
            adapter = new RecyclerDealsAdapter(dealsList, context, RecyclerDealsAdapter.TYPE_GRID);
            recyclerView.setAdapter(adapter);
//            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            fab.setImageResource(R.drawable.ic_action_list);
        }
        setAdapterHolder();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == DEAL_DETAIL_REQUEST_CODE) {
                changeLayout();
                setAdapterHolder();
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
        showToolbarOptions(false);
    }

    public void showToolbarOptions(boolean noneSelected) {
        isOptionOpened = false;
        OPEN_PARAMETER = NONE;
        hideKeyboard();

        if (noneSelected) {
            searchQuery = "";
            category = "";
            sort = "";
            parametersChanged = true;
        }
        if (parametersChanged) {
            dealsList.clear();
//            Log.e("DealFragment", "ShowToolbarOptions " + page + " " + category + " " + searchQuery + " " + sort);
            page = 1;
            if (!searchQuery.isEmpty()) {
                new DealFetcher(ACTION_SEARCH).execute(String.valueOf(page), "", searchQuery, "");
            } else {
                new DealFetcher(action).execute(String.valueOf(page), category, searchQuery, sort);
            }
            parametersChanged = false;
        }
        replacedLayout.setVisibility(View.GONE);
        suggestions.setVisibility(View.GONE);
    }

    String tag;

    /**
     * Hide the category, search, scan and sort options and show the corresponding menu
     */
    public void hideToolbarOptions(String tag1, View v) {
        RelativeLayout holdingLayout = (RelativeLayout) v;
        if (previousLayout != null) {
            previousLayout.getChildAt(1).setVisibility(View.GONE);
        }
        isOptionOpened = true;
        holdingLayout.getChildAt(1).setVisibility(View.VISIBLE);
        previousLayout = holdingLayout;
//        Log.e("DealFragment", "0 SearchQuery=" + searchQuery + " Category=" + category + " SortBy=" + sort);
//        Log.e("DealFragment", "Isloading: " + isloading + " isEmpty:" + isEmpty);
        if (isloading || isEmpty) {
            return;
        }
        tag = tag1;
        toolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();


        switch (OPEN_PARAMETER) {
            case CATEGORY:
                setupCategory();
                break;
            case SEARCH:
                setupSearch();
                break;
            case SORT:
                setupSort();
                break;
            case NONE:
                break;
            default:
                break;
        }

    }


    private void setupSort() {
        replacedLayout.setVisibility(View.GONE);
        ArrayList<String> suggestionList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sort_list)));
        suggestions.setVisibility(View.VISIBLE);
        suggestions.setAdapter(null);
        searchListAdapter = new SearchListAdapter(suggestionList, tag);
        suggestions.setAdapter(searchListAdapter);

        suggestions.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<String> list;
                list = Arrays.asList(getResources().getStringArray(R.array.sort_list));
                sort = list.get(position);
                parametersChanged = true;
                Log.e("DealFragment", "Sort set to " + sort);
                showToolbarOptions();
            }
        }));
    }

    private void setupCategory() {
        replacedLayout.setVisibility(View.GONE);
        suggestions.setVisibility(View.VISIBLE);
        suggestions.setAdapter(null);
        CategoryListAdapter categoryListAdapter = new CategoryListAdapter(categoryList, tag);
        suggestions.setAdapter(categoryListAdapter);

        suggestions.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArrayList<Categories> categories = categoryList;
                category = categories.get(position).getId();
                parametersChanged = true;
                categoryName = categories.get(position).getName();
//                Log.e("DealFragment", "Category set to " + category);
                showToolbarOptions();
            }
        }));
    }

    private void setupSearch() {
        replacedLayout.setVisibility(View.VISIBLE);
        suggestions.setVisibility(View.GONE);
        suggestions.setAdapter(null);

        UserHandler handler = new UserHandler(getActivity());
        String[] suggestions1 = handler.getSuggestions();
        handler.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, suggestions1);
        selectedIconQuery.setAdapter(adapter);
        selectedIconQuery.setThreshold(1);

        selectedIconQuery.setHint("Name, Location, Content...");
        selectedIconQuery.setText(searchQuery);

        selectedIconQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchQuery = v.getText().toString();
                    parametersChanged = true;
                    Log.e("DealFragment", "SearchQuery set to " + searchQuery);
                    showToolbarOptions();
                }
                return false;
            }
        });

        replacedLayout.findViewById(R.id.searchIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText view = (EditText) replacedLayout.findViewById(R.id.selectedQuery);
                searchQuery = view.getText().toString();
                Log.e("DealFragment", "SearchQuery Changed " + searchQuery);
                parametersChanged = true;
                Log.e("DealFragment", "Search Clicked " + page + " " + category + " " + searchQuery + " " + sort);
                showToolbarOptions();
            }
        });

        replacedLayout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedIconQuery.getText().toString().isEmpty()) {
                    selectedIconQuery.setText("");
                }
                searchQuery = "";
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

    public void LoadNextPage() {
        new DealFetcher(action).execute(String.valueOf(page), category, searchQuery, sort);
    }

    @Override
    public boolean onBackKeyPressed() {
        if (isOptionOpened) {
            showToolbarOptions();
            return true;
        }
        return false;
    }


    /**
     * Async task to get the data from the server and process it
     */
    public class DealFetcher extends AsyncTask<String, Void, String> {

        int action;

        public DealFetcher() {
            this(0);
        }

        public DealFetcher(int action) {
            this.action = action;
        }

        String search = "";
        Snackbar snackbar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (reload) {
                reload = false;
            }
            isloading = true;
            if (firstTime) {
                loadingProgressLayout.setVisibility(View.VISIBLE);
                loadingMessage.setText(getResources().getString(R.string.dealListLoading));
                loadingCharacter.setImageResource(R.drawable.child_searching_happy);
                loadingBar.setVisibility(View.VISIBLE);
            }
            if (page == 1 && firstTime) {
                isEmpty = true;
                try {
                    recyclerView.setAdapter(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                if (!changeIndicator) {
                    snackbar = Snackbar.make(coordinatorLayout, "Retrieving your deals...", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    changeIndicator = false;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
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
//                Log.e("DealsFragment", "Action: " + action);
                if (action == ACTION_DEFAULT)
                    url = new URL(Constants.ServerUrls.dealList + "?token=" + user.getToken() + "&page=" + page + "&category=" + category + "&searchQuery=" + searchQuery + "&sortBy=" + sort);
                else
                    url = new URL(Constants.ServerUrls.searchDeal + "?searchQuery=" + searchQuery + "&page=" + page);
//                Log.e("DealFragment", "Url: " + url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestProperty("Authorization", "Bearer " + user.getToken());
                connection.setDoInput(true);


                File ScribblerDirectory = new File(Environment.getExternalStorageDirectory(), "scribbler");
                InputStream inputStream = connection.getInputStream();
                ScribblerDirectory.mkdirs();
                if (inputStream != null) {
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(ScribblerDirectory, "tmpDeal.tmp"));
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, bufferLength);
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        File file = new File(ScribblerDirectory, "tmpDeal.tmp");
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String s = "";
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            s += line;
                        }
                        reader.close();
                        return s;
                    } catch (FileNotFoundException fe) {
                        fe.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            isloading = false;
            swipeRefreshLayout.setRefreshing(false);
            try {
                snackbar.dismiss();
                if (s.isEmpty() && page != 1) {
                    Snackbar.make(recyclerView, "Unable to process your request. Please try later", Snackbar.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            DealListResponse dealListResponse = ParseJson.getParsedData(s);
            try {
                if (s.isEmpty() || dealListResponse == null) {
                    if (dealsList.isEmpty()) {
                        isEmpty = true;
                    }
                    if (firstTime) {
                        loadingBar.setVisibility(View.GONE);
                        loadingCharacter.setImageResource(R.drawable.child_searching_slept);
                        loadingMessage.setText(getResources().getString(R.string.dealListLoadingError));
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isEmpty = false;


            if (page == 1) {
                recyclerView.setAdapter(null);
            }
            try {
                if (action == ACTION_SEARCH) {
                    UserHandler handler = new UserHandler(getActivity());
                    handler.addSuggestions(search);
                    handler.close();
                }

                loadingProgressLayout.setVisibility(View.GONE);
                firstTime = false;

                ArrayList<Deal> dealsList1;
                dealsList1 = dealListResponse.getDealList();

                page = dealListResponse.getCurrentPage();
                if (page == 1) {
//                    Log.e("DealFragment", "Running for first time. Clearing original list");
                    dealsList.clear();
                }
                finished = dealListResponse.getCurrentPage() == dealListResponse.getPageCount();

                if (!finished) {
                    page += 1;
//                    Log.e("DealFragment", "Finished =false, Page=" + page);
                }

                try {
                    dealsList.addAll(dealsList1);
//                    Log.e("DealFragment", "Deal list added to original");
                } catch (Exception e) {
//                    Log.e("DealFragment", "Error adding deals to original list");
                    e.printStackTrace();
                }

                if (page == 1) {
                    changeLayout();
                } else {
//                    Log.e("Running", "Not First Time");
                    changeLayout(false);
                    adapter.notifyDataSetChanged();
                }
                recyclerView.setAdapter(adapter);
                if (dealListResponse.getCurrentPage() > 1) {
                    if (type == RecyclerDealsAdapter.TYPE_LIST)
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset((dealListResponse.getCurrentPage() - 1) * PAGE_LIMIT, 20);
                    else
                        ((GridLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset((dealListResponse.getCurrentPage() - 1) * PAGE_LIMIT, 20);
                }
            } catch (NullPointerException e) {
                e.getStackTrace();
            }
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
        } else if (item.getItemId() == R.id.notification) {
            startActivity(new Intent(getActivity(), NotificationsActivity.class));
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NavigationDrawer) getActivity()).setKeyListener(this);
    }

    /**
     * Run AsyncTask Only after phone is connected to internet
     */
    public void runAsyncTask() {
        new CategoriesRetriever().execute();
        new DealFetcher(action).execute("1", category, searchQuery, sort);

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
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(5000);
                connection.setDoInput(true);

                File ScribblerDirectory = new File(Environment.getExternalStorageDirectory(), "scribbler");
                InputStream inputStream = connection.getInputStream();
                ScribblerDirectory.mkdirs();
                if (inputStream != null) {
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(ScribblerDirectory, "tmpCat.tmp"));
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, bufferLength);
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        File file = new File(ScribblerDirectory, "tmpCat.tmp");
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String s = "";
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            s += line;
                        }
                        reader.close();
                        return s;
                    } catch (FileNotFoundException fe) {
                        fe.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                Log.e("Category", "Retrieving again");
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


    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(selectedIconQuery.getWindowToken(), 0);
    }

}
