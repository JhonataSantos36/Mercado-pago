package com.mercadopago.presenters;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.providers.ReviewPaymentMethodsProvider;
import com.mercadopago.views.ReviewPaymentMethodsView;

import java.util.List;

/**
 * Created by vaserber on 8/17/17.
 */

public class ReviewPaymentMethodsPresenter extends MvpPresenter<ReviewPaymentMethodsView, ReviewPaymentMethodsProvider> {

    private List<PaymentMethod> mSupportedPaymentMethods;

    public void initialize() {
        try {
            validateParameters();
            onValidStart();
        } catch (IllegalStateException exception) {
            getView().showError(new MercadoPagoError(exception.getMessage(), false), "");
        }
    }

    public void setSupportedPaymentMethods(List<PaymentMethod> supportedPaymentMethods) {
        mSupportedPaymentMethods = supportedPaymentMethods;
    }

    private void validateParameters() throws IllegalStateException {
        if (mSupportedPaymentMethods == null || mSupportedPaymentMethods.isEmpty()) {
            throw new IllegalStateException(getResourcesProvider().getEmptyPaymentMethodsListError());
        }
    }

    private void onValidStart() {
        getView().initializeSupportedPaymentMethods(mSupportedPaymentMethods);
    }
}
