package com.example.callslow.objects;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.UUID;

public class PointMap  {
    private final String uuid,name,description,latitude,longitude;

    public PointMap(String uuid, String name, String description, String latitude, String longitude) {
        this.uuid= uuid;
        this.name= name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PointMap(JSONObject object) throws JSONException {
        uuid = object.getString("uuid");
        name = object.getString("name");
        description = object.getString("description");
        latitude = object.getString("latitude");
        longitude = object.getString("longitude");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getUuid() {return uuid;}

    public GeoPoint toGeopoint(){
        return new GeoPoint(Double.parseDouble(latitude),Double.parseDouble(longitude));
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuid", uuid);
            jsonObject.put("name", name);
            jsonObject.put("description", description);
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }
}