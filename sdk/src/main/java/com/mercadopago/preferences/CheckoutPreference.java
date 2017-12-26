package com.mercadopago.preferences;

import com.google.gson.annotations.SerializedName;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.Sites;
import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.model.Item;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Site;
import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mercadopago.util.TextUtil.isEmpty;

public class CheckoutPreference {

    private String id;
    private List<Item> items;
    private Payer payer;

    @SerializedName("payment_methods")
    private PaymentPreference paymentPreference;

    private Date expirationDateTo;
    private Date expirationDateFrom;
    private String siteId;

    private Site localPreferenceSite;

    public CheckoutPreference(String checkoutPreferenceId) {
        this.id = checkoutPreferenceId;
    }

    private CheckoutPreference(Builder builder) {
        this.items = builder.items;
        this.expirationDateFrom = builder.expirationDateFrom;
        this.expirationDateTo = builder.expirationDateTo;
        this.localPreferenceSite = builder.localPreferenceSite;

        Payer payer = new Payer();
        payer.setEmail(builder.payerEmail);
        payer.setAccessToken(builder.payerAccessToken);
        this.payer = payer;

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(builder.excludedPaymentTypes);
        paymentPreference.setExcludedPaymentMethodIds(builder.excludedPaymentMethods);
        paymentPreference.setMaxAcceptedInstallments(builder.maxInstallments);
        paymentPreference.setDefaultInstallments(builder.defaultInstallments);
        this.paymentPreference = paymentPreference;
    }

    public void validate() throws CheckoutPreferenceException {
        if (this.hasId() && !this.itemsValid()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INVALID_ITEM);
        } else if (this.hasId() && !this.hasEmail()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.NO_EMAIL_FOUND);
        } else if (this.isExpired()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.EXPIRED_PREFERENCE);
        } else if (!this.isActive()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INACTIVE_PREFERENCE);
        } else if (!this.validInstallmentsPreference()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INVALID_INSTALLMENTS);
        } else if (!this.validPaymentTypeExclusion()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.EXCLUDED_ALL_PAYMENT_TYPES);
        }
    }

    private boolean hasEmail() {
        return this.payer != null && !isEmpty(this.payer.getEmail());
    }

    public boolean validPaymentTypeExclusion() {
        return paymentPreference == null || paymentPreference.excludedPaymentTypesValid();
    }

    public boolean validInstallmentsPreference() {
        return paymentPreference == null || paymentPreference.installmentPreferencesValid();
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
            return paymentPreference.getExcludedPaymentTypes();
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

    public Site getSite() {
        Site site = null;
        if (localPreferenceSite == null) {

            site = Sites.getById(siteId);

        } else {
            site = localPreferenceSite;
        }
        return site;
    }

    public boolean hasId() {
        return getId() != null;
    }

    public static class Builder {
        private List<Item> items;
        private List<String> excludedPaymentMethods;
        private List<String> excludedPaymentTypes;
        private Integer maxInstallments;
        private Integer defaultInstallments;
        private String payerEmail;
        private Date expirationDateTo;
        private Date expirationDateFrom;
        private Site localPreferenceSite;
        private String payerAccessToken;
        private boolean excludeAccountMoney = true;

        public Builder() {
            items = new ArrayList<>();
            excludedPaymentMethods = new ArrayList<>();
            excludedPaymentTypes = new ArrayList<>();
        }

        public Builder addItem(Item item) {
            if (item != null) {
                this.items.add(item);
            }
            return this;
        }

        public Builder addItems(List<Item> items) {
            if (items != null) {
                this.items.addAll(items);
            }
            return this;
        }

        public Builder addExcludedPaymentMethod(String paymentMethodId) {
            if (paymentMethodId != null) {
                this.excludedPaymentMethods.add(paymentMethodId);
            }
            return this;
        }

        public Builder addExcludedPaymentMethods(List<String> paymentMethodIds) {
            if (paymentMethodIds != null) {
                this.excludedPaymentMethods.addAll(paymentMethodIds);
            }
            return this;
        }

        public Builder addExcludedPaymentType(String paymentTypeId) {
            if (paymentTypeId != null) {
                this.excludedPaymentTypes.add(paymentTypeId);
            }
            return this;
        }

        public Builder addExcludedPaymentTypes(List<String> paymentTypeIds) {
            if (paymentTypeIds != null) {
                this.excludedPaymentTypes.addAll(paymentTypeIds);
            }
            return this;
        }

        public Builder setMaxInstallments(Integer maxInstallments) {
            this.maxInstallments = maxInstallments;
            return this;
        }

        public Builder setDefaultInstallments(Integer defaultInstallments) {
            this.defaultInstallments = defaultInstallments;
            return this;
        }

        public Builder setPayerEmail(String payerEmail) {
            this.payerEmail = payerEmail;
            return this;
        }

        public Builder setSite(Site site) {
            this.localPreferenceSite = site;
            return this;
        }

        public Builder setExpirationDate(Date date) {
            this.expirationDateTo = date;
            return this;
        }

        public Builder setActiveFrom(Date date) {
            this.expirationDateFrom = date;
            return this;
        }

        public Builder setPayerAccessToken(String payerAccessToken) {
            this.payerAccessToken = payerAccessToken;
            return this;
        }

        public Builder enableAccountMoney() {
            this.excludeAccountMoney = false;
            return this;
        }

        public CheckoutPreference build() {

            if (items == null || items.isEmpty())
                throw new IllegalStateException("Items required");
            if (localPreferenceSite == null)
                throw new IllegalStateException("Site is required");
            if (excludeAccountMoney) {
                addExcludedPaymentType(PaymentTypes.ACCOUNT_MONEY);
            }

            return new CheckoutPreference(this);
        }
    }
}
