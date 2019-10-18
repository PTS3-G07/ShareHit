package com.example.sharehit.Model;

public class JeuVideo extends Video {

    public JeuVideo(String titre, String year, String imgUrl, String link) {
        super(titre, year, imgUrl, link);
    }

    public String toString(){
        return "le jeu vidéo "+getName();
    }
}
