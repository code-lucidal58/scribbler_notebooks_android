package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
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

    ImageView userPic, userCoverPic;
    EditText user, userEmail;
    Context context;
    ListView listView;
    ProfileListAdapter profileListAdapter;
    SharedPreferences sharedPreferences;
    ArrayList<String> profileField, profileValue;
    Toolbar appbar;
    String Imageurl;
    DrawerLayout mDrawerLayout;
    RelativeLayout mDrawer;
    SharedPreferences userPref;
    SharedPreferences.Editor userPrefEditor;
    public DisplayImageOptions displayImageOptions, displayImageOptionsCover;
    public ImageLoadingListener imageLoadingListener;
    public ImageLoaderConfiguration imageLoaderConfiguration;
    Boolean isInEditMode = false;

    final int PROFILE_PIC_REQUEST_CODE = 1;
    final int COVER_PIC_REQUEST_CODE = 2;

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

        context = getActivity();
        /**Configurations for image caching library */
        imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(context).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
        imageLoadingListener = new SimpleImageLoadingListener();
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer()).build();

        displayImageOptionsCover = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.navigation_drawer_cover_pic)
                .showImageForEmptyUri(R.drawable.navigation_drawer_cover_pic)
                .showImageOnFail(R.drawable.navigation_drawer_cover_pic)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer()).build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        userPic = (ImageView) v.findViewById(R.id.pic);
        userEmail = (EditText) v.findViewById(R.id.userEmail);
        userCoverPic = (ImageView) v.findViewById(R.id.profileCoverPic);
        user = (EditText) v.findViewById(R.id.userName);
        listView = (ListView) v.findViewById(R.id.user_list);
        appbar = (Toolbar) v.findViewById(R.id.app_bar);

        /**Initiating Shared Prefs*/
        userPref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        /**Setting images from shared Prefs**/
        String coverUrl = userPref.getString(Constants.PREF_DATA_COVER_PIC, "");
        if (!coverUrl.isEmpty()) {
            if (coverUrl.contains("http") || coverUrl.contains("ftp")) {
                ImageLoader.getInstance().displayImage(coverUrl, userCoverPic, displayImageOptionsCover, imageLoadingListener);
            } else {
                userCoverPic.setImageBitmap(Constants.getScaledBitmap(coverUrl, 267, 200));
            }
        }
        String profileUrl = userPref.getString(Constants.PREF_DATA_PHOTO, "");
        if (!profileUrl.isEmpty()) {
            if (profileUrl.contains("http") || profileUrl.contains("ftp")) {
                ImageLoader.getInstance().displayImage(profileUrl, userPic, displayImageOptions, imageLoadingListener);
            } else {
                userPic.setImageBitmap(Constants.getScaledBitmap(profileUrl, 150, 150));
            }
        }

        /**
         * Changing image for cover pic
         */
        userCoverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInEditMode) {
                    checkData();
                    return;
                }
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select Cover Pic"), COVER_PIC_REQUEST_CODE);

            }
        });
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInEditMode) {
                    checkData();
                    return;
                }
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select Cover Pic"), PROFILE_PIC_REQUEST_CODE);
            }
        });


        //Setting of Toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(appbar);
        getActivity().setTitle(Title);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);


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
         * Profile Page main content and user details
         */
        String userName = sharedPreferences.getString(Constants.PREF_DATA_NAME, "Username");
        String email = sharedPreferences.getString(Constants.PREF_DATA_EMAIL, "EmailId");
        user.setText(userName);
        if (!email.isEmpty())
            userEmail.setText(email);
        else {
            userEmail.setHint("Enter Email Id");
            userEmail.setText("");
            userEmail.setEnabled(true);
        }


        View.OnTouchListener enabledView=new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("User Fields Profile","Enabled");
                v.setEnabled(true);
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        };
        user.setOnTouchListener(enabledView);
        userEmail.setOnTouchListener(enabledView);

        /**Profile Settings */
        profileField = new ArrayList<>();
        profileValue = new ArrayList<>();
        profileField.add(Constants.PROFILE_FIELD_CLAIM);
        profileValue.add("0");
        profileField.add(Constants.PROFILE_FIELD_LIKE);
        profileValue.add("0");
        profileField.add(Constants.PROFILE_FIELD_SHARE);
        profileValue.add("0");
        profileField.add(Constants.PROFILE_FIELD_FOLLOWER);
        profileValue.add("0");
        profileField.add(Constants.PROFILE_FIELD_FOLLOWING);
        profileValue.add("0");
        profileField.add(Constants.PROFILE_FIELD_INVITE);
        profileValue.add("");

        profileListAdapter = new ProfileListAdapter(getActivity(), profileField, profileValue);
        listView.setAdapter(profileListAdapter);
        return v;
    }

    public boolean checkData() {
        if (user.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Not a valid Name", Toast.LENGTH_LONG).show();
            return false;
        } else if (!Constants.isValidEmailId(userEmail.getText().toString())) {
            Toast.makeText(getActivity(), "Not a valid Email ID", Toast.LENGTH_LONG).show();
            return false;
        } else {
            SharedPreferences.Editor editor = userPref.edit();
            editor.putString(Constants.PREF_DATA_NAME, user.getText().toString());
            editor.putString(Constants.PREF_DATA_EMAIL, user.getText().toString());
            editor.apply();
            isInEditMode = false;
            user.setEnabled(false);
            userEmail.setEnabled(false);
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case COVER_PIC_REQUEST_CODE:
                    setCoverPic(data);
                    break;
                case PROFILE_PIC_REQUEST_CODE:
                    setProfilePic(data);
                    break;
                default:
                    break;
            }
        }
    }

    public void setCoverPic(Intent result) {
        String picturePath = getImagePath(result);
        userCoverPic.setImageBitmap(Constants.getScaledBitmap(picturePath, 267, 200));
        userPrefEditor = userPref.edit();
        userPrefEditor.putString(Constants.PREF_DATA_COVER_PIC, picturePath);
        userPrefEditor.apply();
        ((ImageView) mDrawer.findViewById(R.id.userCover)).setImageBitmap(Constants.getScaledBitmap(picturePath, 267, 200));
    }

    public void setProfilePic(Intent result) {
        String picturePath = getImagePath(result);
        userPic.setImageBitmap(Constants.getScaledBitmap(picturePath, 160, 160));
        userPrefEditor = userPref.edit();
        userPrefEditor.putString(Constants.PREF_DATA_PHOTO, picturePath);
        userPrefEditor.apply();
        ((ImageView) mDrawer.findViewById(R.id.userPhoto)).setImageBitmap(Constants.getScaledBitmap(picturePath, 100, 100));
    }

    String getImagePath(Intent result) {
        Uri imageUri = result.getData();
        String[] filePathColoumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(imageUri, filePathColoumn, null, null, null);
        cursor.moveToFirst();
        int coloumnIndex = cursor.getColumnIndex(filePathColoumn[0]);
        String picturePath = cursor.getString(coloumnIndex);
        cursor.close();
        return picturePath;
    }

    @Override
    public void onStop() {
        if(isInEditMode) {
            if (checkData()) {
                super.onStop();
            }
            else {
                onResume();
            }
        }
        else
        {
            super.onStop();
        }
    }

    /**
     * Auto Generated required Methods
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
