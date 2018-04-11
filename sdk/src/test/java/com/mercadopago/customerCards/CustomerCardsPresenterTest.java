package com.mercadopago.customerCards;

import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.mocks.Cards;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.presenters.CustomerCardsPresenter;
import com.mercadopago.providers.CustomerCardsProvider;
import com.mercadopago.views.CustomerCardsView;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mromar on 4/17/17.
 */

public class CustomerCardsPresenterTest {

    @Test
    public void whenCardsAreNullThenGetCustomerAndShowCards() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Card> cards = Cards.getCardsMLA();
        provider.setResponse(cards);

        CustomerCardsPresenter presenter = new CustomerCardsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        assertFalse(mockedView.progressShown);
        assertTrue(mockedView.cardsShown);
    }

    @Test
    public void whenCardsAreNotNullThenShowCards() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Card> cards = Cards.getCardsMLA();

        CustomerCardsPresenter presenter = new CustomerCardsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setCards(cards);

        presenter.initialize();

        assertFalse(mockedView.progressShown);
        assertTrue(mockedView.cardsShown);
    }

    @Test
    public void whenGetCustomerFailThenShowMercadoPagoError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        MercadoPagoError mercadoPagoError = new MercadoPagoError("Error", true);
        provider.setResponse(mercadoPagoError);

        CustomerCardsPresenter presenter = new CustomerCardsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        assertFalse(mockedView.progressShown);
        assertTrue(mockedView.errorShown);
    }

    @Test
    public void whenPressOnACardAndSelectionConfirmPromptTextIsNullThenFinishWithCardResult() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Card> cards = Cards.getCardsMLA();

        CustomerCardsPresenter presenter = new CustomerCardsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setCards(cards);

        presenter.initialize();

        mockedView.simulateCardSelection(0);

        assertEquals(cards.get(0), mockedView.selectedCard);
        assertTrue(mockedView.finishWithCardResult);
    }

    @Test
    public void whenPressOnACardAndSelectionConfirmPromptTextIsNotNullThenShowAlertDialog() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Card> cards = Cards.getCardsMLA();

        CustomerCardsPresenter presenter = new CustomerCardsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setCards(cards);
        presenter.setSelectionConfirmPromptText("Select");

        presenter.initialize();

        mockedView.simulateCardSelection(0);

        assertEquals(cards.get(0), mockedView.selectedCard);
        assertTrue(mockedView.alertDialogShown);
    }

    @Test
    public void whenPressOnActionMessageThenFinishWithOkResult() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Card> cards = Cards.getCardsMLA();

        CustomerCardsPresenter presenter = new CustomerCardsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setCards(cards);
        presenter.setCustomActionMessage("Action message");

        presenter.initialize();

        mockedView.simulateActionMessageSelection();

        assertTrue(mockedView.finishWithOkResult);
    }


    private class MockedProvider implements CustomerCardsProvider {

        private boolean shouldFail;
        private List<Card> successfulResponse;
        private MercadoPagoError failedResponse;

        private void setResponse(List<Card> cards) {
            shouldFail = false;
            successfulResponse = cards;
        }

        private void setResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        @Override
        public void getCustomer(TaggedCallback<Customer> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                Customer customer = new Customer();
                customer.setCards(successfulResponse);
                taggedCallback.onSuccess(customer);
            }
        }

        @Override
        public String getLastDigitsLabel() {
            return null;
        }

        @Override
        public String getConfirmPromptYes() {
            return null;
        }

        @Override
        public String getConfirmPromptNo() {
            return null;
        }

        @Override
        public int getIconDialogAlert() {
            return 0;
        }
    }

    private class MockedView implements CustomerCardsView {

        private boolean cardsShown = false;
        private boolean progressShown = false;
        private boolean errorShown = false;
        private boolean alertDialogShown = false;
        private boolean finishWithCardResult = false;
        private boolean finishWithOkResult = false;

        private Card selectedCard;
        private List<Card> cardsOptionsShown;
        private OnSelectedCallback<Card> cardSelectionCallback;

        @Override
        public void showCards(List<Card> cards, String actionMessage, OnSelectedCallback<Card> onSelectedCallback) {
            cardsOptionsShown = cards;
            cardSelectionCallback = onSelectedCallback;

            this.cardsShown = true;
        }

        @Override
        public void showConfirmPrompt(Card card) {
            this.alertDialogShown = true;
        }

        @Override
        public void showProgress() {
            this.progressShown = true;
        }

        @Override
        public void hideProgress() {
            this.progressShown = false;
        }

        @Override
        public void showError(MercadoPagoError error, String requestOrigin) {
            this.errorShown = true;
        }

        @Override
        public void finishWithCardResult(Card card) {
            this.finishWithCardResult = true;
        }

        @Override
        public void finishWithOkResult() {
            this.finishWithOkResult = true;
        }

        private void simulateCardSelection(int index) {
            this.selectedCard = cardsOptionsShown.get(index);
            cardSelectionCallback.onSelected(selectedCard);
        }

        private void simulateActionMessageSelection() {
            cardSelectionCallback.onSelected(null);
        }
    }
}
