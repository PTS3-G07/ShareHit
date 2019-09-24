package com.example.sharehit.Model;

public class Comment {

    public User user;
    public String comment;

    public Comment(User user, String comment) {
        this.user = user;
        this.comment = comment;
    }
}
