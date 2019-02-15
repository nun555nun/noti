package com.example.noti;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingBinFragment extends Fragment {

    Switch switchAll;
    ConstraintLayout c;

    public SettingBinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String binID = getArguments().getString("binID");
        final View view = inflater.inflate(R.layout.fragment_setting_bin, container, false);

        c = view.findViewById(R.id.notify_constranlayout);

        switchAll = view.findViewById(R.id.switch_all);
        switchAll.setChecked(true);
        switchAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), "on", Toast.LENGTH_SHORT).show();
                    c.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "off", Toast.LENGTH_SHORT).show();
                    c.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }

}
