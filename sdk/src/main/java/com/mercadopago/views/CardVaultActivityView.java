package com.mercadopago.views;

import com.mercadopago.model.ApiException;

/**
 * Created by vaserber on 10/12/16.
 */

public interface CardVaultActivityView {
    void onValidStart();
    void onInvalidStart(String message);
    void finishWithResult();
    void startErrorView(String message, String errorDetail);
    void startErrorView(String message);
    void showApiExceptionError(ApiException exception);
    void startInstallmentsActivity();
    void startIssuersActivity();
    void overrideTransitionHold();
    void overrideTransitionSlideOutIn();
}
