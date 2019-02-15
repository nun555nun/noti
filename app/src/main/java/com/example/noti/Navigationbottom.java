package com.example.noti;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class Navigationbottom extends AppCompatActivity {


    Bundle bundle;
    String binID;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Intent intent = getIntent();
            binID = intent.getStringExtra("binID");
            bundle = new Bundle();
            bundle.putString("binID", binID);
            String binName = intent.getStringExtra("binName");
            setTitle(binName);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


            //setname toolbar
            Bundle b = new Bundle();
            Fragment selectedFragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:


                    selectedFragment = new homeFragment();
                    selectedFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.framz, selectedFragment).commit();

                    return true;
                case R.id.navigation_history:

                    selectedFragment = new TapHistory();

                    b.putString("binID",binID);
                    selectedFragment.setArguments(b);

                    fragmentTransaction.replace(R.id.framz, selectedFragment).commit();

                    return true;
                case R.id.navigation_notifications:

                    selectedFragment = new SettingBinFragment();

                    b.putString("binID",binID);
                    selectedFragment.setArguments(b);

                    fragmentTransaction.replace(R.id.framz, selectedFragment).commit();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigationbottom);


        Intent intent = getIntent();
        binID = intent.getStringExtra("binID");
        bundle = new Bundle();
        bundle.putString("binID", binID);
        String binName = intent.getStringExtra("binName");
        setTitle(binName);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment selectedFragment = new homeFragment();
        selectedFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.framz, selectedFragment).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
