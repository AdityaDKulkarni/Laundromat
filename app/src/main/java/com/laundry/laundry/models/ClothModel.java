package com.laundry.laundry.models;

/**
 * @author Aditya Kulkarni
 */

public class ClothModel {
    private int pk;
    private String uid,cloth_type,color, bagTagId;
    private int bag;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCloth_type() {
        return cloth_type;
    }

    public void setCloth_type(String cloth_type) {
        this.cloth_type = cloth_type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBagTagId() {
        return bagTagId;
    }

    public void setBagTagId(String bagTagId) {
        this.bagTagId = bagTagId;
    }

    public int getBag() {
        return bag;
    }

    public void setBag(int bag) {
        this.bag = bag;
    }
}
