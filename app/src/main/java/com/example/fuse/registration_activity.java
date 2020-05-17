package com.example.fuse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class registration_activity extends AppCompatActivity {
    EditText name_edittext, email_edittext, password_edittext;
    Button register_btn, already_member_btn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_activity);
        fAuth = FirebaseAuth.getInstance();
        name_edittext = (EditText)findViewById(R.id.name_register_textview);
        email_edittext = (EditText)findViewById(R.id.email_register_textview);
        password_edittext = (EditText)findViewById(R.id.password_register_textview);
        register_btn = (Button)findViewById(R.id.register_button);
        already_member_btn = (Button)findViewById(R.id.member_button);
        if(fAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(registration_activity.this, browse_activity.class));
            finish();
        }
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_edittext.getText().toString().trim();
                String password = password_edittext.getText().toString().trim();
                String email = email_edittext.getText().toString().trim();
                if(TextUtils.isEmpty(name))
                {
                    name_edittext.setError("Name cannot be empty!");
                    return;
                }
                if(TextUtils.isEmpty(email))
                {
                    email_edittext.setError("Email cannot be empty!");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    password_edittext.setError("Password cannot be empty!");
                    return;
                }
                if(password.length() < 6 )
                {
                    password_edittext.setError("At least 6 character!");
                    return;
                }
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(registration_activity.this, "You are now registered", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(registration_activity.this, browse_activity.class));
                            finish();
                        }else
                        {
                            Toast.makeText(registration_activity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
        already_member_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registration_activity.this, MainActivity.class));
                finish();
            }
        });
    }
}
