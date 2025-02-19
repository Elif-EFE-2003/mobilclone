package com.softwarelif.javainstagramclone.view;

import java.security.Timestamp;

public class Comment {
    private String commentText;
    private String userEmail;
    private Timestamp timestamp;

    public Comment() {

    }

    public Comment(String commentText, String userEmail, Timestamp timestamp) {
        this.commentText = commentText;
        this.userEmail = userEmail;
        this.timestamp = timestamp;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
