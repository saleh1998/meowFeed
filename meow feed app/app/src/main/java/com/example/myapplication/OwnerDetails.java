package com.example.myapplication;

public class OwnerDetails {

    private String owner;
    private String cat;
    private String imgPath;

    public OwnerDetails(String owner, String cat, String imgPath) {
        this.owner = owner;
        this.cat = cat;
        this.imgPath = imgPath;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
