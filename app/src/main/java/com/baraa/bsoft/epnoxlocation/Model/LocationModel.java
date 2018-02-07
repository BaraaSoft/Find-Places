package com.baraa.bsoft.epnoxlocation.Model;

/**
 * Created by baraa on 04/01/2018.
 */

public class LocationModel {
    private float latitude;
    private float longitude;
    private String title;
    private String address;
    private String imgUrl;
    private boolean checked;

    public LocationModel(float latitude, float longitude, String title, String address, String imgUrl) {
        this.checked = false;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.address = address;
        this.imgUrl = imgUrl;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {return checked;}
}
