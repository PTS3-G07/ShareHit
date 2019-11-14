package com.example.sharehit.Model;

public class JeuVideo extends Video {

    public JeuVideo(String titre, String year, String imgUrl, String link) {
        super(titre, year, imgUrl, link);
        setCONST_NOMMAGE("Année de sortie: ");
    }

    public String toString(){
        return "le jeu vidéo "+getName();
    }
}
