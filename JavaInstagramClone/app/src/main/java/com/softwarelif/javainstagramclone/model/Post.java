package com.softwarelif.javainstagramclone.model;

import java.util.ArrayList;

public class Post {
    public String id;
    public String email;
    public String comment;
    public String downloadUrl;
    public ArrayList<String> comments;


    public Post(String id, String email, String comment, String downloadUrl) {
        this.id = id;
        this.email = email;
        this.comment = comment;
        this.downloadUrl = downloadUrl;
        this.comments = new ArrayList<>();
    }

    public Post() {}
}


