package com.example.sharehit.Model;

public class Morceau extends Type {

    private String titre;
    private String artiste;

    public Morceau(String titre, String artiste){
        this.artiste = artiste;
        this.titre = titre;
    }

    public String getTitre() {
        return titre;
    }

    public String getArtiste() {
        return artiste;
    }

    public String toString(){
        return "le morceau "+titre+" de "+artiste;
    }
}
