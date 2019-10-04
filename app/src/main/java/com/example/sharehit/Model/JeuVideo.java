package com.example.sharehit.Model;

public class JeuVideo extends Video {

    public JeuVideo(String titre, String year, String imgUrl) {
        super(titre, year, imgUrl);
    }

    public String toString(){
        return "le jeu vidéo "+getName();
    }
}
