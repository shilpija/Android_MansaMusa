package com.freshhome.datamodel;

public class DeliveryAddress {
    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        this.addressTitle = addressTitle;
    }

    public String getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(String addressLocation) {
        this.addressLocation = addressLocation;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressBuildingno() {
        return addressBuildingno;
    }

    public void setAddressBuildingno(String addressBuildingno) {
        this.addressBuildingno = addressBuildingno;
    }

    public String getAddressFloorno() {
        return addressFloorno;
    }

    public void setAddressFloorno(String addressFloorno) {
        this.addressFloorno = addressFloorno;
    }

    public String getAddressFlatno() {
        return addressFlatno;
    }

    public void setAddressFlatno(String addressFlatno) {
        this.addressFlatno = addressFlatno;
    }

    public String getAddressLandmark() {
        return addressLandmark;
    }

    public void setAddressLandmark(String addressLandmark) {
        this.addressLandmark = addressLandmark;
    }

    String addressId;
    String addressTitle;
    String addressLocation;
    String addressCity;
    String addressBuildingno;
    String addressFloorno;
    String addressFlatno;
    String addressLandmark;
    String address_lat;
    String address_lng;

    public String getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(String isdefault) {
        this.isdefault = isdefault;
    }

    String isdefault;

    public String getAddress_lat() {
        return address_lat;
    }

    public void setAddress_lat(String address_lat) {
        this.address_lat = address_lat;
    }

    public String getAddress_lng() {
        return address_lng;
    }

    public void setAddress_lng(String address_lng) {
        this.address_lng = address_lng;
    }
}
