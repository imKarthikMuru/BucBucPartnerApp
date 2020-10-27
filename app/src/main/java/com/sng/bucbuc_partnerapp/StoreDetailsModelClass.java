package com.sng.bucbuc_partnerapp;

class StoreDetailsModelClass {
    String StoreOwnerName,StoreMail,ContactNo,Address,StoreImage,StoreName,StoreLogo,AcceptingOrders,Category;

    public StoreDetailsModelClass() {
    }

    public StoreDetailsModelClass(String storeOwnerName, String storeMail, String contactNo, String address, String storeImage, String storeName, String storeLogo, String acceptingOrders, String category) {
        StoreOwnerName = storeOwnerName;
        StoreMail = storeMail;
        ContactNo = contactNo;
        Address = address;
        StoreImage = storeImage;
        StoreName = storeName;
        StoreLogo = storeLogo;
        AcceptingOrders = acceptingOrders;
        Category = category;
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
