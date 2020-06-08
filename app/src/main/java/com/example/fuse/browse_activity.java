package com.example.fuse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.security.keystore.StrongBoxUnavailableException;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class browse_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar; DrawerLayout drawer; ActionBarDrawerToggle toogle; // Navigation view
    String UserID; String UserName; String UserEmail; Uri ProfileImageURI; String Userbio; String UserAge; String UserGender; // User Data passed on to other activities
    ImageView drawerImageView; // setting navigation view menu image
    TextView drawerUsername;
    RecyclerView recyclerView;
    FirebaseAuth fAuth; FirebaseFirestore fstore;
    CollectionReference colref;
    ActivityAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_activity);
        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        colref = fstore.collection("activities");
        FirebaseUser user = fAuth.getCurrentUser();
        UserID = user.getUid();

        recyclerView = (RecyclerView)findViewById(R.id.activities_recyclerView);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        drawerUsername = (TextView) headerView.findViewById(R.id.navigation_view_textview_username);
        drawerImageView = (ImageView) headerView.findViewById(R.id.imageView);

        //drawerUsername.setText(user.getEmail().toString());


        //------------------------------------------------------------//
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        toogle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toogle);
        toogle.setDrawerIndicatorEnabled(true);
        toogle.syncState();
        //------------------------------------------------------------//

        try {
            DocumentReference docref = fstore.collection("users").document(UserID);
            docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        UserName = documentSnapshot.getString("Name");
                        drawerUsername.setText(UserName);
                        UserEmail = documentSnapshot.getString("Email");
                        Userbio = documentSnapshot.getString("Bio");
                        UserAge = documentSnapshot.getString("Age");
                        UserGender = documentSnapshot.getString("Gender");
                    } else {
                        Toast.makeText(browse_activity.this, "User Data Does not Exits", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(browse_activity.this, MainActivity.class));
                        finish();
                    }
                }
            });
        }catch (Exception ex)
        {
            Toast.makeText(browse_activity.this, "Error"+ex, Toast.LENGTH_LONG).show();
            return;
        }
        //Setting Profile Image
        try {
            StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child(UserID+".jpg");
            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                        ProfileImageURI = uri;
                        System.out.println(ProfileImageURI.toString());
                        Picasso.get().load(uri).into(drawerImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ProfileImageURI = null;
                    drawerImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_foreground));
                }
            });
        }catch (Exception ex)
        {
            ProfileImageURI = null;
            Toast.makeText(browse_activity.this, "Failed To Load Profile Image", Toast.LENGTH_LONG).show();
            return;
        }
        SetUpRecyclerView();

    }

    private void SetUpRecyclerView() {
        Query query = colref.orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Activity> options = new FirestoreRecyclerOptions.Builder<Activity>().setQuery(query, Activity.class).build();
        adapter = new ActivityAdapter(options);
        recyclerView = findViewById(R.id.activities_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
   @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.post_activity)
        {
            Intent intent = new Intent(browse_activity.this, post_activity.class);
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
        else if (item.getItemId() == R.id.profile)
        {

            Intent intent = new Intent(browse_activity.this, Profile.class);
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
            startActivity(new Intent(browse_activity.this, MainActivity.class));
            finish();
        }
        return  true;
    }

}
