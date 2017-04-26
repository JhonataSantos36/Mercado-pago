package com.mercadopago.views;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.exceptions.MercadoPagoError;
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

    void showSelectedItem(PaymentMethodSearchItem item);

    void showProgress();

    void hideProgress();

    void showCustomOptions(List<CustomSearchItem> customSearchItems, OnSelectedCallback<CustomSearchItem> customSearchItemOnSelectedCallback);

    void showSearchItems(List<PaymentMethodSearchItem> searchItems, OnSelectedCallback<PaymentMethodSearchItem> paymentMethodSearchItemSelectionCallback);

    void showError(MercadoPagoError mercadoPagoError);

    void setTitle(String title);

    void startCardFlow(String paymentType, BigDecimal transactionAmount, Boolean automaticallySelection);

    void startPaymentMethodsSelection();

    void selectPaymentMethod(PaymentMethod selectedPaymentMethod);

    void showDiscount(BigDecimal transactionAmount);

    void startDiscountFlow(BigDecimal transactionAmount);

    void cleanPaymentMethodOptions();
}
