package com.scribblernotebooks.scribblernotebooks.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import java.io.InputStream;


public class LogIn extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{

    EditText name, mobile;
    Button signIn,signOut;
    String userName, userMobile;
    SignInButton signInButton;

    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "MainActivity";

    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private ProgressDialog mConnectionProgressDialog;
    private GoogleApiClient mGoogleApiClient;

    SharedPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


        userPrefs = getSharedPreferences(Constants.USER_PREF, MODE_PRIVATE);
        try {
            String userName = userPrefs.getString(Constants.USER_NAME_PREF, Constants.NO_USER_SAVED);
            if (!userName.equals(Constants.NO_USER_SAVED)) {
                startActivity(new Intent(this, ManualScribblerCode.class));
                finish();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        name=(EditText)findViewById(R.id.name);
        mobile=(EditText)findViewById(R.id.mobileNo);
        signIn=(Button)findViewById(R.id.signUp);
//        signOut=(Button)findViewById(R.id.sign_out);
        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
//        signOut.setOnClickListener(this);

        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

        signInButton.setSize(SignInButton.SIZE_WIDE);

        userName="";
        userMobile="";

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName=name.getText().toString();
                userMobile=mobile.getText().toString();
                if(userName.isEmpty()|| userMobile.isEmpty())
                {
                    Toast.makeText(getBaseContext(), "Enter valid inputs", Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences.Editor editor=userPrefs.edit();
                    editor.putString(Constants.USER_NAME_PREF,userName);
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(), ManualScribblerCode.class));
                    finish();
//                    Toast.makeText(getBaseContext(), userName + " " + userMobile, Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(getBaseContext(), NavigationDrawer.class));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        try {
            if (!result.hasResolution()) {
                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                        0).show();
                return;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.sign_in_button:
                if (!mGoogleApiClient.isConnecting()) {
                    mSignInClicked = true;
                    resolveSignInError();
                }
                break;
//            case R.id.sign_out:
//                if (mGoogleApiClient.isConnected()) {
//                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//                    mGoogleApiClient.disconnect();
//                    mGoogleApiClient.connect();
//                }
//                break;
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                SharedPreferences.Editor editor=userPrefs.edit();
                editor.putString(Constants.USER_NAME_PREF,personName);
                editor.apply();
                startActivity(new Intent(this, ManualScribblerCode.class));
                finish();
                Toast.makeText(getBaseContext(),personName,Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /**trial signOut**/
}
