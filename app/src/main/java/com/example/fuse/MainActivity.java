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

public class MainActivity extends AppCompatActivity {
    EditText email_edittext, password_edittext;
    Button register_btn, login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FirebaseAuth fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(MainActivity.this, browse_activity.class));
            finish();
        }
        email_edittext = (EditText)findViewById(R.id.username_textview);
        password_edittext = (EditText)findViewById(R.id.password_textview);
        login_btn = (Button)findViewById(R.id.login_button);
        register_btn = (Button)findViewById(R.id.register_button);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_edittext.getText().toString().trim();
                String password = password_edittext.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    email_edittext.setError("Email cannot be empty");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    password_edittext.setError("Password cannot be empty");
                    return;
                }
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            startActivity(new Intent(MainActivity.this, browse_activity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, registration_activity.class));
            }
        });

    }
}
