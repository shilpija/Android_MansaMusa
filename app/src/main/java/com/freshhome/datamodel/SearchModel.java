package com.freshhome.datamodel;

import com.google.gson.annotations.SerializedName;

public class SearchModel {

    @SerializedName("label")
    private String label;

    @SerializedName("value")
    private String value;

    @SerializedName("type")
    private String type;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
