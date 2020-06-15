package com.example.locationupdate.db;

import io.realm.RealmObject;

/**
 * Created by ghulam.akber on 03-Oct-16.
 */

public class Location_db extends RealmObject {


    private String ID;
    private String Name;
    private double lat;
    private double lng;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}
