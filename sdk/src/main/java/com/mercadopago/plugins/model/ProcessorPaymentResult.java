package com.mercadopago.plugins.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.mercadopago.model.Payment;

/**
 * Created by nfortuna on 12/29/17.
 */

public class ProcessorPaymentResult {

    public final long paymentId;
    public final String status;
    public final String statusDetail;
    public final String paymentMethodId;
    public final String paymentMethodName;
    public final @DrawableRes int paymentMethodIcon;

    public ProcessorPaymentResult(final long paymentId,
                                  final @NonNull String status,
                                  final @NonNull String statusDetail,
                                  final @NonNull String paymentMethodId,
                                  final @NonNull String paymentMethodName,
                                  final int paymentMethodIcon) {

        this.paymentId = paymentId;
        this.status = status;
        this.statusDetail = processStatusDetail(status, statusDetail);
        this.paymentMethodId = paymentMethodId;
        this.paymentMethodName = paymentMethodName;
        this.paymentMethodIcon = paymentMethodIcon;
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