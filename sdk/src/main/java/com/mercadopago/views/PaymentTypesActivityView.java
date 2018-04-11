package com.mercadopago.views;

import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.model.PaymentType;

import java.util.List;

/**
 * Created by vaserber on 10/25/16.
 */

public interface PaymentTypesActivityView {
    void startErrorView(String message, String errorDetail);

    void onValidStart();

    void onInvalidStart(String message);

    void initializePaymentTypes(List<PaymentType> paymentTypes);

    void showApiExceptionError(ApiException exception, String requestOrigin);

    void showLoadingView();

    void stopLoadingView();

    void finishWithResult(PaymentType paymentType);
}
