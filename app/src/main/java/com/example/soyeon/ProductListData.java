package com.example.soyeon;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ProductListData {
    private String pImage;
    private String pName;
    private String pPrice;

    public ProductListData() {
    }

    public ProductListData(String pImage, String pName, String pPrice) {
        this.pImage = pImage;
        this.pName = pName;
        this.pPrice = pPrice;
    }

    public String getPImage(){
        return pImage;
    }

    public String getPName(){
        return pName;
    }

    public String getPrice(){
        return pPrice;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("pImage", pImage);
        result.put("pName", pName);
        result.put("pPrice", pPrice);
        return result;
    }

}