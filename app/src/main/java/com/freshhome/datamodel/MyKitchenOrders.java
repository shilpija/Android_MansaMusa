package com.freshhome.datamodel;

public class MyKitchenOrders {
    String order_id;
    String order_status;
    String currency_code;
    String total;
    String order_date;
    String payment_method;
    String total_items;
    String date;
    String time;
    String user_name;
    String user_profile_pic;
    String timmer;
    String rating;

    public String getTimmer() {
        return timmer;
    }

    public void setTimmer(String timmer) {
        this.timmer = timmer;
    }



    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }



    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getTotal_items() {
        return total_items;
    }

    public void setTotal_items(String total_items) {
        this.total_items = total_items;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String gettime() {
        return time;
    }

    public void settime(String user_time) {
        this.time = user_time;
    }

    public String getuserName() {
        return user_name;
    }

    public void setuserName(String name) {
        this.user_name = name;
    }

    public String getUser_profile_pic() {
        return user_profile_pic;
    }

    public void setUser_profile_pic(String user_profile_pic) {
        this.user_profile_pic = user_profile_pic;
    }
}
