package com.example.noti;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
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
        View view = inflater.inflate(R.layout.fragment_tap_history, container, false);
        tabLayout = view.findViewById(R.id.tablaout_history);
        viewPager = view.findViewById(R.id.viewpager_history);

        ViewpagerAdapter adapter = new ViewpagerAdapter(getChildFragmentManager());
        adapter.AddFragment(new HistoryFragment(), "ในถัง");
        adapter.AddFragment(new HistoryFragment(), "นอกถัง");
        adapter.AddFragment(new Fragment1(), "การแจ้งเตือน");




        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
