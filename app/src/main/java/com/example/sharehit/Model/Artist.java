package com.example.sharehit.Model;

public class Artist {

    private int id;
    private String name;
    private String imgUrl;
    private String nbFans;

    public Artist(String name,  String nbFans) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.nbFans = nbFans;
    }


    public Artist(String name,  String nbFans,  String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.nbFans = nbFans;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getNbFans() {
        return nbFans;
    }

}

