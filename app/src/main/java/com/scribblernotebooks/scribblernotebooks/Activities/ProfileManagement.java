
package com.scribblernotebooks.scribblernotebooks.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableRow;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;
import com.scribblernotebooks.scribblernotebooks.Services.SignUpService;

import java.util.HashMap;


public class ProfileManagement extends AppCompatActivity {

    static final int PROFILE_PIC_REQUEST_CODE = 11;
    static final int COVER_PIC_REQUEST_CODE = 12;
    public static GoogleApiClient mGoogleApiClient;
    public DisplayImageOptions displayImageOptions, displayImageOptionsCover;
    public ImageLoadingListener imageLoadingListener;
    public ImageLoaderConfiguration imageLoaderConfiguration;
    ImageView userPic, userCoverPic;
    Toolbar appbar;
    User user;
    SharedPreferences userPref;
    SharedPreferences.Editor userPrefEditor;
    EditText userName, userEmail, userPass, userMob, userLocation;
    ScrollView scrollView;
    Button saveButton;
    String idName, idValue;
    String coverUrl;
    String profileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);

        idName = getIntent().getStringExtra(Constants.INTENT_ID_NAME);
        idValue = getIntent().getStringExtra(Constants.INTENT_ID_VALUE);

        userPic = (ImageView) findViewById(R.id.pic);
        userCoverPic = (ImageView) findViewById(R.id.profileCoverPic);
        appbar = (Toolbar) findViewById(R.id.toolbar);
        scrollView = (ScrollView) findViewById(R.id.sl);
        saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        /**Configurations for image caching library */
        imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
        imageLoadingListener = new SimpleImageLoadingListener();

        displayImageOptions = Constants.getProfilePicDisplayImageOptions();
        displayImageOptionsCover = Constants.getCoverPicDisplayImageOptions();

        /**Google API**/
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


        /**View Setup*/
        userName = (EditText) findViewById(R.id.et_name);
        userEmail = (EditText) findViewById(R.id.et_email);
        userPass = (EditText) findViewById(R.id.et_password);
        userMob = (EditText) findViewById(R.id.et_mobile);
        userLocation = (EditText) findViewById(R.id.et_location);

        /**Initiating Shared Prefs and setting values*/
        user = Constants.getUser(this);
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        userLocation.setText(user.getLocation());
        userMob.setText(user.getMobile());

        imageChanger(userName);
        imageChanger(userMob);
        imageChanger(userEmail);
        imageChanger(userPass);

        /**Setting images from shared Prefs**/
        coverUrl = user.getCoverImage();
        if (!coverUrl.isEmpty()) {
            if (coverUrl.contains("http") || coverUrl.contains("ftp")) {
                ImageLoader.getInstance().displayImage(coverUrl, userCoverPic, displayImageOptionsCover, imageLoadingListener);
            } else {
                userCoverPic.setImageBitmap(Constants.getScaledBitmap(coverUrl, 267, 200));
            }
        }
        profileUrl = user.getProfilePic();
        if (!profileUrl.isEmpty()) {
            if (profileUrl.contains("http") || profileUrl.contains("ftp")) {
                ImageLoader.getInstance().displayImage(profileUrl, userPic, displayImageOptions, imageLoadingListener);
            } else {
                userPic.setImageBitmap(Constants.getScaledBitmap(profileUrl, 150, 150));
            }
        }

        appbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(appbar);
        getSupportActionBar().setTitle("Profile");


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

    public boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            userPass.setError("Password cannot be empty");
            return false;
        }
        if (password.length() < 8) {
            userPass.setError("Password must be at least 8 characters");
            return false;
        }
        return true;
    }

    /**
     * Saving the user details
     */
    public void save() {
        if (!isValidPassword(userPass.getText().toString())) {
            return;
        }

        HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put(Constants.POST_NAME, userName.getText().toString());
        postDataParams.put(Constants.POST_EMAIL, userEmail.getText().toString());
        postDataParams.put(Constants.POST_MOBILE, userMob.getText().toString());
        postDataParams.put(Constants.POST_PASSWORD, userPass.getText().toString());
        postDataParams.put(Constants.POST_COVERPIC, coverUrl);
        postDataParams.put(Constants.POST_PROFILEPIC, profileUrl);

        new SignUpService(Constants.ServerUrls.signUp, this).execute(postDataParams);
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
                default:
                    break;
            }
        }
    }


    /**
     * Setting cover picture
     */
    public void setCoverPic(Intent result) {
        String picturePath = getImagePath(result);
        userCoverPic.setImageBitmap(Constants.getScaledBitmap(picturePath, 267, 200));
        userPrefEditor = userPref.edit();
        userPrefEditor.putString(Constants.PREF_DATA_COVER_PIC, picturePath);
        userPrefEditor.apply();
    }

    /**
     * Setting profile picture
     */
    public void setProfilePic(Intent result) {
        String picturePath = getImagePath(result);
        userPic.setImageBitmap(Constants.getScaledBitmap(picturePath, 160, 160));
        userPrefEditor = userPref.edit();
        userPrefEditor.putString(Constants.PREF_DATA_PHOTO, picturePath);
        userPrefEditor.apply();
    }


    /**
     * Getting path of selected image
     */
    String getImagePath(Intent result) {
        Uri imageUri = result.getData();
        String[] filePathColoumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imageUri, filePathColoumn, null, null, null);
        cursor.moveToFirst();
        int coloumnIndex = cursor.getColumnIndex(filePathColoumn[0]);
        String picturePath = cursor.getString(coloumnIndex);
        cursor.close();
        return picturePath;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void logOutAll(View v) {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
//            mGoogleApiClient.connect();
        }

        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences userPref = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userPref.edit().clear().apply();
        startActivity(new Intent(this, LogIn.class));
        finish();
        overridePendingTransition(R.anim.login_slide_in, R.anim.profile_slide_out);
    }

}
