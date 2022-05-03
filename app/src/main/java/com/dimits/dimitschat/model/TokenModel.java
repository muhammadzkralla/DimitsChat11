package com.dimits.dimitschat.model;

public class TokenModel {
    private String phone,token,status;


    public TokenModel() {
    }

    public TokenModel(String phone, String token, String status) {
        this.phone = phone;
        this.token = token;
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
