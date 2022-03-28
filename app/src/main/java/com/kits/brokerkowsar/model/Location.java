package com.kits.brokerkowsar.model;

import com.google.gson.annotations.SerializedName;

public class Location {


    @SerializedName("Longitude")
    private String Longitude;

    @SerializedName("Latitude")
    private String Latitude;

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }
}
