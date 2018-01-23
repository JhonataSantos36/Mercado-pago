package com.mercadopago.plugins.model;

import android.support.annotation.NonNull;

import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;

/**
 * Created by nfortuna on 12/29/17.
 */

public class ProcessorPaymentResult {

    public final long paymentId;
    public final String status;
    public final String statusDetail;
    public final PaymentData paymentData;

    public ProcessorPaymentResult(final long paymentId,
                                  final @NonNull String status,
                                  final @NonNull String statusDetail,
                                  final @NonNull PaymentData paymentData) {

        this.paymentId = paymentId;
        this.status = status;
        this.statusDetail = processStatusDetail(status, statusDetail);
        this.paymentData = paymentData;
    }

    private String processStatusDetail(@NonNull final String status, @NonNull final String statusDetail) {
        if (Payment.StatusCodes.STATUS_APPROVED.equals(status)) {
            return Payment.StatusCodes.STATUS_DETAIL_APPROVED_PLUGIN_PM;
        } if (Payment.StatusCodes.STATUS_REJECTED.equals(status)) {
            if (Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER.equals(statusDetail)
                    || Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE.equals(statusDetail)
                    || Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE.equals(statusDetail)
                    || Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED.equals(statusDetail)
                    || Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER.equals(statusDetail)
                    || Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE.equals(statusDetail)) {
                return statusDetail;
            } else {
                return Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM;
            }
        }
        return statusDetail;
    }
}