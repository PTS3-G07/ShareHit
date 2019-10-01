package com.example.sharehit.Model;

public class Album extends Type {

    private String artiste;
    private String titre;
    private String imgUrl;

    public Album(/*String artiste*/ String titre, String imgUrl) {
        //this.artiste = artiste;
        this.titre = titre;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }


    public String getArtiste() {
        return artiste;
    }

    public String getTitre() {
        return titre;
    }

    public String toString(){
        return "l'album "+titre+" de "+artiste;
    }
}
