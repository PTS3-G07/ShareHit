package com.example.sharehit.Model;

public class Morceau extends Type {

    private String songUrl;

    public Morceau(String name, String artiste, String imgUrl, String songUrl, String link){
        super(name, imgUrl, artiste, link);
        this.songUrl = songUrl;
        setCONST_NOMMAGE("Artiste: ");
    }

    public String getSongUrl() {
        return songUrl;
    }

    public String toString(){
        return "le morceau "+getName()+" de "+getSpec();
    }
}
