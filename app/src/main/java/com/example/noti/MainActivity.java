package com.example.noti;

import android.app.Notification;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;
import static com.example.noti.App.CHANNEL_1_ID;
import static com.example.noti.App.CHANNEL_2_ID;

public class MainActivity extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;
    public DatabaseReference testapp;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        notificationManager = NotificationManagerCompat.from(this);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextMessage = findViewById(R.id.edit_text_message);
         database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        testapp = database.getReference();

        testapp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map map = (Map)dataSnapshot.getValue();
                String x = String.valueOf(map.get("x"));
                int xz = Integer.parseInt(x);
                String title = "อุณหภูมิในถังไม่ได้อยู่ในช่วงที่กำหนด";
                String message = "ทำการเติมอากาศ";
                if(xz>60){
                    Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.thermometer)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setVisibility(VISIBILITY_PUBLIC)
                            .build();

                    notificationManager.notify(1, notification);
                    String title2 = "ความชื้นในถังน้อยเกินไป";
                    String message2 = "ทำการเติมน้ำ";

                    Notification notification2 = new NotificationCompat.Builder(MainActivity.this, CHANNEL_2_ID)
                            .setSmallIcon(R.drawable.humidity)
                            .setContentTitle(title2)
                            .setContentText(message2)
                            .setPriority(NotificationCompat.PRIORITY_LOW)
                            .build();

                    notificationManager.notify(2, notification2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Button b=findViewById(R.id.button3);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,FCM.class);
                startActivity(i);
            }
        });
        Button b2=findViewById(R.id.button4);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testapp = database.getReference("User").child(auth.getUid());
                testapp.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map map = (Map)dataSnapshot.getValue();
                        String x = String.valueOf(map.get("email"));
                        String y = String.valueOf(map.get("pass"));
                        Toast.makeText(MainActivity.this,x+" "+y,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                String s =auth.getUid();
                //Toast.makeText(MainActivity.this,s+ " Logout",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, login.class);
                auth.signOut();
                startActivity(intent);
                finish();
            }
        });

        String[] clubList = {"Arsenal", "Chelsea", "Everton",
                "Liverpool", "Man City", "Man Utd", "Spurs" };
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,clubList);


        
    }

    public void sendOnChannel1(View v) {
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();
        int x = Integer.parseInt(title);
        if(x>60){
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_one)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();

            notificationManager.notify(1, notification);
        }

    }

    public void sendOnChannel2(View v) {
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.humidity)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(2, notification);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
