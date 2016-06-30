package com.mercadopago.model;

import java.math.BigDecimal;

public class Item {

    private String categoryId;
    private String currencyId;
    private String description;
    private String id;
    private String pictureUrl;
    private Integer quantity;
    private String title;
    private BigDecimal unitPrice;

    public Item() {}

    public Item(String id, Integer quantity) {

        this.id = id;
        this.quantity = quantity;
    }

    public Item(String id, Integer quantity, BigDecimal unitPrice) {

        this.id = id;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
