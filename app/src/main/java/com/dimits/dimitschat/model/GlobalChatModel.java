package com.dimits.dimitschat.model;

public class GlobalChatModel {
    private String message;
    private String sender;
    private String img;
    private String imageurl;

    public GlobalChatModel() {
    }

    public GlobalChatModel(String message, String sender, String img, String imageurl) {
        this.message = message;
        this.sender = sender;
        this.img = img;
        this.imageurl = imageurl;
    }

    public GlobalChatModel(String sender, String img, String imageurl) {
        this.sender = sender;
        this.img = img;
        this.imageurl = imageurl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
