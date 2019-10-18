package com.example.sharehit.Model;

public class Serie extends Video {

    public Serie(String titre, String year, String imgUrl, String link) {
        super(titre, year, imgUrl, link);
    }

    public String toString(){
        return "la série "+getName();
    }
}
