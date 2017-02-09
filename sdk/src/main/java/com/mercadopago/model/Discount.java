package com.mercadopago.model;

import java.math.BigDecimal;

public class Discount {

    private Long id;
    private String name;
    private BigDecimal percentOff;
    private BigDecimal amountOff;
    private BigDecimal couponAmount;
    private String currencyId;
    private String couponCode;

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

    public BigDecimal getAmountWithDiscount(BigDecimal amount) {
        return amount.subtract(couponAmount);
    }

    public Boolean hasPercentOff() {
        return !percentOff.equals(new BigDecimal(0));
    }
}
