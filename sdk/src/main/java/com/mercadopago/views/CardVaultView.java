package com.mercadopago.views;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.mvp.MvpView;

/**
 * Created by vaserber on 10/12/16.
 */

public interface CardVaultView extends MvpView {

    void finishWithResult();

    void showApiExceptionError(ApiException exception, String requestOrigin);

    void showError(MercadoPagoError mercadoPagoError, String requestOrigin);

    void askForInstallments();

    void startIssuersActivity();

    void startSecurityCodeActivity();

    void showProgressLayout();

    void askForCardInformation();

    void askForSecurityCodeFromTokenRecovery();

    void askForSecurityCodeFromInstallments();

    void askForSecurityCodeWithoutInstallments();

    void askForInstallmentsFromIssuers();

    void askForInstallmentsFromNewCard();

    void cancelCardVault();

}
