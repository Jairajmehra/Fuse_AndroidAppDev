package com.example.fuse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class post_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    DrawerLayout drawer;
    String UserID; String UserName; String UserEmail; Uri ProfileImageURI; String Userbio; String UserAge; String UserGender;
    ActionBarDrawerToggle toogle;
    ImageView drawerImageView; ImageView activityImageView;
    FirebaseAuth fauth;     FirebaseFirestore fstore;
    TextView drawerUsername;
    EditText header_editText; EditText description_editText;
    Button postBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_activity);

        header_editText = (EditText)findViewById(R.id.heading_editText);
        description_editText = (EditText)findViewById(R.id.description_editText);
        postBtn = (Button)findViewById(R.id.postActivityBtn);
//        fauth = FirebaseAuth.getInstance();
//        FirebaseUser user = fauth.getCurrentUser();
//        final String UserID = user.getUid();
        Intent intent = getIntent();
        UserID = intent.getStringExtra("UserID");
        UserName = intent.getStringExtra("UserName");
        UserGender = intent.getStringExtra("UserGender");
        UserAge = intent.getStringExtra("UserAge");
        Userbio = intent.getStringExtra("UserBio");
        UserEmail = intent.getStringExtra("UserEmail");
        try {
            ProfileImageURI = Uri.parse(intent.getStringExtra("ImageUrl"));
        } catch (Exception ex) {
            ProfileImageURI = null;
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        drawerUsername = (TextView) headerView.findViewById(R.id.navigation_view_textview_username);
        drawerImageView = (ImageView)headerView.findViewById(R.id.imageView);
        drawerUsername.setText(UserName);
        if (ProfileImageURI != null) {
            Picasso.get().load(ProfileImageURI).into(drawerImageView);
        }

        //-------------------------------------------//
        toolbar = findViewById(R.id.toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        toogle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toogle);
        toogle.setDrawerIndicatorEnabled(true);
        toogle.syncState();
        //-------------------------------------------//
        fstore = FirebaseFirestore.getInstance();
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String heading = header_editText.getText().toString().trim();
                final String description = description_editText.getText().toString().trim();

                if(heading.length() < 5 || heading.length() > 50)
                {
                    Toast.makeText(post_activity.this, "Heading should be between 5 to 50 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                if(description.length() < 20 || description.length()> 200)
                {
                    Toast.makeText(post_activity.this, "Heading should be between 20 to 200 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                //DocumentReference docref = fstore.collection("activities").document(heading);
                CollectionReference colref = FirebaseFirestore.getInstance().collection("activities");
                colref.add(new Activity(heading, UserName, description, UserEmail, date, UserAge, ProfileImageURI.toString(),UserGender));
                Toast.makeText(post_activity.this, "Your Activity Has Been Posted", Toast.LENGTH_LONG).show();
                startActivity(new Intent(post_activity.this, browse_activity.class));
                finish();


//                Map<String, Object> activity = new HashMap<>();
//                activity.put("Heading",heading); activity.put("Description",description); activity.put("Date",date);
//                activity.put("Email",UserEmail); activity.put("UserID", UserID);
//                docref.set(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(post_activity.this, "Your Activity Has Been Posted", Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(post_activity.this, browse_activity.class));
//                        finish();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(post_activity.this, "Failed To Post Your Activity, Please Try Again", Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(post_activity.this, browse_activity.class));
//                        finish();
//                    }
//                });
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

         if (item.getItemId() == R.id.profile)
        {
            startActivity(new Intent(post_activity.this, Profile.class));
            finish();
        }
        else if (item.getItemId() == R.id.browse_activity)
        {
            startActivity(new Intent(post_activity.this, browse_activity.class));
            finish();
        }
        else if (item.getItemId() == R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(post_activity.this, MainActivity.class));
            finish();
        }
        return  true;
    }
}
