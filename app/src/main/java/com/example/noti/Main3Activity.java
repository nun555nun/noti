package com.example.noti;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Main3Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public DatabaseReference dbRef;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    AlertDialog dialog;
    EditText etID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        dbRef = database.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Main3Activity.this);
                View mview = getLayoutInflater().inflate(R.layout.dialog_add,null);

                EditText etName = mview.findViewById(R.id.et_bin_name);
                etID = mview.findViewById(R.id.et_bin_id);
                TextView tvStartDate = mview.findViewById(R.id.tv_start_date);

                Button addButton = mview.findViewById(R.id.add_button);

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkData();

                    }
                });
                mBuilder.setView(mview);
                dialog = mBuilder.create();
                dialog.show();

               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        final TextView Emailtextview = navHeaderView.findViewById(R.id.textEmail);
        final TextView Nametextview = navHeaderView.findViewById(R.id.textName);
        dbRef = database.getReference("User").child(auth.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map map = (Map) dataSnapshot.getValue();
                String email = String.valueOf(map.get("email"));
                String name = String.valueOf(map.get("name"));

                Emailtextview.setText(email);
                Nametextview.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        setTitle("หน้าหลัก");
        BinFragment fragment = new BinFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fram, fragment);
        fragmentTransaction.commit();

    }

    private void checkData() {

        dbRef = FirebaseDatabase.getInstance().getReference("bin/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean check= false;
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    if(ds.getKey().equals(etID.getText().toString())){
                       check =true;
                       break;
                    }
                }
                if(check){
                    setBinId();
                }
                else{
                    etID.setText("");
                    Toast.makeText(Main3Activity.this,"ไม่มี รหัสถังนี้ในระบบ หรือ ถังยังไม่ได้เปิดใช้",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setBinId() {
        dbRef = FirebaseDatabase.getInstance().getReference("User/" + auth.getCurrentUser().getUid() + "/bin");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean check = true;
                int count = (int) dataSnapshot.getChildrenCount();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Map map = (Map) ds.getValue();
                    String binID = String.valueOf(map.get("binid"));
                    if(binID.equals(etID.getText().toString())){
                        check = false;
                        break;
                    }
                }
                if(check){
                    Map binId = new HashMap();
                    binId.put("binid",etID.getText().toString());
                    dbRef.push().setValue(binId);
                    Toast.makeText(Main3Activity.this,"เพิ่มถังเรียบร้อย",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    dbRef.removeEventListener(this);
                }
                else {
                    dbRef.removeEventListener(this);
                    etID.setText("");
                    Toast.makeText(Main3Activity.this,"คุณเคยเพิ่มถังนี้ไปแล้ว",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            setTitle("หน้าหลัก");
            BinFragment fragment = new BinFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fram, fragment);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_gallery) {
            setTitle("เทส");
            Fragment1 fragment = new Fragment1();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fram, fragment);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_slideshow) {
            Intent i = new Intent(Main3Activity.this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_share) {
            Intent i = new Intent(Main3Activity.this, Account.class);
            startActivity(i);
        } else if (id == R.id.nav_send) {

            new AlertDialog.Builder(Main3Activity.this)
                    .setTitle("ต้องการออกจากระบบใช่หรือไม่")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbRef = database.getReference("User").child(auth.getUid());
                            dbRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Map map = (Map) dataSnapshot.getValue();
                                    String x = String.valueOf(map.get("email"));
                                    String y = String.valueOf(map.get("pass"));
                                    Toast.makeText(Main3Activity.this, x + " " + y, Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            String s = auth.getUid();
                            //Toast.makeText(MainActivity.this,s+ " Logout",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Main3Activity.this, login.class);
                            auth.signOut();
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("no", null)
                    .show();


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
