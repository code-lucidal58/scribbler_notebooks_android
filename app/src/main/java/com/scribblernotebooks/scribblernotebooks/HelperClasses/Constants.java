package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by Jibin_ism on 08-May-15.
 */
public class Constants {


    public static final String ARGUMENTS_URL = "argumentsUrl";
    public static final String ARGUMENTS_LISTTYPE = "argumentsListType";
    public static final String LISTTYPE_ALL = "ListType_all";
    public static final String LISTTYPE_NEW = "ListType_new";
    public static final String LISTTYPE_TRENDING = "ListType_trending";


    public static final String EXTRA_DEAL_CODE = "dealCode";
    String URLFinal = "";
    public static final String serverURL = "http://jazzyarchitects.orgfree.com/intern58195/sn7315/request.php?table=";
    public static final String TAG_DEAL_NAME = "dealName";
    public static final String TAG_IMAGE_URL = "imgurl";
    public static final String TAG_SHORT_DESCRIPTION = "sdescription";
    public static final String TAG_LONG_DESCRIPTION = "ldescription";
    public static final String TAG_ID = "id";
    public static final String TAG_CATEGORY = "category";
    public static final String TAG_NEW = "newHot";


    public static final String PREF_NAME = "LogInOut";
    public static final String PREF_DATA_NAME = "Name";
    public static final String PREF_DATA_PHOTO = "ImageUrl";
    public static final String PREF_BOOL_SIGN = "SignStatus";

    public static final String URL_ARGUMENT = "urlArg";


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
                    float scale = getRandomFloat(0.8, 2);
                    image.setScaleX(scale);
                    image.setScaleY(scale);
                }
                setMovingAnimation(image, duration, initialX, getRandomFloat(screenHeight / 10, 8 * screenHeight / 10), changeSize, screenHeight);
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


}


