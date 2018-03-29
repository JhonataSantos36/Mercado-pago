package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

import com.mercadopago.core.CheckoutStore;
import com.mercadopago.lite.model.Instruction;
import com.mercadopago.model.PaymentData;
import com.mercadopago.paymentresult.formatter.BodyAmountFormatter;

/**
 * Created by vaserber on 10/23/17.
 */

public class PaymentResultBodyProps {

    public final String status;
    public final String statusDetail;
    public final Instruction instruction;
    public final PaymentData paymentData;
    public final String processingMode;
    public final String disclaimer;
    public final Long paymentId;
    public final BodyAmountFormatter bodyAmountFormatter;

    public PaymentResultBodyProps(@NonNull final Builder builder) {
        status = builder.status;
        statusDetail = builder.statusDetail;
        instruction = builder.instruction;
        paymentData = builder.paymentData;
        disclaimer = builder.disclaimer;
        processingMode = builder.processingMode;
        paymentId = builder.paymentId;
        bodyAmountFormatter = builder.bodyAmountFormatter;
    }

    public Builder toBuilder() {
        return new Builder()
                .setStatus(status)
                .setStatusDetail(statusDetail)
                .setInstruction(instruction)
                .setPaymentData(paymentData)
                .setDisclaimer(disclaimer)
                .setProcessingMode(processingMode)
                .setPaymentId(paymentId)
                .setBodyAmountFormatter(bodyAmountFormatter);
    }

    public boolean isReceiptEnabled() {
        return CheckoutStore.getInstance().getPaymentResultScreenPreference() == null ||
                CheckoutStore.getInstance().getPaymentResultScreenPreference().isApprovedReceiptEnabled();
    }

    public static class Builder {

        public String status;
        public String statusDetail;
        public Instruction instruction;
        public PaymentData paymentData;
        public String disclaimer;
        public String processingMode;
        public Long paymentId;
        public BodyAmountFormatter bodyAmountFormatter;

        public Builder setStatus(@NonNull final String status) {
            this.status = status;
            return this;
        }

        public Builder setStatusDetail(@NonNull final String statusDetail) {
            this.statusDetail = statusDetail;
            return this;
        }

        public Builder setInstruction(final Instruction instruction) {
            this.instruction = instruction;
            return this;
        }

        public Builder setPaymentData(final PaymentData paymentData) {
            this.paymentData = paymentData;
            return this;
        }

        public Builder setDisclaimer(String disclaimer) {
            this.disclaimer = disclaimer;
            return this;
        }

        public Builder setProcessingMode(final String processingMode) {
            this.processingMode = processingMode;
            return this;
        }

        public Builder setPaymentId(Long paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder setBodyAmountFormatter(BodyAmountFormatter bodyAmountFormatter) {
            this.bodyAmountFormatter = bodyAmountFormatter;
            return this;
        }

        public PaymentResultBodyProps build() {
            return new PaymentResultBodyProps(this);
        }
    }
}
