package com.example.callslow.objects;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

public class PointMap  {
    private final String name,description,latitude,longitude;

    public PointMap(String name, String description, String latitude, String longitude) {
        this.name= name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PointMap(JSONObject object) throws JSONException {
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

    public GeoPoint toGeopoint(){
        return new GeoPoint(Double.parseDouble(latitude),Double.parseDouble(longitude));
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        try {
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