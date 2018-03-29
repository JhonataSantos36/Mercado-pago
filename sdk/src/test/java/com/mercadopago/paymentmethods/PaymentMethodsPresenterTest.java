package com.mercadopago.paymentmethods;

import com.mercadopago.lite.model.PaymentTypes;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.lite.preferences.PaymentPreference;
import com.mercadopago.presenters.PaymentMethodsPresenter;
import com.mercadopago.providers.PaymentMethodsProvider;
import com.mercadopago.views.PaymentMethodsView;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 1/5/17.
 */

public class PaymentMethodsPresenterTest {

    @Test
    public void whenPaymentMethodsPresenterStartsShowPaymentMethods() {


        MockedView mockedView = new MockedView();
        MockedResourcesProvider resourcesProvider = new MockedResourcesProvider();

        PaymentMethodsPresenter presenter = new PaymentMethodsPresenter();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(resourcesProvider);

        presenter.start();

        Assert.assertTrue(mockedView.paymentMethods.size() == 2);
        Assert.assertTrue(!mockedView.progressVisible);
        Assert.assertTrue(!mockedView.bankDealsShown);
        Assert.assertTrue(mockedView.error == null);
    }

    @Test
    public void whenPaymentTypeExcludedDoNotShowIt() {

        MockedView mockedView = new MockedView();
        MockedResourcesProvider resourcesProvider = new MockedResourcesProvider();

        PaymentMethodsPresenter presenter = new PaymentMethodsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(resourcesProvider);

        PaymentPreference paymentPreference = new PaymentPreference();

        List<String> paymentTypes = new ArrayList<>();
        paymentTypes.add(PaymentTypes.CREDIT_CARD);

        paymentPreference.setExcludedPaymentTypeIds(paymentTypes);

        presenter.setPaymentPreference(paymentPreference);
        presenter.start();

        Assert.assertTrue(mockedView.paymentMethods.size() == 1);
        Assert.assertTrue(!mockedView.progressVisible);
        Assert.assertTrue(!mockedView.bankDealsShown);
        Assert.assertTrue(mockedView.error == null);

    }

    private class MockedView implements PaymentMethodsView {

        public List<PaymentMethod> paymentMethods;
        private boolean progressVisible;
        private MercadoPagoError error;
        private boolean bankDealsShown;

        @Override
        public void showPaymentMethods(List<PaymentMethod> paymentMethods) {
            this.paymentMethods = paymentMethods;
        }

        @Override
        public void showProgress() {
            this.progressVisible = true;
        }

        @Override
        public void hideProgress() {
            this.progressVisible = false;
        }

        @Override
        public void showError(MercadoPagoError exception) {
            this.error = exception;
        }

        @Override
        public void showBankDeals() {
            this.bankDealsShown = true;
        }
    }

    private class MockedResourcesProvider implements PaymentMethodsProvider {

        @Override
        public void getPaymentMethods(TaggedCallback<List<PaymentMethod>> resourcesRetrievedCallback) {
            List<PaymentMethod> paymentMethods = new ArrayList<>();

            PaymentMethod paymentMethod1 = new PaymentMethod();
            paymentMethod1.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

            PaymentMethod paymentMethod2 = new PaymentMethod();
            paymentMethod2.setPaymentTypeId(PaymentTypes.DEBIT_CARD);

            paymentMethods.add(paymentMethod1);
            paymentMethods.add(paymentMethod2);
            resourcesRetrievedCallback.onSuccess(paymentMethods);
        }
    }
}
