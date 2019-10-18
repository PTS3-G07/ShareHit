package com.example.sharehit.Model;

public class Album extends Type {

    public Album(String artiste, String titre, String imgUrl, String link) {
        super(titre, imgUrl, artiste, link);
        setCONST_NOMMAGE("Artiste: ");
    }

    public String toString(){
        return "l'album "+getName()+" de "+getSpec();
    }
}
