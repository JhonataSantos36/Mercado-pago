package com.mercadopago.paymentresult;

import com.mercadopago.constants.Sites;
import com.mercadopago.mocks.PaymentMethods;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.Site;
import com.mercadopago.presenters.PaymentResultPresenter;
import com.mercadopago.providers.PaymentResultProvider;
import com.mercadopago.views.PaymentResultView;

import junit.framework.Assert;

import org.junit.Test;

import java.math.BigDecimal;

public class PaymentResultTest {

    @Test
    public void whenPaymentWithCardApprovedThenShowCongrats() {
        PaymentResultPresenter presenter = new PaymentResultPresenter();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus(Payment.StatusCodes.STATUS_APPROVED)
                .setPaymentData(paymentData)
                .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDiscountEnabled(Boolean.TRUE);

        MockedView mockedView = new MockedView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(mockedView.congratsShown);
    }

    @Test
    public void whenPaymentWithCardRejectedThenShowRejection() {
        PaymentResultPresenter presenter = new PaymentResultPresenter();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus(Payment.StatusCodes.STATUS_REJECTED)
                .setPaymentStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_OTHER_REASON)
                .setPaymentData(paymentData)
                .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDiscountEnabled(Boolean.TRUE);

        MockedView mockedView = new MockedView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(mockedView.rejectionShown);
    }

    @Test
    public void whenCallForAuthNeededThenShowCallForAuthScreen() {
        PaymentResultPresenter presenter = new PaymentResultPresenter();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus(Payment.StatusCodes.STATUS_REJECTED)
                .setPaymentStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE)
                .setPaymentData(paymentData)
                .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDiscountEnabled(Boolean.TRUE);

        MockedView mockedView = new MockedView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(mockedView.callForAuthorizeShown);
    }

    @Test
    public void whenPaymentOffPendingThenShowInstructions() {
        PaymentResultPresenter presenter = new PaymentResultPresenter();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus(Payment.StatusCodes.STATUS_PENDING)
                .setPaymentStatusDetail(Payment.StatusCodes.STATUS_DETAIL_PENDING_WAITING_PAYMENT)
                .setPaymentData(paymentData)
                .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDiscountEnabled(Boolean.TRUE);

        MockedView mockedView = new MockedView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(mockedView.instructionsShown);
    }

    @Test
    public void whenPaymentOnInProcessThenShowPendingScreen() {
        PaymentResultPresenter presenter = new PaymentResultPresenter();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus(Payment.StatusCodes.STATUS_IN_PROCESS)
                .setPaymentData(paymentData)
                .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDiscountEnabled(Boolean.TRUE);

        MockedView mockedView = new MockedView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(mockedView.pendingShown);
    }

    @Test
    public void whenUnknownStatusThenShowError() {
        PaymentResultPresenter presenter = new PaymentResultPresenter();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus("UNKNOWN")
                .setPaymentData(paymentData)
                .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDiscountEnabled(Boolean.TRUE);

        MockedView mockedView = new MockedView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(mockedView.errorShown);
    }

    @Test
    public void whenPaymentDataIsNullThenShowError() {
        PaymentResultPresenter presenter = new PaymentResultPresenter();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus("UNKNOWN")
                .setPaymentData(null)
                .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDiscountEnabled(Boolean.TRUE);

        MockedView mockedView = new MockedView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(mockedView.errorShown);
    }

    @Test
    public void whenPaymentResultIsNullThenShowError() {
        PaymentResultPresenter presenter = new PaymentResultPresenter();

        presenter.setPaymentResult(null);
        presenter.setAmount(new BigDecimal("100"));
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDiscountEnabled(Boolean.TRUE);

        MockedView mockedView = new MockedView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(mockedView.errorShown);
    }

    @Test
    public void whenPaymentResultStatusIsNullThenShowError() {
        PaymentResultPresenter presenter = new PaymentResultPresenter();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus(null)
                .setPaymentData(null)
                .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDiscountEnabled(Boolean.TRUE);

        MockedView mockedView = new MockedView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(mockedView.errorShown);
    }

    @Test
    public void whenPaymentOffRejectedThenShowRejection() {
        PaymentResultPresenter presenter = new PaymentResultPresenter();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus(Payment.StatusCodes.STATUS_REJECTED)
                .setPaymentData(paymentData)
                .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        presenter.setSite(Sites.ARGENTINA);
        presenter.setDiscountEnabled(Boolean.TRUE);

        MockedView mockedView = new MockedView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(mockedView.rejectionShown);
    }

    private class MockedView implements PaymentResultView {
        private boolean congratsShown = false;
        private boolean callForAuthorizeShown = false;
        private boolean rejectionShown = false;
        private boolean pendingShown = false;
        private boolean instructionsShown = false;
        private boolean errorShown = false;

        @Override
        public void showCongrats(Site site, BigDecimal amount, PaymentResult paymentResult, Boolean discountEnabled) {
            this.congratsShown = true;
        }

        @Override
        public void showCallForAuthorize(Site site, PaymentResult paymentResult) {
            this.callForAuthorizeShown = true;
        }

        @Override
        public void showRejection(PaymentResult paymentResult) {
            this.rejectionShown = true;
        }

        @Override
        public void showPending(PaymentResult paymentResult) {
            this.pendingShown = true;
        }

        @Override
        public void showInstructions(Site site, BigDecimal amount, PaymentResult paymentResult) {
            this.instructionsShown = true;
        }

        @Override
        public void showError(String errorMessage) {
            this.errorShown = true;
        }

        @Override
        public void showError(String errorMessage, String errorDetail) {
            this.errorShown = true;
        }
    }

    private class MockedProvider implements PaymentResultProvider {

        private String STANDARD_ERROR_MESSAGE = "Algo salió mal";

        @Override
        public String getStandardErrorMessage() {
            return STANDARD_ERROR_MESSAGE;
        }
    }
}
