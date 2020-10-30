package com.sng.bucbuc_partnerapp;

class StoreDetailsModelClass {
    String StoreOwnerName,StoreMail,ContactNo,Address,StoreImage,StoreName,StoreLogo,AcceptingOrders,Category,PushToken;

    public StoreDetailsModelClass() {
    }

    public StoreDetailsModelClass(String storeOwnerName, String storeMail, String contactNo, String address, String storeImage, String storeName, String storeLogo, String acceptingOrders, String category, String pushToken) {
        StoreOwnerName = storeOwnerName;
        StoreMail = storeMail;
        ContactNo = contactNo;
        Address = address;
        StoreImage = storeImage;
        StoreName = storeName;
        StoreLogo = storeLogo;
        AcceptingOrders = acceptingOrders;
        Category = category;
        PushToken = pushToken;
    }

    public void setStoreOwnerName(String storeOwnerName) {
        StoreOwnerName = storeOwnerName;
    }

    public void setStoreMail(String storeMail) {
        StoreMail = storeMail;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setStoreImage(String storeImage) {
        StoreImage = storeImage;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public void setStoreLogo(String storeLogo) {
        StoreLogo = storeLogo;
    }

    public void setAcceptingOrders(String acceptingOrders) {
        AcceptingOrders = acceptingOrders;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPushToken() {
        return PushToken;
    }

    public void setPushToken(String pushToken) {
        PushToken = pushToken;
    }

    public String getCategory() {
        return Category;
    }

    public String getAcceptingOrders() {
        return AcceptingOrders;
    }

    public String getStoreOwnerName() {
        return StoreOwnerName;
    }

    public String getStoreMail() {
        return StoreMail;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public String getAddress() {
        return Address;
    }

    public String getStoreImage() {
        return StoreImage;
    }

    public String getStoreName() {
        return StoreName;
    }

    public String getStoreLogo() {
        return StoreLogo;
    }
}
