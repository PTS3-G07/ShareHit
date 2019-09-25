package com.example.sharehit.Model;

public class Film extends Type {

    private String titre;

    public Film(String titre) {
        this.titre = titre;
    }

    public String getTitre() {
        return titre;
    }

    public String toString(){
        return "le film "+titre;
    }
}
