package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;

import com.mercadopago.callbacks.OnChangePaymentMethodCallback;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Site;
import com.mercadopago.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.uicontrollers.payercosts.PayerCostViewController;

import java.math.BigDecimal;

/**
 * Created by vaserber on 11/7/16.
 */

public class ReviewPaymentViewFactory {

    public static ReviewPaymentViewController getReviewPaymentMethodOnViewController(Context context, PaymentMethod paymentMethod,
                                                                                     CardInfo cardInfo, String currencyId, PayerCost payerCost,
                                                                                     OnChangePaymentMethodCallback callback,
                                                                                     boolean uniquePaymentMethod, DecorationPreference decorationPreference) {
        ReviewPaymentViewController controller = new ReviewPaymentOnView(context, paymentMethod, cardInfo, currencyId, payerCost, callback,
                uniquePaymentMethod, decorationPreference);
        return controller;
    }

    public static ReviewPaymentViewController getReviewPaymentMethodOffViewController(Context context, PaymentMethod paymentMethod,
                                                                                      BigDecimal amount, PaymentMethodSearchItem item,
                                                                                      String currencyId, Site site,
                                                                                      OnChangePaymentMethodCallback callback,
                                                                                      boolean uniquePaymentMethod, DecorationPreference decorationPreference) {
        ReviewPaymentViewController controller = new ReviewPaymentOffView(context, paymentMethod, amount, item, currencyId, site, callback,
                uniquePaymentMethod, decorationPreference);
        return controller;
    }

    public static PayerCostViewController getReviewPayerCostController(Context context, String currencyId) {
        PayerCostViewController controller = null;
        controller = new PayerCostColumn(context, currencyId);
        return controller;
    }
}
