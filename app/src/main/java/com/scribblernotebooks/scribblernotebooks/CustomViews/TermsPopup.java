package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.scribblernotebooks.scribblernotebooks.Handlers.DatabaseHandler;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jibin_ism on 12-May-15.
 * This is the popup which is shown when the user enters the the scribbler code or scans the code
 */
public class TermsPopup extends Dialog implements View.OnClickListener {

    TextView titleView, contentView;
    private String url;
    Context context;
    String  title;

    /**
     * Default Constructors
     */
    public TermsPopup(Context context) {
        super(context);
        this.context=context;
    }

    public TermsPopup(Context context, int theme) {
        super(context, theme);
        this.context=context;
    }

    protected TermsPopup(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context=context;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Removing title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Setting View
        setContentView(R.layout.popup_privacy);



        //View Setup
        titleView=(TextView)findViewById(R.id.title);
        contentView=(TextView)findViewById(R.id.content);

        titleView.setText(title);

        displayContent();

    }
    /**
     * Setting URL for request
     * @param url
     */
    public void setUrl(String url){
        this.url=url;
    }

    public void setTitle(String title){
        this.title=title;
    }


    /**
     * Code for getting the scribbler code from the server
     */
    public void displayContent(){
        new AsyncTask<Void,Void,String>(){

            ProgressDialog progressDialog=null;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=new ProgressDialog(context);
                progressDialog.setMessage("Loading");
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    URL url1=new URL(url);
                    HttpURLConnection connection=(HttpURLConnection)url1.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.setReadTimeout(15000);
                    connection.setConnectTimeout(10000);
                    BufferedReader b=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    return b.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                JSONObject js;
                if(progressDialog!=null){
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
                try {
                    js = new JSONObject(s);
                    /*{success: true, data:"Content here"}*/
                    String code=js.optString("data");
                    contentView.setText(Html.fromHtml(code));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onPostExecute(s);
            }
        }.execute();
    }
    @Override
    public void onClick(View v) {

    }
}
