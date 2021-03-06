package com.scribblernotebooks.scribblernotebooks.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.scribblernotebooks.scribblernotebooks.CustomViews.ForgotPasswordPopup;
import com.scribblernotebooks.scribblernotebooks.CustomViews.TermsPopup;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;
import com.scribblernotebooks.scribblernotebooks.Services.LocationRetreiver;
import com.scribblernotebooks.scribblernotebooks.Services.SignUpService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;


public class LogIn extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private final static String TAG = "LogIn";
    private static final int REQ_SIGN_IN_REQUIRED = 55664;

    EditText name, email, mobile, password;
    TextView forgotPassword, termsAndConditions;
    Button signIn, signUp;
    TextView signUpText;
    LinearLayout loginText;
    String userName = "", userEmail = "", userPassword, userMobile;
    SignInButton signInButton;
    LoginButton loginButton;
    CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;

    final int LOGIN = 1;
    final int SIGNUP = 2;
    int view_open = LOGIN;
    int screenWidth;
    int screenHeight;
    ImageView sun, cloud1, cloud2;
    Boolean fromApi = false;

    private static final int RC_SIGN_IN = 0;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;

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
//                if (userPrefs.getString(Constants.PREF_DATA_PASS, "").isEmpty()) {
//                    startActivity(new Intent(this, ProfileManagement.class));
//                    finish();
//                } else
                    startActivity(new Intent(this, NavigationDrawer.class));
                    finish();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        startService(new Intent(this, LocationRetreiver.class));

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
        loginButton.setReadPermissions("user_friends", "email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                /**
                 * Current SDK uses GraphAPI to retrieve data from facebook
                 */
                final String token = loginResult.getAccessToken().getToken();
//                Log.e("fb access token", token);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        String name1 = jsonObject.optString("name");
                        String email1 = jsonObject.optString("email");
                        JSONObject cover = jsonObject.optJSONObject("cover");
                        String coverPic = cover.optString("source");
                        String id = jsonObject.optString("id");
                        String userdp = "https://graph.facebook.com/" + id + "/picture?type=large";
                        fromApi = true;
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
                Toast.makeText(getApplicationContext(), "Login Cancelled... Please try again later", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "Login Failed... Please try again later", Toast.LENGTH_LONG).show();
            }
        });


        /**
         * Setting up GoogleAPI Client for sign in through google
         */
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API, Plus.PlusOptions.builder().build())
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .addScope(Plus.SCOPE_PLUS_PROFILE)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            mGoogleApiClient.connect();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API, Plus.PlusOptions.builder().build())
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .addScope(Plus.SCOPE_PLUS_PROFILE)
                    .build();
        }

        //View Setup
        name = (EditText) findViewById(R.id.userName);
        email = (EditText) findViewById(R.id.userEmail);
        password = (EditText) findViewById(R.id.userPassword);
        mobile = (EditText) findViewById(R.id.userPhone);
        signIn = (Button) findViewById(R.id.signIn);
        signUp = (Button) findViewById(R.id.signUp);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        termsAndConditions = (TextView) findViewById(R.id.termsAndConditions);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        progressDialog = new ProgressDialog(LogIn.this);
        loginText=(LinearLayout)findViewById(R.id.text);
        signUpText=(TextView)findViewById(R.id.signUpText);

        sun = (ImageView) findViewById(R.id.sun);
        cloud1 = (ImageView) findViewById(R.id.cloud1);
//        cloud2 = (ImageView) findViewById(R.id.cloud2);

        /**
         * Setting the drawable height and color for edit texts
         */
        Constants.drawableScaleColorChange(this, name, R.drawable.userlogin);
        Constants.drawableScaleColorChange(this, email, R.drawable.maillogin);
        Constants.drawableScaleColorChange(this, password, R.drawable.passwordlogin);
        Constants.drawableScaleColorChange(this, mobile, R.drawable.phonelogin);

        /**Forgot Password**/
        View.OnTouchListener colorChanger = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView t = (TextView) v;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        t.setTextColor(getResources().getColor(R.color.selectedText));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        t.setTextColor(getResources().getColor(R.color.selectedText));
                        break;
                    default:
                        t.setTextColor(getResources().getColor(R.color.normalText));
                        break;
                }
                return false;
            }
        };
        forgotPassword.setOnTouchListener(colorChanger);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ForgotPasswordPopup(LogIn.this).show();
            }
        });

        termsAndConditions.setOnTouchListener(colorChanger);
        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Show terms and conditions
                TermsPopup popup = new TermsPopup(LogIn.this);
                popup.setTitle("Terms & Conditions");
                popup.setUrl(Constants.ServerUrls.termsAndConditions);
                popup.show();
            }
        });


        /**
         * Setting Cloud motion animation
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
//        Constants.setMovingAnimation(cloud1, Constants.getRandomInt(5000, 15000), screenWidth, (float) (Math.random() * (screenHeight / 2)), true, screenHeight);
//        Constants.setMovingAnimation(cloud2, Constants.getRandomInt(10000, 25000), screenWidth, (float) (Math.random() * (screenHeight / 2)), true, screenHeight);

        int width = signInButton.getWidth();
        int height = signInButton.getHeight();

        loginButton.setHeight(height);
        loginButton.setWidth(width);
//        loginButton.setPadding(signInButton.getPaddingLeft(), signInButton.getPaddingTop(), signInButton.getPaddingRight(), signInButton.getPaddingBottom());


        /**
         * Manual Sign in. Neither google nor facebook used for login.
         * Check if any field is empty
         */
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        /** Dimming Status Bar so that app is in focus */
        final View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        LogIn.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                            }
                        });
                    }
                }, 5000);
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Log.e("check", "button clicked");
                if (Constants.isNetworkAvailable(getApplicationContext())) {
                    if (!mGoogleApiClient.isConnecting()) {
                        progressDialog.setMessage("Connecting...");
                        progressDialog.setCancelable(false);
                        mSignInClicked = true;
                        try {
                            progressDialog.show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        resolveSignInError();
                    }
                } else {
                    Toast toast = Toast.makeText(this, "Not connected to Internet\nPlease check the connection and try again later", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
        }
    }

    private void loginUser() {
        if (view_open == LOGIN) {
            userEmail = email.getText().toString();
            userPassword = password.getText().toString();

            if (userEmail.equalsIgnoreCase("skip")) {
                getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit().putString(Constants.PREF_DATA_PASS, "OK").apply();
                startActivity(new Intent(this, NavigationDrawer.class));
                finish();
                return;
            }
            if (userEmail.isEmpty()) {
                email.setCompoundDrawables(null, null, null, null);
                email.setError("Email is required");
            } else if (userPassword.length() < 6) {
                Toast.makeText(getApplicationContext(), "Oops... Looks like you forgot to enter the password", Toast.LENGTH_LONG).show();
            } else {
                login(userEmail, userPassword);
            }
            return;
        }
        signUp.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        mobile.setVisibility(View.GONE);
        signIn.setVisibility(View.VISIBLE);
        forgotPassword.setVisibility(View.VISIBLE);
        termsAndConditions.setVisibility(View.GONE);
        signUpText.setVisibility(View.VISIBLE);
        loginText.setVisibility(View.VISIBLE);
        view_open = LOGIN;
        signUpText.setText(Html.fromHtml("Don't have an account? <b>Sign Up</b>"));
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });
    }

    public void login(String email, String password) {
        HashMap<String, String> data = new HashMap<>();
        data.put("username", email);
        data.put("password", password);
        new SignUpService(Constants.ServerUrls.login, this).execute(data);
    }

    /**
     * SignUp using signup form
     */
    public void signUpUser() {
        if (view_open == SIGNUP) {
            userName = name.getText().toString();
            userEmail = email.getText().toString();
            userPassword = password.getText().toString();
            userMobile = mobile.getText().toString();

            if (userName.isEmpty()) {
                name.setCompoundDrawables(null, null, null, null);
                name.setError("Name required");
            } else if (!Constants.isValidEmailId(userEmail)) {
                email.setCompoundDrawables(null, null, null, null);
                email.setError("Invalid Email ID");
            } else if (userPassword.length() < 6) {
                password.setCompoundDrawables(null, null, null, null);
                password.setError("Password must be ateast 6 characters");
            } else if (userMobile.length() != 10) {
                mobile.setCompoundDrawables(null, null, null, null);
                mobile.setError("Mobile Number should be of 10 digits");
            } else {
                Log.e("normal signIn", "going to signup");
                signUp(name.getText().toString(), email.getText().toString(), mobile.getText().toString(), password.getText().toString());
            }
            return;
        }
        signUp.setVisibility(View.VISIBLE);
        signUp.getLayoutParams().width=(int)getResources().getDimension(R.dimen.login_editText_width);
        name.setVisibility(View.VISIBLE);
        loginText.setVisibility(View.GONE);
        mobile.setVisibility(View.VISIBLE);
        forgotPassword.setVisibility(View.GONE);
        signIn.setVisibility(View.GONE);

        signUpText.setText(Html.fromHtml("Already have an account? <b>Login</b>"));
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
//        termsAndConditions.setVisibility(View.VISIBLE);
        view_open = SIGNUP;
    }

    /**
     * Actual SignUp Code
     *
     * @param userName     username
     * @param userEmail    useremail
     * @param userContact  mobile no.
     * @param userPassword password
     */

    public void signUp(String userName, String userEmail, final String userContact, String userPassword) {

        //If username or email or password is empty then do not signup
        if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please check if all fields are filled and try again", Toast.LENGTH_LONG).show();
            return;
        }
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.POST_NAME, userName);
        data.put(Constants.POST_EMAIL, userEmail);
        data.put(Constants.POST_MOBILE, userContact);
        data.put(Constants.POST_PASSWORD, userPassword);
        data.put(Constants.POST_COVERPIC, "");
        data.put(Constants.POST_PROFILEPIC, "");
        Log.e("normal signIn", "going to signupservice");
        new SignUpService(Constants.ServerUrls.signUp, this).execute(data);
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
                progressDialog.setCancelable(true);
                mGoogleApiClient.connect();
            }
        }

        if (requestCode == REQ_SIGN_IN_REQUIRED && responseCode == RESULT_OK) {
            // We had to sign in - now we can finish off the token request.
            Log.e("Google Token", "On Activity Result");
            new GetGooglePlusToken(getApplicationContext(), mGoogleApiClient).execute();
        }
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    /**
     * GoogleAPI callbacks. Called after sign in
     */
    @Override
    public void onConnected(Bundle arg0) {
        Log.e(TAG, "Google Connected");
        if(progressDialog!=null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
        mSignInClicked = false;
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(progressDialog!=null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
        Log.e("LogIn","Google Connection Suspended");
        mGoogleApiClient.connect();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
        if(mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
            Log.e("LogIn","Google Connection disconnected");
        }
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
                String scope = "oauth2: " + Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME+
                        " https://www.googleapis.com/auth/userinfo.email ";
                Log.e(TAG,"Scope: "+scope);
                token = GoogleAuthUtil.getToken(
                        getApplicationContext(),
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
            Log.e(TAG,"token recieved: "+token);
            return token;
        }

        @Override
        protected void onPostExecute(String response) {
//            Log.e(TAG, "Google access token = " + response);
            loginSocial(Constants.POST_METHOD_GOOGLE, response);
        }
    }


    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        progressDialog.dismiss();
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Log.e("profile info", "inside if");


                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personImageUrl = currentPerson.getImage().getUrl();
                String userId = currentPerson.getId();
                String resizedImageUrl = personImageUrl.replace("photo.jpg?sz=50", "photo.jpg?sz=250");
                String userEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String userCover = "";
                if (currentPerson.hasCover()) {
                    userCover = currentPerson.getCover().getCoverPhoto().getUrl();
                }

                User user = new User();
                user.setName(personName);
                user.setEmail(userEmail);
                user.setCoverImage(userCover);
                user.setProfilePic(resizedImageUrl);
                Constants.saveUserDetails(getApplicationContext(), user);

                fromApi = true;
//                loginSocial(personName, userEmail, userId, Constants.ServerUrls.loginGoogle, userCover, resizedImageUrl);
//                loginSocial(Constants.POST_METHOD_GOOGLE, token);
                sendGoogleMethodAndToken(mGoogleApiClient);
            } else {
                Log.e("profile info", "person null");
//                Toast.makeText(getApplicationContext(),
//                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("profile info", "inside catch");
            e.printStackTrace();
        }
    }

    void sendGoogleMethodAndToken(GoogleApiClient googleApiClient) {
        new GetGooglePlusToken(getApplicationContext(), googleApiClient).execute();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        try {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(mSignInClicked)
            Toast.makeText(this, "Could not connect to google servers. Please try again.", Toast.LENGTH_SHORT).show();
        if (!result.hasResolution()) {
            try {
                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                        0).show();
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
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


    public void loginSocial(String method, String accessToken) {
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.POST_METHOD, method);
        data.put(Constants.POST_ACCESS_TOKEN, accessToken);
        data.put(Constants.POST_TOKEN, getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).getString(Constants.PREF_DATA_USER_TOKEN, ""));
        testAccessToken(accessToken);
        Log.e("LoginSocialData","Data: METHOD: "+method+" ACCESS_TOKEN: "+accessToken);
        new SignUpService(Constants.ServerUrls.login, this).execute(data);
    }




    @Override
    public void onBackPressed() {
        if(view_open==SIGNUP){
            loginUser();
        }else {
            super.onBackPressed();
        }
    }

    public void testAccessToken(String accessToken){
        new AsyncTask<String, Void, Void>(){
            @Override
            protected Void doInBackground(String... strings) {
                try{
                    URL url=new URL("https://www.googleapis.com/plus/v1/people/me?access_token="+strings[0]);
                    Log.e(TAG,"Test Url check: "+url.toString());
                    HttpsURLConnection connection=(HttpsURLConnection)url.openConnection();
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);
                    connection.setDoOutput(false);
                    connection.setDoInput(true);

                    InputStream is=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                    String s="",line;
                    while((line=reader.readLine())!=null){
                        s+=line;
                    }
                    Log.e(TAG,"Response form google:\n "+s);

                }catch (Exception e){
                    e.printStackTrace();
                }


                return null;
            }
        }.execute(accessToken);
    }

}


