package com.mercadopago.model;

import java.io.Serializable;

public class CheckoutIntent implements Serializable {

    private Item item;
    private String merchantAccessToken;

    public CheckoutIntent() {}

    public CheckoutIntent(String merchantAccessToken, Item item) {

        this.item = item;
        this.merchantAccessToken = merchantAccessToken;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getMerchantAccessToken() {
        return merchantAccessToken;
    }

    public void setMerchantAccessToken(String merchantAccessToken) {
        this.merchantAccessToken = merchantAccessToken;
    }
}
