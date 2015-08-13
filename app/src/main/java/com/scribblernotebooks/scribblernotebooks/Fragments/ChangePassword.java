package com.scribblernotebooks.scribblernotebooks.Fragments;


import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassword extends DialogFragment {

    EditText oldPassword, newPassword, confirmPassword;
    TextView success;
    Button submit;

    public ChangePassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_change_password, container, false);

        this.getDialog().setTitle("Change Password");


        oldPassword=(EditText)v.findViewById(R.id.oldPassword);
        newPassword=(EditText)v.findViewById(R.id.newPassword);
        confirmPassword=(EditText)v.findViewById(R.id.confirmPassword);
        submit=(Button)v.findViewById(R.id.submit);
        success=(TextView)v.findViewById(R.id.tv_success);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n,nc;

                if(oldPassword.getText().toString().isEmpty())
                    oldPassword.setError("Cannot be Empty");
                if(newPassword.getText().toString().isEmpty())
                    newPassword.setError("Cannot be Empty");
                if(confirmPassword.getText().toString().isEmpty())
                    confirmPassword.setError("Cannot be Empty");

                if(newPassword.getText().toString().length()<8) {
                    newPassword.setError("Must be atleast 8 characters");
                    return;
                }


                n=newPassword.getText().toString();
                nc=confirmPassword.getText().toString();

                if(n.equals(nc)){
                    changePassword(n);
                }
                else{
                    newPassword.setError("Password does not match");
                    confirmPassword.setError("Password does not match");
                }
            }
        });

        return v;
    }

    public void changePassword(String pass){
        String email=getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).getString(Constants.PREF_DATA_EMAIL,"");
        new AsyncTask<String, Void, Void>(){
            @Override
            protected Void doInBackground(String... params) {
                String email=params[0];
                String pass=params[1];

                try {
                    URL url=new URL(Constants.ServerUrls.changePassword);
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    User user=Constants.getUser(getActivity());
                    String token=user.getToken();
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setReadTimeout(15000);
                    connection.setConnectTimeout(15000);

                    HashMap<String, String> data=new HashMap<String, String>();
                    data.put(Constants.POST_EMAIL,email);
                    data.put(Constants.POST_PASSWORD,pass);

                    OutputStream os=connection.getOutputStream();
                    BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                    writer.write(Constants.getPostDataString(data));
                    writer.flush();
                    writer.close();
                    os.close();

                    String response="";
                    if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                        BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        response=reader.readLine();
                        JSONObject jsonObject=new JSONObject(response);
                        Boolean success1=Boolean.parseBoolean(jsonObject.optString("success"));
                        if(success1){
                            success.setVisibility(View.VISIBLE);
                        }else{
                            oldPassword.setError("Password is incorrect");
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(email,pass);
    }


}
