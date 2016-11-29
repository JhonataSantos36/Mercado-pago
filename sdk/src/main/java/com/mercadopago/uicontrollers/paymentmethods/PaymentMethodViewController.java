package com.mercadopago.uicontrollers.paymentmethods;

import android.view.View;

import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by mreverter on 12/5/16.
 */
public interface PaymentMethodViewController extends CustomViewController{
    void draw();
    void showSeparator();
    void setOnClickListener(View.OnClickListener listener);
}
