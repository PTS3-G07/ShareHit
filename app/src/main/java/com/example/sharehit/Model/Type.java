package com.example.sharehit.Model;

public class Type {

    private String CONST_NOMMAGE;
    private String name;
    private String imgUrl;
    private String spec;
    private String link;

    public Type(String name, String imgUrl, String spec, String link){
        this.imgUrl=imgUrl;
        this.name=name;
        this.spec=spec;
        this.link=link;
    }

    public String getLink() {
        return link;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getConstNommage() {
        return CONST_NOMMAGE;
    }

    public void setCONST_NOMMAGE(String CONST_NOMMAGE) {
        this.CONST_NOMMAGE = CONST_NOMMAGE;
    }

    public String getSpec() {
        return spec;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        return "";
    }
}
