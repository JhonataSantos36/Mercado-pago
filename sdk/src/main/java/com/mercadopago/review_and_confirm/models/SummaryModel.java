package com.mercadopago.review_and_confirm.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Site;

import java.math.BigDecimal;
import java.util.List;

import static com.mercadopago.util.TextUtils.isEmpty;

/**
 * Created by mromar on 3/2/18.
 */

public class SummaryModel implements Parcelable {

    private final String amount;
    public final String currencyId;
    public final String siteId;
    public final String paymentTypeId;
    private final String payerCostTotalAmount;
    private final String installments;
    public final String cftPercent;
    private final String couponAmount;
    public final boolean hasPercentOff;
    private final String installmentsRate;
    private final String installmentAmount;
    public final String title;

    public SummaryModel(BigDecimal amount,
                        PaymentMethod paymentMethod,
                        Site site,
                        PayerCost payerCost,
                        Discount discount,
                        String title) {

        this.amount = amount.toString();
        this.currencyId = site.getCurrencyId();
        this.siteId = site.getId();
        this.paymentTypeId = paymentMethod.getPaymentTypeId();
        this.payerCostTotalAmount = payerCost != null && payerCost.getTotalAmount() != null ? payerCost.getTotalAmount().toString() : null;
        this.installments = payerCost != null && payerCost.getInstallments() != null ? payerCost.getInstallments().toString() : null;
        this.cftPercent = payerCost != null && payerCost.getCFTPercent() != null ? payerCost.getCFTPercent() : null;
        this.couponAmount = discount != null ? discount.getCouponAmount().toString() : null;
        this.hasPercentOff = discount != null ? discount.hasPercentOff() : false;
        this.installmentsRate = payerCost != null && payerCost.getInstallmentRate() != null ? payerCost.getInstallmentRate().toString() : null;
        this.installmentAmount = payerCost != null && payerCost.getInstallmentAmount() != null ? payerCost.getInstallmentAmount().toString() : null;
        this.title = title;
    }

    protected SummaryModel(Parcel in) {
        amount = in.readString();
        currencyId = in.readString();
        siteId = in.readString();
        paymentTypeId = in.readString();
        payerCostTotalAmount = in.readString();
        installments = in.readString();
        cftPercent = in.readString();
        couponAmount = in.readString();
        hasPercentOff = in.readByte() != 0;
        installmentsRate = in.readString();
        installmentAmount = in.readString();
        title = in.readString();
    }

    public static final Creator<SummaryModel> CREATOR = new Creator<SummaryModel>() {
        @Override
        public SummaryModel createFromParcel(Parcel in) {
            return new SummaryModel(in);
        }

        @Override
        public SummaryModel[] newArray(int size) {
            return new SummaryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(amount);
        dest.writeString(currencyId);
        dest.writeString(siteId);
        dest.writeString(paymentTypeId);
        dest.writeString(payerCostTotalAmount);
        dest.writeString(installments);
        dest.writeString(cftPercent);
        dest.writeString(couponAmount);
        dest.writeByte((byte) (hasPercentOff ? 1 : 0));
        dest.writeString(installmentsRate);
        dest.writeString(installmentAmount);
        dest.writeString(title);
    }

    public BigDecimal getTotalAmount() {
        return new BigDecimal(this.amount);
    }

    public BigDecimal getPayerCostTotalAmount() {
        return this.payerCostTotalAmount != null ? new BigDecimal(this.payerCostTotalAmount) : null;
    }

    public BigDecimal getCouponAmount() {
        return this.couponAmount != null ? new BigDecimal(this.couponAmount) : null;
    }

    public BigDecimal getInstallmentsRate() {
        return this.installmentsRate != null ? new BigDecimal(this.installmentsRate) : null;
    }

    public BigDecimal getInstallmentAmount() {
        return this.installmentAmount != null ? new BigDecimal(this.installmentAmount) : null;
    }

    public Integer getInstallments() {
        return this.installments != null ? Integer.valueOf(this.installments) : null;
    }

    public static String resolveTitle(List<Item> items, String singularTitle, String pluralTitle) {
        String title;

        if (items.size() == 1) {
            if (isEmpty(items.get(0).getTitle())) {
                if (items.get(0).getQuantity() > 1) {
                    title = pluralTitle;
                } else {
                    title = singularTitle;
                }
            } else {
                title = items.get(0).getTitle();
            }
        } else {
            title = pluralTitle;
        }

        return title;
    }
}

