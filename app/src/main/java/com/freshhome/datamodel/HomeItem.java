package com.freshhome.datamodel;

public class HomeItem {
    public String getDish_id() {
        return dish_id;
    }

    public void setDish_id(String dish_id) {
        this.dish_id = dish_id;
    }

    public String getDish_name() {
        return dish_name;
    }

    public void setDish_name(String dish_name) {
        this.dish_name = dish_name;
    }

    public String getDish_description() {
        return dish_description;
    }

    public void setDish_description(String dish_description) {
        this.dish_description = dish_description;
    }

    public String getDish_image() {
        return dish_image;
    }

    public void setDish_image(String dish_image) {
        this.dish_image = dish_image;
    }

    public String getDish_price() {
        return dish_price;
    }

    public void setDish_price(String dish_price) {
        this.dish_price = dish_price;
    }

    public String getDish_categories() {
        return dish_categories;
    }

    public void setDish_categories(String dish_categories) {
        this.dish_categories = dish_categories;
    }

    public String getDish_cuisines() {
        return dish_cuisines;
    }

    public void setDish_cuisines(String dish_cuisines) {
        this.dish_cuisines = dish_cuisines;
    }

    public String getDish_meal() {
        return dish_meal;
    }

    public void setDish_meal(String dish_meal) {
        this.dish_meal = dish_meal;
    }

    public String getRate_point() {
        return rate_point;
    }

    public void setRate_point(String rate_point) {
        this.rate_point = rate_point;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    String supplier_id;
    String dish_id;
    String dish_name;
    String dish_description;
    String dish_image;
    String dish_price;
    String rate_point;
    String date_time;
    String dish_categories;
    String dish_cuisines;
    String dish_meal;
    String discount;
    String value;
    String image;

    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    String product_type;

    public int getCart_qty() {
        return cart_qty;
    }

    public void setCart_qty(int cart_qty) {
        this.cart_qty = cart_qty;
    }

    int cart_qty;

    public String getFavo_count() {
        return favo_count;
    }

    public void setFavo_count(String favo_count) {
        this.favo_count = favo_count;
    }

    public String getUser_views() {
        return user_views;
    }

    public void setUser_views(String user_views) {
        this.user_views = user_views;
    }

    String favo_count, user_views;

    public String getIsFavo() {
        return isFavo;
    }

    public void setIsFavo(String isFavo) {
        this.isFavo = isFavo;
    }

    String isFavo;

    public String getDate_status() {
        return date_status;
    }

    public void setDate_status(String date_status) {
        this.date_status = date_status;
    }

    String date_status;

    public boolean isAttributes() {
        return isAttributes;
    }

    public void setAttributes(boolean attributes) {
        isAttributes = attributes;
    }

    boolean isAttributes;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
