package com.example.sharehit.Model;

public class Bookmark {

    private String keyBookmark, keyReco, type, imgUrl, titre, artist;

    public Bookmark(String keyBookmark, String keyReco, String type, String imgUrl, String titre, String artist) {
        this.keyBookmark = keyBookmark;
        this.keyReco = keyReco;
        this.type = type;
        this.imgUrl = imgUrl;
        this.titre = titre;
        this.artist = artist;
    }

    public String getKeyBookmark() {
        return keyBookmark;
    }

    public void setKeyBookmark(String keyBookmark) {
        this.keyBookmark = keyBookmark;
    }

    public String getKeyReco() {
        return keyReco;
    }

    public void setKeyReco(String keyReco) {
        this.keyReco = keyReco;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
