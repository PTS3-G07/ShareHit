package com.example.sharehit.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class User {

    public String pseudo, userId;


    public User(){
    }

    public User(String pseudo) {
        this.pseudo = pseudo;
    }

    public User(String pseudo, String userId) {
        this.pseudo = pseudo;
        this.userId = userId;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

}

