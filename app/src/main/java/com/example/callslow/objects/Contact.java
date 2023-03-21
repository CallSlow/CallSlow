package com.example.callslow.objects;

public class Contact {
    private String nom, tel;

    public Contact(String nm, String tl) {
        nom = nm; tel = tl;
    }


    public String getName() {
        return nom;
    }

    public String getPhone() {
        return tel;
    }

    public int getPhoto() {
        return 0;
    }
}
