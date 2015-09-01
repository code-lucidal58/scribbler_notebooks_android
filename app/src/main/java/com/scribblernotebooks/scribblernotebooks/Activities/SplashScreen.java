package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andraskindler.parallaxviewpager.ParallaxViewPager;
import com.scribblernotebooks.scribblernotebooks.Adapters.IllustrationsPageAdapter;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    ImageView topCurve, whiteLogo;
    LinearLayout lowerCurve;
    TextView bottomText;
    int screenWidth, screenHeight;

    final boolean IS_RELEASE = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        topCurve = (ImageView) findViewById(R.id.topCurve);
        whiteLogo = (ImageView) findViewById(R.id.whiteLogo);
        lowerCurve = (LinearLayout) findViewById(R.id.lowerCurve);
        bottomText = (TextView) findViewById(R.id.bottomText);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        setScreenWidth(metrics.widthPixels);
        setScreenHeight(metrics.heightPixels);

        topCurve.getLayoutParams().height = (int) (getScreenHeight() * 0.6);
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
                        SplashScreen.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                            }
                        });
                    }
                }, 5000);
            }
        });

        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREF_ONE_TIME_NAME, MODE_PRIVATE);
                    if (sharedPreferences.getBoolean(Constants.PREF_SHOW_ILLUSTRATION, true)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                changeColor();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                translateAndLogIn();
                            }
                        });
                    }
                }
            }
        });
        timer.start();

    }

    public void translateAndLogIn() {
        startActivity(new Intent(getApplicationContext(), LogIn.class));
        finish();
        overridePendingTransition(R.anim.flash_appear, R.anim.flash_disappear);
//        Log.e("Splash", "Starting translate and Login Animation");
//        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, getLogoHeight() - (int) (0.6 * getScreenHeight()));
//        TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, getLogoHeight() - (int) (0.59 * getScreenHeight())+10);
//        animation.setDuration(1000);
//        animation.setFillEnabled(true);
//        animation.setFillAfter(true);
//        animation2.setDuration(1000);
//        animation2.setFillEnabled(true);
//        animation2.setFillAfter(true);
//
//        Thread a=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(500);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }finally {
//                    startActivity(new Intent(getApplicationContext(),LogIn.class));
//                    finish();
//                    overridePendingTransition(R.anim.flash_appear,R.anim.flash_disappear);
//                }
//            }
//        });
//        a.start();
//
//        topCurve.startAnimation(animation);
//        whiteLogo.startAnimation(animation2);
    }

    public void changeColor() {
//        Log.e("Splash", "Starting animation");
//        Drawable[] topCurves = new Drawable[2];
//        topCurves[0] = getResources().getDrawable(R.drawable.splash_top_blue);
//        topCurves[1] = getResources().getDrawable(R.drawable.splash_top_green);
//        TransitionDrawable drawable = new TransitionDrawable(topCurves);
//        topCurve.setImageDrawable(drawable);
//        drawable.startTransition(750);

        translateCurve();

    }

    public void translateCurve() {
        Log.e("Splash", "Starting translate Animation");
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, getLogoHeight() - (int) (0.6 * getScreenHeight()));
        TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, getLogoHeight() - (int) (0.58 * getScreenHeight()));
        animation.setDuration(750);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation2.setDuration(750);
        animation2.setFillEnabled(true);
        animation2.setFillAfter(true);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startIllustration();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        topCurve.startAnimation(animation);
        whiteLogo.startAnimation(animation2);
    }

    public void startIllustration() {
        lowerCurve.setVisibility(View.VISIBLE);
        IllustrationsPageAdapter illustrationsPageAdapter;
        ParallaxViewPager mViewPager;

        int[] imageId = {R.drawable.illustration1, R.drawable.illustration2, R.drawable.illustration3};

        illustrationsPageAdapter = new IllustrationsPageAdapter(this, imageId);

        mViewPager = (ParallaxViewPager) findViewById(R.id.illustrations_pager);
        mViewPager.setVisibility(View.VISIBLE);
        mViewPager.setAdapter(illustrationsPageAdapter);
        mViewPager.setScaleType(ParallaxViewPager.FIT_WIDTH);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    bottomText.setText("Lets Get Started");
                    lowerCurve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getSharedPreferences(Constants.PREF_ONE_TIME_NAME, MODE_PRIVATE).edit().putBoolean(Constants.PREF_SHOW_ILLUSTRATION, false).apply();
                            startActivity(new Intent(getApplicationContext(), LogIn.class));
                            finish();
                            overridePendingTransition(R.anim.login_slide_in, R.anim.profile_slide_out);
                        }
                    });
                } else {
                    bottomText.setText("Swipe to Continue -->");
                    lowerCurve.setOnClickListener(null);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getLogoHeight() {
        return (int) getResources().getDimension(R.dimen.topCurveHeight);
    }

    public int getLowerCurveHeight() {
        return (int) getResources().getDimension(R.dimen.lowerCurveHeight);
    }
}
