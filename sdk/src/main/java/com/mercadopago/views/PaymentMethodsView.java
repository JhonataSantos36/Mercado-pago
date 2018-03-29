package com.mercadopago.views;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.mvp.MvpView;

import java.util.List;

/**
 * Created by mreverter on 1/5/17.
 */
public interface PaymentMethodsView extends MvpView{
    void showPaymentMethods(List<PaymentMethod> paymentMethods);
    void showProgress();
    void hideProgress();
    void showError(MercadoPagoError exception);
    void showBankDeals();
}
