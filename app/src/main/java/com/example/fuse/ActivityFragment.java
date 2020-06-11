package com.example.fuse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class ActivityFragment extends Fragment {

    private static final String KEY = "activity"; private static final String ID_KEY = "id"; private static final String PERMISSION_KEY = "permissions";
    private static final String BIO_KEY = "bio";
    private Activity activityObj;
    private String id,bio;
    private int permission;
    FirebaseFirestore fstore;
    ImageButton imageBtn, deleteBtn; Button connectBtn;
    TextView nameTextView, ageTextView, genderTextView, bioTextView,headingTextView, descriptionTextView, dateTextView;
    ImageView profileimage;

    public ActivityFragment() {
        // Required empty public constructor
    }

    public static ActivityFragment newInstance(Activity activityObj1, String id, int deletePermission, String bio ) {
        ActivityFragment fragment = new ActivityFragment(); // changes
        Bundle bundle = new Bundle();
        bundle.putString(BIO_KEY,bio);
        bundle.putParcelable(KEY, activityObj1);
        bundle.putInt(PERMISSION_KEY, deletePermission);
        bundle.putString(ID_KEY, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v  =  inflater.inflate(R.layout.fragment_activity, container, false);
        nameTextView = v.findViewById(R.id.nameTextView); ageTextView = v.findViewById(R.id.ageTextView); genderTextView = v.findViewById(R.id.genderTextView);
        bioTextView = v.findViewById(R.id.bio_text); headingTextView = v.findViewById(R.id.headingTextView); descriptionTextView = v.findViewById(R.id.descriptionTextViewFrag);
        dateTextView = v.findViewById(R.id.dateTextView); profileimage = v.findViewById(R.id.profile_imageView); imageBtn = v.findViewById(R.id.backBtn);
        connectBtn = v.findViewById(R.id.connectBtn);

        activityObj = (Activity) getArguments().getParcelable(KEY); id = (String)getArguments().getString(ID_KEY);
        permission = (Integer)getArguments().getInt(PERMISSION_KEY); bio = (String)getArguments().getString(BIO_KEY);
        deleteBtn = v.findViewById(R.id.deleteBtn);
        if(permission == 1)
        {
            deleteBtn.setVisibility(v.VISIBLE);
            deleteBtn.setEnabled(true);
            connectBtn.setEnabled(false);
        }else{
            deleteBtn.setEnabled(false);
            connectBtn.setEnabled(true);
            connectBtn.setVisibility(v.VISIBLE);
        }

        Picasso.get().load(Uri.parse(activityObj.getImageUri())).into(profileimage); nameTextView.setText(activityObj.getUserName());
        ageTextView.setText(activityObj.getUserAge()+" years old"); genderTextView.setText(activityObj.getUserGender()); bioTextView.setText(bio);
        headingTextView.setText(activityObj.getHeading()); descriptionTextView.setText(activityObj.getDescription()); dateTextView.setText(activityObj.getDate());

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("activities").document(id).delete();
                Toast.makeText(v.getContext(), "Your Post was Deleted", Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = activityObj.getEmail().split(",");
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Interested in "+activityObj.getHeading()+" activity");
                intent.putExtra(Intent.EXTRA_TEXT, "Hi, I am interest in your activity posted on Fuse");
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Please select a client"));

            }
        });

        return v;


    }
}
