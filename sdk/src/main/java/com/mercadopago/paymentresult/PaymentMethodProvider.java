package com.mercadopago.paymentresult;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mvp.ResourcesProvider;

public interface PaymentMethodProvider extends ResourcesProvider {

    int getIconResource(PaymentMethod paymentMethod);

    String getLastDigitsText();

    String getAccountMoneyText();

    String getDisclaimer(String statementDescription);
}
