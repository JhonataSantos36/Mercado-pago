package com.mercadopago.installments;

import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.lite.model.Sites;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.mocks.Installments;
import com.mercadopago.mocks.Issuers;
import com.mercadopago.mocks.PayerCosts;
import com.mercadopago.mocks.PaymentMethods;
import com.mercadopago.lite.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.lite.model.Discount;
import com.mercadopago.lite.model.Installment;
import com.mercadopago.lite.model.Issuer;
import com.mercadopago.lite.model.PayerCost;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.lite.preferences.PaymentPreference;
import com.mercadopago.presenters.InstallmentsPresenter;
import com.mercadopago.providers.InstallmentsProvider;
import com.mercadopago.views.InstallmentsActivityView;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.util.TextUtil.isEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mromar on 5/4/17.
 */

public class InstallmentsPresenterTest {

    @Test
    public void whenPayerCostIsNullThenGetInstallments() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installments = Installments.getInstallmentsList();
        provider.setResponse(installments);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setIssuer(issuer);

        presenter.initialize();

        assertTrue(mockedView.headerShown);
        assertFalse(mockedView.loadingViewShown);
        assertTrue(mockedView.installmentsShown);
    }

    @Test
    public void whenGetInstallmentsGetEmptyListThenShowError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installments = new ArrayList<Installment>();
        provider.setResponse(installments);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setIssuer(issuer);

        presenter.initialize();

        assertTrue(mockedView.errorShown);
        assertTrue(provider.noInstallmentsFoundErrorGotten);
    }

    @Test
    public void whenGetInstallmentsGetMoreThanOneElementsThenShowError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installments = getThreeInstallmentList();
        provider.setResponse(installments);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setIssuer(issuer);

        presenter.initialize();

        assertTrue(mockedView.errorShown);
        assertTrue(provider.multipleInstallmentsErrorGotten);
    }

    @Test
    public void whenPayerCostIsNotNullThenFinishWithPayerCost() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PayerCost> payerCosts = PayerCosts.getPayerCostsWithCFT();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setPayerCosts(payerCosts);
        presenter.setIssuer(issuer);

        presenter.initialize();

        assertTrue(mockedView.headerShown);
        assertFalse(mockedView.loadingViewShown);
        assertTrue(mockedView.installmentsShown);
    }

    @Test
    public void whenIsReviewEnabledThenShowReview() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PayerCost> payerCosts = PayerCosts.getPayerCostsWithCFT();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setPayerCosts(payerCosts);
        presenter.setIssuer(issuer);
        presenter.setInstallmentsReviewEnabled(true);

        presenter.initialize();

        mockedView.simulateInstallmentSelection(0);

        assertFalse(mockedView.installmentRecyclerViewShown);
        assertTrue(mockedView.installmentsReviewViewShown);
        assertTrue(mockedView.discountRowShown);
        assertTrue(mockedView.installmentsReviewViewInitialized);
    }

    @Test
    public void whenIsReviewEnabledButIsNotRequiredThenNotShowReview() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PayerCost> payerCosts = PayerCosts.getPayerCostList();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setPayerCosts(payerCosts);
        presenter.setIssuer(issuer);
        presenter.setInstallmentsReviewEnabled(true);

        presenter.initialize();

        mockedView.simulateInstallmentSelection(0);

        assertFalse(mockedView.installmentsReviewViewShown);
        assertTrue(mockedView.finishWithResult);
    }

    @Test
    public void whenIsNotReviewEnabledThenFinishWithResult() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PayerCost> payerCosts = PayerCosts.getPayerCostsWithCFT();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setPayerCosts(payerCosts);
        presenter.setIssuer(issuer);
        presenter.setInstallmentsReviewEnabled(false);

        presenter.initialize();

        mockedView.simulateInstallmentSelection(0);

        assertFalse(mockedView.installmentsReviewViewShown);
        assertTrue(mockedView.finishWithResult);
    }

    @Test
    public void whenSelectOnInstallmentThenFinishWithPayerCost() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installments = Installments.getInstallmentsList();
        provider.setResponse(installments);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setIssuer(issuer);

        presenter.initialize();

        mockedView.simulateInstallmentSelection(0);

        assertTrue(mockedView.installmentsShown);
        assertTrue(mockedView.headerShown);
        assertEquals(installments.get(0).getPayerCosts().get(0), mockedView.selectedPayerCost);
        assertTrue(mockedView.finishWithResult);
    }

    @Test
    public void whenGetInstallmentFailThenShowError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        MercadoPagoError mercadoPagoError = new MercadoPagoError("Error", true);
        provider.setResponse(mercadoPagoError);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setIssuer(issuer);

        presenter.initialize();

        assertFalse(mockedView.loadingViewShown);
        assertTrue(mockedView.errorShown);
    }

    @Test
    public void whenRecoverFromFailureThenGetInstallmentsAgain() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        MercadoPagoError mercadoPagoError = new MercadoPagoError("Error", true);
        provider.setResponse(mercadoPagoError);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setIssuer(issuer);

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

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.recoverFromFailure();

        assertEquals(presenter.getFailureRecovery(), null);
    }

    @Test
    public void whenPayerCostsSizeIsOneThenFinishWithResult() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();
        List<PayerCost> payerCosts = PayerCosts.getOnePayerCostList();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPayerCosts(payerCosts);
        presenter.setPaymentMethod(paymentMethod);
        presenter.setIssuer(issuer);

        presenter.initialize();

        assertEquals(mockedView.selectedPayerCost, payerCosts.get(0));
        assertTrue(mockedView.finishWithResult);
    }

    @Test
    public void whenPayerCostsIsEmptyThenShowError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installments = Installments.getInstallmentsListWithoutPayerCosts();
        provider.setResponse(installments);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setIssuer(issuer);

        presenter.initialize();

        assertTrue(mockedView.errorShown);
        assertTrue(provider.noPayerCostFoundErrorGotten);
    }

    @Test
    public void whenPaymentPreferenceHasDefaultPayerCostThenFinishWithResult() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PayerCost> payerCosts = PayerCosts.getPayerCostsWithCFT();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(1);

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setPaymentPreference(paymentPreference);
        presenter.setDiscountEnabled(false);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPayerCosts(payerCosts);
        presenter.setPaymentMethod(paymentMethod);
        presenter.setIssuer(issuer);

        presenter.initialize();

        assertTrue(mockedView.finishWithResult);
        assertEquals(mockedView.selectedPayerCost, getPayerCost(payerCosts, paymentPreference.getDefaultInstallments()));
    }

    @Test
    public void whenIsCardInfoAndPaymentMethodAvailableThenIsNotRequiredCardDrawn() {
        CardInfo cardInfo = getCardInfo();
        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.setCardInfo(cardInfo);
        presenter.setPaymentMethod(paymentMethod);

        assertTrue(presenter.isRequiredCardDrawn());
    }

    @Test
    public void whenIsNotPaymentMethodAvailableThenIsNotRequiredCardDrawn() {
        CardInfo cardInfo = getCardInfo();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.setCardInfo(cardInfo);

        assertFalse(presenter.isRequiredCardDrawn());
    }

    @Test
    public void whenIsNotCardInfoAvailableThenIsNotRequiredCardDrawn() {
        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.setPaymentMethod(paymentMethod);

        assertFalse(presenter.isRequiredCardDrawn());
    }

    @Test
    public void whenCardInfoNullThenBinIsNull() {
        InstallmentsPresenter installmentsPresenter = new InstallmentsPresenter();
        CardInfo cardInfo = null;

        installmentsPresenter.setCardInfo(cardInfo);

        assertTrue(isEmpty(installmentsPresenter.getBin()));
    }

    @Test
    public void whenIssuerIsNullThenIssuerIdIsNull() {
        InstallmentsPresenter presenter = new InstallmentsPresenter();
        Issuer issuer = null;

        presenter.setIssuer(issuer);

        assertTrue(presenter.getIssuerId() == null);
    }

    @Test
    public void whenHasDiscountThenGetAmountWithDiscount() {
        Discount discount = getDiscount();
        BigDecimal amount = new BigDecimal(500);

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.setAmount(amount);
        presenter.setDiscountEnabled(true);

        presenter.setDiscount(discount);

        assertEquals(presenter.getAmount(), discount.getAmountWithDiscount(amount));
    }

    @Test
    public void whenHasDiscountWithoutCurrencyThenGetAmountWithoutDiscount() {
        Discount discount = getDiscountWithoutCurrency();
        BigDecimal amount = new BigDecimal(500);

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.setAmount(amount);
        presenter.setDiscountEnabled(true);
        presenter.setDiscount(discount);

        assertEquals(presenter.getAmount(), amount);
    }

    @Test
    public void whenHasDiscountWithoutIdThenGetAmountWithoutDiscount() {
        Discount discount = getDiscountWithoutId();
        BigDecimal amount = new BigDecimal(500);

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.setAmount(amount);
        presenter.setDiscountEnabled(true);
        presenter.setDiscount(discount);

        assertEquals(presenter.getAmount(), amount);
    }

    @Test
    public void whenHasDiscountWithoutCouponAmountThenGetAmountWithoutDiscount() {
        Discount discount = getDiscountWithoutCouponAmount();
        BigDecimal amount = new BigDecimal(500);

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.setAmount(amount);
        presenter.setDiscountEnabled(true);
        presenter.setDiscount(discount);

        assertEquals(presenter.getAmount(), amount);
    }

    @Test
    public void whenDiscountHasEmptyCurrencyThenGetAmountWithoutDiscount() {
        Discount discount = getDiscountWithEmptyCurrency();
        BigDecimal amount = new BigDecimal(500);

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.setAmount(amount);
        presenter.setDiscountEnabled(true);

        presenter.setDiscount(discount);

        assertEquals(presenter.getAmount(), amount);
    }

    @Test
    public void whenDiscountHasNegativeCouponAmountThenGetAmountWithoutDiscount() {
        Discount discount = getDiscountWithoutNegativeCouponAmount();
        BigDecimal amount = new BigDecimal(500);

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.setAmount(amount);
        presenter.setDiscountEnabled(true);

        presenter.setDiscount(discount);

        assertEquals(presenter.getAmount(), amount);
    }

    @Test
    public void whenDiscountIsNullThenGetAmountWithoutDiscount() {
        Discount discount = null;
        BigDecimal amount = new BigDecimal(500);

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.setAmount(amount);
        presenter.setDiscountEnabled(true);

        presenter.setDiscount(discount);

        assertEquals(presenter.getAmount(), amount);
    }

    @Test
    public void whenMCOThenShowBankInterestsNotCoveredWarning() {

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);


        presenter.setSite(Sites.COLOMBIA);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setIssuer(issuer);

        presenter.initialize();

        assertTrue(mockedView.bankInterestsWarningShown);
    }

    @Test
    public void whenNotMCOThenDoNotShowBankInterestsNotCoveredWarning() {

        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);

        InstallmentsPresenter presenter = new InstallmentsPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);


        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(1000));
        presenter.setCardInfo(getCardInfo());
        presenter.setPaymentMethod(paymentMethod);
        presenter.setIssuer(issuer);

        presenter.initialize();

        assertFalse(mockedView.bankInterestsWarningShown);
    }

    private CardInfo getCardInfo() {
        Card card = new Card();
        card.setLastFourDigits("4321");
        card.setFirstSixDigits("123456");

        return new CardInfo(card);
    }

    private List<Installment> getThreeInstallmentList() {
        List<Installment> installments = new ArrayList<Installment>();

        Installment installment = new Installment();
        installments.add(installment);
        installments.add(installment);
        installments.add(installment);

        return installments;
    }

    private PayerCost getPayerCost(List<PayerCost> payerCosts, Integer defaultInstallments) {
        PayerCost payerCost = new PayerCost();

        for (PayerCost currentPayerCost : payerCosts) {
            if (defaultInstallments.equals(currentPayerCost.getInstallments())) {
                payerCost = currentPayerCost;
            }
        }

        return payerCost;
    }

    private Discount getDiscount() {
        Discount discount = new Discount();

        discount.setId("77");
        discount.setCurrencyId("ARS");
        discount.setCouponAmount(new BigDecimal(50));

        return discount;
    }

    private Discount getDiscountWithoutCurrency() {
        Discount discount = new Discount();

        discount.setId("77");
        discount.setCouponAmount(new BigDecimal(50));

        return discount;
    }

    private Discount getDiscountWithEmptyCurrency() {
        Discount discount = new Discount();

        discount.setId("77");
        discount.setCurrencyId("");
        discount.setCouponAmount(new BigDecimal(50));

        return discount;
    }

    private Discount getDiscountWithoutId() {
        Discount discount = new Discount();

        discount.setCurrencyId("ARS");
        discount.setCouponAmount(new BigDecimal(50));

        return discount;
    }

    private Discount getDiscountWithoutNegativeCouponAmount() {
        Discount discount = new Discount();

        discount.setCurrencyId("ARS");
        discount.setCouponAmount(new BigDecimal(-50));

        return discount;
    }

    private Discount getDiscountWithoutCouponAmount() {
        Discount discount = new Discount();

        discount.setCurrencyId("ARS");
        discount.setCouponAmount(new BigDecimal(50));

        return discount;
    }

    private class MockedProvider implements InstallmentsProvider {

        private boolean shouldFail;
        private List<Installment> successfulResponse;
        private MercadoPagoError failedResponse;

        private boolean noInstallmentsFoundErrorGotten = false;
        private boolean noPayerCostFoundErrorGotten = false;
        private boolean multipleInstallmentsErrorGotten = false;

        MockedProvider() {
            successfulResponse = Installments.getInstallmentsList();
            failedResponse = new MercadoPagoError("Default mocked error", false);
        }

        private void setResponse(List<Installment> installments) {
            shouldFail = false;
            successfulResponse = installments;
        }

        private void setResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        @Override
        public void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, TaggedCallback<List<Installment>> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulResponse);
            }
        }

        @Override
        public MercadoPagoError getNoInstallmentsFoundError() {
            this.noInstallmentsFoundErrorGotten = true;
            return null;
        }

        @Override
        public MercadoPagoError getMultipleInstallmentsFoundForAnIssuerError() {
            this.multipleInstallmentsErrorGotten = true;
            return null;
        }

        @Override
        public MercadoPagoError getNoPayerCostFoundError() {
            this.noPayerCostFoundErrorGotten = true;
            return null;
        }
    }

    private class MockedView implements InstallmentsActivityView {

        private boolean installmentsShown = false;
        private boolean finishWithResult = false;
        private boolean headerShown = false;
        private boolean errorShown = false;
        private boolean loadingViewShown = false;
        private boolean discountRowShown = false;
        private boolean installmentRecyclerViewShown = false;
        private boolean installmentsReviewViewShown = false;
        private boolean installmentsReviewViewInitialized = false;
        private PayerCost selectedPayerCost;
        private OnSelectedCallback<Integer> installmentSelectionCallback;
        private boolean bankInterestsWarningShown = false;

        @Override
        public void showInstallments(List<PayerCost> payerCostList, OnSelectedCallback<Integer> onSelectedCallback) {
            this.installmentSelectionCallback = onSelectedCallback;
            this.installmentsShown = true;
        }

        @Override
        public void startDiscountFlow(BigDecimal transactionAmount) {
            //Do something
        }

        @Override
        public void finishWithResult(PayerCost payerCost) {
            this.finishWithResult = true;
            this.selectedPayerCost = payerCost;
        }

        @Override
        public void showLoadingView() {
            this.loadingViewShown = true;
        }

        @Override
        public void hideLoadingView() {
            this.loadingViewShown = false;
        }

        @Override
        public void showError(MercadoPagoError error, String requestOrigin) {
            this.errorShown = true;
        }

        @Override
        public void showHeader() {
            this.headerShown = true;
        }

        @Override
        public void showDiscountRow(BigDecimal transactionAmount) {
            this.discountRowShown = true;
        }

        @Override
        public void initInstallmentsReviewView(PayerCost payerCost) {
            this.installmentsReviewViewInitialized = true;
        }

        @Override
        public void hideInstallmentsRecyclerView() {
            this.installmentRecyclerViewShown = false;
        }

        @Override
        public void showInstallmentsRecyclerView() {
            this.installmentRecyclerViewShown = true;
        }

        @Override
        public void hideInstallmentsReviewView() {
            this.installmentsReviewViewShown = false;
        }

        @Override
        public void showInstallmentsReviewView() {
            this.installmentsReviewViewShown = true;
        }

        @Override
        public void warnAboutBankInterests() {
            bankInterestsWarningShown = true;
        }

        private void simulateInstallmentSelection(int index) {
            installmentSelectionCallback.onSelected(index);
        }
    }
}
