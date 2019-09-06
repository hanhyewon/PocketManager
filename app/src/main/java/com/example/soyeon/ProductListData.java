package com.example.soyeon;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ProductListData {
    private String pId;
    private Uri pImage;
    private String pName;
    private String pPrice;

    public ProductListData() {
    }

    public ProductListData(String pId, Uri pImage, String pName, String pPrice) {
        this.pId = pId;
        this.pImage = pImage;
        this.pName = pName;
        this.pPrice = pPrice;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("pId", pId);
        result.put("pImage", pImage);
        result.put("pName", pName);
        result.put("pPrice", pPrice);
        return result;
    }

}