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
 */
public class DealPopup extends Dialog implements View.OnClickListener {

    Button codeButton;
    TextView title, description;
    private String url;
    Context context;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.deal_popup);
        title=(TextView)findViewById(R.id.title);
        codeButton=(Button)findViewById(R.id.codeButton);
        codeButton.setOnClickListener(this);
        description=(TextView)findViewById(R.id.description);
        description.setText(url+"\n\nClick here to get your scribble code which enables you to claim this deal at the retailer");

    }

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

    public void getCodeAndDisplay(String url){
        Toast.makeText(context,"Deal Claimed",Toast.LENGTH_LONG).show();
    }
}
