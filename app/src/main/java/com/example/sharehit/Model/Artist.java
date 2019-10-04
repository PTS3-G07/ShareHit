package com.example.sharehit.Model;

public class Artist extends Type {

    public Artist(String name,  String nbFans,  String imgUrl) {
        super(name, imgUrl, nbFans);
        setCONST_NOMMAGE("Nombre de fans: ");
    }

    public String toString(){
        return "l'artiste "+getName();
    }

}

