package com.mercadopago.callbacks;

import com.mercadopago.model.PaymentMethodSearchItem;

/**
 * Created by mreverter on 18/1/16.
 */
public interface PaymentMethodSearchCallback {

    void onGroupItemClicked(PaymentMethodSearchItem groupItem);

    void onPaymentTypeItemClicked(PaymentMethodSearchItem paymentTypeItem);

    void onPaymentMethodItemClicked(PaymentMethodSearchItem paymentMethodItem);

}
