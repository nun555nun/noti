package com.example.noti;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class TapHistory extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public TapHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String binID = getArguments().getString("binID");
        Log.v("zxc", "Tab    "+binID);
        View view = inflater.inflate(R.layout.fragment_tap_history, container, false);
        tabLayout = view.findViewById(R.id.tablaout_history);
        viewPager = view.findViewById(R.id.viewpager_history);

        ViewpagerAdapter adapter = new ViewpagerAdapter(getChildFragmentManager());

        adapter.AddBinId(binID);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
