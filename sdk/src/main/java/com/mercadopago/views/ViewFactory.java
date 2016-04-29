package com.mercadopago.views;

import android.content.Context;

import com.mercadopago.model.PaymentMethodSearchItem;

/**
 * Created by mreverter on 29/4/16.
 */
public class ViewFactory {
    public static PaymentMethodRow getPaymentMethodSearchItemRow(PaymentMethodSearchItem item, Context context) {

        PaymentMethodRow row;
        if(item.hasComment()) {
            row = new PaymentMethodLargeRow(context);
        } else {
            row = new PaymentMethodRegularRow(context);
        }
        return row;
    }

    public static PaymentMethodRow getPaymentMethodEditableRow(Context context) {
        return new PaymentMethodEditableRow(context);
    }
}
