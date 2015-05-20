package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.R;

/**
 * Created by Jibin_ism on 12-May-15.
 * This is the popup which is shown when the user enters the the scribbler code or scans the code
 */
public class DealPopup extends Dialog implements View.OnClickListener {

    Button codeButton;
    TextView title, description;
    private String url;
    Context context;

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
        setContentView(R.layout.deal_popup);

        //View Setup
        title=(TextView)findViewById(R.id.title);
        codeButton=(Button)findViewById(R.id.codeButton);
        description=(TextView)findViewById(R.id.description);

        codeButton.setOnClickListener(this);
        description.setText(url+"\n\nClick here to get your scribble code which enables you to claim this deal at the retailer");

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
                getCodeAndDisplay(url);
                break;
            default:
                break;
        }
    }

    /**
     * Code for getting the scribbler code from the server
     * @param url the url to which request is to be sent
     */
    public void getCodeAndDisplay(String url){
        Toast.makeText(context,"Deal Claimed",Toast.LENGTH_LONG).show();
    }
}
