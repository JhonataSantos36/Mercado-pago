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

    private Site localPreferenceSite;

    //To support external integrations
    private String siteId;
    private BigDecimal marketplaceFee;
    private BigDecimal shippingCost;
    private String operationType;
    private Integer differentialPricingId;
    private BigDecimal conceptAmount;
    private String conceptId;

    public CheckoutPreference(String checkoutPreferenceId) {
        id = checkoutPreferenceId;
    }

    private CheckoutPreference(Builder builder) {
        items = builder.items;
        expirationDateFrom = builder.expirationDateFrom;
        expirationDateTo = builder.expirationDateTo;
        localPreferenceSite = builder.localPreferenceSite;
        marketplaceFee = builder.marketplaceFee;
        shippingCost = builder.shippingCost;
        operationType = builder.operationType;
        differentialPricingId = builder.differentialPricingId;
        conceptAmount = builder.conceptAmount;
        conceptId = builder.conceptId;

        final Payer payer = new Payer();
        payer.setEmail(builder.payerEmail);
        payer.setAccessToken(builder.payerAccessToken);
        this.payer = payer;

        final PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(builder.excludedPaymentTypes);
        paymentPreference.setExcludedPaymentMethodIds(builder.excludedPaymentMethods);
        paymentPreference.setMaxAcceptedInstallments(builder.maxInstallments);
        paymentPreference.setDefaultInstallments(builder.defaultInstallments);
        this.paymentPreference = paymentPreference;
    }

    public void validate() throws CheckoutPreferenceException {
        if (hasId() && !itemsValid()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INVALID_ITEM);
        } else if (hasId() && !hasEmail()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.NO_EMAIL_FOUND);
        } else if (isExpired()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.EXPIRED_PREFERENCE);
        } else if (!isActive()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INACTIVE_PREFERENCE);
        } else if (!validInstallmentsPreference()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.INVALID_INSTALLMENTS);
        } else if (!validPaymentTypeExclusion()) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.EXCLUDED_ALL_PAYMENT_TYPES);
        }
    }

    private boolean hasEmail() {
        return payer != null && !isEmpty(payer.getEmail());
    }

    public boolean validPaymentTypeExclusion() {
        return paymentPreference == null || paymentPreference.excludedPaymentTypesValid();
    }

    public boolean validInstallmentsPreference() {
        return paymentPreference == null || paymentPreference.installmentPreferencesValid();
    }

    public Boolean itemsValid() {

        boolean valid = true;

        if (items == null || items.isEmpty() || items.get(0) == null) {
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

    public String getOperationType() {
        return operationType;
    }

    public BigDecimal getMarketplaceFee() {
        return marketplaceFee;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setExpirationDate(Date date) {
        expirationDateTo = date;
    }

    public void setActiveFrom(Date date) {
        expirationDateFrom = date;
    }

    public Site getLocalPreferenceSite() {
        return localPreferenceSite;
    }

    public String getSiteId() {
        return siteId;
    }

    public Integer getDifferentialPricingId() {
        return differentialPricingId;
    }

    public BigDecimal getConceptAmount() {
        return conceptAmount;
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setExpirationDateTo(final Date expirationDateTo) {
        this.expirationDateTo = expirationDateTo;
    }

    public void setExpirationDateFrom(final Date expirationDateFrom) {
        this.expirationDateFrom = expirationDateFrom;
    }

    public void setLocalPreferenceSite(final Site localPreferenceSite) {
        this.localPreferenceSite = localPreferenceSite;
    }

    public void setSiteId(final String siteId) {
        this.siteId = siteId;
    }

    public void setOperationType(final String operationType) {
        this.operationType = operationType;
    }

    public void setDifferentialPricingId(final Integer differentialPricingId) {
        this.differentialPricingId = differentialPricingId;
    }

    public void setConceptAmount(final BigDecimal conceptAmount) {
        this.conceptAmount = conceptAmount;
    }

    public void setConceptId(final String conceptId) {
        this.conceptId = conceptId;
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

    public void setMarketplaceFee(final BigDecimal marketplaceFee) {
        this.marketplaceFee = marketplaceFee;
    }

    public void setShippingCost(final BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
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

    public Date getExpirationDateTo() {
        return expirationDateTo;
    }

    public Date getExpirationDateFrom() {
        return expirationDateFrom;
    }

    public static class Builder {
        private final List<Item> items;
        private final List<String> excludedPaymentMethods;
        private final List<String> excludedPaymentTypes;
        private Integer maxInstallments;
        private Integer defaultInstallments;
        private String payerEmail;
        private Date expirationDateTo;
        private Date expirationDateFrom;
        private Site localPreferenceSite;
        private String payerAccessToken;
        private boolean excludeAccountMoney = true;
        private BigDecimal marketplaceFee;
        private BigDecimal shippingCost;
        private String operationType;
        private Integer differentialPricingId;
        private BigDecimal conceptAmount;
        private String conceptId;

        public Builder() {
            items = new ArrayList<>();
            excludedPaymentMethods = new ArrayList<>();
            excludedPaymentTypes = new ArrayList<>();
        }

        public Builder addItem(Item item) {
            if (item != null) {
                items.add(item);
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
                excludedPaymentMethods.add(paymentMethodId);
            }
            return this;
        }

        public Builder addExcludedPaymentMethods(List<String> paymentMethodIds) {
            if (paymentMethodIds != null) {
                excludedPaymentMethods.addAll(paymentMethodIds);
            }
            return this;
        }

        public Builder addExcludedPaymentType(String paymentTypeId) {
            if (paymentTypeId != null) {
                excludedPaymentTypes.add(paymentTypeId);
            }
            return this;
        }

        public Builder addExcludedPaymentTypes(List<String> paymentTypeIds) {
            if (paymentTypeIds != null) {
                excludedPaymentTypes.addAll(paymentTypeIds);
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
            localPreferenceSite = site;
            return this;
        }

        public Builder setExpirationDate(Date date) {
            expirationDateTo = date;
            return this;
        }

        public Builder setActiveFrom(Date date) {
            expirationDateFrom = date;
            return this;
        }

        public Builder setPayerAccessToken(String payerAccessToken) {
            this.payerAccessToken = payerAccessToken;
            return this;
        }

        public Builder enableAccountMoney() {
            excludeAccountMoney = false;
            return this;
        }

        public Builder setMarketplaceFee(final BigDecimal marketplaceFee) {
            this.marketplaceFee = marketplaceFee;
            return this;
        }

        public Builder setShippingCost(final BigDecimal shippingCost) {
            this.shippingCost = shippingCost;
            return this;
        }

        public Builder setOperationType(final String operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder setDifferentialPricingId(final Integer differentialPricingId) {
            this.differentialPricingId = differentialPricingId;
            return this;
        }

        public Builder setConceptAmount(final BigDecimal conceptAmount) {
            this.conceptAmount = conceptAmount;
            return this;
        }

        public Builder setConceptId(final String conceptId) {
            this.conceptId = conceptId;
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
