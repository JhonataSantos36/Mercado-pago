package com.mercadopago.uicontrollers;

import android.content.Context;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Token;
import com.mercadopago.uicontrollers.payercosts.PayerCostEditableRow;
import com.mercadopago.uicontrollers.payercosts.PayerCostViewController;
import com.mercadopago.uicontrollers.paymentmethods.PaymentMethodCardEditableRow;
import com.mercadopago.uicontrollers.paymentmethods.PaymentMethodOffEditableRow;
import com.mercadopago.uicontrollers.paymentmethods.PaymentMethodViewController;
import com.mercadopago.uicontrollers.paymentmethodsearch.PaymentMethodSearchLargeRow;
import com.mercadopago.uicontrollers.paymentmethodsearch.PaymentMethodSearchRegularRow;
import com.mercadopago.uicontrollers.paymentmethodsearch.PaymentMethodSearchViewController;
import com.mercadopago.util.MercadoPagoUtil;

/**
 * Created by mreverter on 29/4/16.
 */
public class ViewControllerFactory {
    public static PaymentMethodSearchViewController getPaymentMethodSelectionViewController(PaymentMethodSearchItem item, Context context) {

        PaymentMethodSearchViewController row;
        if(item.hasComment()) {
            row = new PaymentMethodSearchLargeRow(context);
        } else {
            row = new PaymentMethodSearchRegularRow(context);
        }
        return row;
    }

    public static PaymentMethodViewController getPaymentMethodOnEditionViewController(Context context, PaymentMethod paymentMethod, Token token) {
        return new PaymentMethodCardEditableRow(context, paymentMethod, token);
    }


    public static PaymentMethodViewController getPaymentMethodOffEditionViewController(Context context, PaymentMethodSearchItem item) {
        return new PaymentMethodOffEditableRow(context, item);
    }

    public static PayerCostViewController getPayerCostEditionViewController(Context context, String currencyId) {
        return new PayerCostEditableRow(context, currencyId);
    }
}
