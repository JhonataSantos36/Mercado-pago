package com.mercadopago.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import com.mercadopago.lite.util.CurrenciesUtil;

import java.math.BigDecimal;

public class Discount implements Parcelable {

    private String id;
    private String name;
    private String currencyId;
    private String couponCode;
    private String concept;
    private String campaignId;
    private BigDecimal percentOff;
    private BigDecimal amountOff;
    private BigDecimal couponAmount;

    @VisibleForTesting
    public Discount() {
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public BigDecimal getAmountOff() {
        return amountOff;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPercentOff() {
        return percentOff;
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
        return concept;
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


    protected Discount(Parcel in) {
        id = in.readString();
        name = in.readString();
        currencyId = in.readString();
        couponCode = in.readString();
        concept = in.readString();
        campaignId = in.readString();
        percentOff = new BigDecimal(in.readString());
        amountOff = new BigDecimal(in.readString());
        couponAmount = new BigDecimal(in.readString());
    }

    public static final Creator<Discount> CREATOR = new Creator<Discount>() {
        @Override
        public Discount createFromParcel(Parcel in) {
            return new Discount(in);
        }

        @Override
        public Discount[] newArray(int size) {
            return new Discount[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(currencyId);
        dest.writeString(couponCode);
        dest.writeString(concept);
        dest.writeString(campaignId);
        dest.writeString(percentOff.toString());
        dest.writeString(amountOff.toString());
        dest.writeString(couponAmount.toString());
    }
}
