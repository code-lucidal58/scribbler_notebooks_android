package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.R;

/**
 * Created by Jibin_ism on 12-May-15.
 * This is the popup which is shown when the user enters the the scribbler code or scans the code
 */
public class DealPopup extends Dialog implements View.OnClickListener {

    Button codeButton;
    TextView title, description,category;
    private String url;
    Context context;
    ImageView imageView;
    private String content;
    Deal currentDeal;

    String t,d,i,c;

    public DisplayImageOptions displayImageOptions;
    public ImageLoadingListener imageLoadingListener;
    public ImageLoaderConfiguration imageLoaderConfiguration;
    /**
     * Default Constructors
     */
    public DealPopup(Context context) {
        super(context);
        this.context=context;
    }

    public DealPopup(Context context, int theme) {
        super(context, theme);
        this.context=context;
    }

    protected DealPopup(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context=context;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Removing title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Setting View
        setContentView(R.layout.popup_deal);


        imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(context).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
        imageLoadingListener = new SimpleImageLoadingListener();
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer()).build();


        //View Setup
        title=(TextView)findViewById(R.id.dealTitle);
        codeButton=(Button)findViewById(R.id.codeButton);
        description=(TextView)findViewById(R.id.dealDescription);
        category=(TextView)findViewById(R.id.dealCategory);
        imageView=(ImageView)findViewById(R.id.dealIcon);

        codeButton.setOnClickListener(this);
        description.setText(content);

        title.setText(t);
        category.setText(c);
        description.setText(d);
        ImageLoader.getInstance().displayImage(i, imageView, displayImageOptions, imageLoadingListener);
    }

    public void setTitle(String s){
        t=s;
    }

    public void setCategory(String s){
        c=s;
    }

    public void setDescription(String s){
        d=s;
    }

    public void setImage(String url){
        i=url;
    }

    public void setCurrentDeal(Deal deal){
        this.currentDeal=deal;
    }

    /**
     * Setting URL for request
     * @param url
     */
    public void setUrl(String url){
        this.url=url;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.codeButton:
                String s=currentDeal.claimDeal(context);
                if(s.isEmpty()){
                    codeButton.setText("Error claiming... Try Again");
                    break;
                }
                codeButton.setText(s);
                codeButton.setOnClickListener(null);
                break;
            default:
                break;
        }
    }


    public void setContent(String content){
        this.content=content;
    }
}
