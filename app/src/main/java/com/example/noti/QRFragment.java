package com.example.noti;


import android.app.DatePickerDialog;
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
import com.google.zxing.Result;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.Activity.RESULT_OK;
import static android.graphics.Color.BLACK;


/**
 * A simple {@link Fragment} subclass.
 */
public class QRFragment extends Fragment {

    private ZXingScannerView zXingScannerView;
    public DatabaseReference dbRef;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    AlertDialog dialog;
    EditText etID;
    EditText etName;
    TextView tvStartDate;
    private int day, month, year;
    private Calendar mDate;
    String resultString;
    public static final int REQUEST_QR_SCAN = 4;
    public QRFragment() {
        // Required empty public constructor
    }
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDate = Calendar.getInstance();

        day = mDate.get(Calendar.DAY_OF_MONTH);
        month = mDate.get(Calendar.MONTH);
        year = mDate.get(Calendar.YEAR);

        month += 1;

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        dbRef = database.getReference();
//        QR scan Controller
        Button button = getView().findViewById(R.id.btnQRscan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*Intent intent =
                        new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(Intent.createChooser(intent
                        , "Scan with"), REQUEST_QR_SCAN);*/
               /* zXingScannerView = new ZXingScannerView(getActivity());
                getActivity().setContentView(zXingScannerView);
                zXingScannerView.startCamera();

                zXingScannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
                    @Override
                    public void handleResult(Result result) {

                        zXingScannerView.stopCamera();
                        getActivity().setContentView(R.layout.activity_qrmain);
                        resultString = result.getText().toString();
                        Toast.makeText(getActivity(), "QR code = " + resultString,
                                Toast.LENGTH_LONG).show();
                        Log.d("12MarchV1", "QR code ==> " + resultString);
                    }
                });*/

            }
        });



    }   // Main Method

    public void onActivityResult(int requestCode, int resultCode
            , Intent intent) {
        if (requestCode == REQUEST_QR_SCAN && resultCode == RESULT_OK) {
            resultString = intent.getStringExtra("SCAN_RESULT");
            addBin();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qr, container, false);
        return view;
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

        etID.setFocusable(false);
        etID.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                etID.setFocusableInTouchMode(true);

                return false;
            }
        });

    }

    private void addBin() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mview = getLayoutInflater().inflate(R.layout.dialog_add, null);

        etName = mview.findViewById(R.id.et_bin_name);
        etID = mview.findViewById(R.id.et_bin_id);
        tvStartDate = mview.findViewById(R.id.tv_start_date);
etID.setText(resultString);
        tvStartDate.setText(day + "/" + month + "/" + year);
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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
                if (etName.getText().toString().length() > 0 && resultString.trim().length() > 0) {
                    checkData();
                } else {
                    Toast.makeText(getContext(), "โปรดใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBuilder.setView(mview);
        dialog = mBuilder.create();
        dialog.show();
    }

    private void checkData() {

        dbRef = FirebaseDatabase.getInstance().getReference("bin/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean check = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(resultString.trim())) {
                        check = true;
                        break;
                    }
                }
                dbRef.removeEventListener(this);
                if (check) {
                    setBinId();
                } else {
                    etID.setText("");
                    Toast.makeText(getContext(), "ไม่มี รหัสถังนี้ในระบบ หรือ ถังยังไม่ได้เปิดใช้", Toast.LENGTH_LONG).show();

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
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map map = (Map) ds.getValue();
                    String binID = String.valueOf(map.get("binid"));
                    if (binID.equals(resultString.trim())) {
                        check = false;
                        break;
                    }
                }
                if (check) {
                    Map binId = new HashMap();
                    binId.put("binid", resultString.trim());
                    dbRef.push().setValue(binId);
                    Toast.makeText(getContext(), "เพิ่มถังเรียบร้อย", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    dbRef.removeEventListener(this);
                    setBinData();

                } else {
                    dbRef.removeEventListener(this);
                    etID.setText("");
                    Toast.makeText(getContext(), "คุณเคยเพิ่มถังนี้ไปแล้ว", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setBinData() {
        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + resultString.trim());
        dbRef.child("binName").setValue(etName.getText().toString());
        dbRef.child("date").setValue(tvStartDate.getText().toString());
    }



}
