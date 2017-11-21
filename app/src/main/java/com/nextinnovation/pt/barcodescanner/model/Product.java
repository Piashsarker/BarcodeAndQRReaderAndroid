package com.nextinnovation.pt.barcodescanner.model;

import java.io.Serializable;


/**
 * Created by PT on 2/9/2017.
 */

public class Product implements Serializable {
    private String productNo ;
    private String productBarcodeNo ;
    private  String scanTime;
    private String scanDate ;

    public String getProductBarcodeNo() {
        return productBarcodeNo;
    }

    public void setProductBarcodeNo(String productBarcodeNo) {
        this.productBarcodeNo = productBarcodeNo;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }

    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
    }



    public Product(String productBarcodeNo, String scanTime, String scanDate) {
        this.productBarcodeNo = productBarcodeNo;
        this.scanTime = scanTime;
        this.scanDate = scanDate;
    }

    public Product(){

    }







}
