package com.example.hyejin;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class SalesListData {
    private String sName;
    private String sDate_Start;
    private String sDate_End;

    public SalesListData() {
    }

    public SalesListData(String sName, String sDate_Start, String sDate_End) {
        this.sName = sName;
        this.sDate_Start = sDate_Start;
        this.sDate_End = sDate_End;
    }

    public String getSName(){
        return sName;
    }

    public String getSDate_Start(){
        return sDate_Start;
    }

    public String getSDate_End(){
        return sDate_End;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("sName", sName);
        result.put("sDate_Start", sDate_Start);
        result.put("sDate_End", sDate_End);
        return result;
    }

}