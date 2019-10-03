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
import com.example.sharehit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private Context mContext;
    private ArrayList<Artist> mArtistList;
    private OnItemclickListener mListener;


    public interface OnItemclickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemclickListener listener){
        mListener = listener;
    }

    public ArtistAdapter(Context context, ArrayList<Artist> matistList) {
        mContext = context;
        mArtistList = matistList;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.artist_item, parent, false);
        return new ArtistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist currentItem = mArtistList.get(position);
        String imageUrl = currentItem.getImgUrl();
        String name= currentItem.getName();
        String nbFan = currentItem.getNbFans();

        holder.name_ar.setText(name);
        holder.nbFan.setText("Nombre de fan: " +nbFan);
       Picasso.with(mContext).load(imageUrl).fit().centerInside().into(holder.img_ar);
        //holder.img_ar.setImageURI(Uri.parse("https://e-cdns-images.dzcdn.net/images/artist/0707267475580b1b82f4da20a1b295c6/120x120-000000-80-0-0.jpg"));
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

            img_ar = itemView.findViewById(R.id.img_ar);
            name_ar = itemView.findViewById(R.id.name_ar);
            nbFan = itemView.findViewById(R.id.nbFan);

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
