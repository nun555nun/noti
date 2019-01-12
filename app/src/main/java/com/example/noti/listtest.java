package com.example.noti;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listtest extends AppCompatActivity {
    ListView listViewLogDHT;
    DatabaseReference dbRef;

    List<LogDHT> logDHTList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listtest);

        dbRef = FirebaseDatabase.getInstance().getReference("bin/bin1/logDHT");

        listViewLogDHT =findViewById(R.id.list_view_logDHT2);
        logDHTList = new ArrayList<>();
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
    public void setAdaptor(){
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                logDHTList.clear();
                for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                    LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);

                    logDHTList.add(logDHT);

                }
                LogDHTList adapter = new LogDHTList(listtest.this,logDHTList);
                listViewLogDHT.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
