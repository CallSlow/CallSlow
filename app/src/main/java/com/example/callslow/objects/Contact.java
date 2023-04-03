package com.example.callslow.objects;

public class Contact {
    private String nom, mac;

    public Contact(String nm, String mc) {
        nom = nm; mac = mc;
    }


    public String getName() {
        return nom;
    }

    public String getMac() {
        return mac;
    }

    public int getPhoto() {
        return 0;
    }
}
