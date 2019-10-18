package com.example.sharehit.Model;

public class Video extends Type {

    private String annee;

    public Video(String name, String annee, String imgUrl, String link) {
        super(name, imgUrl, annee, link);
        setCONST_NOMMAGE("Année de sortie: ");
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }
}
