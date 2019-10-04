package com.example.sharehit.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.ApiManager;
import com.example.sharehit.Model.Morceau;
import com.example.sharehit.Model.Type;
import com.example.sharehit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeViewHolder> {

    private Context context;
    private ArrayList<Type> types;
    private OnItemclickListener listener;

    public MediaPlayer mediaPlayer;

    String songUrl;


    public interface OnItemclickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemclickListener listener){
        this.listener = listener;
    }

    public TypeAdapter(Context context, ArrayList<Type> matistList) {
        this.context = context;
        types = matistList;
    }

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.research_item, parent, false);
        return new TypeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        Type currentItem = types.get(position);
        String imageUrl = currentItem.getImgUrl();
        String name = currentItem.getName();
        String spec = currentItem.getSpec();
        if(currentItem instanceof Morceau){
            songUrl = ((Morceau) currentItem).getSongUrl();
            mediaPlayer = MediaPlayer.create(context, Uri.parse(songUrl));
        }

        holder.name_ar.setText(name);
        holder.spec.setText(currentItem.getConstNommage() + spec);
        Picasso.with(context).load(imageUrl).fit().centerInside().into(holder.img_ar);
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    public class TypeViewHolder extends RecyclerView.ViewHolder {

        public ImageButton img_ar;
        public TextView name_ar;
        public TextView spec;


        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);



            img_ar = itemView.findViewById(R.id.img_ar);
            name_ar = itemView.findViewById(R.id.name_ar);
            spec = itemView.findViewById(R.id.spec);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            img_ar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    if(mediaPlayer != null){
                        mediaPlayer.start();
                    }



                }
            });
        }
    }
}

