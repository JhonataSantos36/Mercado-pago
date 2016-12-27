package com.mercadopago.uicontrollers.reviewandconfirm;

import com.mercadopago.model.CardInfo;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by vaserber on 12/6/16.
 */

public interface ReviewPaymentViewOnController extends CustomViewController {
    void drawPaymentMethod(PaymentMethod paymentMethod, CardInfo cardInfo, PayerCost payerCost,
                           String currencyId);
}
