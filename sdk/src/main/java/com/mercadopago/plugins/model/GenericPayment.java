package com.mercadopago.plugins.model;

import android.support.annotation.NonNull;

import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;


public class GenericPayment implements PluginPayment {

    public final Long paymentId;
    public final String status;
    public final String statusDetail;
    public final PaymentData paymentData;

    public GenericPayment(final Long paymentId,
                          @NonNull final String status,
                          @NonNull final String statusDetail,
                          @NonNull final PaymentData paymentData) {

        this.paymentId = paymentId;
        this.status = status;
        this.statusDetail = processStatusDetail(status, statusDetail);
        this.paymentData = paymentData;
    }

    /**
     * Resolve the status type, it transforms a generic status and detail
     * into a known status detail
     * {@link Payment.StatusDetail }
     *
     * @param status       the payment status type
     * @param statusDetail the payment detail type
     * @return an status detail type
     */
    private String processStatusDetail(@NonNull final String status, @NonNull final String statusDetail) {

        if (Payment.StatusCodes.STATUS_APPROVED.equals(status)) {
            return Payment.StatusDetail.STATUS_DETAIL_APPROVED_PLUGIN_PM;
        }

        if (Payment.StatusCodes.STATUS_REJECTED.equals(status)) {

            if (Payment.StatusDetail.isKnownErrorDetail(statusDetail)) {
                return statusDetail;
            } else {
                return Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM;
            }
        }

        return statusDetail;
    }

    @Override
    public void process(final Processor processor) {
        processor.process(this);
    }
}