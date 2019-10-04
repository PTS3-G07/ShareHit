package com.example.sharehit.Model;

public class Morceau extends Type {

    private String titre;
    private String artiste;
    private String imgUrl;

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    private String songUrl;

    public Morceau(String titre, String artiste, String imgUrl, String songUrl){
        this.artiste = artiste;
        this.titre = titre;
        this.imgUrl = imgUrl;
        this.songUrl = songUrl;
    }

    public String getTitre() {
        return titre;
    }

    public String getArtiste() {
        return artiste;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getSongUrl() {
        return songUrl;
    }


    public String toString(){
        return "le morceau "+titre+" de "+artiste;
    }
}
