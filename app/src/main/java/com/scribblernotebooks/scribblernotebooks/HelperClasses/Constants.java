package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.scribblernotebooks.scribblernotebooks.R;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Jibin_ism on 08-May-15.
 */


public class Constants {


    public static class ServerUrls {
        public static String websiteUrl = "http://192.168.1.117:3000/api/";
        //User Module
        public static String signUp = websiteUrl + "signup";
        public static String login = websiteUrl + "signin";
        public static String linkSocialAccount = websiteUrl + "user/link-social-account";
        public static String changePassword = websiteUrl + "user/change-password";
        public static String forgotPassword = websiteUrl + "forgot-password";
        public static String insertGCM = websiteUrl + "insertGcm";

        //Deal Details
        public static String dealDetail = websiteUrl + "deal/";
        public static String dealList = websiteUrl + "deal";

        public static String likeDeal = websiteUrl + "like-deal/";
        public static String shareDeal = websiteUrl + "shareDeal";

        public static String dealCategories = websiteUrl + "deal-category";


    }

    public static URL getDealDetailsURL(String dealId, String token) {
        try {
            return new URL(ServerUrls.dealDetail + dealId + "?token=" + token);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final String MIXPANEL_TOKEN = "873f1995dd119bdb63b8a51bc2f4951d";
    public static final String parentURLForGetRequest = "http://192.168.1.117:3000/deal/";
    public static final String parentURLForCouponCode = "http://192.168.1.117:3000/deal/";
    public static final String serverURL = "http://jazzyarchitects.orgfree.com/deal.php";
    public static final String TAG_DEAL_NAME = "title";
    public static final String TAG_IMAGE_URL = "imgUrl";
    public static final String TAG_IMAGE_UUID = "imgUUID";
    public static final String TAG_SHORT_DESCRIPTION = "shortDescription";
    public static final String TAG_LONG_DESCRIPTION = "description";
    public static final String TAG_CODE = "code";
    public static final String TAG_ID = "Id";
    public static final String TAG_CATEGORY = "category";
    public static final String TAG_IF_FEATURED = "ifFeatured";
    public static final String TAG_IF_FAVOURITE = "ifFavorite";
    public static final String TAG_DATA = "data";
    public static final String TAG_DEALS = "deals";
    public static final String TAG_COUPON_CODE = "couponCode";
    public static final String PARCELABLE_DEAL_LIST_KEY = "dealList";
    public static final String PARCELABLE_DEAL_KEY = "dealList";
    public static final String CURRENT_DEAL_INDEX = "index";
    public static final String URL_ARGUMENT = "urlArg";
    /**
     * Shared Pref Tags for USER DATA *
     */
    public static final String PREF_NAME = "LogInOut";
    public static final String PREF_DATA_NAME = "Name";
    public static final String PREF_DATA_PHOTO = "ImageUrl";
    public static final String PREF_DATA_COVER_PIC = "CoverPic";
    public static final String PREF_DATA_EMAIL = "EmailId";
    public static final String PREF_DATA_MOBILE = "mobileNo";
    public static final String PREF_DATA_LOCATION = "location";
    public static final String PREF_DATA_PASS = "password";
    public static final String PREF_DATA_USER_TOKEN = "userToken";
    public static final String PREF_DATA_MIXPANEL_USER_ID = "mixpanelUserId";
    public static final String INTENT_ID_NAME = "idName";
    public static final String INTENT_ID_VALUE = "idValue";
    public static final String FACEBOOKID = "facebookId";
    public static final String GOOGLEID = "googleId";
    public static final String POST_SUCCESS = "success";
    public static final String POST_DATA = "data";
    public static final String POST_IS_NEW = "isNew";
    public static final String POST_ERROR = "error";
    public static final String POST_TOKEN = "token";
    public static final String POST_NAME_FIRST = "first";
    public static final String POST_NAME_LAST = "last";
    public static final String POST_NAME = "name";
    public static final String POST_EMAIL = "email";
    public static final String POST_MOBILE = "mobile";
    public static final String POST_PASSWORD = "password";
    public static final String POST_MIXPANELID = "_id";
    public static final String POST_GOOGLE = "googleId";
    public static final String POST_FACEBOOK = "facebookId";
    public static final String POST_COVERPIC = "coverImage";
    public static final String POST_PROFILEPIC = "profilePic";
    public static final String POST_METHOD = "method";
    public static final String POST_ACCESS_TOKEN = "accessToken";
    public static final String POST_METHOD_FACEBOOK = "facebook";
    public static final String POST_METHOD_GOOGLE = "google";
    public static final String PREF_NOTIFICATION_NAME = "Notification";
    public static final String PREF_NOTIFICATION_ON_OFF = "NotifyOnOff";
    public static final String PREF_NOTIFICATION_DEAL_OF_DAY = "DealOfDay";
    public static final String PREF_NOTIFICATION_RINGTONE = "Ringtone";
    /**
     * Shared Pref Tags for GCM *
     */
    public static final String PREF_GCM_NAME = "gcmSharedPreferences";
    public static final String GCM_REG_ID = "registrationId";
    public static final String GCM_APP_VERSION = "appVersion";
    /**
     * Basic Profile Info fields
     */
    public static final String[] sharedPrefTags = {Constants.PREF_DATA_NAME, Constants.PREF_DATA_EMAIL,
            Constants.PREF_DATA_MOBILE, Constants.PREF_DATA_LOCATION};
    public static View.OnFocusChangeListener drawableColorChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText e = (EditText) v;
            if (!hasFocus) {
                if (!e.getText().toString().isEmpty()) {
                    Drawable d = e.getCompoundDrawables()[2];
                    d.setColorFilter(Color.parseColor("#ff13657b"), PorterDuff.Mode.MULTIPLY);
                }
            }
        }
    };

public static boolean saveUserDetails(Context context, User user) {
        if (user == null)
            return false;
        SharedPreferences userPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        try {
            if (!user.getName().isEmpty())
                editor.putString(PREF_DATA_NAME, user.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!user.getProfilePic().isEmpty())
                editor.putString(PREF_DATA_PHOTO, user.getProfilePic());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!user.getCoverImage().isEmpty())
                editor.putString(PREF_DATA_COVER_PIC, user.getCoverImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!user.getEmail().isEmpty())
                editor.putString(PREF_DATA_EMAIL, user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!user.getMobile().isEmpty())
                editor.putString(PREF_DATA_MOBILE, user.getMobile());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!user.getToken().isEmpty())
                editor.putString(PREF_DATA_USER_TOKEN, user.getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!user.getMixpanelId().isEmpty())
                editor.putString(PREF_DATA_MIXPANEL_USER_ID, user.getMixpanelId());
        }catch (Exception e){
            e.printStackTrace();
        }
            editor.apply();
            return true;
        }

    public static User getUser(Context context) {
        SharedPreferences userPref;
        userPref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String name = userPref.getString(Constants.PREF_DATA_NAME, "");
        String email = userPref.getString(Constants.PREF_DATA_EMAIL, "");
        String mobile = userPref.getString(Constants.PREF_DATA_MOBILE, "");
        String coverPic = userPref.getString(Constants.PREF_DATA_COVER_PIC, "");
        String profilePic = userPref.getString(Constants.PREF_DATA_PHOTO, "");
        String mixPanelId = userPref.getString(Constants.PREF_DATA_MIXPANEL_USER_ID, "");
        String token = userPref.getString(Constants.PREF_DATA_USER_TOKEN, "");
        String location = userPref.getString(Constants.PREF_DATA_LOCATION, "");

        return new User(name, email, mobile, coverPic, profilePic, token, mixPanelId);
    }


    /**
     * To convert hashmap to post request for sending to server
     *
     * @param params the hashmap
     * @return post request string
     * @throws UnsupportedEncodingException
     */
    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    public static DisplayImageOptions getProfilePicDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nodp)
                .showImageForEmptyUri(R.drawable.nodp)
                .showImageOnFail(R.drawable.nodp)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer()).build();
    }

    public static DisplayImageOptions getCoverPicDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.navigation_drawer_cover_pic)
                .showImageForEmptyUri(R.drawable.navigation_drawer_cover_pic)
                .showImageOnFail(R.drawable.navigation_drawer_cover_pic)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer()).build();
    }


    public static MixpanelAPI getMixPanelInstance(Context context) {
        MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
        String id = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).getString(Constants.PREF_DATA_MIXPANEL_USER_ID, "0");
        mixpanelAPI.identify(id);
        return mixpanelAPI;
    }

    public static ArrayList<Pair<Integer, String>> getProfileInfoFields() {
        ArrayList<Pair<Integer, String>> list = new ArrayList<>();
        list.add(Pair.create(R.drawable.userlogin, "Name"));
        list.add(Pair.create(R.drawable.maillogin, "Email Id"));
        list.add(Pair.create(R.drawable.phonelogin, "Contact Number"));
        list.add(Pair.create(R.drawable.location, "Location"));
        return list;
    }

    /**
     * Verify Email
     */
    public static boolean isValidEmailId(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Image Size Reducing
     */
    public static Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * Code for Animation
     */
    public static void setMovingAnimation(final ImageView image, final int duration, final float initialX,
                                          float initialY, final Boolean changeSize, final int screenHeight) {
        TranslateAnimation translateAnimation = new TranslateAnimation(initialX + image.getWidth(), 0 - 2 * image.getWidth(), initialY, initialY * getRandomFloat(0.9, 1.2));
        translateAnimation.setDuration(duration);
        translateAnimation.setInterpolator(new LinearInterpolator());
        translateAnimation.setFillAfter(true);
        translateAnimation.setFillEnabled(true);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (changeSize) {
                    float scale = getRandomFloat(0.8, 1.5);
                    image.setScaleX(scale);
                    image.setScaleY(scale);
                }
                setMovingAnimation(image, duration, initialX, getRandomFloat(screenHeight / 10, 6 * screenHeight / 10), changeSize, screenHeight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        image.startAnimation(translateAnimation);
    }

    public static int getRandomInt(int min, int max) {
        Random random = new Random();
        int result = random.nextInt();
        while (result < min || result > max) {
            result = random.nextInt();
        }
        return result;
    }

    public static float getRandomFloat(double min, double max) {
        double result = Math.random() * max;
        while (result < min || result > max) {
            result = Math.random() * max;
        }
        return (float) result;
    }

    /**
     * Navigation Drawer Items
     */
    public static ArrayList<Pair<Integer, String>> getNavigationDrawerItems() {
        ArrayList<Pair<Integer, String>> item = new ArrayList<>();
        item.add(Pair.create(R.drawable.scan_icon, "Scan QR Code"));
        item.add(Pair.create(R.drawable.enter_code_icon, "Enter Scribbler Code"));
        item.add(Pair.create(R.drawable.all_deals_icon, "All Deals"));
        item.add(Pair.create(R.drawable.featured_deals_icon, "Claimed Deals"));
        //item.add(Pair.create(R.drawable.sign_out_icon,"Sign Out"));
        return item;
    }

    /**
     * Check if phone is connected to internet
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}


