package com.example.fuse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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


public class MainActivity extends AppCompatActivity {
    EditText email_edittext, password_edittext;
    Button register_btn, login_btn;
    TextView forgetpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FirebaseAuth fAuth = FirebaseAuth.getInstance(); // Fetching an instance of Firebase authentication object
        if(fAuth.getCurrentUser() != null) // If the user is already logged in take him/her to browse activities page
        {
            startActivity(new Intent(MainActivity.this, browse_activity.class));
            finish();
        }
        email_edittext = (EditText)findViewById(R.id.username_textview);
        password_edittext = (EditText)findViewById(R.id.password_textview);
        login_btn = (Button)findViewById(R.id.login_button);
        register_btn = (Button)findViewById(R.id.register_button);
        forgetpassword = (TextView)findViewById(R.id.forgotpassword_textview);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_edittext.getText().toString().trim();
                String password = password_edittext.getText().toString().trim(); // If users try's to login validate the credentials
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
                        // Wrong credentials
                        else
                        {
                            Toast.makeText(MainActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        // if users wants to register
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, registration_activity.class));
            }
        });
        // User can reset password by clicking on forget password textview.
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetmail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext()); // Create a alert dialog
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Please enter your registered email to get the reset link");
                passwordResetDialog.setView(resetmail);
                passwordResetDialog.setPositiveButton("Send Me The Link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            String email = resetmail.getText().toString().trim(); // Take the user's registered email from the user
                            //Firebase authentication object has a function to reset password.
                            fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "Reset Email Has Been Sent", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Failed "+e.getMessage() , Toast.LENGTH_LONG).show();
                                }
                            });
                    }
                });
                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing to be done
                    }
                });
                passwordResetDialog.create().show();
            }
        });

    }
}
