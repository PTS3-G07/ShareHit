package com.example.sharehit.Model;

public class Album extends Type {

    public Album(String artiste, String titre, String imgUrl) {
        super(titre, imgUrl, artiste);
        setCONST_NOMMAGE("Artiste: ");
    }

    public String toString(){
        return "l'album "+getName()+" de "+getSpec();
    }
}
