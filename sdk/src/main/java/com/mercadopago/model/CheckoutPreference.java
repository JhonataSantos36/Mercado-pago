package com.mercadopago.model;

import com.google.gson.annotations.SerializedName;
import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class CheckoutPreference {

    private String id;
    private List<Item> items;
    private Payer payer;

    @SerializedName("payment_methods")
    private PaymentPreference paymentPreference;

    private Date expirationDateTo;
    private Date expirationDateFrom;
    private String siteId;

    public void validate() throws CheckoutPreferenceException {
        if (!this.itemsValid()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INVALID_ITEM);
        } else if (this.isExpired()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.EXPIRED_PREFERENCE);
        } else if (!this.isActive()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INACTIVE_PREFERENCE);
        } else if (!this.validInstallmentsPreference()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INVALID_INSTALLMENTS);
        } else if (!this.validPaymentTypeExclusion()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.EXCLUDED_ALL_PAYMENT_TYPES);
        } else if (!this.hasEmail()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.NO_EMAIL_FOUND);
        }
    }

    private boolean hasEmail() {
        return this.payer != null && !isEmpty(this.payer.getEmail());
    }

    public boolean validPaymentTypeExclusion() {
        return paymentPreference == null || paymentPreference.excludedPaymentTypesValid();
    }

    public boolean validInstallmentsPreference() {
        return paymentPreference.installmentPreferencesValid();
    }

    public Boolean itemsValid() {

        boolean valid = true;

        if (this.items == null || this.items.isEmpty() || items.get(0) == null) {
            valid = false;
        } else if (isEmpty(items.get(0).getCurrencyId())) {
            valid = false;
        } else {
            String firstCurrencyId = items.get(0).getCurrencyId();
            String currentCurrencyId;

            for (Item item : items) {
                currentCurrencyId = item.getCurrencyId();
                if (!isItemValid(item) || !currentCurrencyId.equals(firstCurrencyId)) {
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }


    private boolean isItemValid(Item item) {
        Boolean valid = true;

        if (item == null) {
            valid = false;
        } else if (item.getId() == null) {
            valid = false;
        } else if (item.getQuantity() == null || item.getQuantity() < 1) {
            valid = false;
        } else if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            valid = false;
        } else if (item.getCurrencyId() == null) {
            valid = false;
        } else if ((!CurrenciesUtil.isValidCurrency(item.getCurrencyId()))) {
            valid = false;
        }
        return valid;
    }


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
        if (paymentPreference != null)
            return paymentPreference.getMaxInstallments();
        else
            return null;
    }

    public Integer getDefaultInstallments() {
        if (paymentPreference != null)
            return paymentPreference.getDefaultInstallments();
        else
            return null;
    }

    public List<String> getExcludedPaymentMethods() {
        if (paymentPreference != null)
            return paymentPreference.getExcludedPaymentMethodIds();
        else
            return null;
    }

    public List<String> getExcludedPaymentTypes() {
        if (paymentPreference != null)
            return paymentPreference.getExcludedPaymentTypes();
        else
            return null;
    }

    public String getDefaultPaymentMethodId() {
        if (paymentPreference != null)
            return paymentPreference.getDefaultPaymentMethodId();
        else
            return null;
    }

    public PaymentPreference getPaymentPreference() {
        return paymentPreference;
    }

    public String getSiteId() {
        return siteId;
    }
}
