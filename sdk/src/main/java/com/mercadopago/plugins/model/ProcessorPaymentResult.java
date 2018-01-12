package com.mercadopago.plugins.model;

import android.support.annotation.DrawableRes;

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
                                  final String status,
                                  final String statusDetail,
                                  final String paymentMethodId,
                                  final String paymentMethodName,
                                  final int paymentMethodIcon) {

        this.paymentId = paymentId;
        this.status = status;
        this.statusDetail = statusDetail;
        this.paymentMethodId = paymentMethodId;
        this.paymentMethodName = paymentMethodName;
        this.paymentMethodIcon = paymentMethodIcon;
    }
}