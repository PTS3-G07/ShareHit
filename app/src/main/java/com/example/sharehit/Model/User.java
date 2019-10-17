package com.example.sharehit.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class User {

    public String pseudo;
    public String email;
    public String pdp;
    public List<Recommendation> bookmarks;

    public List<User> followed;


    public User(){

    }

    public User(String pseudo, String email) {
        this.pseudo = pseudo;
        this.email = email;
        this.bookmarks = new ArrayList<>();
        this.followed = new ArrayList<>();
    }



    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPdp() {
        return pdp;
    }

    public void setPdp(String pdp) {
        this.pdp = pdp;
    }

    public void setBookmarked(Recommendation bookmark) {
        bookmarks.add(bookmark);
    }

    /*
    public void likeRecommendation(Recommendation recommendation){
        recommendation.getLikers().add(this);
    }

     */

    public void commentRecommendation(Recommendation recommendation, String comment){
        recommendation.getComments().add(new Comment(this, comment));
    }

    public void follow(User user){
        this.followed.add(user);
    }

    public List<Recommendation> getBookmarks() {
        return bookmarks;
    }

    public List<User> getFollowed() {
        return followed;
    }
}

