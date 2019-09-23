package com.example.gpdnj.pocketmanager;

public class EventDTO {
    //private String uid;
    //private String eventId;
    private String title;
    private String subTitle;
    private String date;
    private String location;
    private String detailText;
    //private String imgUrl;

    //테스트용 생성자
    public EventDTO (String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    public EventDTO(String title, String date, String location, String detailText) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.detailText = detailText;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetailText() {
        return detailText;
    }

    public void setDetailText(String detailText) {
        this.detailText = detailText;
    }
}
