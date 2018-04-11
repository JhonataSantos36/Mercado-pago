package com.mercadopago.issuers;

import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.mocks.Issuers;
import com.mercadopago.mocks.PaymentMethods;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.presenters.IssuersPresenter;
import com.mercadopago.providers.IssuersProvider;
import com.mercadopago.views.IssuersActivityView;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mromar on 5/3/17.
 */

public class IssuersPresenterTest {

    @Test
    public void whenIssuersAreNullThenGetIssuersAndShow() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Issuer> issuers = Issuers.getIssuersListMLA();
        provider.setResponse(issuers);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setPaymentMethod(paymentMethod);

        presenter.initialize();

        mockedView.simulateIssuerSelection(0);

        assertTrue(mockedView.issuersShown);
        assertTrue(mockedView.headerShown);
        assertEquals(issuers.get(0), mockedView.selectedIssuer);
        assertTrue(mockedView.finishWithResult);
    }

    @Test
    public void whenGetIssuersHaveOneIssuerThenFinishWithResult() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Issuer> issuers = Issuers.getOneIssuerListMLA();
        provider.setResponse(issuers);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setPaymentMethod(paymentMethod);

        presenter.initialize();

        assertFalse(mockedView.issuersShown);
        assertFalse(mockedView.headerShown);
        assertEquals(mockedView.selectedIssuer, issuers.get(0));
        assertTrue(mockedView.finishWithResult);
    }

    @Test
    public void whenIssuersAreNotNullThenShowIssuers() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Issuer> issuers = Issuers.getIssuersListMLA();
        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setIssuers(issuers);
        presenter.setPaymentMethod(paymentMethod);

        presenter.initialize();

        mockedView.simulateIssuerSelection(0);

        assertTrue(mockedView.issuersShown);
        assertTrue(mockedView.headerShown);
        assertEquals(issuers.get(0), mockedView.selectedIssuer);
        assertTrue(mockedView.finishWithResult);
    }

    @Test
    public void whenGetIssuersIsNullThenGetNewIssuersList() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Issuer> issuers = new ArrayList<Issuer>();
        provider.setResponse(issuers);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setPaymentMethod(paymentMethod);

        presenter.initialize();

        assertTrue(mockedView.errorShown);
    }

    @Test
    public void whenGetIssuersFailThenShowMercadoPagoError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        MercadoPagoError mercadoPagoError = new MercadoPagoError("Error", true);
        provider.setResponse(mercadoPagoError);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setPaymentMethod(paymentMethod);

        presenter.initialize();

        assertFalse(mockedView.loadingViewShown);
        assertTrue(mockedView.errorShown);
    }

    @Test
    public void whenGetIssuersReturnNullThenShowMercadoPagoError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Issuer> issuers = null;
        provider.setResponse(issuers);
        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setPaymentMethod(paymentMethod);

        presenter.initialize();

        assertFalse(mockedView.loadingViewShown);
        assertTrue(mockedView.errorShown);
        assertTrue(provider.emptyIssuersErrorGotten);
    }

    @Test
    public void whenRecoverFromFailureThenGetIssuersAgain() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        MercadoPagoError mercadoPagoError = new MercadoPagoError("Error", true);
        provider.setResponse(mercadoPagoError);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setPaymentMethod(paymentMethod);

        presenter.initialize();

        presenter.recoverFromFailure();

        assertFalse(mockedView.loadingViewShown);
        assertTrue(mockedView.errorShown);
        assertNotEquals(presenter.getFailureRecovery(), null);
    }

    @Test
    public void whenRecoverFromFailureIsNullThenNotRecoverFromError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.recoverFromFailure();

        assertEquals(presenter.getFailureRecovery(), null);
    }

    @Test
    public void whenSetCardInfoThenSetBin() {
        CardInfo cardInfo = getCardInfo();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.setCardInfo(cardInfo);

        assertEquals(presenter.getBin(), getCardInfo().getFirstSixDigits());
    }

    @Test
    public void whenCardInfoIsNullThenPresenterBinIsEmpty() {
        IssuersPresenter presenter = new IssuersPresenter();
        presenter.setCardInfo(null);

        assertEquals(presenter.getBin(), "");
    }

    @Test
    public void whenIsCardInfoAndPaymentMethodAvailableThenIsNotRequiredCardDrawn() {
        CardInfo cardInfo = getCardInfo();
        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.setCardInfo(cardInfo);
        presenter.setPaymentMethod(paymentMethod);

        assertTrue(presenter.isRequiredCardDrawn());
    }

    @Test
    public void whenIsNotPaymentMethodAvailableThenIsNotRequiredCardDrawn() {
        CardInfo cardInfo = getCardInfo();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.setCardInfo(cardInfo);

        assertFalse(presenter.isRequiredCardDrawn());
    }

    @Test
    public void whenIsNotCardInfoAvailableThenIsNotRequiredCardDrawn() {
        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();

        IssuersPresenter presenter = new IssuersPresenter();
        presenter.setPaymentMethod(paymentMethod);

        assertFalse(presenter.isRequiredCardDrawn());
    }

    private CardInfo getCardInfo() {
        Card card = new Card();
        card.setLastFourDigits("4321");
        card.setFirstSixDigits("123456");

        return new CardInfo(card);
    }

    private class MockedProvider implements IssuersProvider {

        private boolean shouldFail;
        private List<Issuer> successfulResponse;
        private MercadoPagoError failedResponse;

        private boolean emptyIssuersErrorGotten = false;

        private void setResponse(List<Issuer> issuers) {
            shouldFail = false;
            successfulResponse = issuers;
        }

        private void setResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        @Override
        public void getIssuers(String paymentMethodId, String bin, TaggedCallback<List<Issuer>> taggedCallback) {

            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulResponse);
            }
        }

        @Override
        public MercadoPagoError getEmptyIssuersError() {
            this.emptyIssuersErrorGotten = true;
            return null;
        }

        @Override
        public String getCardIssuersTitle() {
            return null;
        }
    }

    private class MockedView implements IssuersActivityView {

        private boolean issuersShown = false;
        private boolean headerShown = false;
        private boolean loadingViewShown = false;
        private boolean errorShown = false;
        private boolean finishWithResult = false;
        private Issuer selectedIssuer;
        private OnSelectedCallback<Integer> issuerSelectionCallback;

        @Override
        public void showIssuers(List<Issuer> issuers, OnSelectedCallback<Integer> onSelectedCallback) {
            this.issuerSelectionCallback = onSelectedCallback;
            this.issuersShown = true;
        }

        @Override
        public void showHeader() {
            this.headerShown = true;
        }

        @Override
        public void showLoadingView() {
            this.loadingViewShown = true;
        }

        @Override
        public void stopLoadingView() {
            this.loadingViewShown = false;
        }

        @Override
        public void showError(MercadoPagoError error, String requestOrigin) {
            this.errorShown = true;
        }

        @Override
        public void finishWithResult(Issuer issuer) {
            this.finishWithResult = true;
            this.selectedIssuer = issuer;
        }

        private void simulateIssuerSelection(int index) {
            issuerSelectionCallback.onSelected(index);
        }
    }
}
