package com.example.sharehit.Model;

import java.security.Timestamp;
import java.util.List;

public class Recommendation {

    private List<Comment> comments;
    private String album;
    private String artist;
    private List<String> likeUserUid;
    private long timeStamp;
    private String track;
    private String type;
    private String name;
    private String urlImage;
    private String urlPreview;
    private String userRecoUid;

    public Recommendation(){

    }

    public Recommendation(String type, String user,  String name, String img, String urlPreview, long timeStamp) {
        this.type = type;
        this.userRecoUid = user;
        this.name = name;
        this.urlImage = img;
        this.urlPreview=urlPreview;
        this.timeStamp=timeStamp;
    }


    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrlPreview() {
        return urlPreview;
    }

    public void setUrlPreview(String urlPreview) {
        this.urlPreview = urlPreview;
    }

    public String getType() {
        return type;
    }

    public List<String> getLikers() {
        return likeUserUid;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return urlImage;
    }

    public void setImg(String img) {
        this.urlImage = img;
    }

    public String getUserRecoUid() {
        return userRecoUid;
    }

    public void setUserRecoUid(String userRecoUid) {
        this.userRecoUid = userRecoUid;
    }
}
