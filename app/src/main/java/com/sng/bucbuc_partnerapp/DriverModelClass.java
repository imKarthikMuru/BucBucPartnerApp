package com.sng.bucbuc_partnerapp;

public class DriverModelClass {

    String Name,Mobile,Status,UID,UserID,BikeNo,BikeModel,Address,PushToken;
    double Lat,Lng;

    public DriverModelClass() {
    }

    public DriverModelClass(String name, String mobile, String status, String UID, String userID, String bikeNo, String bikeModel, String address, double lat, double lng,String pushToken) {
        Name = name;
        Mobile = mobile;
        Status = status;
        this.UID = UID;
        UserID = userID;
        BikeNo = bikeNo;
        BikeModel = bikeModel;
        Address = address;
        Lat = lat;
        Lng = lng;
        PushToken=pushToken;
    }

    public String getPushToken() {
        return PushToken;
    }

    public void setPushToken(String pushToken) {
        PushToken = pushToken;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getBikeNo() {
        return BikeNo;
    }

    public void setBikeNo(String bikeNo) {
        BikeNo = bikeNo;
    }

    public String getBikeModel() {
        return BikeModel;
    }

    public void setBikeModel(String bikeModel) {
        BikeModel = bikeModel;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLng() {
        return Lng;
    }

    public void setLng(double lng) {
        Lng = lng;
    }
}
