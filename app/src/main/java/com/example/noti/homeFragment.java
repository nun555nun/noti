package com.example.noti;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class homeFragment extends Fragment {

    DatabaseReference dbRef;
    String binID;

    TextView tvTemp;
    TextView tvHumid;

    TextView tvDateCount;
    TextView tvTime;
    TextView tvStatusAir;
    TextView tvStatusWater;
    TextView tvST;
    TextView tvSD;
    TextView tvLT;
    TextView tvLD;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binID = getArguments().getString("binID");

        final View view = inflater.inflate(R.layout.new_home, container, false);

        tvTemp = view.findViewById(R.id.tv_temp);
        tvHumid = view.findViewById(R.id.tv_humid);
        tvDateCount = view.findViewById(R.id.tv_date_count);
        tvTime = view.findViewById(R.id.tv_time);
        tvStatusAir = view.findViewById(R.id.tv_air_status);
        tvStatusWater = view.findViewById(R.id.tv_water_status);

        tvSD = view.findViewById(R.id.tv_sd);
        tvST = view.findViewById(R.id.tv_st);
        tvLD = view.findViewById(R.id.tv_ld);
        tvLT = view.findViewById(R.id.tv_lt);

        if (isNetworkConnected()) {
            Toast.makeText(getContext(), "เย่", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "เปิดเน็ตซะ", Toast.LENGTH_SHORT).show();
        }
        setData();

        return view;
    }

    private void setData() {
        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map map = (Map) dataSnapshot.getValue();
                String dateCount = String.valueOf(map.get("dateCount"));
                String temp = String.valueOf(map.get("tempIn"));

                String humid = String.valueOf(map.get("humidIn"));

                String time = String.valueOf(map.get("time"));
                String statusAir = String.valueOf(map.get("statusAir"));
                String statusWater = String.valueOf(map.get("statusWater"));

                String st = String.valueOf(map.get("time"));
                String sd = String.valueOf(map.get("time"));
                String lt = String.valueOf(map.get("time"));
                String ld = String.valueOf(map.get("time"));

                tvTemp.setText(temp);

                tvDateCount.setText(dateCount);
                tvHumid.setText(humid);
                tvTime.setText(time);
                tvStatusAir.setText(statusAir);
                tvStatusWater.setText(statusWater);

                tvST.setText(st);
                tvSD.setText(sd);
                tvLT.setText(lt);
                tvLD.setText(ld);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
