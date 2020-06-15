package com.example.locationupdate;

/**
 * Created by AbdulRahim on 3/19/2017.
 */

public class Viewport {

    public Northeast getNortheast() {
        return northeast;
    }

    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }

    public Southwest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Southwest southwest) {
        this.southwest = southwest;
    }

    public Northeast northeast;
    public Southwest southwest;

}
