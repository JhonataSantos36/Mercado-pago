package com.mercadopago.views;

import com.mercadopago.model.ApiException;

/**
 * Created by vaserber on 10/26/16.
 */

public interface SecurityCodeActivityView {
    void onValidStart();
    void onInvalidStart(String message);
    void setSecurityCodeInputMaxLength(int length);
    void setErrorView(String message);
    void clearErrorView();
    void showLoadingView();
    void stopLoadingView();
    void showApiExceptionError(ApiException exception);
    void finishWithResult();
}
