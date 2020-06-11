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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class post_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar; DrawerLayout drawer; ActionBarDrawerToggle toggle;

    String UserID; String UserName; String UserEmail; Uri ProfileImageURI; String Userbio; String UserAge; String UserGender;

    ImageView drawerImageView;  TextView drawerEmail,drawerUsername; EditText header_editText; EditText description_editText;  Button postBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_activity);


        header_editText = (EditText)findViewById(R.id.heading_editText); // Heading of the Activity
        description_editText = (EditText)findViewById(R.id.description_editText); // Description of the activity
        postBtn = (Button)findViewById(R.id.postActivityBtn); // Submit/Post Activity button

        //------------Getting Data passed from previous activity------------------//
        Intent intent = getIntent();
        UserID = intent.getStringExtra("UserID"); UserName = intent.getStringExtra("UserName");
        UserGender = intent.getStringExtra("UserGender"); UserAge = intent.getStringExtra("UserAge");
        Userbio = intent.getStringExtra("UserBio"); UserEmail = intent.getStringExtra("UserEmail");
        if(intent.getStringExtra("ImageUrl").equals("0")){
            ProfileImageURI = null; // if "0" was passed as profile image url
        }
        else {
            ProfileImageURI = Uri.parse(intent.getStringExtra("ImageUrl"));
        }
        //------------------------------------------------------//

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        drawerUsername = (TextView) headerView.findViewById(R.id.navigation_view_textview_username); // Side navigation menu username
        drawerEmail = (TextView) headerView.findViewById(R.id.email_navTextview); // Side navigation menu email
        drawerImageView = (ImageView)headerView.findViewById(R.id.imageView); // Side navigation menu profile image
        drawerUsername.setText(UserName);
        drawerEmail.setText(UserEmail);
        if (ProfileImageURI != null) {
            Picasso.get().load(ProfileImageURI).into(drawerImageView); // setting profile image on profile page(activity)

        }

        //--------------Side Navigation Menu Click Listener and Toggle------------------------//
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        //-------------------------------------------//

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Posting Activity

                final String heading = header_editText.getText().toString().trim();
                final String description = description_editText.getText().toString().trim();

                if(heading.length() < 5 || heading.length() > 30) // Validating Data
                {
                    Toast.makeText(post_activity.this, "Heading should be between 5 to 30 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                if(description.length() < 20 || description.length()> 200) // Validating Data
                {
                    Toast.makeText(post_activity.this, "Description should be between 20 to 200 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                if(ProfileImageURI == null)
                {
                    Toast.makeText(post_activity.this, "Please Upload a Profile Picture before posting", Toast.LENGTH_LONG).show();
                    return;
                }
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()); // Getting Date and formatting
                CollectionReference colref = FirebaseFirestore.getInstance().collection("activities"); // reference to activities collection
                colref.add(new Activity(heading, UserName, description, UserEmail, date, UserAge, ProfileImageURI.toString(),UserGender)); // adding new Activity object to collection
                Toast.makeText(post_activity.this, "Your Activity Has Been Posted", Toast.LENGTH_LONG).show();
                startActivity(new Intent(post_activity.this, browse_activity.class));
                finish();
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // This function is used for navigation between pages (activities), Side menu Navigation.
         if (item.getItemId() == R.id.profile)
        {
            Intent intent = new Intent(post_activity.this, Profile.class);
            if(ProfileImageURI != null)
            {
                intent.putExtra("ImageUrl", ProfileImageURI.toString());
            }
            else
            {
                intent.putExtra("ImageUrl", "0");
            }
            intent.putExtra("UserName",UserName);
            intent.putExtra("UserEmail",UserEmail); intent.putExtra("UserAge",UserAge);intent.putExtra("UserBio",Userbio);
            intent.putExtra("UserGender",UserGender); intent.putExtra("UserID",UserID);
            startActivity(intent);

        }
        else if (item.getItemId() == R.id.browse_activity)
        {
            Intent intent = new Intent(post_activity.this, browse_activity.class);
            if(ProfileImageURI != null)
            {
                intent.putExtra("ImageUrl", ProfileImageURI.toString());
            }
            else
            {
                intent.putExtra("ImageUrl", "0");
            }
            intent.putExtra("UserName",UserName);
            intent.putExtra("UserEmail",UserEmail); intent.putExtra("UserAge",UserAge);intent.putExtra("UserBio",Userbio);
            intent.putExtra("UserGender",UserGender); intent.putExtra("UserID",UserID);
            startActivity(intent);
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
