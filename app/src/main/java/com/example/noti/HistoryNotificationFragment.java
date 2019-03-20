package com.example.noti;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryNotificationFragment extends Fragment {
    ListView listViewLogDHT;
    DatabaseReference dbRef;
    public ProgressDialog progressDialog;
    List<LogNotification> logNotiList;


    String startDate;
    String binID;

    private Spinner dateSpinner;
    private Spinner typeSpinner;

    ArrayList<String> type;
    ArrayList<String> date;

    public HistoryNotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binID = getArguments().getString("binID");
        startDate = getArguments().getString("startDate");
        View view = inflater.inflate(R.layout.fragment_history_notification, container, false);

        TextView tv = view.findViewById(R.id.bin);
        tv.setText(binID + "     " + startDate);

        dateSpinner = view.findViewById(R.id.dateN_spinner);
        typeSpinner = view.findViewById(R.id.type_spinner);

        String[] type = getResources().getStringArray(R.array.notitype);
        ArrayAdapter<String> adapterSort = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, type);
        typeSpinner.setAdapter(adapterSort);

        getDate();
        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, date);
        dateSpinner.setAdapter(adapterDate);

        Button searchButton = view.findViewById(R.id.search_log_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setAdaptor();
                Toast.makeText(getContext(), "ค้นหา การแจ้งเตือน '" + typeSpinner.getSelectedItem().toString() + "' วันที่ " + dateSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID );

        listViewLogDHT = view.findViewById(R.id.list_view_logDHT);
        return view;
    }


    private void getDate() {
        date = new ArrayList<>();
        date.add("-");

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/date_time");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    String binPart = BinSnapshot.getKey();
                    binPart = binPart.replace("_", "/");
                    date.add(binPart);

                    sortDate();
                }
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sortDate() {

        Collections.sort(date, new Comparator<String>() {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(String o1, String o2) {
                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public void setAdaptor() {
        logNotiList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();
        final DatabaseReference dbRef;
        String datePart = dateSpinner.getSelectedItem().toString();
        datePart = datePart.replace("/", "_");

        String timePart = String.valueOf(typeSpinner.getSelectedItemPosition());
        if (timePart.equals("1")) {
            dbRef = FirebaseDatabase.getInstance().getReference("notification/" + binID +"/"+timePart);
        } else {
            dbRef = FirebaseDatabase.getInstance().getReference("notification/" + binID +"/"+timePart);
        }

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    dbRef.removeEventListener(this);
                } else {
                    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);
                    logNotiList.clear();
                    for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                        LogNotification logNotification = logDHTSnapshot.getValue(LogNotification.class);
                        logNotification.setType(typeSpinner.getSelectedItem().toString());
                        logNotiList.add(logNotification);
                    }
                    if (logNotiList.size() > 0 && getContext() != null) {
                        progressDialog.dismiss();
                        LogNotificationList adapter = new LogNotificationList(getContext(), logNotiList);

                        listViewLogDHT.setAdapter(adapter);
                        listViewLogDHT.setLayoutAnimation(controller);
                        listViewLogDHT.scheduleLayoutAnimation();
                    }
                    progressDialog.dismiss();
                }

                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

