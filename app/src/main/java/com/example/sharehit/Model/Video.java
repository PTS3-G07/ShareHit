package com.example.sharehit.Model;

public class Video extends Type {
    private String titre;
    private String annee;

    public Video(String titre, String annee, String imgUrl) {
        super(imgUrl);
        this.titre = titre;
        this.annee = annee;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }
}
