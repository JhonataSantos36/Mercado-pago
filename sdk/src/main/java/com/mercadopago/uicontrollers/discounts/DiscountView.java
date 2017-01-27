package com.mercadopago.uicontrollers.discounts;

import android.view.View;

import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by mromar on 1/19/17.
 */

public interface DiscountView extends CustomViewController {

    void draw();
    void setOnClickListener(View.OnClickListener listener);
}
