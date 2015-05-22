package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.scribblernotebooks.scribblernotebooks.Adapters.ProfileListAdapter;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TITLE = "title";

    ImageView imageView;
    TextView user;
    Context context;
    ListView listView;
    ProfileListAdapter profileListAdapter;
    SharedPreferences sharedPreferences;
    ArrayList<String> profileField,profileValue;
    Toolbar appbar;
    String Imageurl;
    DrawerLayout mDrawerLayout;
    RelativeLayout mDrawer;
    public DisplayImageOptions displayImageOptions;
    public ImageLoadingListener imageLoadingListener;
    public ImageLoaderConfiguration imageLoaderConfiguration;

    // TODO: Rename and change types of parameters
    private String Title;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Title = getArguments().getString(TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_profile, container, false);
        context=getActivity();
        sharedPreferences=context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        imageView=(ImageView)v.findViewById(R.id.pic);
        user=(TextView)v.findViewById(R.id.username);
        listView=(ListView)v.findViewById(R.id.user_list);
        appbar=(Toolbar)v.findViewById(R.id.app_bar);

        /**Configurations for image caching library */
        imageLoaderConfiguration=new ImageLoaderConfiguration.Builder(context).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
        imageLoadingListener=new SimpleImageLoadingListener();
        displayImageOptions=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20)).build();

        //Setting of Toolbar
        ((AppCompatActivity)getActivity()).setSupportActionBar(appbar);
        getActivity().setTitle(Title);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        /**
         * Navigation Drawer Hamburger Icon Setup
         */
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mDrawer = (RelativeLayout) getActivity().findViewById(R.id.left_drawer_relative);
        final ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(getActivity(),mDrawerLayout,appbar, R.string.open, R.string.close){
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
         * Profile Page main content and user details
         */
        String userName=sharedPreferences.getString(Constants.PREF_DATA_NAME,"Username");
        String userEmail=sharedPreferences.getString(Constants.PREF_DATA_EMAIL,"EmailId");
        user.setText(userName + "\n" + userEmail);

        Imageurl=sharedPreferences.getString(Constants.PREF_DATA_PHOTO,"");
        /**Loading and caching image from url*/
        ImageLoader.getInstance().displayImage(Imageurl,imageView,displayImageOptions,imageLoadingListener);

        profileField =new ArrayList<>();
        profileValue=new ArrayList<>();
        profileField.add(Constants.PROFILE_FIELD_CLAIM);profileValue.add("0");
        profileField.add(Constants.PROFILE_FIELD_LIKE);profileValue.add("0");
        profileField.add(Constants.PROFILE_FIELD_SHARE);profileValue.add("0");
        profileField.add(Constants.PROFILE_FIELD_FOLLOWER);profileValue.add("0");
        profileField.add(Constants.PROFILE_FIELD_FOLLOWING);profileValue.add("0");
        profileField.add(Constants.PROFILE_FIELD_INVITE);profileValue.add("");

        profileListAdapter=new ProfileListAdapter(getActivity(), profileField, profileValue);
        listView.setAdapter(profileListAdapter);
        return v;
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
