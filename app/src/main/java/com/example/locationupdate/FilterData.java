package com.example.locationupdate;

import java.util.Comparator;

/**
 * Created by mosaad on 4/12/2017.
 */

public class FilterData  {

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

    public double getlng() {
        return lng;
    }

    public void setlng(double lng) {
        this.lng = lng;
    }

    public String ID;
    public String Name;
    public double lat;
    public double lng;

    public FilterData(String _ID, String _name, double _lat, double _lng)
    {
        ID = _ID;
        Name = _name;
        lat = _lat;
        lng = _lng;
    }

    public static Comparator<FilterData> LatCompare = new Comparator<FilterData>() {

        public int compare(FilterData s1, FilterData s2) {

            return Double.compare(s1.getLat(), s2.getLat());

        }};

}