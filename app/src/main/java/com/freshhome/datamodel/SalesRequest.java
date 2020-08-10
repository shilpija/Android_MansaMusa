package com.freshhome.datamodel;

public class SalesRequest {
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public String getSupplier_loc() {
        return supplier_loc;
    }

    public void setSupplier_loc(String supplier_loc) {
        this.supplier_loc = supplier_loc;
    }

    public String getSupplier_phonenumber() {
        return supplier_phonenumber;
    }

    public void setSupplier_phonenumber(String supplier_phonenumber) {
        this.supplier_phonenumber = supplier_phonenumber;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getSupplier_lat() {
        return supplier_lat;
    }

    public void setSupplier_lat(String supplier_lat) {
        this.supplier_lat = supplier_lat;
    }

    public String getSupplier_lng() {
        return supplier_lng;
    }

    public void setSupplier_lng(String supplier_lng) {
        this.supplier_lng = supplier_lng;
    }

    String msg;
    String id;
    String supplier_id;
    String supplier_name;
    String supplier_loc;
    String supplier_phonenumber;
    String request_status;
    String supplier_lat;
    String supplier_lng;

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    String notification_id;
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsread() {
        return isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    String time;
    String isread;
    String request_type;
}
