package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Jibin_ism on 25-Jun-15.
 */
public class SurveyDialog extends DialogFragment {

    String id, question;
    ArrayList<String> options;
    TextView questionView;
    RecyclerView optionList;
    Button skip, submit;
    ArrayList<String> selected;

    public static SurveyDialog newInstance(String id, String question, Set<String> options){
        SurveyDialog surveyDialog=new SurveyDialog();
        Bundle args=new Bundle();
        args.putString("id",id);
        args.putString("question", question);
        ArrayList<String> opts=new ArrayList<>();
        opts.addAll(options);
        args.putStringArrayList("options", opts);
        surveyDialog.setArguments(args);
        return surveyDialog;
    }

    public SurveyDialog() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id=getArguments().getString("id");
        question=getArguments().getString("question");
        options=getArguments().getStringArrayList("options");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.popup_survey,container,false);
        questionView=(TextView)v.findViewById(R.id.question);
        getDialog().setTitle("Tell more about you");
        optionList=(RecyclerView)v.findViewById(R.id.optionList);
        skip=(Button)v.findViewById(R.id.skip);
        submit=(Button)v.findViewById(R.id.submit);

        selected=new ArrayList<>();

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendDataToServer().execute();
                getDialog().dismiss();
            }
        });


        questionView.setText(question);
        RecyclerViewAdapter adapter=new RecyclerViewAdapter(options);
        optionList.setLayoutManager(new LinearLayoutManager(getActivity()));
        optionList.setHasFixedSize(true);
        optionList.setAdapter(adapter);
        return v;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        ArrayList<String> options;
        public RecyclerViewAdapter(ArrayList<String> options) {
            super();
            this.options=options;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_survey_option,parent,false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            String option=options.get(position);
            holder.option.setText(option);
            holder.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox c=(CheckBox)v;
                    Boolean checked=c.isChecked();
                    if(checked){
                        selected.add(options.get(position));
                    }else{
                        selected.remove(options.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            CheckBox option;
            public ViewHolder(View itemView) {
                super(itemView);
                option=(CheckBox)itemView.findViewById(R.id.optionCheckBox);
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        SharedPreferences pref=getActivity().getSharedPreferences(Constants.SURVEY_PREF_NAME,Context.MODE_PRIVATE);
        pref.edit().clear().apply();
    }

    public class SendDataToServer extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url=new URL(Constants.ServerUrls.surveySubmit);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoOutput(true);
                String dataString="";
                for(String s:selected){
                    if(dataString.isEmpty()){
                        dataString=s;
                    }else{
                        dataString=", "+s;
                    }
                }
                HashMap<String, String> data=new HashMap<>();
                data.put("response",dataString);

                OutputStream os=connection.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(Constants.getPostDataString(data));
                writer.flush();
                writer.close();
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
