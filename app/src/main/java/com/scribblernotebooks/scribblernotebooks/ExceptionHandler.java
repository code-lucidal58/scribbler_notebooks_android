package com.scribblernotebooks.scribblernotebooks;

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

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
            dir.mkdirs();
            try{
                FileOutputStream f=new FileOutputStream(new File(dir,"ScribblerLogFile.txt"));
                String data=ex.toString();
                String data2="";
                for(int i=0;i<ex.getStackTrace().length;i++){
                    data2+="\n\n****\n\n"+ex.getStackTrace()[i];
                }
                f.write(data.getBytes());
                f.write("***********************************".getBytes());
                f.write(data2.getBytes());
                f.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            exceptionHandler.uncaughtException(thread, ex);
        }
    };
}
