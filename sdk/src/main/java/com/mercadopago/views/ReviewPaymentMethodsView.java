package com.mercadopago.views;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.mvp.MvpView;

import java.util.List;

/**
 * Created by vaserber on 8/17/17.
 */

public interface ReviewPaymentMethodsView extends MvpView {

    void showError(MercadoPagoError error, String requestOrigin);

    void initializeSupportedPaymentMethods(List<PaymentMethod> supportedPaymentMethods);
}
