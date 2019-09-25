package com.example.sharehit.Model;

public class Album extends Type {

    private String artiste;
    private String titre;

    public Album(String artiste, String titre) {
        this.artiste = artiste;
        this.titre = titre;
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
