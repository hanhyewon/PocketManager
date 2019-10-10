package com.example.gpdnj.pocketmanager;

public class OrderFirstDTO {
    private String orderId;
    private String date;
    private int sum;
    private String pay;

    public OrderFirstDTO(String orderId, String date, int sum, String pay) {
        this.orderId = orderId;
        this.date = date;
        this.sum = sum;
        this.pay = pay;
    }

    public OrderFirstDTO(String date, int sum, String pay) {
        this.date = date;
        this.sum = sum;
        this.pay = pay;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }
}
