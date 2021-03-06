package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
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
public class ForgotPasswordPopup extends Dialog implements View.OnClickListener {

    Button resetButton;
    TextView postResetText;
    Context context;
    EditText email;

    /**
     * Default Constructors
     */
    public ForgotPasswordPopup(Context context) {
        super(context);
        this.context=context;
    }

    public ForgotPasswordPopup(Context context, int theme) {
        super(context, theme);
        this.context=context;
    }

    protected ForgotPasswordPopup(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context=context;
    }

    Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Removing title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getAttributes().windowAnimations=R.style.ForgotPasswordAnimation;
        //Setting View
        setContentView(R.layout.popup_forgot_password);

        //View Setup
        email=(EditText)findViewById(R.id.et_email);
        postResetText=(TextView)findViewById(R.id.t2);
        resetButton=(Button)findViewById(R.id.codeButton);
        resetButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.codeButton:
                getRecoveryLink();
                break;
            default:
                break;
        }
    }

    protected void getRecoveryLink(){
        String userEmail=email.getText().toString();
        new AsyncTask<String,Void,String>(){

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Hold on a second. We are recovering your password...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String email=params[0];

                try {
                    URL url=new URL(Constants.ServerUrls.forgotPassword+"?email="+email);
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(15000);
                    connection.setConnectTimeout(15000);
                    connection.setDoInput(true);
                    connection.connect();

                    BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    return reader.readLine();

                } catch (Exception e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

//                Log.e("ForgotPassword", "Server response " + s);
                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                postResetText.setVisibility(View.VISIBLE);
                if(s==null){
                    postResetText.setText("There is some problem connecting to server. Please try again later.");
                    return;
                }
                if(s.isEmpty()){
                    postResetText.setText("There is some problem connecting to server. Please try again later.");
                    return;
                }
                try {
                    JSONObject object=new JSONObject(s);
                    String err=object.optString("error");
                    if(err.equalsIgnoreCase("INVALID_USER")){
                        postResetText.setText("We are unable to verify this account. Check the email id you have entered.");
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    resetButton.setText("Close");
                    resetButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                }


            }

        }.execute(userEmail);
    }
}
