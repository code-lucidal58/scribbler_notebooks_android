package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.scribblernotebooks.scribblernotebooks.R;

/**
 * Created by Aanisha on 25-Jun-15.
 * PopUp to get College from User
 */
public class CollegePopUp extends Dialog implements View.OnClickListener{
    ArrayAdapter<String> arrayAdapter;
    AutoCompleteTextView collegeName;
    Button skip,okay;
    String[] college;

    public CollegePopUp(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Removing title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_college);

        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.select_dialog_item,college);
        collegeName=(AutoCompleteTextView)findViewById(R.id.college);
        skip=(Button)findViewById(R.id.skip);
        okay=(Button)findViewById(R.id.okay);

        skip.setOnClickListener(this);
        okay.setOnClickListener(this);

        collegeName.setThreshold(1);
        collegeName.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id){
            case R.id.skip:
        }

    }
}
