package com.example.gpdnj.pocketmanager;

public class OrderDTO {
   // private String orderId;
    private String name;
    private int price;
    private int amount;
    //private String date;
    //private int sum;

    //상품 클릭 시, 상품명/수량/선택취소 ListView 띄울 때 사용
    public OrderDTO(String name, int price, int amount) {
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
