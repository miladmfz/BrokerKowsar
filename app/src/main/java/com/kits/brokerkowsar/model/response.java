package com.kits.brokerkowsar.model;

import com.google.gson.annotations.SerializedName;

public class response {

    @SerializedName("StatusCode") private String StatusCode;
    @SerializedName("Errormessage") private String Errormessage;

    public String getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(String statusCode) {
        StatusCode = statusCode;
    }

    public String getErrormessage() {
        return Errormessage;
    }

    public void setErrormessage(String errormessage) {
        Errormessage = errormessage;
    }
}
