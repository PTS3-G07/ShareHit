package com.example.sharehit.Model;

import java.util.List;

public class Recommendation {

    private String type;

    public String getUserRecoUid() {
        return userRecoUid;
    }

    public void setUserRecoUid(String userRecoUid) {
        this.userRecoUid = userRecoUid;
    }

    private String userRecoUid;
    private String desc;
    private String name;
    private String img;
    private List<User> likers;
    private List<Comment> comments;

    public Recommendation(String type, String user,  String name, String img) {
        this.type = type;
        this.userRecoUid = user;
        this.name = name;
        this.img = img;
    }

    public String getType() {
        return type;
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

    /*public String toString(){
        return this.user.getPseudo()+" a recommandé "+type.toString();
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
