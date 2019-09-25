package com.example.sharehit.Model;

public class Artiste extends Type {

    private String artiste;

    public Artiste(String artiste) {
        this.artiste = artiste;
    }

    public String getArtiste() {
        return artiste;
    }

    public String toString(){
        return "l'artiste "+artiste;
    }
}
