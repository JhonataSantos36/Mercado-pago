package com.mercadopago.views;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.MvpView;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.PaymentPreference;

import java.math.BigDecimal;

/**
 * Created by vaserber on 2/1/17.
 */

public interface CheckoutActivityView extends MvpView {

    void showError(String message);

    void toPaymentMethodsSelection(Boolean discountsEnabled, Discount discount);

    void backToPaymentMethodsSelection(Boolean discountsEnabled, Discount discount);

    void showError(MercadoPagoError error);

    void initializeCheckout(CheckoutPreference checkoutPreference);
}
