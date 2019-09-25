package com.example.sharehit.Model;

import java.util.ArrayList;
import java.util.List;

public class Recommendation {

    private Type type;
    private User user;
    private String desc;
    private List<User> likers;
    private List<Comment> comments;

    public Recommendation(Type type, User user, String desc) {
        this.type = type;
        this.user = user;
        this.desc = desc;
        this.likers=new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public Type getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    public String getDesc() {
        return desc;
    }

    public List<User> getLikers() {
        return likers;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String toString(){
        return this.user.getPseudo()+" a recommandé "+type.toString();
    }
}
