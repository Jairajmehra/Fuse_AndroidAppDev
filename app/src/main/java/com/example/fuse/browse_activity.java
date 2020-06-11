package com.example.fuse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class browse_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ActivityAdapter.OnItemClickListener {

    Toolbar toolbar; DrawerLayout drawer; ActionBarDrawerToggle toogle; // Toolbar and side menu drawer toggle
    String UserID; String UserName; String UserEmail; Uri ProfileImageURI; String Userbio; String UserAge; String UserGender; // User Data fetched from Firebase and  passed on to other activities
    ImageView drawerImageView; // Side Navigation view Menu image
    TextView drawerUsername, draweremail; // Side Navigation view Menu TextView to display username and email

    RecyclerView recyclerView;  ActivityAdapter adapter;

    FirebaseAuth fAuth; FirebaseFirestore fstore;
    CollectionReference colref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_activity);

        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        colref = fstore.collection("activities"); // Generating a reference to our activities Collection or Table (in SQL terminology)

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

        drawerUsername = (TextView) headerView.findViewById(R.id.navigation_view_textview_username);
        draweremail = (TextView) headerView.findViewById(R.id.email_navTextview);
        drawerImageView = (ImageView) headerView.findViewById(R.id.imageView);
        recyclerView = (RecyclerView)findViewById(R.id.activities_recyclerView);

        //--------------Side Navigation Menu Click Listener and Toogle------------------------//
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        toogle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toogle);
        toogle.setDrawerIndicatorEnabled(true);
        toogle.syncState();
        //------------------------------------------------------------//
        UserID =  fAuth.getCurrentUser().getUid(); // Fetching current user ID


        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child(UserID + ".jpg"); // Reference to fetch profile image
        DocumentReference docref = fstore.collection("users").document(UserID); // reference to get user data

        //----------------------Getting Profile Image---------------//
            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // if user has upload a profile photo
                    ProfileImageURI = uri; // we pass on this profile image Uri to other activities.
                    Picasso.get().load(uri).into(drawerImageView); // load the profile image into side navigation drawer
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // user dont have a profile image or we failed to load it.
                    ProfileImageURI = null;
                    Toast.makeText(browse_activity.this, "Failed To Load Profile Image", Toast.LENGTH_LONG).show();
                }
            });
        //----------------------Getting User Data---------------//
            docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Fetching user data from Firebase Firestore.
                        UserName = documentSnapshot.getString("Name"); drawerUsername.setText(UserName);
                        UserEmail = documentSnapshot.getString("Email");Userbio = documentSnapshot.getString("Bio");
                        draweremail.setText(UserEmail);
                        UserAge = documentSnapshot.getString("Age"); UserGender = documentSnapshot.getString("Gender");
                    } else {
                        // If we fail to get user data, it might mean that the user is not a registered user, so sign out.
                        Toast.makeText(browse_activity.this, "Failed To get user data, Please Try again", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(browse_activity.this, MainActivity.class));
                        finish();
                    }
                }
            });

        SetUpRecyclerView(); // Setting up Recycler view

    }

    private void SetUpRecyclerView() {
        Query query = colref.orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Activity> options = new FirestoreRecyclerOptions.Builder<Activity>().setQuery(query, Activity.class).build();
        adapter = new ActivityAdapter(options);
        recyclerView = findViewById(R.id.activities_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }
   @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening(); // Enables the recyclerview to listen for changes.
    }
    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // This function is used for navigation between pages (activities), Side menu Navigation.
        if(item.getItemId() == R.id.post_activity) // If user wants to go to post_activity page
        {
            Intent intent = new Intent(browse_activity.this, post_activity.class);
            if(ProfileImageURI != null)
            {
                intent.putExtra("ImageUrl", ProfileImageURI.toString());
            }
            else
            {
                intent.putExtra("ImageUrl", "0"); // if we failed to load profile image pass "0" as Uri.
            }
            intent.putExtra("UserName",UserName);
            intent.putExtra("UserEmail",UserEmail); intent.putExtra("UserAge",UserAge);intent.putExtra("UserBio",Userbio);
            intent.putExtra("UserGender",UserGender); intent.putExtra("UserID",UserID);
            startActivity(intent);

        }
        else if (item.getItemId() == R.id.profile) // if user want to go to his/her profile page
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
        else if (item.getItemId() == R.id.logout) // if user wants to logout.
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(browse_activity.this, MainActivity.class));
            finish();
        }
        return  true;
    }

    @Override
    public void onItemClick(final DocumentSnapshot documentSnapshot, int position) {
        // if user wants to know more about a post (expand a post)
        final Activity activityObj = documentSnapshot.toObject(Activity.class);
            if (activityObj != null) {
                fstore.collection("users").whereEqualTo("Email",activityObj.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot queryDocumentSnapshot :task.getResult()){

                                OpenActivityFragment(queryDocumentSnapshot.get("Bio").toString(), activityObj, documentSnapshot); // we first fetch the details and call the function below
                            }
                        }
                    }
                });
            }
    }
    public void OpenActivityFragment(String bio, Activity activityObj, DocumentSnapshot documentSnapshot){
        // we open a fragment to view all the information about the activity posted.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (UserEmail.equals(activityObj.getEmail())) {
            Fragment fragment = ActivityFragment.newInstance(activityObj, documentSnapshot.getId(), 1, bio);
            ft.replace(R.id.drawer_layout, fragment).addToBackStack(null);
            ft.commit();
        } else {
            Fragment fragment = ActivityFragment.newInstance(activityObj, documentSnapshot.getId(), 0, bio);
            ft.replace(R.id.drawer_layout, fragment).addToBackStack(null);
            ft.commit();
        }

    }
}
