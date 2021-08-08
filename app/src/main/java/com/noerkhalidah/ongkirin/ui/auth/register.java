package com.noerkhalidah.ongkirin.ui.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.noerkhalidah.ongkirin.R;
import com.noerkhalidah.ongkirin.ui.home.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {
    TextView tvRegister;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    EditText txtUsernameReg,txtPasswordReg,txtConfirmPassword;
    Button btnRegister;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txtUsernameReg = findViewById(R.id.txtUsernameReg);
        txtPasswordReg = findViewById(R.id.txtPasswordReg);
        fAuth = FirebaseAuth.getInstance();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtUsernameReg.getText().toString().trim();
                String password = txtPasswordReg.getText().toString().trim();
                String confpassword = txtConfirmPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    txtUsernameReg.setError("Masukkan email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    txtPasswordReg.setError("password is required");
                    return;
                }
                if (password.length() < 6) {
                    txtPasswordReg.setError("password must be >= 6 character");
                    return;
                }
                if (TextUtils.isEmpty(confpassword)) {
                    txtPasswordReg.setError("password is required");
                    return;
                }
                if (confpassword.length() < 6) {
                    txtConfirmPassword.setError("password must be >= 6 character");
                    return;
                }
                if (password == confpassword) {
                    fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(register.this,"User created", Toast.LENGTH_SHORT).show();
                                userID = fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fstore.collection("users").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("femail",email);
                                user.put("fpassword",password);
                                user.put("confpaswword",confpassword);

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "onSuccess : Akun berhasil dibuat" + userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", "onFailure : " + e.toString());
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }else{
                                Toast.makeText(register.this,"Gagal dibuat", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(register.this,"Password tidak sama dengan confirm password", Toast.LENGTH_SHORT).show();
                }

            }
        });
        tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),login.class);
                startActivity(intent);
            }
        });

    }
}