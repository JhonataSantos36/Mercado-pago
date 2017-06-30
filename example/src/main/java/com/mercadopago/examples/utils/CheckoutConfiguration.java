package com.mercadopago.examples.utils;

import com.google.gson.annotations.SerializedName;

import com.mercadopago.constants.Sites;
import com.mercadopago.model.Item;
import com.mercadopago.model.Site;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.TextUtil;

import java.util.List;

public class CheckoutConfiguration {
    private String startFor;
    private String prefId;
    private String publicKey;
    private List<Item> items;
    private String payerEmail;
    private String siteId;
    private ServicePreference servicePreference;

    @SerializedName(value = "timer")
    private Integer time;

    @SerializedName(value = "show_max_saved_cards")
    private Integer maxSavedCards;

    @SerializedName(value = "show_max_saved_cards_string")
    private String maxSavedCardsString;

    public String getStartFor() {
        return startFor;
    }

    public String getPrefId() {
        return prefId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public boolean paymentRequired() {
        return "payment".equals(startFor);
    }

    public boolean paymentDataRequired() {
        return "payment_data".equals(startFor);
    }

    public List<Item> getItems() {
        return items;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public Site getSite() {
        return TextUtil.isEmpty(siteId) ? null : Sites.getById(siteId);
    }

    public Integer getTime() {
        return time;
    }

    public Integer getMaxSavedCards() {
        return maxSavedCards;
    }

    public String getMaxSavedCardsString() {
        return maxSavedCardsString;
    }

    public boolean timerSet() {
        return time != null && time != 0;
    }

    public boolean maxSavedCardsSet() {
        return maxSavedCards != null || !TextUtil.isEmpty(maxSavedCardsString);
    }

    public ServicePreference getServicePreference() {
        return servicePreference;
    }
}
