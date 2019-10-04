package com.example.sharehit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Model.Recommendation;
import com.example.sharehit.Model.User;
import com.example.sharehit.R;

import java.util.List;

public class AdapterRecs extends RecyclerView.Adapter<AdapterRecs.MyHolder> {

    Context context;
    List<Recommendation> postRec;

    public AdapterRecs(Context context, List<Recommendation> postRec) {
        this.context = context;
        this.postRec = postRec;
    }



    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recommandation_item, parent, false);



        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {

        String name = postRec.get(i).getName();
        String imageUrl = postRec.get(i).getImg() ;
        String type = postRec.get(i).getType();
        User user = postRec.get(i).getUser();
        String userName = user.getPseudo();

        holder.name.setText(name);
        holder.desc.setText(type);


    }

    @Override
    public int getItemCount() {
        return postRec.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView recImg, pdp;
        TextView name,desc, nbrLike, nbrComment;
        Button likeButton, commentButton, bookButton;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            /*recImg = itemView.findViewById(R.id.recImg);
            pdp = itemView.findViewById(R.id.pdp);
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
            nbrLike = itemView.findViewById(R.id.nbrLike);
            nbrComment = itemView.findViewById(R.id.nbrComment);*/
            likeButton = itemView.findViewById(R.id.likeButton);
            commentButton = itemView.findViewById(R.id.commentButton);
            bookButton = itemView.findViewById(R.id.bookButton);



        }
    }

}
