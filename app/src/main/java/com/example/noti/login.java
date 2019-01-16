package com.example.noti;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

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
    TextView tv_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);

        editTextemail = findViewById(R.id.eemail);
        editTextpass = findViewById(R.id.epass);
        progressBar = findViewById(R.id.progressBar);
        buttonlogin = findViewById(R.id.buttonlogin);
        forgetText = findViewById(R.id.forget_text_view);
        regiter = findViewById(R.id.register_text_view);

        tv_pass = findViewById(R.id.pass_show);
        tv_pass.setVisibility(View.GONE);

        editTextemail.setAnimation(fromBottom);
        editTextpass.setAnimation(fromBottom);
        buttonlogin.setAnimation(fromBottom);
        forgetText.setAnimation(fromBottom);
        regiter.setAnimation(fromBottom);
        tv_pass.setAnimation(fromBottom);

        editTextpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextpass.getText().length() > 0) {
                    tv_pass.setVisibility(View.VISIBLE);
                } else {
                    tv_pass.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tv_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_pass.getText().equals("1")){
                    tv_pass.setText("0");
                    editTextpass.setTransformationMethod(null);
                    tv_pass.setBackgroundResource(R.drawable.ic_visibility_black_24dp);
                }
                else{
                    tv_pass.setText("1");
                    editTextpass.setTransformationMethod(new PasswordTransformationMethod());
                    tv_pass.setBackgroundResource(R.drawable.ic_visibility_off_black_24dp);
                }
            }
        });

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
                    editTextpass.setError("ตัวอักษรอย่างน้อย6ตัวขึ้นไป");
                    editTextpass.requestFocus();
                }
                if (password.length() < 6) {
                    editTextpass.setError("ตัวอักษรอย่างน้อย6ตัวขึ้นไป");
                    editTextpass.requestFocus();
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextemail.setError("โปรดระบุ email ให้ถูกต้อง");
                    editTextemail.requestFocus();
                }
                if (!email.isEmpty() && password.length() >= 6) {
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
                Intent i = new Intent(login.this, ResetPassword.class);
                startActivity(i);

            }
        });

    }
}
