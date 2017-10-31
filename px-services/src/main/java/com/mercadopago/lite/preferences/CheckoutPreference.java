package com.mercadopago.lite.preferences;

import com.google.gson.annotations.SerializedName;
import com.mercadopago.lite.model.Item;
import com.mercadopago.lite.model.Payer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CheckoutPreference {

    private String id;
    private List<Item> items;
    private Payer payer;

    @SerializedName("payment_methods")
    private PaymentPreference paymentPreference;

    private Date expirationDateTo;
    private Date expirationDateFrom;
    private String siteId;


    public Boolean isExpired() {
        Date date = new Date();
        return expirationDateTo != null && date.after(expirationDateTo);
    }

    public Boolean isActive() {
        Date date = new Date();
        return expirationDateFrom == null || date.after(expirationDateFrom);
    }


    public void setExpirationDate(Date date) {
        this.expirationDateTo = date;
    }

    public void setActiveFrom(Date date) {
        this.expirationDateFrom = date;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.paymentPreference = paymentPreference;
    }

    public BigDecimal getAmount() {

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (items != null) {
            for (Item item : items) {
                if ((item != null) && (item.getUnitPrice() != null) && (item.getQuantity() != null)) {
                    totalAmount = totalAmount.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
                } else {
                    return null;
                }
            }
        }
        return totalAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public Integer getMaxInstallments() {
        if (paymentPreference != null) {
            return paymentPreference.getMaxInstallments();
        } else {
            return null;
        }
    }

    public Integer getDefaultInstallments() {
        if (paymentPreference != null) {
            return paymentPreference.getDefaultInstallments();
        } else {
            return null;
        }
    }

    public List<String> getExcludedPaymentMethods() {
        if (paymentPreference != null) {
            return paymentPreference.getExcludedPaymentMethodIds();
        } else {
            return null;
        }
    }

    public List<String> getExcludedPaymentTypes() {
        if (paymentPreference != null)
            return paymentPreference.getExcludedPaymentTypeIds();
        else
            return null;
    }

    public String getDefaultPaymentMethodId() {
        if (paymentPreference != null) {
            return paymentPreference.getDefaultPaymentMethodId();
        } else {
            return null;
        }
    }

    public PaymentPreference getPaymentPreference() {
        return paymentPreference;
    }

    public String getSite() {
        return siteId;
    }

    public boolean hasId() {
        return getId() != null;
    }
}
