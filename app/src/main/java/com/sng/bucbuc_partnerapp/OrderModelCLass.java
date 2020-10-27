package com.sng.bucbuc_partnerapp;

public class OrderModelCLass {


    String Name,Address,Mobile,AddressType,ProductName,ProductID,StoreID,OrderedDate,Time,OrderStatus,UserID,PaymentMethod,Category,Suggestion;
    int Quantity;
    float Price,ToPay;

    public OrderModelCLass() {
    }

    public OrderModelCLass(String name, String address, String mobile, String addressType, String productName, String productID, String storeID, String orderedDate, String time, String orderStatus, String userID, String paymentMethod, String category, String suggestion, int quantity, float price, float toPay) {
        Name = name;
        Address = address;
        Mobile = mobile;
        AddressType = addressType;
        ProductName = productName;
        ProductID = productID;
        StoreID = storeID;
        OrderedDate = orderedDate;
        Time = time;
        OrderStatus = orderStatus;
        UserID = userID;
        PaymentMethod = paymentMethod;
        Category = category;
        Suggestion = suggestion;
        Quantity = quantity;
        Price = price;
        ToPay = toPay;
    }

    public String getSuggestion() {
        return Suggestion;
    }

    public void setSuggestion(String suggestion) {
        Suggestion = suggestion;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getStoreID() {
        return StoreID;
    }

    public void setStoreID(String storeID) {
        StoreID = storeID;
    }

    public String getOrderedDate() {
        return OrderedDate;
    }

    public void setOrderedDate(String orderedDate) {
        OrderedDate = orderedDate;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getAddressType() {
        return AddressType;
    }

    public void setAddressType(String addressType) {
        AddressType = addressType;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public float getToPay() {
        return ToPay;
    }

    public void setToPay(float toPay) {
        ToPay = toPay;
    }
}
