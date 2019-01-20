package com.example.noti;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class BinFragment extends Fragment {
    ListView listViewBin;
    DatabaseReference dbRef;
    public ProgressDialog progressDialog;
    List<UserBin> userBinList;
    FirebaseAuth auth;
    ArrayList<String> binArrylist;
    LayoutAnimationController controller;
    ConstraintLayout cl;
    Intent intent;
    public BinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bin, container, false);
        auth = FirebaseAuth.getInstance();

        binArrylist = new ArrayList<>();
        cl = view.findViewById(R.id.constraint_laout);
        listViewBin = view.findViewById(R.id.bin_list_view);
        userBinList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();

        return view;
    }

    private void setAdaptor() {


        dbRef = FirebaseDatabase.getInstance().getReference("bin");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userBinList.clear();
                progressDialog.cancel();
                for (String bin : binArrylist) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.getKey().equals(bin)) {
                            Map map = (Map) data.getValue();
                            String binName = String.valueOf(map.get("binName"));
                            String startDate = String.valueOf(map.get("date"));
                            String temperature = String.valueOf(map.get("temperature"));
                            String humidity = String.valueOf(map.get("humidity"));

                            UserBin ub = new UserBin(binName, bin, temperature, humidity, startDate);
                            //Toast.makeText(getContext(), bin + " add", Toast.LENGTH_SHORT).show();
                            userBinList.add(ub);
                        }
                    }
                }
                if (userBinList.size() > 0 && getContext() != null) {

                    UserBinList adapter = new UserBinList(getContext(), userBinList);

                    listViewBin.setAdapter(adapter);
                    listViewBin.setLayoutAnimation(controller);
                    listViewBin.scheduleLayoutAnimation();
                    controller = null;
                    for (int i = 0; i < userBinList.size(); i++) {
                        Log.v("test", userBinList.get(i).getBinID());
                    }
                    Log.v("test2", "--------------------------");
                    listViewBin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            intent = new Intent(getContext(), Navigationbottom.class);
                            Log.v("binName", userBinList.get(position).getBinID());
                            intent.putExtra("binID", userBinList.get(position).getBinID());
                            intent.putExtra("binName", userBinList.get(position).getBinName());
                            startActivity(intent);
                        }
                    });

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getbin() {

        dbRef = FirebaseDatabase.getInstance().getReference("User/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binArrylist.clear();

                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    Map map = (Map) BinSnapshot.getValue();
                    final String bin = String.valueOf(map.get("binid"));
                    binArrylist.add(bin);
                }
                if (binArrylist.size() > 0) {
                    if (getContext() != null) {
                        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_slide_from_left);
                        cl.setBackgroundResource(R.drawable.bg);
                        setAdaptor();
                    }


                } else {
                    cl.setBackgroundResource(R.drawable.bg_block);
                    progressDialog.cancel();
                    listViewBin.setAdapter(null);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_slide_from_left);
        getbin();
    }

}
