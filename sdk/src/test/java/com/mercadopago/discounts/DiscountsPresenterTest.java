package com.mercadopago.discounts;

import com.mercadopago.lite.model.Discount;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.presenters.DiscountsPresenter;
import com.mercadopago.providers.DiscountsProvider;
import com.mercadopago.views.DiscountsActivityView;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

/**
 * Created by mromar on 1/24/17.
 */

public class DiscountsPresenterTest {

    @Test
    public void showDiscountSummaryWhenGetDirectDiscount() {
        MockedView mockedView = new MockedView();
        DiscountMockedResourcesProvider provider = new DiscountMockedResourcesProvider();

        DiscountsPresenter presenter = new DiscountsPresenter();
        presenter.setDirectDiscountEnabled(true);
        presenter.setTransactionAmount(new BigDecimal(100));

        presenter.attachResourcesProvider(provider);
        presenter.attachView(mockedView);

        presenter.initialize();

        assertTrue(mockedView.drawedSummary);
    }

    @Test
    public void showDiscountCodeRequestWhenDirectDiscountIsNotEnabled() {
        MockedView mockedView = new MockedView();
        DiscountMockedResourcesProvider provider = new DiscountMockedResourcesProvider();

        DiscountsPresenter presenter = new DiscountsPresenter();
        presenter.setDirectDiscountEnabled(false);
        presenter.setTransactionAmount(new BigDecimal(100));

        presenter.attachResourcesProvider(provider);
        presenter.attachView(mockedView);

        presenter.initialize();

        assertTrue(mockedView.requestedDiscountCode);
    }

    @Test
    public void showDiscountSummaryWhenInitializePresenterWithDiscount() {
        MockedView mockedView = new MockedView();
        DiscountMockedResourcesProvider provider = new DiscountMockedResourcesProvider();

        DiscountsPresenter presenter = new DiscountsPresenter();
        Discount discount = new Discount();

        presenter.setDiscount(discount);
        presenter.setDirectDiscountEnabled(false);
        presenter.setTransactionAmount(new BigDecimal(100));

        presenter.attachResourcesProvider(provider);
        presenter.attachView(mockedView);

        presenter.initialize();

        assertTrue(mockedView.drawedSummary);
    }

    private class MockedView implements DiscountsActivityView {

        private Boolean drawedSummary;
        private Boolean requestedDiscountCode;
        private Boolean finishedWithResult;
        private Boolean finishedWithCancelResult;
        private Boolean progressVisible;
        private Boolean showedEmptyDiscountCodeError;
        private Boolean hidedKeyboard;
        private Boolean setedSoftInputModeSummary;
        private Boolean hidedDiscountSummary;
        private String error;

        @Override
        public void drawSummary() {
            this.drawedSummary = true;
        }

        @Override
        public void requestDiscountCode() {
            this.requestedDiscountCode = true;
        }

        @Override
        public void finishWithResult() {
            this.finishedWithResult = true;
        }

        @Override
        public void finishWithCancelResult() {
            this.finishedWithCancelResult = true;
        }

        @Override
        public void showCodeInputError(String message) {
            this.error = message;
        }

        @Override
        public void clearErrorView() {
            this.error = null;
        }

        @Override
        public void showProgressBar() {
            this.progressVisible = true;
        }

        @Override
        public void hideProgressBar() {
            this.progressVisible = false;
        }

        @Override
        public void showEmptyDiscountCodeError() {
            this.showedEmptyDiscountCodeError = true;
        }

        @Override
        public void hideKeyboard() {
            this.hidedKeyboard = true;
        }

        @Override
        public void setSoftInputModeSummary() {
            this.setedSoftInputModeSummary = true;
        }

        @Override
        public void hideDiscountSummary() {
            this.hidedDiscountSummary = true;
        }
    }

    private class DiscountMockedResourcesProvider implements DiscountsProvider {

        @Override
        public void getDirectDiscount(String transactionAmount, String payerEmail, TaggedCallback<Discount> taggedCallback) {
            taggedCallback.onSuccess(new Discount());
        }

        @Override
        public void getCodeDiscount(String transactionAmount, String payerEmail, String discountCode, TaggedCallback<Discount> taggedCallback) {
            taggedCallback.onSuccess(new Discount());
        }

        @Override
        public String getApiErrorMessage(String error) {
            return null;
        }

        @Override
        public String getStandardErrorMessage() {
            return null;
        }
    }
}
