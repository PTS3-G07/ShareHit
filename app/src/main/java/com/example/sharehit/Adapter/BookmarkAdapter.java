package com.example.sharehit.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Model.Bookmark;
import com.example.sharehit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookmarkAdapter extends
        RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    Context context;
    private List<Bookmark> mBookmark;

    private FirebaseAuth mAuth;

    public BookmarkAdapter(List<Bookmark> bookmarks) {
        this.mBookmark = bookmarks;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View bookmarkView = inflater.inflate(R.layout.list_bookmark, parent, false);

        ViewHolder viewHolder = new ViewHolder(bookmarkView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BookmarkAdapter.ViewHolder viewHolder, final int position) {
        final Bookmark bookmark = mBookmark.get(position);

        TextView textView = viewHolder.titreBookmark;
        textView.setText(bookmark.getTitre());
        TextView textView2 = viewHolder.sousTitreBookmark;
        textView2.setText(bookmark.getArtist());
        TextView textView3 = viewHolder.typeBookmark;
        textView3.setText(bookmark.getType());
        ImageView imageView = viewHolder.imgBookmark;
        if(!bookmark.getImgUrl().equals("")) Picasso.with(context).load(bookmark.getImgUrl()).fit().centerInside().into(imageView);
        LinearLayout linearLayout = viewHolder.bookmarkMain;
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Supprimer ?")
                        .setMessage(bookmark.getTitre())
                        .setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("bookmarks").child(bookmark.getKeyBookmark()).removeValue();
                                mBookmark.remove(bookmark);
                            }
                        })
                        .setNegativeButton("Annuler", null)
                        .setIcon(R.drawable.ic_exclamation_mark)
                        .show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mBookmark.size();
    }

    public void clear() {
        mBookmark.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Bookmark> list) {
        mBookmark.addAll(list);
        notifyDataSetChanged();
    }





    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titreBookmark, sousTitreBookmark, typeBookmark;
        public ImageView imgBookmark;
        public LinearLayout bookmarkMain;

        public ViewHolder(View itemView) {
            super(itemView);

            titreBookmark = (TextView) itemView.findViewById(R.id.bookmarkTitre);
            sousTitreBookmark = (TextView) itemView.findViewById(R.id.bookmarkSoustitre);
            typeBookmark = (TextView) itemView.findViewById(R.id.bookmarkType);
            imgBookmark = (ImageView) itemView.findViewById(R.id.bookmarkImage);
            bookmarkMain = (LinearLayout) itemView.findViewById(R.id.bookmarkMain);
        }
    }
}
