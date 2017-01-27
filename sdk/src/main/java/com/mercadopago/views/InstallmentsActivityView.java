package com.mercadopago.views;

import com.mercadopago.model.ApiException;
import com.mercadopago.model.PayerCost;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 9/29/16.
 */

public interface InstallmentsActivityView {
    void onValidStart();
    void onInvalidStart(String message);
    void finishWithResult(PayerCost payerCost);
    void startErrorView(String message, String errorDetail);
    void showLoadingView();
    void stopLoadingView();
    void showHeader();
    void showApiExceptionError(ApiException exception);
    void initializeInstallments(List<PayerCost> payerCostList);
    void showDiscountRow(BigDecimal transactionAmount);
    void startDiscountActivity(BigDecimal transactionAmount);
}
