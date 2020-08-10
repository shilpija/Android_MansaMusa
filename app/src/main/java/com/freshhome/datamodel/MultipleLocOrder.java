package com.freshhome.datamodel;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MultipleLocOrder {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination_lat() {
        return destination_lat;
    }

    public void setDestination_lat(String destination_lat) {
        this.destination_lat = destination_lat;
    }

    public String getDestination_lng() {
        return destination_lng;
    }

    public void setDestination_lng(String destination_lng) {
        this.destination_lng = destination_lng;
    }

    public String getStarting_lat() {
        return starting_lat;
    }

    public void setStarting_lat(String starting_lat) {
        this.starting_lat = starting_lat;
    }

    public String getStarting_lng() {
        return starting_lng;
    }

    public void setStarting_lng(String starting_lng) {
        this.starting_lng = starting_lng;
    }

    public String getDestination_name() {
        return destination_name;
    }

    public void setDestination_name(String destination_name) {
        this.destination_name = destination_name;
    }

    public String getStarting_name() {
        return starting_name;
    }

    public void setStarting_name(String starting_name) {
        this.starting_name = starting_name;
    }

    String id;
    String destination_lat;
    String destination_lng;
    String starting_lat;
    String starting_lng;
    String destination_name;
    String starting_name;

    public double getDistance_fromuser_loc() {
        return distance_fromuser_loc;
    }

    public void setDistance_fromuser_loc(double distance_fromuser_loc) {
        this.distance_fromuser_loc = distance_fromuser_loc;
    }

    double distance_fromuser_loc;

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    String  order_id;
    String order_total;
    String order_currency;
    String order_items;
    String order_delivery_time;
    String order_paymethod;
    String user_name;
    String user_phonenumber;

    public String getOrder_delivery_address() {
        return order_delivery_address;
    }

    public void setOrder_delivery_address(String order_delivery_address) {
        this.order_delivery_address = order_delivery_address;
    }

    String order_delivery_address;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_total() {
        return order_total;
    }

    public void setOrder_total(String order_total) {
        this.order_total = order_total;
    }

    public String getOrder_currency() {
        return order_currency;
    }

    public void setOrder_currency(String order_currency) {
        this.order_currency = order_currency;
    }

    public String getOrder_items() {
        return order_items;
    }

    public void setOrder_items(String order_items) {
        this.order_items = order_items;
    }

    public String getOrder_delivery_time() {
        return order_delivery_time;
    }

    public void setOrder_delivery_time(String order_delivery_time) {
        this.order_delivery_time = order_delivery_time;
    }

    public String getOrder_paymethod() {
        return order_paymethod;
    }

    public void setOrder_paymethod(String order_paymethod) {
        this.order_paymethod = order_paymethod;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phonenumber() {
        return user_phonenumber;
    }

    public void setUser_phonenumber(String user_phonenumber) {
        this.user_phonenumber = user_phonenumber;
    }

    Marker marker;

    public String getKitchen_loc() {
        return kitchen_loc;
    }

    public void setKitchen_loc(String kitchen_loc) {
        this.kitchen_loc = kitchen_loc;
    }

    public String getKitchen_phonenumber() {
        return kitchen_phonenumber;
    }

    public void setKitchen_phonenumber(String kitchen_phonenumber) {
        this.kitchen_phonenumber = kitchen_phonenumber;
    }

    public String getKitchen_name() {
        return kitchen_name;
    }

    public void setKitchen_name(String kitchen_name) {
        this.kitchen_name = kitchen_name;
    }

    String kitchen_loc;
    String kitchen_phonenumber;
    String kitchen_name;

    public String getDriver_earning() {
        return driver_earning;
    }

    public void setDriver_earning(String driver_earning) {
        this.driver_earning = driver_earning;
    }

    String driver_earning;

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    String order_status;
}
