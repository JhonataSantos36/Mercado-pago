package com.mercadopago.guessing;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.mocks.PaymentMethods;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.presenters.ReviewPaymentMethodsPresenter;
import com.mercadopago.providers.ReviewPaymentMethodsProvider;
import com.mercadopago.views.ReviewPaymentMethodsView;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by vaserber on 8/24/17.
 */

public class ReviewPaymentMethodsPresenterTest {

    @Test
    public void testShowSupportedPaymentMethodsList() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();

        ReviewPaymentMethodsPresenter presenter = new ReviewPaymentMethodsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSupportedPaymentMethods(paymentMethodList);
        presenter.initialize();

        assertTrue(mockedView.initialized);
        assertEquals(mockedView.supportedPaymentMethodsCount, paymentMethodList.size());
    }

    @Test
    public void initializeWithEmptySupportedPaymentMethodsListThenShowError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = new ArrayList<>();

        ReviewPaymentMethodsPresenter presenter = new ReviewPaymentMethodsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSupportedPaymentMethods(paymentMethodList);
        presenter.initialize();

        assertFalse(mockedView.initialized);
        assertTrue(mockedView.errorShown);
        assertEquals(mockedView.errorMessage, provider.getEmptyPaymentMethodsListError());
    }

    private class MockedView implements ReviewPaymentMethodsView {

        private boolean initialized;
        private boolean errorShown;
        private String errorMessage;
        private int supportedPaymentMethodsCount;

        @Override
        public void showError(MercadoPagoError error, String requestOrigin) {
            errorShown = true;
            errorMessage = error.getMessage();
        }

        @Override
        public void initializeSupportedPaymentMethods(List<PaymentMethod> supportedPaymentMethods) {
            initialized = true;
            supportedPaymentMethodsCount = supportedPaymentMethods.size();
        }
    }

    private class MockedProvider implements ReviewPaymentMethodsProvider {

        private static final String EMPTY_PAYMENT_METHOD_LIST = "empty payment methods";
        private static final String STANDARD_ERROR_MESSAGE = "oops something went wrong";

        @Override
        public String getEmptyPaymentMethodsListError() {
            return EMPTY_PAYMENT_METHOD_LIST;
        }

        @Override
        public String getStandardErrorMessage() {
            return STANDARD_ERROR_MESSAGE;
        }
    }
}
