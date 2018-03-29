package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

import com.mercadopago.lite.model.Discount;
import com.mercadopago.lite.model.PayerCost;
import com.mercadopago.paymentresult.formatter.BodyAmountFormatter;

/**
 * Created by mromar on 11/28/17.
 */

public class TotalAmountProps {

    public final BodyAmountFormatter amountFormatter;
    public final PayerCost payerCost;
    public final Discount discount;

    public TotalAmountProps(BodyAmountFormatter amountFormatter, PayerCost payerCost, Discount discount) {
        this.amountFormatter = amountFormatter;
        this.payerCost = payerCost;
        this.discount = discount;
    }

    public TotalAmountProps(@NonNull final Builder builder) {
        amountFormatter = builder.amountFormatter;
        payerCost = builder.payerCost;
        discount = builder.discount;
    }

    public Builder toBuilder() {
        return new Builder()
                .setAmountFormatter(amountFormatter)
                .setPayerCost(payerCost)
                .setDiscount(discount);
    }

    public static class Builder {

        public BodyAmountFormatter amountFormatter;
        public PayerCost payerCost;
        public Discount discount;

        public Builder setAmountFormatter(BodyAmountFormatter amountFormatter) {
            this.amountFormatter = amountFormatter;
            return this;
        }

        public Builder setPayerCost(PayerCost payerCost) {
            this.payerCost = payerCost;
            return this;
        }

        public Builder setDiscount(Discount discount) {
            this.discount = discount;
            return this;
        }

        public TotalAmountProps build() {
            return new TotalAmountProps(this);
        }
    }
}
