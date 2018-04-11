package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.paymentresult.formatter.BodyAmountFormatter;

/**
 * Created by mromar on 11/22/17.
 */

public class PaymentMethodProps {

    public final PaymentMethod paymentMethod;
    public final PayerCost payerCost;
    public final Issuer issuer;
    public final Token token;
    public final String disclaimer;
    public final BodyAmountFormatter amountFormatter;
    public final Discount discount;

    public PaymentMethodProps(@NonNull final PaymentMethod paymentMethod,
                              @NonNull final PayerCost payerCost,
                              @NonNull final Issuer issuer,
                              @NonNull final Token token,
                              @NonNull final String disclaimer,
                              @NonNull final BodyAmountFormatter amountFormatter,
                              final Discount discount) {

        this.paymentMethod = paymentMethod;
        this.payerCost = payerCost;
        this.issuer = issuer;
        this.token = token;
        this.disclaimer = disclaimer;
        this.amountFormatter = amountFormatter;
        this.discount = discount;
    }

    public PaymentMethodProps(@NonNull final Builder builder) {
        paymentMethod = builder.paymentMethod;
        payerCost = builder.payerCost;
        issuer = builder.issuer;
        token = builder.token;
        disclaimer = builder.disclaimer;
        amountFormatter = builder.amountFormatter;
        discount = builder.discount;
    }

    public Builder toBuilder() {
        return new Builder()
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setIssuer(issuer)
                .setToken(token)
                .setDisclaimer(disclaimer)
                .setAmountFormatter(amountFormatter)
                .setDiscount(discount);
    }

    public static class Builder {

        public PaymentMethod paymentMethod;
        public PayerCost payerCost;
        public Issuer issuer;
        public Token token;
        public String disclaimer;
        public BodyAmountFormatter amountFormatter;
        public Discount discount;

        public Builder setPaymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder setPayerCost(PayerCost payerCost) {
            this.payerCost = payerCost;
            return this;
        }

        public Builder setIssuer(Issuer issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder setToken(Token token) {
            this.token = token;
            return this;
        }

        public Builder setDisclaimer(String disclaimer) {
            this.disclaimer = disclaimer;
            return this;
        }

        public Builder setAmountFormatter(BodyAmountFormatter amountFormatter) {
            this.amountFormatter = amountFormatter;
            return this;
        }

        public Builder setDiscount(Discount discount) {
            this.discount = discount;
            return this;
        }

        public PaymentMethodProps build() {
            return new PaymentMethodProps(this);
        }
    }
}
