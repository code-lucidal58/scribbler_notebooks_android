package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jibin_ism on 08-May-15.
 */
public class Constants {

    public static final String serverURL = "http://jazzyarchitects.orgfree.com/intern58195/sn7315/request.php?table=";
    public static final String TAG_DEAL_NAME = "dealName";
    public static final String TAG_IMAGE_URL = "imgurl";
    public static final String TAG_SHORT_DESCRIPTION = "sdescription";
    public static final String TAG_LONG_DESCRIPTION = "ldescription";
    public static final String TAG_ID = "id";
    public static final String TAG_CATEGORY = "category";

    public static final String URL_ARGUMENT = "urlArg";

    public static final String PREF_NAME="LogInOut";
    public static final String PREF_DATA_NAME="Name";
    public static final String PREF_DATA_PHOTO="ImageUrl";
    public static final String PREF_DATA_COVER_PIC="CoverPic";
    public static final String PREF_DATA_EMAIL="EmailId";

    public static final String PROFILE_FIELD_CLAIM="Claimed Deals";
    public static final String PROFILE_FIELD_LIKE="Liked Deals";
    public static final String PROFILE_FIELD_SHARE="Shared Deals";
    public static final String PROFILE_FIELD_FOLLOWER="Followers";
    public static final String PROFILE_FIELD_FOLLOWING="Following";
    public static final String PROFILE_FIELD_INVITE="Invite Friends";


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
        item.add(Pair.create(R.drawable.featured_deals_icon,"Featured Deals"));
        item.add(Pair.create(R.drawable.sign_out_icon,"Sign Out"));
        return item;
    }

}


