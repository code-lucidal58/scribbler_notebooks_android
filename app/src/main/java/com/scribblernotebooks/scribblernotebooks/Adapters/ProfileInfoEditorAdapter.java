package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.Fragments.ProfileFragment;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Jibin_ism on 26-May-15.
 */
public class ProfileInfoEditorAdapter extends RecyclerView.Adapter<ProfileInfoEditorAdapter.ViewHolder> {

    ArrayList<Pair<Integer, String>> fieldList;
    String[] sharedPrefTags;
    SharedPreferences userPrefs;
    Context mContext;
    SharedPreferences.Editor userPrefEditor;
    View drawer;
    int[] viewids={R.id.userName,R.id.userEmail};
    ProfileFragment.OnFragmentInteractionListener mListener;

    public ProfileInfoEditorAdapter(Context context, View view, ProfileFragment.OnFragmentInteractionListener listener) {
        this.fieldList=Constants.getProfileInfoFields();
        sharedPrefTags= Constants.sharedPrefTags;
        this.mContext=context;
        drawer=view;
        this.mListener=listener;
        userPrefs=mContext.getSharedPreferences(Constants.PREF_NAME,Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayout= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_profile_basic,parent,false);
        return new ViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Pair<Integer, String> field=fieldList.get(position);
        final String prefTag=sharedPrefTags[position];
        ImageView icon=holder.icon;
        EditText editText=holder.detail;

        icon.setImageResource(field.first);
        editText.setHint(field.second);

        editText.setText(userPrefs.getString(prefTag, ""));

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    userPrefEditor = userPrefs.edit();
                    userPrefEditor.putString(prefTag, ((EditText) v).getText().toString());
                    userPrefEditor.apply();
                    if(position==0 || position==1)
                        ((TextView)drawer.findViewById(viewids[position])).setText(((EditText) v).getText().toString());
                    Log.i("UserData", "Name " + ((EditText)v).getText().toString());
                    if(position==0){
                        mListener.onUserNameChanged();
                    }
                    if(position==1){
                        mListener.onUserEmailChanged();
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    /**
     * View Holder Class to point to recycler view item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public EditText detail;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            /** View Setup */
            icon=(ImageView)itemLayoutView.findViewById(R.id.item_icon);
            detail=(EditText)itemLayoutView.findViewById(R.id.item_editText);
        }
    }

}
