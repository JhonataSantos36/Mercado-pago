package com.mercadopago.reviewandconfirm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.ReviewKeys;
import com.mercadopago.constants.Sites;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.ReviewSubscriber;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.presenters.ReviewAndConfirmPresenter;
import com.mercadopago.providers.ReviewAndConfirmProvider;
import com.mercadopago.views.ReviewAndConfirmView;

import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReviewAndConfirmPresenterTest {

    @Test
    public void ifPaymentMethodNotSetShowError() {
        PayerCost payerCost = new PayerCost();

        Token token = Mockito.mock(Token.class);
        Mockito.when(token.getLastFourDigits()).thenReturn("1234");

        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();

        presenter.setPayerCost(payerCost);
        presenter.setToken(token);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.initialize();

        assertTrue(view.errorMessage != null && !view.errorMessage.isEmpty());
    }

    @Test
    public void ifSiteNotSetShowError() {
        PayerCost payerCost = new PayerCost();
        Token token = Mockito.mock(Token.class);
        Mockito.when(token.getLastFourDigits()).thenReturn("1234");

        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        PaymentMethod paymentMethod = new PaymentMethod();

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();

        presenter.setPayerCost(payerCost);
        presenter.setToken(token);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setPaymentMethod(paymentMethod);

        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.initialize();

        assertTrue(view.errorMessage != null && !view.errorMessage.isEmpty());
    }

    @Test
    public void ifItemsNotSetShowError() {
        PayerCost payerCost = new PayerCost();
        Token token = Mockito.mock(Token.class);
        Mockito.when(token.getLastFourDigits()).thenReturn("1234");

        Site site = Sites.ARGENTINA;
        BigDecimal amount = BigDecimal.TEN;
        PaymentMethod paymentMethod = new PaymentMethod();

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();

        presenter.setPayerCost(payerCost);
        presenter.setToken(token);
        presenter.setAmount(amount);
        presenter.setPaymentMethod(paymentMethod);
        presenter.setSite(site);

        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.initialize();

        assertTrue(view.errorMessage != null && !view.errorMessage.isEmpty());
    }

    @Test
    public void ifAmountNotSetShowError() {
        PayerCost payerCost = new PayerCost();

        Token token = Mockito.mock(Token.class);
        Mockito.when(token.getLastFourDigits()).thenReturn("1234");

        Site site = Sites.ARGENTINA;
        PaymentMethod paymentMethod = new PaymentMethod();
        List<Item> items = new ArrayList<>();

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();

        presenter.setPayerCost(payerCost);
        presenter.setToken(token);
        presenter.setItems(items);
        presenter.setPaymentMethod(paymentMethod);
        presenter.setSite(site);

        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.initialize();

        assertTrue(view.errorMessage != null && !view.errorMessage.isEmpty());
    }

    @Test
    public void ifPaymentMethodOnSetButNoCardInfoShowError() {

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.CREDIT_CARD);
        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;
        PayerCost payerCost = new PayerCost();

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();

        presenter.setPaymentMethod(paymentMethod);
        presenter.setPayerCost(payerCost);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.initialize();

        assertTrue(view.errorMessage != null && !view.errorMessage.isEmpty());
    }

    @Test
    public void ifPaymentMethodOnSetButNoPayerCostShowError() {

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.CREDIT_CARD);
        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        Token token = Mockito.mock(Token.class);
        Mockito.when(token.getLastFourDigits()).thenReturn("1234");

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();

        presenter.setPaymentMethod(paymentMethod);
        presenter.setToken(token);

        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        assertTrue(view.errorMessage != null && !view.errorMessage.isEmpty());
    }

    @Test
    public void onReviewAndConfirmStartedShowFixedReviewables() {
        PaymentMethod paymentMethod = new PaymentMethod();

        Token token = Mockito.mock(Token.class);
        Mockito.when(token.getLastFourDigits()).thenReturn("1234");

        PayerCost payerCost = new PayerCost();
        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.setPaymentMethod(paymentMethod);
        presenter.setPayerCost(payerCost);
        presenter.setToken(token);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        assertTrue(view.reviewables != null && !view.reviewables.isEmpty());
    }

    @Test
    public void ifPaymentMethodOnChangeRequestedTellViewToChangePaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        Token token = Mockito.mock(Token.class);
        Mockito.when(token.getLastFourDigits()).thenReturn("1234");

        PayerCost payerCost = new PayerCost();
        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.setPaymentMethod(paymentMethod);
        presenter.setPayerCost(payerCost);
        presenter.setToken(token);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        provider.paymentMethodOnReviewable.changePayment();

        assertTrue(view.paymentMethodChanged);
    }

    @Test
    public void ifPaymentConfirmedInSummaryTellViewToConfirmPayment() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        Token token = Mockito.mock(Token.class);
        Mockito.when(token.getLastFourDigits()).thenReturn("1234");

        PayerCost payerCost = new PayerCost();
        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.setPaymentMethod(paymentMethod);
        presenter.setPayerCost(payerCost);

        presenter.setToken(token);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        provider.paymentSummaryReviewable.confirmPayment();

        assertTrue(view.paymentConfirmed);
    }

    @Test
    public void ifPaymentMethodOffChangeRequestedTellViewToChangePaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.TICKET);

        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.setPaymentMethod(paymentMethod);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        provider.paymentMethodOffReviewable.changePayment();

        assertTrue(view.paymentMethodChanged);
    }

    @Test
    public void ifPaymentConfirmedInViewAssertPaymentConfirmed() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.TICKET);

        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.setPaymentMethod(paymentMethod);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        view.confirmPayment();

        assertTrue(view.paymentConfirmed);
    }

    @Test
    public void ifPaymentCanceledInViewAssertPaymentCanceled() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.TICKET);

        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.setPaymentMethod(paymentMethod);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        view.cancelPayment();

        assertTrue(view.paymentCanceled);
    }

    @Test
    public void ifReviewOrderSetOrderReviewablesAccordingToIt() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.TICKET);

        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        List<String> reviewOrder = new ArrayList<String>() {{
            add(ReviewKeys.ITEMS);
            add(ReviewKeys.PAYMENT_METHODS);
            add(ReviewKeys.SUMMARY);
        }};

        presenter.setReviewOrder(reviewOrder);
        presenter.setPaymentMethod(paymentMethod);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        assertEquals(provider.itemsReviewable, view.reviewables.get(0));
        assertEquals(provider.paymentMethodOffReviewable, view.reviewables.get(1));
        assertEquals(provider.paymentSummaryReviewable, view.reviewables.get(2));
    }

    @Test
    public void ifReviewOrderNotSetUseDefaultOrder() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.TICKET);

        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.setPaymentMethod(paymentMethod);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        assertEquals(provider.paymentSummaryReviewable, view.reviewables.get(0));
        assertEquals(provider.itemsReviewable, view.reviewables.get(1));
        assertEquals(provider.paymentMethodOffReviewable, view.reviewables.get(2));
    }

    @Test
    public void showTitleAccordingToResourceFromProvider() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.TICKET);

        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        String newTitle = "Revisa tu recarga";
        provider.title = newTitle;

        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);


        presenter.setPaymentMethod(paymentMethod);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        assertEquals(newTitle, view.title);
    }

    @Test
    public void showConfirmationMessageAccordingToResourceFromProvider() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.TICKET);

        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        String newConfirmMessage = "Cargar";
        provider.confirmationMessage = newConfirmMessage;

        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);


        presenter.setPaymentMethod(paymentMethod);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        assertEquals(newConfirmMessage, view.confirmationMessage);
    }

    @Test
    public void showCancelMessageAccordingToResourceFromProvider() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(PaymentTypes.TICKET);

        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        String newCancelMessage = "Cancelar recarga";
        provider.cancelMessage = newCancelMessage;

        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);


        presenter.setPaymentMethod(paymentMethod);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        assertEquals(newCancelMessage, view.cancelMessage);
    }

    public class ReviewAndConfirmMockedProvider implements ReviewAndConfirmProvider {

        private PaymentMethodMockedReviewable paymentMethodOnReviewable;
        private PaymentMethodMockedReviewable paymentMethodOffReviewable;
        private SummaryMockedReviewable paymentSummaryReviewable;
        private ItemsMockedReviewable itemsReviewable;
        private String title = "Revisa tu compra";
        private String confirmationMessage = "Confirmar";
        private String cancelMessage = "Cancelar";

        @Override
        public Reviewable getSummaryReviewable(PaymentMethod paymentMethod, PayerCost payerCost, BigDecimal amount, Discount discount, Site site, Issuer issuer, OnConfirmPaymentCallback onConfirmPaymentCallback) {
            paymentSummaryReviewable = new SummaryMockedReviewable(onConfirmPaymentCallback);
            return paymentSummaryReviewable;
        }

        @Override
        public Reviewable getItemsReviewable(String currency, List<Item> items) {
            itemsReviewable = new ItemsMockedReviewable();
            return itemsReviewable;
        }

        @Override
        public Reviewable getPaymentMethodOnReviewable(PaymentMethod paymentMethod, PayerCost payerCost, CardInfo cardInfo, Site site, Boolean editionEnabled, OnReviewChange reviewChange) {
            paymentMethodOnReviewable = new PaymentMethodMockedReviewable(reviewChange);
            return paymentMethodOnReviewable;
        }

        @Override
        public Reviewable getPaymentMethodOffReviewable(PaymentMethod paymentMethod, String paymentMethodCommentInfo, String paymentMethodDescriptionInfo, BigDecimal amount, Site site, Boolean editionEnabled, OnReviewChange reviewChange) {
            paymentMethodOffReviewable = new PaymentMethodMockedReviewable(reviewChange);
            return paymentMethodOffReviewable;
        }

        @Override
        public String getReviewTitle() {
            return this.title;
        }

        @Override
        public String getConfirmationMessage() {
            return this.confirmationMessage;
        }

        @Override
        public String getCancelMessage() {
            return this.cancelMessage;
        }
    }

    public class ReviewAndConfirmMockedView implements ReviewAndConfirmView, ReviewSubscriber {

        private String errorMessage;
        private List<Reviewable> reviewables;
        private boolean paymentMethodChanged = false;
        private boolean paymentConfirmed = false;
        private boolean paymentCanceled = false;
        private String title;
        private String confirmationMessage;
        private String cancelMessage;
        private boolean termsAndConditionsShown = false;

        @Override
        public void showError(String message) {
            this.errorMessage = message;
        }

        @Override
        public void showReviewables(List<Reviewable> reviewables) {
            this.reviewables = reviewables;
        }

        @Override
        public void changePaymentMethod() {
            this.paymentMethodChanged = true;
        }

        @Override
        public void confirmPayment() {
            this.paymentConfirmed = true;
        }

        @Override
        public void cancelPayment() {
            this.paymentCanceled = true;
        }

        @Override
        public void showTitle(String title) {
            this.title = title;
        }

        @Override
        public void showConfirmationMessage(String message) {
            this.confirmationMessage = message;
        }

        @Override
        public void showCancelMessage(String message) {
            this.cancelMessage = message;
        }

        @Override
        public void showTermsAndConditions() {
            this.termsAndConditionsShown = true;
        }

        @Override
        public ReviewSubscriber getReviewSubscriber() {
            return this;
        }

        @Override
        public void trackScreen() {
        }

        @Override
        public void changeRequired(Integer resultCode, @Nullable Bundle data) {

        }
    }

    public class ItemsMockedReviewable extends Reviewable {

        @Override
        public void draw() {
            //Mocked
        }

        @Override
        public void initializeControls() {
            //Mocked
        }

        @Override
        public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
            //Mocked
            return null;
        }

        @Override
        public View getView() {
            //Mocked
            return null;
        }

        @Override
        public String getKey() {
            return ReviewKeys.ITEMS;
        }
    }

    public class PaymentMethodMockedReviewable extends Reviewable {

        private final OnReviewChange reviewChange;

        public PaymentMethodMockedReviewable(OnReviewChange reviewChange) {
            this.reviewChange = reviewChange;
        }

        @Override
        public void draw() {
            //Mocked
        }

        @Override
        public void initializeControls() {
            //Mocked
        }

        @Override
        public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
            //Mocked
            return null;
        }

        @Override
        public View getView() {
            //Mocked
            return null;
        }

        @Override
        public String getKey() {
            return ReviewKeys.PAYMENT_METHODS;
        }

        public void changePayment() {
            reviewChange.onChangeSelected();
        }
    }

    public class SummaryMockedReviewable extends Reviewable {

        private OnConfirmPaymentCallback confirmPaymentCallback;

        public SummaryMockedReviewable(OnConfirmPaymentCallback onConfirmPaymentCallback) {
            this.confirmPaymentCallback = onConfirmPaymentCallback;
        }

        @Override
        public void draw() {
            //Mocked
        }

        @Override
        public void initializeControls() {
            //Mocked
        }

        @Override
        public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
            //Mocked
            return null;
        }

        @Override
        public View getView() {
            //Mocked
            return null;
        }

        @Override
        public String getKey() {
            return ReviewKeys.SUMMARY;
        }

        public void confirmPayment() {
            confirmPaymentCallback.confirmPayment();
        }
    }

}
