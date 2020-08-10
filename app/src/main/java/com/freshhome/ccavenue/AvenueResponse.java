package com.freshhome.ccavenue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvenueResponse {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("orderid")
    @Expose
    private String orderid;
    @SerializedName("code")
    @Expose
    private long code;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }
}
