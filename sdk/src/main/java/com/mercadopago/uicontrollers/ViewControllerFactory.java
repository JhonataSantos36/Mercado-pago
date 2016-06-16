package com.mercadopago.uicontrollers;

import android.content.Context;

import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Token;
import com.mercadopago.uicontrollers.payercosts.PayerCostEditableRow;
import com.mercadopago.uicontrollers.payercosts.PayerCostViewController;
import com.mercadopago.uicontrollers.paymentmethods.PaymentMethodCardEditableRow;
import com.mercadopago.uicontrollers.paymentmethods.PaymentMethodOffEditableRow;
import com.mercadopago.uicontrollers.paymentmethods.PaymentMethodViewController;
import com.mercadopago.uicontrollers.paymentmethodsearch.PaymentMethodSearchRow;
import com.mercadopago.uicontrollers.paymentmethodsearch.PaymentMethodSearchSmallRow;
import com.mercadopago.uicontrollers.paymentmethodsearch.PaymentMethodSearchViewController;

/**
 * Created by mreverter on 29/4/16.
 */
public class ViewControllerFactory {

    public static PaymentMethodSearchViewController getPaymentMethodSelectionViewController(PaymentMethodSearchItem item, DecorationPreference mDecorationPreference, Context context) {

        PaymentMethodSearchViewController row;
        if(item.hasComment() && !item.isPaymentType()) {
            row = new PaymentMethodSearchRow(context);
        } else {
            row = new PaymentMethodSearchSmallRow(context, mDecorationPreference);
        }
        return row;
    }

    public static PaymentMethodViewController getPaymentMethodOnEditionViewController(Context context, PaymentMethod paymentMethod, Token token) {
        return new PaymentMethodCardEditableRow(context, paymentMethod, token);
    }

    public static PaymentMethodViewController getPaymentMethodOffEditionViewController(Context context, PaymentMethod paymentMethod) {
        return new PaymentMethodOffEditableRow(context, paymentMethod);
    }

    public static PaymentMethodViewController getPaymentMethodOffEditionViewController(Context context, PaymentMethodSearchItem item) {
        return new PaymentMethodOffEditableRow(context, item);
    }

    public static PayerCostViewController getPayerCostEditionViewController(Context context, String currencyId) {
        return new PayerCostEditableRow(context, currencyId);
    }

}
