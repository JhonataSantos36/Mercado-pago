package com.mercadopago.uicontrollers.paymentmethodsearch;

import android.view.View;

import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by mreverter on 29/4/16.
 */
public interface PaymentMethodSearchViewController extends CustomViewController {
    void drawPaymentMethod(PaymentMethodSearchItem item);
    void showSeparator();
    void setOnClickListener(View.OnClickListener listener);
}
