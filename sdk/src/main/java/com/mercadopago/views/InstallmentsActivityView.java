package com.mercadopago.views;

import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.PayerCost;
import com.mercadopago.mvp.MvpView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 9/29/16.
 */

public interface InstallmentsActivityView extends MvpView {
    void showInstallments(List<PayerCost> payerCostList, OnSelectedCallback<Integer> onSelectedCallback);

    void startDiscountFlow(BigDecimal transactionAmount);

    void finishWithResult(PayerCost payerCost);

    void showLoadingView();

    void hideLoadingView();

    void showError(MercadoPagoError error, String requestOrigin);

    void showHeader();

    void showDiscountRow(BigDecimal transactionAmount);

    void initInstallmentsReviewView(PayerCost payerCost);

    void hideInstallmentsRecyclerView();

    void showInstallmentsRecyclerView();

    void hideInstallmentsReviewView();

    void showInstallmentsReviewView();

    void warnAboutBankInterests();
}
