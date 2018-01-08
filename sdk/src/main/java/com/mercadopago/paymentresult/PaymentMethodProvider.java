package com.mercadopago.paymentresult;

import android.graphics.drawable.Drawable;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mvp.ResourcesProvider;

public interface PaymentMethodProvider extends ResourcesProvider {

    Drawable getImage(PaymentMethod paymentMethod);

    String getLastDigitsText();

    String getAccountMoneyText();

    String getDisclaimer(String statementDescription);
}
