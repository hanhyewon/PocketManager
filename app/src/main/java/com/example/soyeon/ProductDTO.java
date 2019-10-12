package com.example.soyeon;

public class ProductDTO {
    private String productId;
    private String name;
    private String price;
    private String imgUrl;

    public ProductDTO(String productId, String name, String price, String imgUrl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public ProductDTO(String name, String price, String imgUrl) {
        this.name = name;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
