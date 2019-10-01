package com.example.sharehit.Model;

public class main {

    public static void main (String[] args){
        User user = new User("leo","leobrunet@gmail.com");
        User user2 = new User("Thomas","thomas@sousmerde.com");

        Album album = new Album("John Cena","pompolopom pompomlopom");
        //Recommendation recommendation = new Recommendation(album, user,"du blabla");

        //System.out.println(recommendation.toString());

       // user2.setBookmarked(recommendation);

        System.out.println(user2.getBookmarks().toString());
    }

}
