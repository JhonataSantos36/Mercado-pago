package com.mercadopago.uicontrollers.card;

import com.mercadopago.model.PaymentMethod;

/**
 * Created by vaserber on 10/19/16.
 */

public interface BackCardViewController {
    void decorateCardBorder(int borderColor);
    void setPaymentMethod(PaymentMethod paymentMethod);
    void setSize(String size);
    void setSecurityCodeLength(int securityCodeLength);
    void draw();
    void hide();
    void show();
}
