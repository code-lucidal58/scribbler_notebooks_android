package com.scribblernotebooks.scribblernotebooks.Activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.drawable.ScaleDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONObject;


public class LogIn extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static String TAG="LogIn";
    EditText name, mobile;
    Button signIn;
    String userName = "", userEmail = "";
    SignInButton signInButton;
    LoginButton loginButton;
    CallbackManager callbackManager;

    int screenWidth;
    int screenHeight;
    ImageView sun, cloud1, cloud2;

    private static final int RC_SIGN_IN = 0;

    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private GoogleApiClient mGoogleApiClient;

    SharedPreferences userPrefs;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Check if user already logged in...
         */
        userPrefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        try {
            String userName = userPrefs.getString(Constants.PREF_DATA_NAME, "");
            if (!userName.isEmpty()) {
                startActivity(new Intent(this, NavigationDrawer.class));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * Initialize facebook api before setting content. Else facebook button will create Error
         */
        FacebookSdk.sdkInitialize(getApplicationContext());


        setContentView(R.layout.activity_log_in);


        /**
         * Facebook Login initialize
         * @Link https://developers.facebook.com/docs/facebook-login/android/v2.3
         */
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                /**
                 * Current SDK uses GraphAPI to retrieve data from facebook
                 */
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        String name = jsonObject.optString("name");
                        String email=jsonObject.optString("email");
                        JSONObject cover=jsonObject.optJSONObject("cover");
                        String coverPic=cover.optString("source");
                        String userdp="https://graph.facebook.com/"+jsonObject.optString("id")+"/picture?type=large";
                        saveUserDetails(name,email,userdp,coverPic);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,cover");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login Failed... Please try again later", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "Login Failed... Please try again later", Toast.LENGTH_LONG).show();
            }
        });


        /**
         * Setting up GoogleAPI Client for sign in through google
         */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


        //View Setup
        name = (EditText) findViewById(R.id.name);
        mobile = (EditText) findViewById(R.id.mobileNo);
        signIn = (Button) findViewById(R.id.signUp);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        progressDialog=new ProgressDialog(this);

        sun = (ImageView) findViewById(R.id.sun);
        cloud1 = (ImageView) findViewById(R.id.cloud1);
        cloud2 = (ImageView) findViewById(R.id.cloud2);

        /**
         * Setting the drawable heights for both edit texts
         */
        final ScaleDrawable userIcon = new ScaleDrawable(getResources().getDrawable(R.drawable.user), Gravity.CENTER, 1F, 1F) {
            @Override
            public int getIntrinsicHeight() {
                return name.getHeight()*3/4;
            }

            @Override
            public int getIntrinsicWidth() {
                return name.getHeight()*3/4;
            }
        };
        userIcon.setLevel(10000);
        name.setCompoundDrawables(null, null, userIcon, null);
        name.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                try {
                    name.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, userIcon, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        final ScaleDrawable emailIcon = new ScaleDrawable(getResources().getDrawable(R.drawable.email), Gravity.CENTER, 1F, 1F) {
            @Override
            public int getIntrinsicHeight() {
                return mobile.getHeight()*3/4;
            }

            @Override
            public int getIntrinsicWidth() {
                return mobile.getHeight()*3/4;
            }
        };
        emailIcon.setLevel(10000);
        mobile.setCompoundDrawables(null, null, userIcon, null);
        mobile.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                try {
                    mobile.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, emailIcon, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        /**
         * Setting Cloud motion animation
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        Constants.setMovingAnimation(cloud1, Constants.getRandomInt(5000, 15000), screenWidth, (float) (Math.random() * (screenHeight / 2)), true, screenHeight);
        Constants.setMovingAnimation(cloud2, Constants.getRandomInt(10000, 25000), screenWidth, (float) (Math.random() * (screenHeight / 2)), true, screenHeight);

        int width = signInButton.getWidth();
        int height = signInButton.getHeight();
        loginButton.setHeight(height);
        loginButton.setWidth(width);


        /**
         * Manual Sign in. Neither google nor facebook used for login.
         * Check if any field is empty
         */
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = name.getText().toString();
                userEmail = mobile.getText().toString();

                if (userName.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Oops... Looks like its not a valid Name",Toast.LENGTH_LONG).show();
                }
                else if(Constants.isValidEmailId(userEmail) && userEmail.contains("@")){
                    Toast.makeText(getApplicationContext(),"Oops... Looks like its not a valid Email Id",Toast.LENGTH_LONG).show();
                }
                else {
                    saveUserDetails(userName,userEmail,"","");
                }
            }
        });
    }



    /**
     * Function Executed when user SignIns from Either google or facebook.
     *
     * @param requestCode  code of intent which requested the result
     * @param responseCode result code saying if result is OK
     * @param intent       the calling intent
     */
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                progressDialog.setMessage("Connecting...");
                progressDialog.show();
                mGoogleApiClient.connect();
            }
        }
        callbackManager.onActivityResult(requestCode, responseCode, intent);

    }

    /**
     * GoogleAPI callbacks. Called after sign in
     */
    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            progressDialog.hide();
            Toast.makeText(this, "Could not connect", Toast.LENGTH_SHORT).show();
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
     * Connect Google Client on startup of activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /**
     * Handling clicks on buttons
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                if(isNetworkAvailable()) {
                    if (!mGoogleApiClient.isConnecting()) {
                        progressDialog.setMessage("Connecting...");
                        progressDialog.show();
                        mSignInClicked = true;
                        resolveSignInError();
                    }
                }
                else
                {
                    Toast toast=Toast.makeText(this,"Not connected to Internet\nPlease check the connection and try again later",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                break;
        }
    }

    /**
     * Check if phone is connected to internet
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
                    mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        progressDialog.dismiss();
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personImageUrl=currentPerson.getImage().getUrl();
                String userEmail=Plus.AccountApi.getAccountName(mGoogleApiClient);
                String userCover=currentPerson.getCover().getCoverPhoto().getUrl();
                saveUserDetails(personName,userEmail,personImageUrl,userCover);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saving User Details to check next time if already logged in
     */
    private void saveUserDetails(String name,String userEmail, String userImageUrl, String userCoverPic) {
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putString(Constants.PREF_DATA_NAME, name);
        editor.putString(Constants.PREF_DATA_PHOTO,userImageUrl);
        editor.putString(Constants.PREF_DATA_COVER_PIC,userCoverPic);
        editor.putString(Constants.PREF_DATA_EMAIL,userEmail);
        editor.apply();
        startActivity(new Intent(getApplicationContext(), NavigationDrawer.class));
        finish();
    }


    /**
     * Function to check if google play services is available on the phone
     * @return boolean indicating the availability GMS
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}


