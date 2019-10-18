package com.example.sharehit.Model;

public class Film extends Video {

    public Film(String titre, String year, String imgUrl, String link) {

        super(titre, year, imgUrl, link);
    }

    public String toString(){
        return "le film "+getName();
    }
}
