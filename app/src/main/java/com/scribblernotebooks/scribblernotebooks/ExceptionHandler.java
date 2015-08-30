package com.scribblernotebooks.scribblernotebooks;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Jibin_ism on 24-Jun-15.
 */
public class ExceptionHandler extends Application {
    private Thread.UncaughtExceptionHandler exceptionHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        exceptionHandler=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }

    private  Thread.UncaughtExceptionHandler uncaughtExceptionHandler=new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

            File root=Environment.getExternalStorageDirectory();
            File dir=new File(root.getAbsolutePath()+"/scribbler");

            Random random=new Random();
//            int i1=random.nextInt();

            int i1=0;
            dir.mkdirs();
            try{
                FileOutputStream f=new FileOutputStream(new File(dir,"ScribblerLogFile("+i1+").txt"));
                String data=ex.toString();
                String data2="";
                for(int i=0;i<ex.getStackTrace().length;i++){
                    data2+="\n\n****\n\n"+ex.getStackTrace()[i];
                }
                try{
                    f.write(Calendar.getInstance().toString().getBytes());
                }catch (Exception e){
                    e.printStackTrace();
                }
                f.write(data.getBytes());
                f.write("\n".getBytes());
                f.write(data2.getBytes());
                f.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            exceptionHandler.uncaughtException(thread, ex);
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
