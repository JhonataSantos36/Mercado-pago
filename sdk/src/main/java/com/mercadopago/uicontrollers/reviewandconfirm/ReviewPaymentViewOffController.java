package com.mercadopago.uicontrollers.reviewandconfirm;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.uicontrollers.CustomViewController;

import java.math.BigDecimal;

/**
 * Created by vaserber on 11/7/16.
 */

public interface ReviewPaymentViewOffController extends CustomViewController {
    void drawPaymentMethod(PaymentMethod paymentMethod, BigDecimal amount, PaymentMethodSearchItem item, String currencyId);
}
