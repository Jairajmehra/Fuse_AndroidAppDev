package com.example.fuse;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class ActivityAdapter extends FirestoreRecyclerAdapter<Activity, ActivityAdapter.ActivityHolder> {

    public ActivityAdapter(FirestoreRecyclerOptions<Activity> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(ActivityHolder activityHolder, int i, Activity activity) {
        activityHolder.Heading.setText(activity.getHeading());
        activityHolder.Description.setMaxLines(2);
        activityHolder.Description.setText(activity.getDescription());
        Picasso.get().load(Uri.parse(activity.getImageUri())).into(activityHolder.profileimage);
        //activityHolder.profileimage.setImageURI(Uri.parse(activity.getImageUri().toString()));
        activityHolder.DatePosted.setText(activity.getDate());
    }

    @NonNull
    @Override
    public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item, parent, false);

        return new ActivityHolder(v);
    }

    class ActivityHolder extends RecyclerView.ViewHolder{
        ImageView profileimage;
        TextView DatePosted;
        TextView Heading;
        TextView Description;

        public ActivityHolder(@NonNull View itemView) {
            super(itemView);
            profileimage = (ImageView) itemView.findViewById(R.id.profile_imageView);
            DatePosted = (TextView) itemView.findViewById(R.id.date_textview);
            Heading = (TextView) itemView.findViewById(R.id.heading_textView);
            Description = (TextView) itemView.findViewById(R.id.description_textview);

        }
    }
}
