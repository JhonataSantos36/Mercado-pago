package com.mercadopago.views;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.hooks.Hook;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.mvp.MvpView;

public interface CheckoutView extends MvpView {

    void showError(MercadoPagoError error);

    void showProgress();

    void showReviewAndConfirm();

    void showPaymentMethodSelection();

    void startPaymentMethodEdition();

    void showPaymentResult(PaymentResult paymentResult);

    void backToReviewAndConfirm();

    void backToPaymentMethodSelection();

    void finishWithPaymentResult();

    void finishWithPaymentResult(Integer customResultCode);

    void finishWithPaymentResult(Payment payment);

    void finishWithPaymentResult(Integer customResultCode, Payment payment);

    void finishWithPaymentDataResult(PaymentData paymentData, Boolean paymentMethodEdited);

    void cancelCheckout();

    void cancelCheckout(MercadoPagoError mercadoPagoError);

    void cancelCheckout(Integer customResultCode, PaymentData paymentData, Boolean paymentMethodEdited);

    void startPaymentRecoveryFlow(PaymentRecovery paymentRecovery);

    void initializeMPTracker();

    void trackScreen();

    void finishFromReviewAndConfirm();

    void showHook(final Hook hook, final int requestCode);
}
