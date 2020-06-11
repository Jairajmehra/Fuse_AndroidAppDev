package com.example.fuse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.protobuf.NullValue;

import java.util.HashMap;
import java.util.Map;

public class registration_activity extends AppCompatActivity {

    EditText name_edittext, email_edittext, password_edittext, age_edittext;
    Button register_btn, already_member_btn;
    RadioGroup radiogroup; RadioButton radiobtn;
    FirebaseAuth fAuth; FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_activity);

        fAuth = FirebaseAuth.getInstance(); // fetching Firebase authenticaton instance object
        fstore = FirebaseFirestore.getInstance(); // fetching Firebase Firestore instance object

        name_edittext = (EditText)findViewById(R.id.name_register_textview);
        email_edittext = (EditText)findViewById(R.id.email_register_textview);
        age_edittext = (EditText)findViewById(R.id.age_registration_editText);
        radiogroup = (RadioGroup)findViewById(R.id.radioGroup);
        password_edittext = (EditText)findViewById(R.id.password_register_textview);
        register_btn = (Button)findViewById(R.id.register_button);
        already_member_btn = (Button)findViewById(R.id.member_button);

        if(fAuth.getCurrentUser() != null) // checking if the user is already logged in if yes than send to browse activity
        {
            startActivity(new Intent(registration_activity.this, browse_activity.class));
            finish();
        }
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = name_edittext.getText().toString().trim(); // getting all the text from the fields.
                final String password = password_edittext.getText().toString().trim();
                final String email = email_edittext.getText().toString().trim();
                final String Age = age_edittext.getText().toString().trim();
                int selectedId = radiogroup.getCheckedRadioButtonId();
                radiobtn =  findViewById(selectedId);

                if(TextUtils.isEmpty(name))  // validating all the user entered data.
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
                if(Age.isEmpty())
                {
                    age_edittext.setError("Cannot be blank");
                    return;
                }
                else {
                    Integer age = Integer.parseInt(Age);
                    if(age < 0 || age > 150)
                    {
                        age_edittext.setError("Please enter a valid age");
                        return;
                    }
                }
                // creating the user
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            // if user is created store other user data into Firebase Firestore

                            DocumentReference docref = fstore.collection("users").document(fAuth.getCurrentUser().getUid()); // reference object which indicated where to store
                            Map<String,Object> user = new HashMap<>();
                            user.put("Name",name); user.put("Email", email); user.put("Age",Age); user.put("Bio","");user.put("Gender",radiobtn.getText().toString()); // user data into key value pair
                            docref.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // if data is store is successful
                                    Toast.makeText(registration_activity.this, "You are now registered", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(registration_activity.this, "Registration Failed Please Try Again", Toast.LENGTH_LONG).show();
                                }
                            });
                            // send user to browse activity
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
                startActivity(new Intent(registration_activity.this, MainActivity.class)); // if user click already member take to login page.
                finish();
            }
        });
    }

}
