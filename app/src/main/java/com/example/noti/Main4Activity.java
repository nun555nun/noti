package com.example.noti;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
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

public class Main4Activity extends AppCompatActivity {
    ListView listViewLogDHT;
    DatabaseReference dbRef;
    public ProgressDialog progressDialog;
    List<LogAllbinNotification> logDHTList;
    public FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(R.string.notification);
        dbRef = FirebaseDatabase.getInstance().getReference("User/" + "Hw32WJYUqdNa7iFF853HsUVNSwi2" + "/logNotification");

        listViewLogDHT = findViewById(R.id.list_all_noti);
        logDHTList = new ArrayList<>();
        progressDialog = new ProgressDialog(Main4Activity.this);
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        setAdaptor();
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
        // dbRef.orderByChild("time").limitToLast(10).addValueEventListener(new ValueEventListener() {
        dbRef.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(Main4Activity.this, R.anim.layout_fall_down);
                logDHTList.clear();
                for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                    LogAllbinNotification logDHT = logDHTSnapshot.getValue(LogAllbinNotification.class);
                    logDHTList.add(logDHT);

                }
                if (logDHTList.size() > 0) {

                    sortDate();
                    Collections.reverse(logDHTList);
                    progressDialog.cancel();
                    LogAllbinNotificationList adapter = new LogAllbinNotificationList(Main4Activity.this, logDHTList);
                    listViewLogDHT.setAdapter(adapter);

                    listViewLogDHT.setLayoutAnimation(controller);
                    listViewLogDHT.scheduleLayoutAnimation();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sortDate() {

        Collections.sort(logDHTList, new Comparator<LogAllbinNotification>() {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(LogAllbinNotification o1, LogAllbinNotification o2) {
                try {
                    return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}