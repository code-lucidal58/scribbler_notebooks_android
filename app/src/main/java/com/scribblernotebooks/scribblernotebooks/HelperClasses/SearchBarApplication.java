package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.R;

/**
 * Created by Aanisha on 20-May-15.
 *
 * A function when any of the item on the search bar is clicked
 * On selecting an item, a new layout in inflated where the user can input query and suggestions
 * are generated.
 */
public class SearchBarApplication implements SearchQuery.OnFragmentInteractionListener {
    View view;
    ViewGroup viewGroup;
    Context context;
    ImageView search, scan, sort, category;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public SearchBarApplication() {
        super();
    }
    public SearchBarApplication(View v,ViewGroup container,Context c,FragmentManager f) {
        view=v;
        viewGroup=container;
        context=c;
        fragmentManager=f;
    }

    public void ImplementFunctions(){
        category=(ImageView)view.findViewById(R.id.category);
        scan=(ImageView)view.findViewById(R.id.scan);
        search= (ImageView) view.findViewById(R.id.search);
        sort=(ImageView)view.findViewById(R.id.sort);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(),"Category Selected",Toast.LENGTH_SHORT).show();
                InflateFragment();
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(),"Scan Selected",Toast.LENGTH_SHORT).show();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(),"Search Selected",Toast.LENGTH_SHORT).show();
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(),"Sort Selected",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void InflateFragment(){
        Fragment fragment=new SearchQuery();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.deals_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
