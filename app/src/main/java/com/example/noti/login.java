package com.example.noti;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class login extends AppCompatActivity {
    private EditText editTextemail;
    private EditText editTextpass;
    private Button buttonlogin;
    public DatabaseReference testapp;
    public ProgressBar progressBar;
    public FirebaseAuth auth;
    public ProgressDialog progressDialog;
    Animation fromBottom;
    public TextView forgetText;
    TextView regiter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fromBottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom);

        editTextemail = (EditText) findViewById(R.id.eemail);
        editTextpass = (EditText) findViewById(R.id.epass);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        buttonlogin = (Button) findViewById(R.id.buttonlogin);
        forgetText = findViewById(R.id.forget_text_view);
        regiter = findViewById(R.id.textView8);

        editTextemail.setAnimation(fromBottom);
        editTextpass.setAnimation(fromBottom);
        buttonlogin.setAnimation(fromBottom);
        forgetText.setAnimation(fromBottom);
        regiter.setAnimation(fromBottom);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading.....");


        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            Intent intent = new Intent(login.this, Main3Activity.class);
            startActivity(intent);
            finish();
        }
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editTextemail.getText().toString();
                final String password = editTextpass.getText().toString();


                if (email.isEmpty()) {
                    editTextemail.setError("โปรดกรอก email");
                    editTextemail.requestFocus();
                }
                if (password.isEmpty()) {
                    editTextpass.setError("โปรดกรอก password");
                    editTextpass.requestFocus();
                }
                if (password.length() < 6) {
                    editTextpass.setError("ตัวอักษรต้องอย่างน้อย6ตัว");
                    editTextpass.requestFocus();
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextemail.setError("โปรดระบุ email ให้ถูกต้อง");
                    editTextemail.requestFocus();
                }
                if (!email.isEmpty() && password.length()>=6) {
                    progressDialog.show();
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    progressDialog.cancel();
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(login.this, "รหัสผ่าน หรือ  อีเมล์ ไม่ถูกต้อง", Toast.LENGTH_LONG).show();
                                    } else {
                                        Intent intent = new Intent(login.this, Main3Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

       regiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, loginfirebase.class);
                startActivity(intent);

            }
        });

        forgetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(login.this,ResetPassword.class);
                startActivity(i);

            }
        });

    }
}
