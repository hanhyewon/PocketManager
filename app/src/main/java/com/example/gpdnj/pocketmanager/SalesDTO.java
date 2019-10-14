package com.example.gpdnj.pocketmanager;

public class SalesDTO {
    private String salesId;
    private String title;
    private String date;
    private boolean state;

    public SalesDTO(String salesId, String title, String date) {
        this.salesId = salesId;
        this.title = title;
        this.date = date;
    }

    public SalesDTO(String title, String date, boolean state) {
        this.title = title;
        this.date = date;
        this.state = state;
    }

    public String getSalesId() {
        return salesId;
    }

    public void setSalesId(String salesId) {
        this.salesId = salesId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
