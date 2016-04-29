package com.mercadopago.views;

import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.model.PaymentMethodSearchItem;

/**
 * Created by mreverter on 29/4/16.
 */
public interface PaymentMethodRow {
    void setFields(PaymentMethodSearchItem item);
    void initializeControls();
    View inflateInParent(ViewGroup parent);
    View getView();
}
