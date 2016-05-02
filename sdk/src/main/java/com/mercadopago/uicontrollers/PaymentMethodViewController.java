package com.mercadopago.uicontrollers;

import android.view.View;

import com.mercadopago.model.PaymentMethodSearchItem;

/**
 * Created by mreverter on 29/4/16.
 */
public interface PaymentMethodViewController extends CustomViewController {
    void drawPaymentMethod(PaymentMethodSearchItem item);
    void setOnClickListener(View.OnClickListener listener);
}
