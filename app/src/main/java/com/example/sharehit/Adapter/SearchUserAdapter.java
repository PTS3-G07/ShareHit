package com.example.sharehit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Model.Bookmark;
import com.example.sharehit.Model.User;
import com.example.sharehit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapter extends
        RecyclerView.Adapter<SearchUserAdapter.ViewHolder>{

    Context context;
    private List<User> mUser;
    private StorageReference mStorageRef;

    public SearchUserAdapter(List<User> users) {
        this.mUser = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.like_user_profil_display, parent, false);

        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = mUser.get(position);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        holder.pseudoProfil.setText(user.getPseudo());
        final StorageReference filepath = mStorageRef;
        filepath.child(user.getUserId()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/share-hit.appspot.com/o/"+user.getUserId()+"?alt=media").fit().centerInside().into(holder.imageProfil);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Picasso.with(context).load("").fit().centerInside().into(viewHolder.getImgProfil());
                holder.imageProfil.setImageResource(R.drawable.ic_baby);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageProfil;
        TextView pseudoProfil;


        public ViewHolder(View itemView) {
            super(itemView);
            imageProfil = (CircleImageView) itemView.findViewById(R.id.imageSearchUser);
            pseudoProfil = (TextView) itemView.findViewById(R.id.pseudoSearchUser);
        }
    }
}
