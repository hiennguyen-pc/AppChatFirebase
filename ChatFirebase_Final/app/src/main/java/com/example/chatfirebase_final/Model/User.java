package com.example.chatfirebase_final.Model;

public class User {
    private String userID;
    private String email;
    private String imageInfo;
    private String pass;
    private String user;
    private String status;
    private String search;

    public User(String userID, String email, String imageInfo, String pass, String user,String status,String search) {
        this.userID = userID;
        this.email = email;
        this.imageInfo = imageInfo;
        this.pass = pass;
        this.user = user;
        this.status = status;
        this.search=search;
    }

    public User() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(String imageInfo) {
        this.imageInfo = imageInfo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}


