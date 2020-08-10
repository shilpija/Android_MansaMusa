package com.freshhome.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Cart implements Parcelable {
    public Cart() {

    }

    protected Cart(Parcel in) {
        dish_id = in.readString();
        dish_name = in.readString();
        dish_qty = in.readString();
        supplier_name = in.readString();
        dish_price = in.readString();
        dish_image = in.readString();
        currency = in.readString();
        total_price = in.readString();
        dish_rating = in.readString();
        dish_reviews = in.readString();
        dish_status = in.readString();
        product_type = in.readString();
        arrayAttributes = new ArrayList<subAttributes>();
        in.readList(arrayAttributes, getClass().getClassLoader());
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

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

    public String getDish_qty() {
        return dish_qty;
    }

    public void setDish_qty(String dish_qty) {
        this.dish_qty = dish_qty;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public String getDish_price() {
        return dish_price;
    }

    public void setDish_price(String dish_price) {
        this.dish_price = dish_price;
    }

    public String getDish_image() {
        return dish_image;
    }

    public void setDish_image(String dish_image) {
        this.dish_image = dish_image;
    }

    String dish_id;
    String dish_name;
    String dish_qty;
    String supplier_name;
    String dish_price;
    String dish_image;
    String currency;
    String total_price;
    String dish_rating;
    String dish_reviews;

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    String product_type;

    public String getDish_status() {
        return dish_status;
    }

    public void setDish_status(String dish_status) {
        this.dish_status = dish_status;
    }

    String dish_status;

    public String getDish_rating() {
        return dish_rating;
    }

    public void setDish_rating(String dish_rating) {
        this.dish_rating = dish_rating;
    }

    public String getDish_reviews() {
        return dish_reviews;
    }

    public void setDish_reviews(String dish_reviews) {
        this.dish_reviews = dish_reviews;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public ArrayList<subAttributes> getArrayAttributes() {
        return arrayAttributes;
    }

    public void setArrayAttributes(ArrayList<subAttributes> arrayAttributes) {
        this.arrayAttributes = arrayAttributes;
    }

    ArrayList<subAttributes> arrayAttributes;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dish_id);
        dest.writeString(dish_name);
        dest.writeString(dish_qty);
        dest.writeString(supplier_name);
        dest.writeString(dish_price);
        dest.writeString(dish_image);
        dest.writeString(currency);
        dest.writeString(total_price);
        dest.writeString(dish_rating);
        dest.writeString(dish_reviews);
        dest.writeString(dish_status);
        dest.writeString(product_type);
        dest.writeList(arrayAttributes);
    }
}
