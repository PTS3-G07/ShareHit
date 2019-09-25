package com.example.sharehit.Model;

public class Serie extends Type {

    private String titre;

    public Serie(String titre) {
        this.titre = titre;
    }

    public String getTitre() {
        return titre;
    }

    public String toString(){
        return "la série "+titre;
    }
}
