package com.mercadopago.uicontrollers.paymentmethodsearch;

import android.view.View;

import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by mreverter on 29/4/16.
 */
public interface PaymentMethodSearchViewController extends CustomViewController {
    void draw();
    void setOnClickListener(View.OnClickListener listener);
}
