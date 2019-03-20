package com.example.noti;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    ListView listViewLogDHT;
    DatabaseReference dbRef;
    public ProgressDialog progressDialog;
    List<LogDHT> logDHTList;
    private Spinner dateSpinner;
    private Spinner timeSpinner;
    String binID;
    String[] date;
    String[] time;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        dateSpinner = view.findViewById(R.id.date_spinner);
        timeSpinner = view.findViewById(R.id.time_spinner);

        binID = getArguments().getString("binID");
        getDate();
        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, date);

        dateSpinner.setAdapter(adapterDate);
        getTime();
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getTime();
                time[0] = String.valueOf(position);
                ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, time);
                timeSpinner.setAdapter(adapterTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button searchButton = view.findViewById(R.id.search_log_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "ค้นหาวันที่" + dateSpinner.getSelectedItem().toString() + "เวลา" + timeSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        Log.v("zxc", "--" + binID + "--");
        //binID = "bin1";
        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/logDHT");

        listViewLogDHT = view.findViewById(R.id.list_view_logDHT);
        logDHTList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        setAdaptor();

        return view;
    }

    private void getTime() {
        if(dateSpinner.getSelectedItem().toString().equals("00/00/0000")){
            time = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"
                    , "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        }
        else {
            time = new String[]{"14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        }

    }

    private void getDate() {
        date = new String[]{"00/00/0000", "11/11/1111", "33/33/3333", "44/44/4444", "55/55/5555",
                "00/00/0000", "11/11/1111", "33/33/3333", "44/44/4444", "55/55/5555",
                "00/00/0000", "11/11/1111", "33/33/3333", "44/44/4444", "55/55/5555"
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        setAdaptor();
    }

    @Override
    public void onResume() {
        super.onResume();

        setAdaptor();
    }

    public void setAdaptor() {

        progressDialog.show();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    dbRef.removeEventListener(this);
                } else {
                    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);
                    logDHTList.clear();
                    for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                        LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);
                        logDHTList.add(logDHT);
                    }
                    if (logDHTList.size() > 0 && getContext() != null) {
                        progressDialog.cancel();
                        LogDHTList adapter = new LogDHTList(getContext(), logDHTList);

                        listViewLogDHT.setAdapter(adapter);
                        listViewLogDHT.setLayoutAnimation(controller);
                        listViewLogDHT.scheduleLayoutAnimation();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
