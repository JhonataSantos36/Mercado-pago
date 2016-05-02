package com.mercadopago.uicontrollers;

import android.content.Context;

import com.mercadopago.model.PaymentMethodSearchItem;

/**
 * Created by mreverter on 29/4/16.
 */
public class ViewControllerFactory {
    public static PaymentMethodViewController getPaymentMethodSelectionViewController(PaymentMethodSearchItem item, Context context) {

        PaymentMethodViewController row;
        if(item.hasComment()) {
            row = new PaymentMethodSearchLargeRow(context);
        } else {
            row = new PaymentMethodSearchRegularRow(context);
        }
        return row;
    }

    public static PaymentMethodViewController getPaymentMethodEditionViewController(Context context) {
        return new PaymentMethodOffEditableRow(context);
    }
}
