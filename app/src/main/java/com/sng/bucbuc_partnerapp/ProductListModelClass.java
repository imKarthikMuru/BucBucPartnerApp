package com.sng.bucbuc_partnerapp;

class ProductListModelClass {
    String ProductName,ProductImage,CategoryType,Description,Price,Size,VegMark,ProductId,QtyIn,ProductStatus;

    public ProductListModelClass() {
    }

    public ProductListModelClass(String productName, String productImage, String categoryType, String description, String price, String size, String vegMark, String productId, String qtyIn, String productStatus) {
        ProductName = productName;
        ProductImage = productImage;
        CategoryType = categoryType;
        Description = description;
        Price = price;
        Size = size;
        VegMark = vegMark;
        ProductId = productId;
        QtyIn = qtyIn;
        ProductStatus = productStatus;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }

    public void setCategoryType(String categoryType) {
        CategoryType = categoryType;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setSize(String size) {
        Size = size;
    }

    public void setVegMark(String vegMark) {
        VegMark = vegMark;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public void setQtyIn(String qtyIn) {
        QtyIn = qtyIn;
    }

    public String getProductStatus() {
        return ProductStatus;
    }

    public void setProductStatus(String productStatus) {
        ProductStatus = productStatus;
    }

    public String getQtyIn() {
        return QtyIn;
    }

    public String getProductId() {
        return ProductId;
    }

    public String getVegMark() {
        return VegMark;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public String getCategoryType() {
        return CategoryType;
    }

    public String getDescription() {
        return Description;
    }

    public String getPrice() {
        return Price;
    }

    public String getSize() {
        return Size;
    }
}
