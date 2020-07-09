package com.dimits.dimitschat.model;

public class UserModel {
    private String uid,name,phone,banned,img;


    public UserModel() {
    }

    public UserModel(String uid, String name,String phone,String banned,String img) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.banned = banned;
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBanned() {
        return banned;
    }

    public void setBanned(String banned) {
        this.banned = banned;
    }
}