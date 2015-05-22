package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Aanisha on 22-May-15.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    ArrayList<String> suggestionList;
    public SearchListAdapter(ArrayList<String> s) {
        suggestionList=s;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtSuggestion;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtSuggestion=(TextView)itemLayoutView.findViewById(R.id.suggestion);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //create new view
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listview_item_searchquery,viewGroup,false);
        // create ViewHolder
        ViewHolder viewHolder=new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String suggestion = suggestionList.get(position);

        viewHolder.txtSuggestion.setText(suggestion);
    }

    @Override
    public int getItemCount() {
        return suggestionList.size();
    }

    public ArrayList<String> searchResult(CharSequence s){
        ArrayList<String> result=new ArrayList<>();
        for(int i=0;i<suggestionList.size();i++)
        {
            if(suggestionList.get(i).toLowerCase().contains(s.toString().toLowerCase()))
            {
                result.add(suggestionList.get(i));
            }
        }
        return result;
    }
}
