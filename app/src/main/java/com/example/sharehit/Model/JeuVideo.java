package com.example.sharehit.Model;

public class JeuVideo extends Type {

    private String titre;

    public JeuVideo(String titre) {
        this.titre = titre;
    }

    public String getTitre() {
        return titre;
    }

    public String toString(){
        return "le jeu vidéo "+titre;
    }
}
