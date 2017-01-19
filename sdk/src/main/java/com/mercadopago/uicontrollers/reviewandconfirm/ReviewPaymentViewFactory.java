package com.mercadopago.uicontrollers.reviewandconfirm;

import android.content.Context;

import com.mercadopago.callbacks.OnChangePaymentMethodCallback;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.model.Site;
import com.mercadopago.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.uicontrollers.payercosts.PayerCostViewController;

/**
 * Created by vaserber on 11/7/16.
 */

public class ReviewPaymentViewFactory {

    public static ReviewPaymentViewOnController getReviewPaymentMethodOnViewController(Context context,
                                                                                        OnChangePaymentMethodCallback callback,
                                                                                        boolean uniquePaymentMethod, DecorationPreference decorationPreference) {
        ReviewPaymentViewOnController controller = new ReviewPaymentOnView(context, callback, uniquePaymentMethod, decorationPreference);
        return controller;
    }

    public static ReviewPaymentViewOffController getReviewPaymentMethodOffViewController(Context context, Site site,
                                                                                         OnChangePaymentMethodCallback callback,
                                                                                         boolean uniquePaymentMethod, DecorationPreference decorationPreference) {
        ReviewPaymentViewOffController controller = new ReviewPaymentOffView(context, site, callback, uniquePaymentMethod, decorationPreference);
        return controller;
    }

    public static PayerCostViewController getReviewPayerCostController(Context context, String currencyId) {
        PayerCostViewController controller = null;
        controller = new PayerCostColumn(context, currencyId);
        return controller;
    }
}
