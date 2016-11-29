package com.mercadopago.views;

import android.content.Context;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearchItem;

import java.util.List;

/**
 * Created by mreverter on 6/9/16.
 */
public interface PaymentVaultView {

    void startSavedCardFlow(Card card);

    void restartWithSelectedItem(PaymentMethodSearchItem groupIem);

    Context getContext();

    void showProgress();

    void hideProgress();

    void showApiException(ApiException apiException);

    void showCustomOptions(List<CustomSearchItem> customSearchItems, OnSelectedCallback<CustomSearchItem> customSearchItemOnSelectedCallback);

    void showSearchItems(List<PaymentMethodSearchItem> searchItems, OnSelectedCallback<PaymentMethodSearchItem> paymentMethodSearchItemSelectionCallback);

    void showError(MPException mpException);

    void setTitle(String title);

    void setFailureRecovery(FailureRecovery failureRecovery);

    void startCardFlow();

    void startPaymentMethodsActivity();

    void selectPaymentMethod(PaymentMethod selectedPaymentMethod);
}
