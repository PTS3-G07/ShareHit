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
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListLikeAdapter extends
        RecyclerView.Adapter<ListLikeAdapter.ViewHolder> {

    Context context;
    private List<User> mUser;

    private FirebaseAuth mAuth;

    public ListLikeAdapter(List<User> users) {
        this.mUser = users;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public ListLikeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.like_user_profil_display, parent, false);

        ListLikeAdapter.ViewHolder viewHolder = new ListLikeAdapter.ViewHolder(userView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListLikeAdapter.ViewHolder viewHolder, final int position) {
        User user = mUser.get(position);

        TextView textView = viewHolder.pseudoUser;
        textView.setText(user.getPseudo());

        CircleImageView circleImageView = viewHolder.pictureUser;
        Picasso.with(context).load("https://interactive-examples.mdn.mozilla.net/media/examples/grapefruit-slice-332-332.jpg").fit().centerInside().into(circleImageView);

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public void clear() {
        mUser.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<User> list) {
        mUser.addAll(list);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pseudoUser;
        public CircleImageView pictureUser;

        public ViewHolder(View itemView) {
            super(itemView);
            pseudoUser = (TextView) itemView.findViewById(R.id.pseudoProfilListLike);
            pictureUser = (CircleImageView) itemView.findViewById(R.id.imageProfilListLike);

        }
    }
}
