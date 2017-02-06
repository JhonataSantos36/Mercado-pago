package com.mercadopago.reviewandconfirm;

import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.Sites;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.presenters.ReviewAndConfirmPresenter;
import com.mercadopago.providers.ReviewAndConfirmProvider;
import com.mercadopago.views.ReviewAndConfirmView;

import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by mreverter on 2/3/17.
 */

public class ReviewAndConfirmPresenterTest {

    @Test
    public void ifPaymentMethodNotSetShowError() {

        PayerCost payerCost = new PayerCost();
        CardInfo cardInfo = Mockito.mock(CardInfo.class);
        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        Site site = Sites.ARGENTINA;

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();

        presenter.setPayerCost(payerCost);
        presenter.setCardInfo(cardInfo);
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
        CardInfo cardInfo = Mockito.mock(CardInfo.class);
        List<Item> items = new ArrayList<>();
        BigDecimal amount = BigDecimal.TEN;
        PaymentMethod paymentMethod = new PaymentMethod();

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();

        presenter.setPayerCost(payerCost);
        presenter.setCardInfo(cardInfo);
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
        CardInfo cardInfo = Mockito.mock(CardInfo.class);
        Site site = Sites.ARGENTINA;
        BigDecimal amount = BigDecimal.TEN;
        PaymentMethod paymentMethod = new PaymentMethod();

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();

        presenter.setPayerCost(payerCost);
        presenter.setCardInfo(cardInfo);
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
        CardInfo cardInfo = Mockito.mock(CardInfo.class);
        Site site = Sites.ARGENTINA;
        PaymentMethod paymentMethod = new PaymentMethod();
        List<Item> items = new ArrayList<>();

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();

        presenter.setPayerCost(payerCost);
        presenter.setCardInfo(cardInfo);
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

        CardInfo cardInfo = Mockito.mock(CardInfo.class);

        ReviewAndConfirmMockedProvider provider = new ReviewAndConfirmMockedProvider();
        ReviewAndConfirmMockedView view = new ReviewAndConfirmMockedView();

        ReviewAndConfirmPresenter presenter = new ReviewAndConfirmPresenter();

        presenter.setPaymentMethod(paymentMethod);
        presenter.setCardInfo(cardInfo);

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

        CardInfo cardInfo = Mockito.mock(CardInfo.class);
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
        presenter.setCardInfo(cardInfo);
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

        CardInfo cardInfo = Mockito.mock(CardInfo.class);
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
        presenter.setCardInfo(cardInfo);
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

        CardInfo cardInfo = Mockito.mock(CardInfo.class);
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
        presenter.setCardInfo(cardInfo);
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

        CardInfo cardInfo = Mockito.mock(CardInfo.class);
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
        presenter.setCardInfo(cardInfo);
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

        CardInfo cardInfo = Mockito.mock(CardInfo.class);
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
        presenter.setCardInfo(cardInfo);
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

        CardInfo cardInfo = Mockito.mock(CardInfo.class);
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
        presenter.setCardInfo(cardInfo);
        presenter.setItems(items);
        presenter.setAmount(amount);
        presenter.setSite(site);

        presenter.initialize();

        view.cancelPayment();

        assertTrue(view.paymentCanceled);
    }

    public class ReviewAndConfirmMockedProvider implements ReviewAndConfirmProvider {

        private PaymentMethodMockedReviewable paymentMethodOnReviewable;
        private PaymentMethodMockedReviewable paymentMethodOffReviewable;
        private SummaryMockedReviewable paymentSummaryReviewable;

        @Override
        public Reviewable getSummaryReviewable(PaymentMethod paymentMethod, PayerCost payerCost, BigDecimal amount, Discount discount, Site site, DecorationPreference decorationPreference, OnConfirmPaymentCallback onConfirmPaymentCallback) {
            paymentSummaryReviewable = new SummaryMockedReviewable(onConfirmPaymentCallback);
            return paymentSummaryReviewable;
        }

        @Override
        public Reviewable getItemsReviewable(String currency, List<Item> items) {
            return new ItemsMockedReviewable();
        }

        @Override
        public Reviewable getPaymentMethodOnReviewable(PaymentMethod paymentMethod, PayerCost payerCost, CardInfo cardInfo, Site site, DecorationPreference decorationPreference, OnReviewChange reviewChange) {
            paymentMethodOnReviewable = new PaymentMethodMockedReviewable(reviewChange);
            return paymentMethodOnReviewable;
        }

        @Override
        public Reviewable getPaymentMethodOffReviewable(PaymentMethod paymentMethod, String extraPaymentMethodInfo, BigDecimal amount, Site site, DecorationPreference decorationPreference, OnReviewChange reviewChange) {
            paymentMethodOffReviewable = new PaymentMethodMockedReviewable(reviewChange);
            return paymentMethodOffReviewable;
        }
    }

    public class ReviewAndConfirmMockedView implements ReviewAndConfirmView {

        private String errorMessage;
        private List<Reviewable> reviewables;
        private boolean paymentMethodChanged = false;
        private boolean paymentConfirmed = false;
        private boolean paymentCanceled = false;

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

        public void confirmPayment() {
            confirmPaymentCallback.confirmPayment();
        }
    }

}
