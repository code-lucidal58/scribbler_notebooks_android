package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Jibin_ism on 18-May-15.
 */
public class CircleNotificationIndicatorView extends SurfaceView implements SurfaceHolder.Callback2 {


    Paint paint;
    int[] colors = {Color.WHITE};
    int colorPosition = 0;
    int x, y, radius;
    int COLOR_CHANGE_INTERVAL = 1000;
    Bitmap bitmap;

    ColorChangeThread colorChangeThread;
    private SurfaceHolder surfaceHolder;

    public CircleNotificationIndicatorView(Context context) {
        super(context);
        paint = new Paint();
        init();
    }


    public CircleNotificationIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        init();
    }

    public CircleNotificationIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        init();
    }

    public void init() {
        x = getWidth();
        y = getHeight();
        radius = 15;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        colorChangeThread=new ColorChangeThread(this);
        surfaceHolder=getHolder();
    }

    public int getNextColorPosition() {
        if (colorPosition == colors.length - 1) {
            colorPosition = 0;
        } else {
            colorPosition++;
        }
        return colorPosition;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

//    public Thread thread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            paint.setColor(colors[getNextColorPosition()]);
//            canvas1.drawCircle(x/2, y/2, radius, paint);
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            invalidate();
//        }
//    });

    public Thread getThread(){
        return colorChangeThread;
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        colorChangeThread.setRunning(true);
        colorChangeThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                colorChangeThread.join();
                retry = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        colorChangeThread.setRunning(false);
        colorChangeThread.interrupt();
    }


    public  void updateColor(Canvas canvas){
        paint.setColor(colors[getNextColorPosition()]);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x/2, y/2, radius, paint);
    }


    public class ColorChangeThread extends Thread{
        CircleNotificationIndicatorView circleNotificationIndicatorView;
        private boolean running=false;

        public ColorChangeThread(CircleNotificationIndicatorView circleNotificationIndicatorView){
            this.circleNotificationIndicatorView=circleNotificationIndicatorView;
        }

        public void setRunning(boolean run){
            running=run;
        }

        @Override
        public void run() {
            while(running){
                Canvas canvas=circleNotificationIndicatorView.getHolder().lockCanvas();
                if(canvas!=null){
                    synchronized (circleNotificationIndicatorView.getHolder()){
                        circleNotificationIndicatorView.updateColor(canvas);
                    }
                    circleNotificationIndicatorView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            try{
                Thread.sleep(COLOR_CHANGE_INTERVAL);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void setColorChangeInterval(int notificationCount) {
        COLOR_CHANGE_INTERVAL = Math.max(COLOR_CHANGE_INTERVAL - 50 * notificationCount, 100);
    }


}
