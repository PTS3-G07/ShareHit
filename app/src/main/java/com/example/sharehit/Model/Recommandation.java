package com.example.sharehit.Model;

public class Recommandation {

    String album, artist, id, track, type, urlImage, urlPreview, userRecoUid;
    Double timestamp;

    public Recommandation(){

    }

    public Recommandation(String album, String artist, String id, Double timestamp, String track, String type, String urlImage, String urlPreview, String userRecoUid) {
        this.album = album;
        this.artist = artist;
        this.id = id;
        this.timestamp = timestamp;
        this.track = track;
        this.type = type;
        this.urlImage = urlImage;
        this.urlPreview = urlPreview;
        this.userRecoUid = userRecoUid;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUrlPreview() {
        return urlPreview;
    }

    public void setUrlPreview(String urlPreview) {
        this.urlPreview = urlPreview;
    }

    public String getUserRecoUid() {
        return userRecoUid;
    }

    public void setUserRecoUid(String userRecoUid) {
        this.userRecoUid = userRecoUid;
    }
}
