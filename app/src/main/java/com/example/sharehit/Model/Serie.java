package com.example.sharehit.Model;

public class Serie extends Video {

    public Serie(String titre, String year, String imgUrl) {
        super(titre, year, imgUrl);
    }

    public String toString(){
        return "la série "+getTitre();
    }
}
