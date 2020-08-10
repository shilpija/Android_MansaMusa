package com.freshhome.datamodel;

public class categories {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getimage() {
        return cat_image;
    }

    public void setimage(String cat_image) {
        this.cat_image = cat_image;
    }

    public String get_name() {
        return cat_name;
    }

    public void set_name(String cat_name) {
        this.cat_name = cat_name;
    }

    String id;
    String cat_image;
    String cat_name;

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    String available;
}
