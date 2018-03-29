package com.mercadopago.review_and_confirm.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.mercadopago.lite.model.Discount;
import com.mercadopago.lite.model.Item;
import com.mercadopago.lite.model.PayerCost;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.lite.model.Site;

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
        currencyId = site.getCurrencyId();
        siteId = site.getId();
        paymentTypeId = paymentMethod.getPaymentTypeId();
        payerCostTotalAmount = payerCost != null && payerCost.getTotalAmount() != null ? payerCost.getTotalAmount().toString() : null;
        installments = payerCost != null && payerCost.getInstallments() != null ? payerCost.getInstallments().toString() : null;
        cftPercent = payerCost != null && payerCost.getCFTPercent() != null ? payerCost.getCFTPercent() : null;
        couponAmount = discount != null ? discount.getCouponAmount().toString() : null;
        hasPercentOff = discount != null ? discount.hasPercentOff() : false;
        installmentsRate = payerCost != null && payerCost.getInstallmentRate() != null ? payerCost.getInstallmentRate().toString() : null;
        installmentAmount = payerCost != null && payerCost.getInstallmentAmount() != null ? payerCost.getInstallmentAmount().toString() : null;
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
        return new BigDecimal(amount);
    }

    public BigDecimal getPayerCostTotalAmount() {
        return payerCostTotalAmount != null ? new BigDecimal(payerCostTotalAmount) : null;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount != null ? new BigDecimal(couponAmount) : null;
    }

    public BigDecimal getInstallmentsRate() {
        return installmentsRate != null ? new BigDecimal(installmentsRate) : null;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount != null ? new BigDecimal(installmentAmount) : null;
    }

    public Integer getInstallments() {
        return installments != null ? Integer.valueOf(installments) : null;
    }

    public boolean hasMultipleInstallments() {
        return getInstallments() != null && getInstallments() > 1;
    }

    public boolean hasCoupon() {
        return getCouponAmount() != null;
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

