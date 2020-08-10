package com.freshhome.datamodel;

public class NotificationModel {
    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    String notification_id="";
    String type="";
    String order_id="";
    String message="";
    String time="";
    String lat="";
    String lng="";
    String rating_overall="";

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

    public String getSupplier_phone() {
        return supplier_phone;
    }

    public void setSupplier_phone(String supplier_phone) {
        this.supplier_phone = supplier_phone;
    }

    String supplier_name="",supplier_loc="",supplier_phone="";
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }



    public String getRating_overall() {
        return rating_overall;
    }

    public void setRating_overall(String rating_overall) {
        this.rating_overall = rating_overall;
    }



    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getHelp_request_data() {
        return help_request_data;
    }

    public void setHelp_request_data(String help_request_data) {
        this.help_request_data = help_request_data;
    }

    String request_status="";
    String help_request_data="";

    public String getSales_name() {
        return sales_name;
    }

    public void setSales_name(String sales_name) {
        this.sales_name = sales_name;
    }

    public String getSales_phone() {
        return sales_phone;
    }

    public void setSales_phone(String sales_phone) {
        this.sales_phone = sales_phone;
    }

    public String getSales_rating() {
        return sales_rating;
    }

    public void setSales_rating(String sales_rating) {
        this.sales_rating = sales_rating;
    }

    public String getSales_image() {
        return sales_image;
    }

    public void setSales_image(String sales_image) {
        this.sales_image = sales_image;
    }

    String sales_name="",sales_phone="",sales_rating="",sales_image="";

    public String getOrderstatus() {
        return Orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        Orderstatus = orderstatus;
    }

    String Orderstatus="";
    public String getDeliveryLoc() {
        return deliveryLoc;
    }

    public void setDeliveryLoc(String deliveryLoc) {
        this.deliveryLoc = deliveryLoc;
    }

    String deliveryLoc="";

    public String getRequet_id() {
        return requet_id;
    }

    public void setRequet_id(String requet_id) {
        this.requet_id = requet_id;
    }

    String requet_id="";


}

