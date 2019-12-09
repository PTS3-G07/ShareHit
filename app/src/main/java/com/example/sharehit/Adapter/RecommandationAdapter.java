package com.example.sharehit.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Model.Recommandation;
import com.example.sharehit.Model.User;
import com.example.sharehit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecommandationAdapter extends
        RecyclerView.Adapter<RecommandationAdapter.ViewHolder> {

    Context context;
    List<Recommandation> mRecommandation;

    public RecommandationAdapter(List<Recommandation> recommandations) {
        this.mRecommandation = recommandations;
    }

    @Override
    public RecommandationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View recommandationView = inflater.inflate(R.layout.recommandation_item, parent, false);

        RecommandationAdapter.ViewHolder viewHolder = new RecommandationAdapter.ViewHolder(recommandationView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecommandationAdapter.ViewHolder viewHolder, final int position) {
        Recommandation recommandation = mRecommandation.get(position);

        String idRecommandation = recommandation.getCleReco();

        //Initialisation des item de la recommandation
        TextView descRecommandation = viewHolder.descRecommandation;


        if(recommandation.getType().equals("track")){
            String desc ="<b>"+recommandation.getTrack()+"</b>"+" de "+"<b>"+recommandation.getArtist()+"</b>";
            descRecommandation.setText(Html.fromHtml(desc));
        } else if(recommandation.getType().equals("album")){
            String desc ="<b>"+recommandation.getAlbum()+"</b>"+" de "+"<b>"+recommandation.getArtist()+"</b>";
            descRecommandation.setText(Html.fromHtml(desc));
        } else {
            String desc ="<b>"+recommandation.getArtist()+"</b>";
            descRecommandation.setText(Html.fromHtml(desc));
        }

        Picasso.with(context).load(recommandation.getUrlImage()).fit().centerInside().into(viewHolder.pictureRecommandation);

    }

    @Override
    public int getItemCount() {
        return mRecommandation.size();
    }

    public void clear() {
        mRecommandation.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Recommandation> list) {
        mRecommandation.addAll(list);
        notifyDataSetChanged();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nbrlike;
        TextView nbrCom;

        TextView pseudoCom;
        TextView autreComment;

        ImageButton commentButton;
        ImageButton bookmarkButton;
        ImageButton likeButton;

        ImageButton pictureRecommandation;

        TextView timeRecommandation;
        TextView descRecommandation;
        TextView nomRecommandation;
        CircleImageView pictureUserRecommandation;

        ImageView playButton;
        ImageView circle;

        final LinearLayout layout;
        final LinearLayout.LayoutParams params;

        public ViewHolder(View itemView) {
            super(itemView);

            nbrlike = (TextView) itemView.findViewById(R.id.nbrLike);
            nbrCom = (TextView) itemView.findViewById(R.id.nbrComment);

            pseudoCom = itemView.findViewById(R.id.pseudoComment);
            autreComment = itemView.findViewById(R.id.autreComment);

            commentButton = (ImageButton) itemView.findViewById(R.id.commentButton);
            bookmarkButton = (ImageButton) itemView.findViewById(R.id.bookButton);
            likeButton = (ImageButton) itemView.findViewById(R.id.likeButton);


            pictureRecommandation = (ImageButton) itemView.findViewById(R.id.img_ar);

            timeRecommandation = (TextView) itemView.findViewById(R.id.time);
            descRecommandation = (TextView) itemView.findViewById(R.id.desc);
            nomRecommandation = (TextView) itemView.findViewById(R.id.name);
            pictureUserRecommandation = (CircleImageView) itemView.findViewById(R.id.imgProfil);

            playButton = itemView.findViewById(R.id.playButton);
            circle = itemView.findViewById(R.id.circle);


            playButton.setVisibility(View.INVISIBLE);
            circle.setVisibility(View.INVISIBLE);
            layout =(LinearLayout)itemView.findViewById(R.id.linearLayoutReco);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    }
}
