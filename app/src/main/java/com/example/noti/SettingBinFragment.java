package com.example.noti;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingBinFragment extends Fragment {

    Switch switchAll;
    Switch switch1;
    Switch switch2;
    Switch switch3;
    Switch switch4;
    ConstraintLayout c;
    public DatabaseReference dbRef;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    AnimationDrawable networkAnimation;

    ConstraintLayout cs;
    ScrollView sv;
    String binID;

    public SettingBinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting_bin, container, false);
        binID = getArguments().getString("binID");
        Log.d("binID", binID);

        cs = view.findViewById(R.id.not_connect_cl);
        sv = view.findViewById(R.id.scrollview_setting);

        switchAll = view.findViewById(R.id.switch_all);
        switch1 = view.findViewById(R.id.switch_1);
        switch2 = view.findViewById(R.id.switch_2);
        switch3 = view.findViewById(R.id.switch_3);
        switch4 = view.findViewById(R.id.switch_4);

        ImageView imageView = view.findViewById(R.id.iv_wifi);
        imageView.setBackgroundResource(R.drawable.animation);
        networkAnimation = (AnimationDrawable) imageView.getBackground();
        networkAnimation.start();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        dbRef = database.getReference();
        setSwitch();

        Button b = view.findViewById(R.id.water_setting);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "แป่ว", Toast.LENGTH_SHORT).show();
            }
        });
        if (isNetworkConnected()) {
            sv.setVisibility(View.VISIBLE);
            cs.setVisibility(View.GONE);
        } else {
            sv.setVisibility(View.GONE);
            cs.setVisibility(View.VISIBLE);
        }
        cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    sv.setVisibility(View.VISIBLE);
                    cs.setVisibility(View.GONE);
                } else {
                    sv.setVisibility(View.GONE);
                    cs.setVisibility(View.VISIBLE);
                }
            }
        });
        c = view.findViewById(R.id.notify_constranlayout);


        switchAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    setStatus("status", "on");
                    c.setVisibility(View.VISIBLE);
                } else {

                    setStatus("status", "off");
                    switch1.setChecked(false);
                    switch2.setChecked(false);
                    switch3.setChecked(false);
                    switch4.setChecked(false);
                    c.setVisibility(View.GONE);
                }
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    setStatus("1", "on");

                } else {

                    setStatus("1", "off");

                }
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    setStatus("2", "on");

                } else {

                    setStatus("2", "off");

                }
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    setStatus("3", "on");

                } else {

                    setStatus("3", "off");

                }
            }
        });

        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    setStatus("4", "on");

                } else {

                    setStatus("4", "off");

                }
            }
        });
        return view;
    }

    private void setSwitch() {
        dbRef = FirebaseDatabase.getInstance().getReference("User/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    String binPart = BinSnapshot.getKey();
                    Map map = (Map) BinSnapshot.getValue();
                    Log.d("notistaz", binPart);
                    final String bin = String.valueOf(map.get("binid"));
                    if (binID.equals(bin)) {
                        Log.d("notistaz", binID);
                        String notifyStatusAll = String.valueOf(BinSnapshot.child("notificationStatus").child("status").getValue());
                        String notifyStatus1 = String.valueOf(BinSnapshot.child("notificationStatus").child("1").getValue());
                        String notifyStatus2 = String.valueOf(BinSnapshot.child("notificationStatus").child("2").getValue());
                        String notifyStatus3 = String.valueOf(BinSnapshot.child("notificationStatus").child("3").getValue());
                        String notifyStatus4 = String.valueOf(BinSnapshot.child("notificationStatus").child("4").getValue());


                        if (notifyStatusAll.equals("off") || (notifyStatus1.equals("off") && notifyStatus2.equals("off") && notifyStatus3.equals("off") && notifyStatus4.equals("off"))) {
                            Log.d("notista", notifyStatusAll);
                            dbRef.child(binPart).child("notificationStatus").child("status").setValue("off");
                            switchAll.setChecked(false);

                            for (int i = 1; i <= 4; i++) {
                                Log.d("notistaz", binPart);
                                dbRef.child(binPart).child("notificationStatus").child(String.valueOf(i)).setValue("off");
                            }
                            c.setVisibility(View.GONE);
                        } else {
                            switchAll.setChecked(true);

                            if (notifyStatus1.equals("on")) {
                                switch1.setChecked(true);
                            } else {
                                switch1.setChecked(false);
                            }

                            if (notifyStatus2.equals("on")) {
                                switch2.setChecked(true);
                            } else {
                                switch2.setChecked(false);
                            }

                            if (notifyStatus3.equals("on")) {
                                switch3.setChecked(true);
                            } else {
                                switch3.setChecked(false);
                            }

                            if (notifyStatus4.equals("on")) {
                                switch4.setChecked(true);
                            } else {
                                switch4.setChecked(false);
                            }
                        }
                        break;
                    }
                }
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setStatus(final String typeNotify, final String status) {
        dbRef = FirebaseDatabase.getInstance().getReference("User/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    Map map = (Map) BinSnapshot.getValue();
                    final String binid = String.valueOf(map.get("binid"));
                    String binPart = BinSnapshot.getKey();
                    if (binid.equals(binID)) {

                        dbRef.child(binPart).child("notificationStatus").child(typeNotify).setValue(status);

                        if (typeNotify.equals("status") && status.equals("off")) {
                            for (int i = 1; i <= 4; i++) {
                                dbRef.child(binPart).child("notificationStatus").child(String.valueOf(i)).setValue("off");
                            }
                        }
                        break;
                    }
                }
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
