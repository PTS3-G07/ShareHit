package com.example.sharehit.Model;

import java.util.List;

public class User {

    public String pseudo;
    public String email;
    public String pdp;
    public List<Recommendation> bookmarks;
    public List<User> followed;


    public User(String pseudo, String email) {
        this.pseudo = pseudo;
        this.email = email;
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

    public void likeRecommendation(Recommendation recommendation){
        recommendation.getLikers().add(this);
    }

    public void commentRecommendation(Recommendation recommendation, String comment){
        recommendation.getComments().add(new Comment(this, comment));
    }

    public void follow(User user){
        this.followed.add(user);
    }
}

