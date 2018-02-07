package com.mercadopago.model;

import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;

public class Discount {

    private Long id;
    private String name;
    private BigDecimal percentOff;
    private BigDecimal amountOff;
    private BigDecimal couponAmount;
    private String currencyId;
    private String couponCode;
    private String concept;
    private String campaignId;

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public BigDecimal getAmountOff() {
        return amountOff;
    }

    public BigDecimal getCouponAmount() {
        return this.couponAmount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPercentOff() {
        return this.percentOff;
    }

    public void setPercentOff(BigDecimal percentOff) {
        this.percentOff = percentOff;
    }

    public BigDecimal getAmountWithDiscount(BigDecimal amount) {
        return amount.subtract(couponAmount);
    }

    public void setAmountOff(BigDecimal amountOff) {
        this.amountOff = amountOff;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public Boolean hasPercentOff() {
        return percentOff != null && !percentOff.equals(new BigDecimal(0));
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getConcept() {
        return this.concept;
    }

    public boolean isValid() {
        return isDiscountCurrencyIdValid() && isAmountValid(couponAmount) && id != null;
    }

    private Boolean isDiscountCurrencyIdValid() {
        return currencyId != null && CurrenciesUtil.isValidCurrency(currencyId);
    }

    private Boolean isAmountValid(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(final String campaignId) {
        this.campaignId = campaignId;
    }
}
