package com.freshhome.datamodel;

public class track_driver {
    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public double getDriver_lat() {
        return driver_lat;
    }

    public void setDriver_lat(double driver_lat) {
        this.driver_lat = driver_lat;
    }

    public double getDriver_lng() {
        return driver_lng;
    }

    public void setDriver_lng(double driver_lng) {
        this.driver_lng = driver_lng;
    }

    String driver_name;
    double driver_lat,driver_lng;
}
