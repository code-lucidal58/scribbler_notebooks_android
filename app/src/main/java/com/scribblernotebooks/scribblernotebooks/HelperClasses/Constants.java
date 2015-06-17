package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Patterns;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

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


//    public static boolean DEBUG=false;
//    public static String DEBUG_RESPONSE="{\"success\":true,\"message\":[{\"_id\":\"55753f821f3eaf7c28db698c\",\"Id\":\"1\",\"Title\":\"Savaari\",\"Category\":\"Travel\",\"ShortDescription\":\"Savaari Chennai Airport Drop Starting at Rs.149\",\"Description\":\"Savaari Chennai Airport Drop Starting at Rs.149. Cabs to outstation destinations and airport transfers are also available at cheapest fares. No Savaari coupon code is required to get this Savaari Chennai Airport Drop offer. Hurry up!!! Don't miss it!!!\",\"ImageUUID\":\"e60c8daa-05c6-4b7d-b3eb-cca04d949b01\",\"__v\":0},{\"_id\":\"557545671f3eaf7c28db6994\",\"Id\":\"10\",\"Title\":\"SnapDeal\",\"Category\":\"Clothing\",\"ShortDescription\":\"Flat 60% offers on Snapdeal Mens Clothing\",\"Description\":\"Snapdeal Mens Clothing offer Flat 60%. Pick Mens Fashion Clothing Products like Shirts, T-Shirts, Jeans, Trousers, Shorts, Sweaters, Sweat Shirts, Jacket and more. Select Mens Wear Sale from Celio Brand. Mens Clothing online Shopping price starts from Rs.240.\",\"ImageUUID\":\"081b2e48-7dc6-46b2-8ea6-64ba5fea8eb4\",\"__v\":0},{\"_id\":\"55753fed1f3eaf7c28db698d\",\"Id\":\"2\",\"Title\":\"Hotels.com\",\"Category\":\"Restuarent\",\"ShortDescription\":\"Hotels.com Deal Of The Day Upto 65% OFF on Hotel Booking\",\"Description\":\"Offer is Get upto 65% Discounts on your hotel booking payments through Hotels.com Deal Of The day.Check out the mega deals and get discounts of upto 65% on hotels at top destinations. No coupon code is required for this Hotels.com Deal Of The Day Offer!!.\",\"ImageUUID\":\"b078bf86-a0a0-43e9-8ab6-0b450c852086\",\"__v\":0},{\"_id\":\"5575402b1f3eaf7c28db698e\",\"Id\":\"3\",\"Title\":\"Gaana.com\",\"Category\":\"Music\",\"ShortDescription\":\"Refer a Friend and Get 2 Weeks of Free Gaana Plus\",\"Description\":\"Gaana app new free recharge offer- Invite your friends and Get free 2 weeks pro subscription of gaana app,per refer and 50 rs to the person using your referral code. Each referral code can be used for upto 5 times.\",\"ImageUUID\":\"66746236-0882-49b9-9453-0d6a5c2d9451\",\"__v\":0},{\"_id\":\"557540911f3eaf7c28db698f\",\"Id\":\"4\",\"Title\":\"Fashion And You\",\"Category\":\"Clothing\",\"ShortDescription\":\"Fashionandyou Offer on Sunglasses & Watches Online - Upto 60% Offer\",\"Description\":\"Upto 60% Offer on Fashionandyou Sunglasses & Watches .Sunglasses and Watches Fashionandyou Offer on Both Men & Women From Lacoste Brands such as Unisex Frame, Women Frame, Unisex Sunglsses, Men frame, Men Casual Watches, Women Casual Watches, Unisex Casual Watches and more. \",\"ImageUUID\":\"77a800a2-96c4-417f-b815-4ba44210e5d8\",\"__v\":0},{\"_id\":\"557540e61f3eaf7c28db6990\",\"Id\":\"5\",\"Title\":\"ZoomCar\",\"Category\":\"Travel\",\"ShortDescription\":\"Zommcar Offer On First Ride Get 20% OFF\",\"Description\":\"Book your first drive and get 20% off at Zoomcar Offer O First Ride. Use coupon code ZOOMSD20 to avail this offer.Book your Cab now for your travel Plan And get 20% OFF O Discounts on your first ride alone.have a happy journey with Zoomcar!!\",\"ImageUUID\":\"6b970900-bfab-45fe-9b2d-cb7be7171a26\",\"__v\":0},{\"_id\":\"557541411f3eaf7c28db6991\",\"Id\":\"6\",\"Title\":\"Fashion Jewellery\",\"Category\":\"Jewellery\",\"ShortDescription\":\"Shine and Simmer on Women Fashion Jewellery Online - Upto 70% OFF\",\"Description\":\"Upto 70% Offer on Womens Fashion Jewellery Online from Fashionandyou. Buy online Fashion Jewellery Designs for Bauble Burst Brands such as Bracelet, Earrings, Rings and Bangles. Price Range of Women Fashion Jewellery Online Starts From Rs.229 .\",\"ImageUUID\":\"843a30a1-3423-4642-9c26-f62d5dc8b977\",\"__v\":0},{\"_id\":\"557541801f3eaf7c28db6992\",\"Id\":\"7\",\"Title\":\"FoodPanda\",\"Category\":\"Restuarent\",\"ShortDescription\":\"Get Rs.100 worth Foodpanda Voucher with ICICI Bank Pay \",\"Description\":\"Get Rs.100 worth Foodpanda Voucher with ICICI Bank Pay. ICICI Bank in collaboration with foodpanda.in presents a delightful offer for icicibankpay customers! Simply register for icicibankpay, a first of its kind banking service using Twitter and get Rs.100 offer on order of Rs.250 on foodpanda.in. Hurry up to grab this nice deal! Don't miss this excusive Online food orders offer!!!\",\"ImageUUID\":\"ef6227e2-d13b-4b12-86db-4530beaa52f6\",\"__v\":0},{\"_id\":\"557543a51f3eaf7c28db6993\",\"Id\":\"9\",\"Title\":\"Savaari\",\"Category\":\"Travel\",\"ShortDescription\":\"Savaari Cab Offer Flat 10% Discounts For ICICI Bank Customers!!\",\"Description\":\"ICICI Bank Offer in Savaari Cab Get FLAT 10% off* on Car Rentals! Use Promo code SAVICICI13 to avail the offer.This offer applicable for all kind of travel plans.No More restriction to grab this offer.book your Savaari Cab Offer through ICICI Bank Card And get Discounts on your travel payment!!!\",\"ImageUUID\":\"0a94eed8-041f-4c99-8dd1-8781c65b9544\",\"__v\":0}]}";

    public static class ServerUrls{
        public static String websiteUrl="http://192.168.178.1:3000/";
        public static String signUp=websiteUrl+"signup";
        public static String login=websiteUrl+"login";
        public static String loginFacebook=websiteUrl+"login/facebook";
        public static String loginGoogle=websiteUrl+"login/google";
        public static String linkSocialAccount=websiteUrl+"linkSocialNetwork";
        public static String insertGCM=websiteUrl+"insertGcm";
        public static String regenerateToken=websiteUrl+"token";
        public static String changePassword=websiteUrl+"changePassword";
        public static String dealDetail=websiteUrl+"deal/";
        public static String dealList=websiteUrl+"deal";
        public static String likeDeal=websiteUrl+"likeDeal";
        public static String shareDeal=websiteUrl+"shareDeal";
        public static String forgotPassword=websiteUrl+"forgotPassword";
    }

    public static URL getDealDetailsURL(String dealId, String token) {
        try {
            return new URL(ServerUrls.dealDetail + dealId + "?token=" + token);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final String MIXPANEL_TOKEN="873f1995dd119bdb63b8a51bc2f4951d";

    public static final String parentURLForGetRequest="http://192.168.1.117:3000/deal/";
    public static final String parentURLForCouponCode="http://192.168.1.114:3000/deal/";


    public static final String serverURL = "http://jazzyarchitects.orgfree.com/deal.php";
    public static final String TAG_DEAL_NAME = "Title";
    public static final String TAG_IMAGE_URL = "ImageUrl";
    public static final String TAG_IMAGE_UUID="ImageUUID";
    public static final String TAG_SHORT_DESCRIPTION = "ShortDescription";
    public static final String TAG_LONG_DESCRIPTION = "Description";
    public static final String TAG_ID = "Id";
    public static final String TAG_CATEGORY = "Category";
    public static final String TAG_IF_FEATURED="ifFeatured";
    public static final String TAG_IF_FAVOURITE="ifFavorite";
    public static final String TAG_DETAILS="details";
    public static final String TAG_COUPON_CODE="couponCode";

    public static final String PARCELABLE_DEAL_LIST_KEY ="dealList";
    public static final String PARCELABLE_DEAL_KEY ="dealList";

    public static final String CURRENT_DEAL_INDEX="index";

    public static final String URL_ARGUMENT = "urlArg";

    /**Shared Pref Tags for USER DATA **/
    public static final String PREF_NAME="LogInOut";
    public static final String PREF_DATA_NAME="Name";
    public static final String PREF_DATA_PHOTO="ImageUrl";
    public static final String PREF_DATA_COVER_PIC="CoverPic";
    public static final String PREF_DATA_EMAIL="EmailId";
    public static final String PREF_DATA_MOBILE="mobileNo";
    public static final String PREF_DATA_LOCATION="location";
    public static final String PREF_DATA_PASS="password";
    public static final String PREF_DATA_USER_TOKEN="userToken";
    public static final String PREF_DATA_MIXPANEL_USER_ID="mixpanelUserId";

    public static final String INTENT_ID_NAME="idName";
    public static final String INTENT_ID_VALUE="idValue";
    public static final String FACEBOOKID="facebookId";
    public static final String GOOGLEID="googleId";

    public static final String POST_SUCCESS="success";
    public static final String POST_ERROR="error";
    public static final String POST_TOKEN="token";
    public static final String POST_NAME="name";
    public static final String POST_EMAIL="email";
    public static final String POST_MOBILE="mobile";
    public static final String POST_PASSWORD="password";
    public static final String POST_MIXPANELID="mixpanelId";
    public static final String POST_GOOGLE="googleId";
    public static final String POST_FACEBOOK="facebookId";

    public static final String PREF_NOTIFICATION_NAME="Notification";
    public static final String PREF_NOTIFICATION_ON_OFF="NotifyOnOff";
    public static final String PREF_NOTIFICATION_DEAL_OF_DAY="DealOfDay";
    public static final String PREF_NOTIFICATION_RINGTONE="Ringtone";



    /**Shared Pref Tags for GCM **/
    public static final String PREF_GCM_NAME="gcmSharedPreferences";
    public static final String GCM_REG_ID="registrationId";
    public static final String GCM_APP_VERSION="appVersion";


    public static boolean saveUserDetails(Context context, User user){
        if(user==null)
            return false;
        SharedPreferences userPrefs=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        if(!user.getName().isEmpty())
            editor.putString(PREF_DATA_NAME, user.getName());
        if(!user.getProfilePic().isEmpty())
            editor.putString(PREF_DATA_PHOTO, user.getProfilePic());
        if(!user.getCoverImage().isEmpty())
            editor.putString(PREF_DATA_COVER_PIC, user.getCoverImage());
        if(!user.getEmail().isEmpty())
            editor.putString(PREF_DATA_EMAIL, user.getEmail());
        if(!user.getMobile().isEmpty())
            editor.putString(PREF_DATA_MOBILE, user.getMobile());
        if(!user.getToken().isEmpty())
            editor.putString(PREF_DATA_USER_TOKEN, user.getToken());
        if(!user.getMixpanelId().isEmpty())
            editor.putString(PREF_DATA_MIXPANEL_USER_ID, user.getMixpanelId());
        editor.apply();
        return true;
    }

    public static User getUser(Context context){
        SharedPreferences userPref;
        userPref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String name=userPref.getString(Constants.PREF_DATA_NAME,"");
        String email=userPref.getString(Constants.PREF_DATA_EMAIL,"");
        String mobile=userPref.getString(Constants.PREF_DATA_MOBILE, "");
        String coverPic=userPref.getString(Constants.PREF_DATA_COVER_PIC,"");
        String profilePic=userPref.getString(Constants.PREF_DATA_PHOTO,"");
        String mixPanelId=userPref.getString(Constants.PREF_DATA_MIXPANEL_USER_ID,"");
        String token=userPref.getString(Constants.PREF_DATA_USER_TOKEN,"");
        String location=userPref.getString(Constants.PREF_DATA_LOCATION,"");

        return new User(name,email,mobile,coverPic,profilePic,token,mixPanelId,location);
    }


    /**
     * To convert hashmap to post request for sending to server
     * @param params the hashmap
     * @return post request string
     * @throws UnsupportedEncodingException
     */
    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
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



    public static DisplayImageOptions getProfilePicDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nodp)
                .showImageForEmptyUri(R.drawable.nodp)
                .showImageOnFail(R.drawable.nodp)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer()).build();
    }

    public static DisplayImageOptions getCoverPicDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.navigation_drawer_cover_pic)
                .showImageForEmptyUri(R.drawable.navigation_drawer_cover_pic)
                .showImageOnFail(R.drawable.navigation_drawer_cover_pic)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer()).build();
    }


//    public static MixpanelAPI getMixPanelInstance(Context context){
//        MixpanelAPI mixpanelAPI= MixpanelAPI.getInstance(context,MIXPANEL_TOKEN);
//        String id=context.getSharedPreferences(Constants.PREF_NAME,Context.MODE_PRIVATE).getString(Constants.PREF_DATA_MIXPANEL_USER_ID,"0");
//        mixpanelAPI.identify(id);
//        return mixpanelAPI;
//    }


    /**
     * Basic Profile Info fields
     */
    public static final String[] sharedPrefTags={Constants.PREF_DATA_NAME,Constants.PREF_DATA_EMAIL,
            Constants.PREF_DATA_MOBILE,Constants.PREF_DATA_LOCATION};
    public static ArrayList<Pair<Integer,String>> getProfileInfoFields(){
        ArrayList<Pair<Integer,String>> list=new ArrayList<>();
        list.add(Pair.create(R.drawable.userlogin,"Name"));
        list.add(Pair.create(R.drawable.maillogin, "Email Id"));
        list.add(Pair.create(R.drawable.phonelogin,"Contact Number"));
        list.add(Pair.create(R.drawable.location,"Location"));
        return list;
    }

    /**
     * Verify Email
     */
    public static boolean isValidEmailId(CharSequence target){
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
    public static ArrayList<Pair<Integer,String>> getNavigationDrawerItems(){
        ArrayList<Pair<Integer,String>> item=new ArrayList<>();
        item.add(Pair.create(R.drawable.scan_icon,"Scan QR Code"));
        item.add(Pair.create(R.drawable.enter_code_icon,"Enter Scribbler Code"));
        item.add(Pair.create(R.drawable.all_deals_icon,"All Deals"));
        item.add(Pair.create(R.drawable.featured_deals_icon,"Claimed Deals"));
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


