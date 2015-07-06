package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;
import com.scribblernotebooks.scribblernotebooks.Services.LocationRetreiver;
import com.scribblernotebooks.scribblernotebooks.Services.SignUpService;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends android.support.v4.app.Fragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private static final int REQ_SIGN_IN_REQUIRED = 55664;

    final String TAG = "ProfileFragment";
    ImageView userPic, userCoverPic;
    Context context;
    SharedPreferences sharedPreferences;
    Toolbar appbar;
    SharedPreferences userPref;
    SharedPreferences.Editor userPrefEditor;

    User user;

    Button save;

    EditText userName, userEmail, userPass, userMob, userLocation, userCollege;

    public DisplayImageOptions displayImageOptions, displayImageOptionsCover;
    public ImageLoadingListener imageLoadingListener;
    public ImageLoaderConfiguration imageLoaderConfiguration;

    SignInButton signInButton;
    LoginButton loginButton;
    CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    ProgressDialog progressDialog = null;

    RecyclerView basicInfoRecyclerView;

    static final int PROFILE_PIC_REQUEST_CODE = 11;
    static final int COVER_PIC_REQUEST_CODE = 12;
    private static final int RC_SIGN_IN = 10;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ProfileFragment newInstance(String param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
        /**Configurations for image caching library */
        imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(context).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
        imageLoadingListener = new SimpleImageLoadingListener();

        displayImageOptions =Constants.getProfilePicDisplayImageOptions();
        displayImageOptionsCover = Constants.getCoverPicDisplayImageOptions();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity().getApplicationContext();
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        userPic = (ImageView) v.findViewById(R.id.pic);
        userCoverPic = (ImageView) v.findViewById(R.id.profileCoverPic);
        appbar = (Toolbar) v.findViewById(R.id.toolbar);
        signInButton = (SignInButton) v.findViewById(R.id.sign_in_button);

        userPref=context.getSharedPreferences(Constants.PREF_NAME,Context.MODE_PRIVATE);

        /**Setting values to the fields*/
        userName = (EditText) v.findViewById(R.id.et_name);
        userEmail = (EditText) v.findViewById(R.id.et_email);
        userPass = (EditText) v.findViewById(R.id.et_password);
        userMob = (EditText) v.findViewById(R.id.et_mobile);
        userLocation = (EditText) v.findViewById(R.id.et_location);
        userCollege = (EditText) v.findViewById(R.id.et_college);
        save=(Button)v.findViewById(R.id.save);

        appbar.setTitleTextColor(Color.WHITE);

        progressDialog = new ProgressDialog(getActivity(),R.style.Theme_AppCompat_Dialog);
        signInButton.setOnClickListener(this);

        save.setOnClickListener(this);

        /** setting values*/
        user=Constants.getUser(getActivity());
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        userLocation.setText(user.getLocation());
        userMob.setText(user.getMobile());
        userCollege.setText(user.getCollege());

        imageChanger(userName);
        imageChanger(userMob);
        imageChanger(userEmail);
        imageChanger(userPass);
        imageChanger(userCollege);


        for(int i=0;i<signInButton.getChildCount();i++){
            View c=signInButton.getChildAt(i);
            if(c instanceof TextView){
                ((TextView) c).setText("Connect Google");
                break;
            }
        }

        /**Setting images from shared Prefs**/
        String coverUrl = user.getCoverImage();
        if (!coverUrl.isEmpty()) {
            if (coverUrl.contains("http") || coverUrl.contains("ftp")) {
                ImageLoader.getInstance().displayImage(coverUrl, userCoverPic, displayImageOptionsCover, imageLoadingListener);
            } else {
                userCoverPic.setImageBitmap(Constants.getScaledBitmap(coverUrl, 267, 200));
            }
        }
        String profileUrl = user.getProfilePic();
        if (!profileUrl.isEmpty()) {
            if (profileUrl.contains("http") || profileUrl.contains("ftp")) {
                ImageLoader.getInstance().displayImage(profileUrl, userPic, displayImageOptions, imageLoadingListener);
            } else {
                userPic.setImageBitmap(Constants.getScaledBitmap(profileUrl, 150, 150));
            }
        }

        userPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    ChangePassword dialog = new ChangePassword();
                    dialog.show(getActivity().getSupportFragmentManager(), "Change Password");
                }
                return true;
            }
        });

        /**Get User location*/
        context.startService(new Intent(context, LocationRetreiver.class));

        AppCompatActivity currentActivity=(AppCompatActivity)getActivity();
        currentActivity.setSupportActionBar(appbar);
        currentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        currentActivity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        currentActivity.getSupportActionBar().setTitle("Profile");


        /**Changing the data in navigation drawer when data here is changed*/


        /**
         * Changing image for cover pic
         */
        userCoverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select Cover Pic"), COVER_PIC_REQUEST_CODE);

            }
        });
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select Profile Pic"), PROFILE_PIC_REQUEST_CODE);
            }
        });


        /**
         * Facebook Login initialize
         * @Link https://developers.facebook.com/docs/facebook-login/android/v2.3
         */
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) v.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions("user_friends", "email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                /**
                 * Current SDK uses GraphAPI to retrieve data from facebook
                 */
                final String token = loginResult.getAccessToken().getToken();
                Log.e("fb access token", token);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        String name = jsonObject.optString("name");
                        String email = jsonObject.optString("email");
                        JSONObject cover = jsonObject.optJSONObject("cover");
                        String coverPic = cover.optString("source");
                        String userdp = "https://graph.facebook.com/" + jsonObject.optString("id") + "/picture?type=large";
                        //TODO: signup login check
                        saveUserDetails(name, email, userdp, coverPic);
                        setCoverPic(coverPic);
                        setProfilePic(userdp);
                        loginSocial(Constants.POST_METHOD_FACEBOOK, token);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,cover");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(context.getApplicationContext(), "Login Cancelled... Please try again later", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(context.getApplicationContext(), "Login Failed... Please try again later", Toast.LENGTH_LONG).show();
            }
        });


        /**
         * Setting up GoogleAPI Client for sign in through google
         */
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


        return v;
    }

    public void imageChanger(final EditText editText) {
        if (!editText.getText().toString().isEmpty()) {
            ((ImageView) ((TableRow) editText.getParent()).getChildAt(0)).getDrawable()
                    .setColorFilter(getResources().getColor(R.color.darkerBlue), PorterDuff.Mode.MULTIPLY);
        }
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty() || s.toString().equals("")) {
                    ((ImageView) ((TableRow) editText.getParent()).getChildAt(0)).getDrawable().clearColorFilter();
                } else {
                    ((ImageView) ((TableRow) editText.getParent()).getChildAt(0)).getDrawable()
                            .setColorFilter(getResources().getColor(R.color.darkerBlue), PorterDuff.Mode.MULTIPLY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * Function Executed when user SignIns from Either google or facebook.
     *
     * @param requestCode code of intent which requested the result
     * @param resultCode  result code saying if result is OK
     * @param data        the calling intent
     */
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
                case RC_SIGN_IN:
                    mSignInClicked = true;
                    mIntentInProgress = false;
                    if (!mGoogleApiClient.isConnecting()) {
                        progressDialog.setMessage("Connecting...");
                        progressDialog.show();
                        mGoogleApiClient.connect();
                    }
                    break;
                default:
                    break;
            }
        }

        if (requestCode == REQ_SIGN_IN_REQUIRED && resultCode == Activity.RESULT_OK) {
            // We had to sign in - now we can finish off the token request.
            Log.e("Google Token", "On Activity Result");
            new GetGooglePlusToken(getActivity(), mGoogleApiClient).execute();
        }
        if (requestCode != COVER_PIC_REQUEST_CODE && requestCode != PROFILE_PIC_REQUEST_CODE && requestCode != RC_SIGN_IN) {
            callbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }

    /**
     * GoogleAPI callbacks. Called after sign in
     */
    @Override
    public void onConnected(Bundle arg0) {
        Log.e(TAG, "Google Connected");
        dismissProgressDialog();
        if (mSignInClicked) {
            getProfileInformation();
            mSignInClicked = false;
        }

    }

    public void dismissProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            Log.e("ProfileFragment", "Progress Dialog error");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Google Suspended");
        dismissProgressDialog();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "Google Failed");
        dismissProgressDialog();
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(), 0).show();
            progressDialog.hide();
            Toast.makeText(context, "Could not connect", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mIntentInProgress) {
            mConnectionResult = result;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    /**
     * Handling clicks on buttons
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                if (isNetworkAvailable()) {
                    if (!mGoogleApiClient.isConnecting()) {
                        progressDialog.setMessage("Connecting...");
                        progressDialog.show();
                        mSignInClicked = true;
                        resolveSignInError();
                    }
                } else {
                    Toast toast = Toast.makeText(context, "Not connected to Internet\nPlease check the connection and try again later", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
            case R.id.save:
                saveDetails();
                break;
        }
    }

    public void saveDetails(){
        User user=new User();
        user.setName(userName.getText().toString());
        user.setEmail(userEmail.getText().toString());
        user.setCollege(userCollege.getText().toString());
        user.setLocation(userLocation.getText().toString());
        user.setMobile(userLocation.getText().toString());
        Constants.saveUserDetails(getActivity(), user);
        mListener.onUserNameChanged();
        mListener.onUserEmailChanged();

    }



    /**
     * Check if phone is connected to internet
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Google Provided Method
     */
    private void resolveSignInError() {
        try {
            if (mConnectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        } catch (Exception e) {
            dismissProgressDialog();
            Toast.makeText(getActivity(), "Already Logged In", Toast.LENGTH_LONG).show();
        }
    }
//
//    /**
//     * Fetching user's information name, email, profile pic
//     */
    private void getProfileInformation() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personImageUrl = currentPerson.getImage().getUrl();
                String s = personImageUrl.replace("photo.jpg?sz=50", "photo.jpg?sz=250");

                String userEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String userId = currentPerson.getId();

                String userCover="";
                if(currentPerson.hasCover()) {
                    userCover = currentPerson.getCover().getCoverPhoto().getUrl();
                }
                userName.setText(personName);
                this.userEmail.setText(userEmail);
                setCoverPic(userCover);
                setProfilePic(s);


                sendGoogleMethodAndToken(mGoogleApiClient);


            } else {
                Toast.makeText(context,
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect Google Client on startup of activity
     */
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void setCoverPic(Intent result) {
        String picturePath = getImagePath(result);
        userCoverPic.setImageBitmap(Constants.getScaledBitmap(picturePath, 267, 200));
        userPrefEditor = userPref.edit();
        userPrefEditor.putString(Constants.PREF_DATA_COVER_PIC, picturePath);
        userPrefEditor.apply();
        mListener.onUserCoverChanged();
    }

    public void setCoverPic(String url) {
        String coverUrl = userPref.getString(Constants.PREF_DATA_COVER_PIC, "");
        if (!coverUrl.isEmpty()) {
            if (coverUrl.contains("http") || coverUrl.contains("ftp")) {
                ImageLoader.getInstance().displayImage(coverUrl, userCoverPic, displayImageOptionsCover, imageLoadingListener);
            } else {
                userCoverPic.setImageBitmap(Constants.getScaledBitmap(coverUrl, 267, 200));
            }
            mListener.onUserCoverChanged();

        }
    }

    public void setProfilePic(Intent result) {
        String picturePath = getImagePath(result);
        userPic.setImageBitmap(Constants.getScaledBitmap(picturePath, 160, 160));
        userPrefEditor = userPref.edit();
        userPrefEditor.putString(Constants.PREF_DATA_PHOTO, picturePath);
        userPrefEditor.apply();
        mListener.onUserDPChanged();
    }

    public void setProfilePic(String url) {
        String profileUrl = userPref.getString(Constants.PREF_DATA_PHOTO, "");
        if (!profileUrl.isEmpty()) {
            if (profileUrl.contains("http") || profileUrl.contains("ftp")) {
                ImageLoader.getInstance().displayImage(profileUrl, userPic, displayImageOptions, imageLoadingListener);
            } else {
                userPic.setImageBitmap(Constants.getScaledBitmap(profileUrl, 150, 150));
            }
            mListener.onUserDPChanged();
        }
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

    private void saveUserDetails(String name, String userEmail, String userImageUrl, String userCoverPic) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.PREF_DATA_NAME,name);
        editor.putString(Constants.PREF_DATA_PHOTO, userImageUrl);
        editor.putString(Constants.PREF_DATA_COVER_PIC, userCoverPic);
        editor.putString(Constants.PREF_DATA_EMAIL, userEmail);

        basicInfoRecyclerView.getAdapter().notifyDataSetChanged();
        editor.apply();

    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }




    @Override
    public void onStop() {
        super.onStop();
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
        void onFragmentInteraction(Uri uri);

        void onUserNameChanged();

        void onUserEmailChanged();

        void onUserDPChanged();

        void onUserCoverChanged();
    }

    void sendGoogleMethodAndToken(GoogleApiClient googleApiClient) {
        new GetGooglePlusToken(getActivity(), googleApiClient).execute();
    }
    public class GetGooglePlusToken extends AsyncTask<Void, Void, String> {
        Context context;
        private GoogleApiClient mGoogleApiClient;
        private String TAG = this.getClass().getSimpleName();

        public GetGooglePlusToken(Context context, GoogleApiClient mGoogleApiClient) {
            this.context = context;
            this.mGoogleApiClient = mGoogleApiClient;
            Log.e("GoogleClient", "Constructor");
        }

        @Override
        protected String doInBackground(Void... params) {
            String token = null;
            try {
                String scope = "oauth2:" + Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME +
                        " https://www.googleapis.com/auth/userinfo.email ";
                token = GoogleAuthUtil.getToken(
                        getActivity(),
                        Plus.AccountApi.getAccountName(mGoogleApiClient),
                        scope);
            } catch (IOException transientEx) {
                // Network or server error, try later
                Log.e(TAG, transientEx.toString());
            } catch (UserRecoverableAuthException e) {
                // Recover (with e.getIntent())
                Log.e(TAG, "UserRecoverableAuthException");
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException authEx) {
                // The call is not ever expected to succeed
                // assuming you have already verified that
                // Google Play services is installed.
                Log.e(TAG, authEx.toString());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String response) {
            Log.e(TAG, "Google access token = " + response);

            loginSocial(Constants.POST_METHOD_GOOGLE,response);
        }
    }

    public void loginSocial(String method, String accessToken) {
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.POST_METHOD, method);
        data.put(Constants.POST_ACCESS_TOKEN, accessToken);
        data.put(Constants.POST_TOKEN,getActivity().getSharedPreferences(Constants.PREF_NAME,Context.MODE_PRIVATE).getString(Constants.PREF_DATA_USER_TOKEN,""));
        new SignUpService(Constants.ServerUrls.linkSocialAccount, getActivity()).execute(data);
    }

}
