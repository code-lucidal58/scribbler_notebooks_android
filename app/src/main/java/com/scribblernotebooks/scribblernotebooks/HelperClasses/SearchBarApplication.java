package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.Activities.ScannerActivity;
import com.scribblernotebooks.scribblernotebooks.Fragments.SearchQueryFragment;
import com.scribblernotebooks.scribblernotebooks.R;

/**
 * Created by Aanisha on 20-May-15.
 *
 * A function when any of the item on the search bar is clicked
 * On selecting an item, a new layout in inflated where the user can input query and suggestions
 * are generated.
 */
public class SearchBarApplication implements SearchQueryFragment.OnFragmentInteractionListener {
    View view;
    ViewGroup viewGroup;
    Context context;
    RelativeLayout search, scan, sort, category;
    EditText query;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public SearchBarApplication() {
        super();
        //Empty Constructor required
    }

    public SearchBarApplication(View v, ViewGroup container, Context c, FragmentManager f) {
        view=v;
        viewGroup=container;
        context=c;
        fragmentManager=f;
    }

    public void ImplementFunctions(){
        category=(RelativeLayout)view.findViewById(R.id.layoutCategory);
        scan=(RelativeLayout)view.findViewById(R.id.layoutScan);
        search= (RelativeLayout) view.findViewById(R.id.layoutSearch);
        sort=(RelativeLayout)view.findViewById(R.id.layoutSort);
        query=(EditText)view.findViewById(R.id.query);



        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Category",Toast.LENGTH_SHORT).show();
                InflateFragment(R.drawable.category,"Category","Search Category...");
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Scan",Toast.LENGTH_SHORT).show();
                InflateFragment(1,"Scan","");
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Search",Toast.LENGTH_SHORT).show();
                InflateFragment(R.drawable.search, "Search","Search Deals...");
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Sort",Toast.LENGTH_SHORT).show();
                InflateFragment(R.drawable.sort, "Sort","Sort by...");
            }
        });
    }

    public void InflateFragment(int id,String name,String hint){
        if(name.equals("Scan"))
        {
            context.startActivity(new Intent(context, ScannerActivity.class));
        }
        else{
            fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment= SearchQueryFragment.newInstance(id, name);
            fragmentTransaction.setCustomAnimations(R.anim.push_down_in,0,0,R.anim.push_up_out);
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
