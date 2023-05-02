package com.example.callslow.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class Contact {
    private String nom, mac;

    private long creation_timestamp;

    public Contact(String nm, String mc) {
        nom = nm; mac = mc; creation_timestamp = new Timestamp(System.currentTimeMillis()).getTime();
    }

    public Contact(String nm, String mc, long timestamp) {
        nom = nm; mac = mc; creation_timestamp = timestamp;
    }

    public Contact(JSONObject obj) throws JSONException {
        nom = obj.getString("name");
        mac = obj.getString("mac");
        creation_timestamp = obj.getLong("date");
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

    public long getCreationTimestamp() { return creation_timestamp; }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("name", nom);
        obj.put("mac", mac);
        obj.put("date", creation_timestamp);

        return obj;
    }
}
