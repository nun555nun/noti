package com.example.noti;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Map;

import es.dmoral.toasty.Toasty;

public class Account extends AppCompatActivity {
    public FirebaseAuth auth;
    EditText passwordEditText;
    EditText usernameEditText;
    TextView tv_pass;
    EditText emailEditText;
    private FirebaseDatabase database;
    public DatabaseReference dbRef;
    public static final String NODE_fcm = "fcm-token";
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountt);
        progressDialog = new ProgressDialog(Account.this);
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("User").child(auth.getUid());

        tv_pass = findViewById(R.id.visible_pass);
        tv_pass.setVisibility(View.GONE);

        passwordEditText = findViewById(R.id.password_edit_text);
        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        hideKeybord();
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordEditText.getText().length() > 0) {
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
                if (tv_pass.getText().equals("1")) {
                    tv_pass.setText("0");
                    passwordEditText.setTransformationMethod(null);
                    tv_pass.setBackgroundResource(R.drawable.ic_visibility_black_24dp);
                } else {
                    tv_pass.setText("1");
                    passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                    tv_pass.setBackgroundResource(R.drawable.ic_visibility_off_black_24dp);
                }
            }
        });


        Button editPassword = findViewById(R.id.edit_pass_button);
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordEditText.length() >= 6) {

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(Account.this);
                    LayoutInflater inflater = getLayoutInflater();

                    View view = inflater.inflate(R.layout.activity_custom_dialog_login, null);
                    builder.setView(view);

                    final EditText username = (EditText) view.findViewById(R.id.username);
                    final EditText password = (EditText) view.findViewById(R.id.password);

                    final TextView tv_visible = view.findViewById(R.id.pass_visible);
                    password.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (password.getText().length() > 0) {
                                tv_visible.setVisibility(View.VISIBLE);
                            } else {
                                tv_visible.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    tv_visible.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tv_visible.getText().equals("1")) {
                                tv_visible.setText("0");
                                password.setTransformationMethod(null);
                                tv_visible.setBackgroundResource(R.drawable.ic_visibility_black_24dp);
                            } else {
                                tv_visible.setText("1");
                                password.setTransformationMethod(new PasswordTransformationMethod());
                                tv_visible.setBackgroundResource(R.drawable.ic_visibility_off_black_24dp);
                            }
                        }
                    });

                    builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Check username password
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(username.getText().toString(), password.getText().toString());
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("test", "User re-authenticated.");
                                            progressDialog.show();
                                            if (task.isSuccessful()) {
                                                changePassword();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(Account.this, "อีเมล์หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            passwordEditText.setText("");
                        }
                    });

                    builder.show();

                } else {
                    Toasty.warning(Account.this, "กรุณากรอกรหัสผ่าน 6 ตัวอักษรขึ้นไป", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button editUsername = findViewById(R.id.edit_username_button);
        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEditText.length() > 0) {
                    changeUsername();
                } else {
                    Toasty.error(Account.this, "กรุณากรอก Username", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button editEmail = findViewById(R.id.edit_email_button);
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches() || emailEditText.getText().toString().length() == 0) {
                    emailEditText.setError("โปรดระบุ email ให้ถูกต้อง");
                    emailEditText.requestFocus();
                } else {
                    new AlertDialog.Builder(Account.this)
                            .setTitle("แก้ไข email")
                            .setMessage("ต้องการเปลี่ยน email เป็น " + emailEditText.getText().toString() + " ใช่หรือไม่")
                            .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    changeEmail();
                                }
                            })
                            .setNegativeButton("ไม่", null)
                            .show();
                }


            }
        });

        TextView delete = findViewById(R.id.delete_text_view);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Account.this)
                        .setTitle("ต้องการลบบัญชีนี้ออกจากระบบใช่หรือไม่")
                        .setMessage("เมื่อกดปุ่ม ใช่ แล้วจะไม่สามารถกู้คืนบัญชีของท่านได้")
                        .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteAccount();
                            }
                        })
                        .setNegativeButton("ไม่", null)
                        .show();
            }
        });

    }

    private void changeEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {

            user.updateEmail(emailEditText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Account.this, "แก้ไขที่อยู่ email เรียบร้อย", Toast.LENGTH_SHORT).show();
                                emailEditText.setHint(emailEditText.getText().toString());
                                emailEditText.setText("");

                            } else {
                                Toast.makeText(Account.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void deleteAccount() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {

            String token = FirebaseInstanceId.getInstance().getToken();
            dbRef = database.getReference(NODE_fcm + "/bin1").child(token);
            dbRef.removeValue();

            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Account.this, "ลบบัญชีเรียบร้อย", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(Account.this, login.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(Account.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    private void changeUsername() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            dbRef.child("name").setValue(usernameEditText.getText().toString());
            Toast.makeText(Account.this, "แก้ไข username เป็น " + usernameEditText.getText().toString() + " เรียบร้อยแล้ว", Toast.LENGTH_LONG).show();
            usernameEditText.setText("");
        }
    }

    private void hideKeybord() {
        usernameEditText.setFocusable(false);
        usernameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                usernameEditText.setFocusableInTouchMode(true);

                return false;
            }
        });

        emailEditText.setFocusable(false);
        emailEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                emailEditText.setFocusableInTouchMode(true);

                return false;
            }
        });

        passwordEditText.setFocusable(false);
        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                passwordEditText.setFocusableInTouchMode(true);

                return false;
            }
        });

    }


    public void changePassword() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updatePassword(passwordEditText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                passwordEditText.setText("");
                                Toast.makeText(Account.this, "เปลี่ยนรหัสเรียบร้อย", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Account.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
