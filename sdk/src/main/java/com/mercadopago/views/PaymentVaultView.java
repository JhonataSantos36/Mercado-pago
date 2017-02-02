package com.mercadopago.views;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.mvp.MvpView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mreverter on 6/9/16.
 */
public interface PaymentVaultView extends MvpView {

    void startSavedCardFlow(Card card, BigDecimal transactionAmount);

    void restartWithSelectedItem(PaymentMethodSearchItem item);

    void showProgress();

    void hideProgress();

    void showCustomOptions(List<CustomSearchItem> customSearchItems, OnSelectedCallback<CustomSearchItem> customSearchItemOnSelectedCallback);

    void showSearchItems(List<PaymentMethodSearchItem> searchItems, OnSelectedCallback<PaymentMethodSearchItem> paymentMethodSearchItemSelectionCallback);

    void showError(MPException mpException);

    void setTitle(String title);

    void setFailureRecovery(FailureRecovery failureRecovery);

    void startCardFlow(String paymentType, BigDecimal transactionAmount);

    void startPaymentMethodsActivity();

    void selectPaymentMethod(PaymentMethod selectedPaymentMethod);

    void showDiscountRow(BigDecimal transactionAmount);

    void startDiscountActivity(BigDecimal transactionAmount);

    void cleanPaymentMethodOptions();
}
