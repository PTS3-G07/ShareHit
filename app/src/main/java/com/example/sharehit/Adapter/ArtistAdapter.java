package com.example.sharehit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Model.Artist;
import com.example.sharehit.Model.Morceau;
import com.example.sharehit.Model.Type;
import com.example.sharehit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private Context mContext;
    private ArrayList<Type> mArtistList;
    private OnItemclickListener mListener;
    private String nom;


    public interface OnItemclickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemclickListener listener){
        mListener = listener;
    }

    public ArtistAdapter(Context context, ArrayList<Type> matistList, String nom) {
        mContext = context;
        mArtistList = matistList;
        this.nom = nom;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.artist_item, parent, false);
        return new ArtistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        if (mArtistList.get(position) instanceof Artist) {
            Artist currentItem = (Artist) mArtistList.get(position);
            String imageUrl = currentItem.getImgUrl();
            String name = currentItem.getName();
            String nbFan = currentItem.getNbFans();

            holder.name_ar.setText(name);
            holder.nbFan.setText(nom + nbFan);
            Picasso.with(mContext).load(imageUrl).fit().centerInside().into(holder.img_ar);
        }
        if (mArtistList.get(position) instanceof Morceau) {
            Morceau currentItem = (Morceau) mArtistList.get(position);
            String imageUrl = currentItem.getImgUrl();
            String name = currentItem.getTitre();
            String nbFan = currentItem.getArtiste();
            String songUrl = currentItem.getSongUrl();

            holder.name_ar.setText(name);
            holder.nbFan.setText(nom + nbFan);
            Picasso.with(mContext).load(imageUrl).fit().centerInside().into(holder.img_ar);
        }

    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {

        public ImageView img_ar;
        public TextView name_ar;
        public TextView nbFan;


        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);

            img_ar = itemView.findViewById(R.id.recImg);
            name_ar = itemView.findViewById(R.id.nbrLike);
            nbFan = itemView.findViewById(R.id.nbrComment);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

