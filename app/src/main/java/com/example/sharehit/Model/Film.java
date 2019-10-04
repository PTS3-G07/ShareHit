package com.example.sharehit.Model;

public class Film extends Video {

    public Film(String titre, String year, String imgUrl) {
        super(titre, year, imgUrl);
    }

    public String toString(){
        return "le film "+getTitre();
    }
}
