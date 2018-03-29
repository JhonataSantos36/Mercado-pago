package com.mercadopago.views;

import com.mercadopago.lite.exceptions.CardTokenException;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.mvp.MvpView;

/**
 * Created by vaserber on 10/26/16.
 */

public interface SecurityCodeActivityView extends MvpView {
    void setSecurityCodeInputMaxLength(int length);

    void showError(MercadoPagoError error, String requestOrigin);

    void setErrorView(CardTokenException exception);

    void clearErrorView();

    void showLoadingView();

    void stopLoadingView();

    void showApiExceptionError(ApiException exception, String requestOrigin);

    void finishWithResult();

    void initialize();

    void showTimer();

    void trackScreen();

    void showBackSecurityCodeCardView();

    void showFrontSecurityCodeCardView();

}
