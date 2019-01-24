package com.example.noti;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.Map;

public class homeFragment extends Fragment {

    DatabaseReference dbRef;
    String binID;
    TextView tvTempIn;
    TextView tvTempOut;
    TextView tvHumidIn;
    TextView tvHumidOut;
    TextView tvDateCount;
    EditText editWater;
    EditText editAir;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binID = getArguments().getString("binID");

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvTempIn = view.findViewById(R.id.tv_temp_in);
        tvTempOut = view.findViewById(R.id.tv_temp_out);
        tvHumidIn = view.findViewById(R.id.tv_humid_in);
        tvHumidOut = view.findViewById(R.id.tv_humid_out);
        tvDateCount = view.findViewById(R.id.tv_date_count);

        setData();

        //hide keybord
        editWater = view.findViewById(R.id.editWater);
        editWater.setFocusable(false);

        editWater.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editWater.setFocusableInTouchMode(true);

                return false;
            }
        });
        //hide keybord
        editAir = view.findViewById(R.id.editAir);
        editAir.setFocusable(false);
        editAir.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editAir.setFocusableInTouchMode(true);

                return false;
            }
        });

        Button fillWater = view.findViewById(R.id.button_water);
        fillWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editWater.onEditorAction(EditorInfo.IME_ACTION_DONE);
                String time = editWater.getText().toString();
                if (time.length() == 0 || time.startsWith("0")) {
                    Toast.makeText(getContext(), "กรุณากรอกตัวเลขตั้งแต่1ขึ้นไป", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("ต้องการตั้งเวลาเติมน้ำเป็นเวลา " + time + " นาที ใช่หรือไม่?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("no", null)
                            .show();
                }
            }
        });


        Button fillAir = view.findViewById(R.id.button_air);

        fillAir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = editAir.getText().toString();
                if (time.length() == 0 || time.startsWith("0")) {
                    Toast.makeText(getContext(), "กรุณากรอกตัวเลขตั้งแต่1ขึ้นไป", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("ต้องการตั้งเวลาเติมอากาศเป็นเวลา " + time + " นาที ใช่หรือไม่?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("no", null)
                            .show();
                }
            }
        });

        return view;
    }

    private void setData() {
        dbRef = FirebaseDatabase.getInstance().getReference("bin/"+binID);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Map map = (Map) dataSnapshot.getValue();

                    String tempIn = String.valueOf(map.get("tempIn"));
                    String tempOut = String.valueOf(map.get("tempOut"));
                    String humidIn = String.valueOf(map.get("humidIn"));
                    String humidOut = String.valueOf(map.get("humidOut"));
                    String dateCount = String.valueOf(map.get("dateCount"));
                    String fillAir = String.valueOf(map.get("fillAir"));
                    String fillWater = String.valueOf(map.get("fillWater"));

                    tvTempIn.setText(tempIn);
                    tvTempOut.setText(tempOut);
                    tvDateCount.setText(dateCount);
                    tvHumidIn.setText(humidIn);
                    tvHumidOut.setText(humidOut);
                    editAir.setHint(fillAir);
                    editWater.setHint(fillWater);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
