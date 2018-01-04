package com.mercadopago.plugins.model;

/**
 * Created by nfortuna on 12/29/17.
 */

public class PluginPaymentResult {

    public final long paymentId;
    public final String status;
    public final String statusDetail;
    public final PaymentMethodInfo paymentMethodInfo;

    public PluginPaymentResult(long paymentId, String status, String statusDetail, PaymentMethodInfo paymentMethodInfo) {
        this.paymentId = paymentId;
        this.status = status;
        this.statusDetail = statusDetail;
        this.paymentMethodInfo = paymentMethodInfo;
    }
}