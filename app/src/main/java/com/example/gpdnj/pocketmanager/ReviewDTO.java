package com.example.gpdnj.pocketmanager;

public class ReviewDTO {
    private String reviewId;
    private String uid;
    private String userName;
    private String category;
    private String title;
    private String eventName; //행사일 때만
    private String location;
    private String salesDate;
    private float rating;
    private String detailText;
    private String reviewDate;

    //Review Main Activity
    public ReviewDTO(String reviewId, String uid, String category, String title, String reviewDate, String detailText, String location) {
        this.reviewId = reviewId;
        this.uid = uid;
        this.category = category;
        this.title = title;
        this.reviewDate = reviewDate;
        this.detailText = detailText;
        this.location = location;
    }

    //Review Add Activity (행사명 포함)
    public ReviewDTO(String uid, String userName, String category, String title, String eventName, String location, String salesDate, float rating, String detailText, String reviewDate) {
        this.uid = uid;
        this.userName = userName;
        this.category = category;
        this.title = title;
        this.eventName = eventName;
        this.location = location;
        this.salesDate = salesDate;
        this.rating = rating;
        this.detailText = detailText;
        this.reviewDate = reviewDate;
    }

    //Review Add Activity
    public ReviewDTO(String uid, String userName, String category, String title, String location, String salesDate,  float rating, String detailText, String reviewDate) {
        this.uid = uid;
        this.userName = userName;
        this.category = category;
        this.title = title;
        this.location = location;
        this.salesDate = salesDate;
        this.rating = rating;
        this.detailText = detailText;
        this.reviewDate = reviewDate;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalesDate() {
        return salesDate;
    }

    public void setSalesDate(String salesDate) {
        this.salesDate = salesDate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDetailText() {
        return detailText;
    }

    public void setDetailText(String detailText) {
        this.detailText = detailText;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }
}
