package com.example.noti;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.BLACK;

public class Main3Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public DatabaseReference dbRef;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    AlertDialog dialog;
    TextView etID;
    EditText etName;
    TextView tvStartDate;
    private int day, month, year;
    private Calendar mDate;
    String binIDFromQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent i = getIntent();
        binIDFromQR = i.getStringExtra("binID");


        mDate = Calendar.getInstance();

        day = mDate.get(Calendar.DAY_OF_MONTH);
        month = mDate.get(Calendar.MONTH);
        year = mDate.get(Calendar.YEAR);

        month += 1;

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        dbRef = database.getReference();


        dbRef = FirebaseDatabase.getInstance().getReference("User/" + auth.getCurrentUser().getUid() + "/bin");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.getChildrenCount() == 0) {
                    if (binIDFromQR == null) {
                        new AlertDialog.Builder(Main3Activity.this)
                                .setMessage("ตอนนี้คุณไม่ได้ทำการเชื่อมต่อถัง คุณต้องการเพิ่มถังตอนนี้หรือไม่")
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(Main3Activity.this, QRMainActivity.class);
                                        startActivity(i);
                                        // Toast.makeText(Main3Activity.this, "ได้ทำการเพิ่มถังเรียบร้อย", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(Main3Activity.this, "เมื่อคุณต้องการเพิ่มถังสามารถกดปุ่ม + ด้านขวามือเพื่อทำการเพิ่มถัง", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .show();
                        dbRef.removeEventListener(this);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Main3Activity.this, QRMainActivity.class);
                startActivity(i);


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
        if (binIDFromQR != null) {
            checkData();
        }

        setTitle("หน้าหลัก");
        BinFragment fragment = new BinFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fram, fragment);
        fragmentTransaction.commit();

    }

    private void hideKeybord() {
        etName.setFocusable(false);
        etName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                etName.setFocusableInTouchMode(true);

                return false;
            }
        });


    }

    private void addDataBin() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Main3Activity.this);
        View mview = getLayoutInflater().inflate(R.layout.dialog_add, null);

        etName = mview.findViewById(R.id.et_bin_name);
        etID = mview.findViewById(R.id.et_bin_id);
        tvStartDate = mview.findViewById(R.id.tv_start_date);
        if (binIDFromQR.length()>0){
            etID.setText(binIDFromQR);
        }

        tvStartDate.setText(day + "/" + month + "/" + year);
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Main3Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        tvStartDate.setText(dayOfMonth + "/" + month + "/" + year);
                        tvStartDate.setTextColor(BLACK);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        hideKeybord();

        Button addButton = mview.findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().length() > 0 && binIDFromQR.trim().length() > 0) {
                    dialog.dismiss();
                    setUserBin();


                } else {
                    Toast.makeText(Main3Activity.this, "โปรดใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBuilder.setView(mview);
        dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void setUserBin() {

        dbRef = FirebaseDatabase.getInstance().getReference("User/" + auth.getCurrentUser().getUid() + "/bin");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map binId = new HashMap();
                binId.put("binid", binIDFromQR.trim());
                dbRef.push().setValue(binId);
                dbRef.removeEventListener(this);
                setBinData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void checkData() {

        dbRef = FirebaseDatabase.getInstance().getReference("bin/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean check = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(binIDFromQR.trim())) {
                        check = true;
                        break;
                    }
                }
                dbRef.removeEventListener(this);
                if (check) {
                    checkBinId();
                } else {
                    Toast.makeText(Main3Activity.this, "ไม่มี รหัสถังนี้ในระบบ หรือ ถังยังไม่ได้เปิดใช้", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkBinId() {
        dbRef = FirebaseDatabase.getInstance().getReference("User/" + auth.getCurrentUser().getUid() + "/bin");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean check = true;
                int count = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map map = (Map) ds.getValue();
                    String binID = String.valueOf(map.get("binid"));
                    if (binID.equals(binIDFromQR.trim())) {
                        check = false;
                        break;
                    }
                }
                if (check) {
                    dbRef.removeEventListener(this);
                    addDataBin();

                } else {
                    dbRef.removeEventListener(this);
                    Toast.makeText(Main3Activity.this, "คุณเคยเพิ่มถังนี้ไปแล้ว", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setBinData() {

        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binIDFromQR.trim());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dbRef.child("binName").setValue(etName.getText().toString());
                dbRef.child("date").setValue(tvStartDate.getText().toString());
                Toast.makeText(Main3Activity.this, "เพิ่มถังเรียบร้อย", Toast.LENGTH_SHORT).show();
                dbRef.removeEventListener(this);
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
            setTitle("การแจ้งเตือน");
            Intent i = new Intent(Main3Activity.this, Main4Activity.class);
            startActivity(i);


        } else if (id == R.id.nav_slideshow) {
            Intent i = new Intent(Main3Activity.this, QRMainActivity.class);
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
