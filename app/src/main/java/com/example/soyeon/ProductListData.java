package com.example.soyeon;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ProductListData {
    private String pId;
    private String pImage;
    private String pName;
    private String pPrice;

    public ProductListData(String pId, String pImage, String pName, String pPrice) {
        this.pId = pId;
        this.pImage = pImage;
        this.pName = pName;
        this.pPrice = pPrice;
    }

    public ProductListData(String pImage, String pName, String pPrice) {
        this.pImage = pImage;
        this.pName = pName;
        this.pPrice = pPrice;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpPrice() {
        return pPrice;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
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