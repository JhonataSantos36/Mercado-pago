package com.mercadopago.paymentresult;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.tracking.model.ScreenViewEvent;

/**
 * Created by vaserber on 10/27/17.
 */

public interface PaymentResultNavigator {

    void showApiExceptionError(ApiException exception, String requestOrigin);

    void showError(MercadoPagoError error, String requestOrigin);

    void openLink(String url);

    void finishWithResult(final int resultCode);

    void changePaymentMethod();

    void recoverPayment();

    void trackScreen(ScreenViewEvent event);
}
