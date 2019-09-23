package com.example.gpdnj.pocketmanager;

public class EventDTO {
    private String eventId;
    private String title;
    private String subTitle;
    private String date;
    //private String location;
    private String detailText;
    private String imgUrl;

    //조회할 때
    public EventDTO (String eventId, String title, String subTitle, String date, String detailText, String imgUrl) {
        this.eventId = eventId;
        this.title = title;
        this.subTitle = subTitle;
        this.date = date;
        this.detailText = detailText;
        this.imgUrl = imgUrl;
    }

    //등록할 때
    public EventDTO (String title, String subTitle, String date, String detailText, String imgUrl) {
        this.title = title;
        this.subTitle = subTitle;
        this.date = date;
        this.detailText = detailText;
        this.imgUrl = imgUrl;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public String getDetailText() {
        return detailText;
    }

    public void setDetailText(String detailText) {
        this.detailText = detailText;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
