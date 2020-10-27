package com.sng.bucbuc_partnerapp;


public class SectionHeader {

    String productId;
    String headerText;

    public SectionHeader() {
    }

    public SectionHeader(String productId, String headerText) {
        this.productId = productId;
        this.headerText = headerText;
    }

    public String getProductId() {
        return productId;
    }

    public String getHeaderText() {
        return headerText;
    }
}