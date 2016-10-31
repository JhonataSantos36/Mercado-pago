package com.mercadopago.views;

import com.mercadopago.model.ApiException;
import com.mercadopago.model.Issuer;

import java.util.List;

/**
 * Created by vaserber on 10/11/16.
 */

public interface IssuersActivityView {
    void onValidStart();
    void onInvalidStart(String message);
    void finishWithResult(Issuer issuer);
    void startErrorView(String message, String errorDetail);
    void showLoadingView();
    void stopLoadingView();
    void showApiExceptionError(ApiException exception);
    void initializeIssuers(List<Issuer> issuersList);
}
