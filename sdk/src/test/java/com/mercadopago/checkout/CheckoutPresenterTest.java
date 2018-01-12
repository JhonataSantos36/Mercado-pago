package com.mercadopago.checkout;

import com.mercadopago.constants.Sites;
import com.mercadopago.controllers.Timer;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.hooks.Hook;
import com.mercadopago.mocks.Cards;
import com.mercadopago.mocks.Customers;
import com.mercadopago.mocks.Discounts;
import com.mercadopago.mocks.Installments;
import com.mercadopago.mocks.Issuers;
import com.mercadopago.mocks.PaymentMethodSearchs;
import com.mercadopago.mocks.PaymentMethods;
import com.mercadopago.mocks.Payments;
import com.mercadopago.mocks.Tokens;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Campaign;
import com.mercadopago.model.Card;
import com.mercadopago.model.Cause;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Identification;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.FlowPreference;
import com.mercadopago.presenters.CheckoutPresenter;
import com.mercadopago.providers.CheckoutProvider;
import com.mercadopago.util.TextUtil;
import com.mercadopago.views.CheckoutView;

import junit.framework.Assert;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CheckoutPresenterTest {

    //Validations
    @Test
    public void onCheckoutInitializedWithoutCheckoutPreferenceThenShowError() {

        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        presenter.initialize();
        assertTrue(view.showingError);
    }

    //Discounts
    @Test
    public void ifDiscountNotSetAndDiscountsEnabledThenGetDiscountCampaigns() {

        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference preference = new CheckoutPreference.Builder()
                .setSite(Sites.ARGENTINA)
                .addItem(new Item("Item", BigDecimal.TEN))
                .build();

        presenter.setCheckoutPreference(preference);

        provider.setCampaignsResponse(Discounts.getCampaigns());
        presenter.initialize();
        assertTrue(provider.campaignsRequested);
    }

    @Test
    public void ifDirectDiscountCampaignAvailableThenRequestDirectDiscount() {

        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference preference = new CheckoutPreference.Builder()
                .setSite(Sites.ARGENTINA)
                .addItem(new Item("Item", BigDecimal.TEN))
                .build();

        presenter.setCheckoutPreference(preference);

        provider.setCampaignsResponse(Discounts.getCampaigns());
        presenter.initialize();
        assertTrue(provider.directDiscountRequested);
    }

    @Test
    public void ifNullCampaignsRetrievedThenDisableDiscounts() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference preference = new CheckoutPreference.Builder()
                .setSite(Sites.ARGENTINA)
                .addItem(new Item("Item", BigDecimal.TEN))
                .build();

        presenter.setCheckoutPreference(preference);

        presenter.initialize();
        assertFalse(presenter.isDiscountEnabled());
    }

    @Test
    public void ifEmptyCampaignsRetrievedThenDisableDiscounts() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference preference = new CheckoutPreference.Builder()
                .setSite(Sites.ARGENTINA)
                .addItem(new Item("Item", BigDecimal.TEN))
                .build();

        presenter.setCheckoutPreference(preference);

        provider.setCampaignsResponse(new ArrayList<Campaign>());
        presenter.initialize();
        assertFalse(presenter.isDiscountEnabled());
    }

    //Preferences configuration

    @Test
    public void ifPreferenceSetHasIdThenRetrievePreferenceFromMercadoPago() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference preference = new CheckoutPreference("dummy_id");

        provider.setCheckoutPreferenceResponse(preference);
        presenter.setCheckoutPreference(preference);

        presenter.initialize();
        assertTrue(provider.checkoutPreferenceRequested);
    }

    @Test
    public void ifPreferenceSetDoesNotHaveIdThenDoNotRetrievePreferenceFromMercadoPago() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference preference = new CheckoutPreference.Builder()
                .setSite(Sites.ARGENTINA)
                .addItem(new Item("Dummy", BigDecimal.TEN))
                .build();

        presenter.setCheckoutPreference(preference);

        presenter.initialize();
        assertFalse(provider.checkoutPreferenceRequested);
    }

    @Test
    public void ifPreferenceIsInvalidThenShowError() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference("dummy_id");

        provider.setCheckoutPreferenceResponse(preference);
        presenter.setCheckoutPreference(preference);
        presenter.initialize();
        assertTrue(view.showingError);
    }

    @Test
    public void ifCheckoutInitiatedThenRequestPaymentMethodSearch() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        presenter.setCheckoutPreference(preference);
        presenter.initialize();
        assertTrue(provider.paymentMethodSearchRequested);
    }

    //Flow started

    @Test
    public void ifCheckoutInitiatedThenStartPaymentMethodSelection() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        presenter.setCheckoutPreference(preference);
        presenter.initialize();
        assertTrue(view.showingPaymentMethodSelection);
    }

    //Response from payment methodSelection
    @Test
    public void ifOkPaymentMethodSelectionResponseReceivedThenStartRyC() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        presenter.setCheckoutPreference(preference);
        presenter.initialize();

        //Payment method off, no issuer, installments or token
        presenter.onPaymentMethodSelectionResponse(PaymentMethods.getPaymentMethodOff(), null, null, null, null, null, null);
        assertTrue(view.showingReviewAndConfirm);
    }

    @Test
    public void onBackFromPaymentMethodSelectionThenCancelCheckout() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        presenter.setCheckoutPreference(preference);
        presenter.initialize();

        //Payment method off, no issuer, installments or token
        presenter.onPaymentMethodSelectionCancel();
        assertTrue(view.checkoutCanceled);
    }

    //Review and confirm disabled
    @Test
    public void ifPaymentDataRequestedAndReviewConfirmDisabledThenFinishWithPaymentData() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);


        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        FlowPreference flowPreference = new FlowPreference.Builder()
                .disableReviewAndConfirmScreen()
                .build();
        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        //Payment method off, no issuer, installments or token
        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOff();
        presenter.onPaymentMethodSelectionResponse(paymentMethod, null, null, null, null, null, null);
        assertEquals(paymentMethod.getId(), view.paymentDataFinalResponse.getPaymentMethod().getId());
    }

    @Test
    public void ifPaymentRequestedAndReviewConfirmDisabledThenStartPaymentResultScreen() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getApprovedPayment());

        FlowPreference flowPreference = new FlowPreference.Builder()
                .disableReviewAndConfirmScreen()
                .build();

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();

        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);
        assertTrue(view.showingPaymentResult);
    }

    @Test
    public void whenPaymentRequestedAndOnReviewAndConfirmOkResponseThenCreatePayment() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getApprovedPayment());

        FlowPreference flowPreference = new FlowPreference.Builder()
                .disableReviewAndConfirmScreen()
                .build();

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertTrue(provider.paymentRequested);
    }

    @Test
    public void whenPaymentCreatedThenShowResultScreen() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getApprovedPayment());

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertTrue(view.showingPaymentResult);
    }

    @Test
    public void onPaymentResultScreenResponseThenFinishWithPaymentResponse() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        Payment payment = Payments.getApprovedPayment();
        provider.setPaymentResponse(payment);

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);

        presenter.onPaymentConfirmation();

        //On Payment Result Screen
        assertEquals(view.paymentFinalResponse, null);

        presenter.onPaymentResultResponse();

        assertEquals(view.paymentFinalResponse.getId(), payment.getId());
    }

    @Test
    public void whenPaymentMethodEditedAndPaymentConfirmedThenPayEditedPaymentMethod() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference preference = new CheckoutPreference.Builder()
                .setSite(Sites.ARGENTINA)
                .addItem(new Item("Item", BigDecimal.TEN))
                .build();

        presenter.setCheckoutPreference(preference);
        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);

        provider.setCampaignsResponse(Discounts.getCampaigns());
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getApprovedPayment());
        presenter.initialize();

        presenter.onPaymentMethodSelectionResponse(PaymentMethods.getPaymentMethodOff(), null, null, null, null, null, null);

        //User changes paymentMethod
        presenter.changePaymentMethod();

        assertTrue(view.showingPaymentMethodSelection);

        PaymentMethod editedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        presenter.onPaymentMethodSelectionResponse(editedPaymentMethod,
                Issuers.getIssuers().get(0),
                Installments.getInstallments().getPayerCosts().get(0),
                Tokens.getVisaToken(), null, null, null);

        assertTrue(view.showingReviewAndConfirm);

        presenter.onPaymentConfirmation();

        assertTrue(provider.paymentMethodPaid.getId().equals(editedPaymentMethod.getId()));
    }

    //Flow preference tests

    @Test
    public void whenDiscountDisabledThenDoNotMakeDiscountsAPICall() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        Payment payment = Payments.getApprovedPayment();
        provider.setPaymentResponse(payment);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .disableDiscount()
                .build();

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        assertFalse(provider.campaignsRequested);
    }

    @Test
    public void whenPaymentCreatedAndResultScreenDisabledThenFinishWithPaymentResponse() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        Payment payment = Payments.getApprovedPayment();
        provider.setPaymentResponse(payment);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .disablePaymentResultScreen()
                .build();

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertEquals(view.paymentFinalResponse.getId(), payment.getId());
    }

    @Test
    public void whenApprovedPaymentCreatedAndApprovedResultScreenDisabledThenFinishWithPaymentResponse() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        Payment payment = Payments.getApprovedPayment();
        provider.setPaymentResponse(payment);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .disablePaymentApprovedScreen()
                .build();

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertEquals(view.paymentFinalResponse.getId(), payment.getId());
    }

    @Test
    public void whenApprovedPaymentCreatedAndCongratsDisplayIsZeroThenFinishWithPaymentResponse() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);


        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        Payment payment = Payments.getApprovedPayment();
        provider.setPaymentResponse(payment);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .setCongratsDisplayTime(0)
                .build();

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertEquals(view.paymentFinalResponse.getId(), payment.getId());
    }

    @Test
    public void whenRejectedPaymentCreatedAndRejectedResultScreenDisabledThenFinishWithPaymentResponse() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        Payment payment = Payments.getRejectedPayment();
        provider.setPaymentResponse(payment);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .disablePaymentRejectedScreen()
                .build();

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertEquals(view.paymentFinalResponse.getId(), payment.getId());
    }

    @Test
    public void whenPendingPaymentCreatedAndPendingResultScreenDisabledThenFinishWithPaymentResponse() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        Payment payment = Payments.getPendingPayment();
        provider.setPaymentResponse(payment);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .disablePaymentPendingScreen()
                .build();

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertEquals(view.paymentFinalResponse.getId(), payment.getId());
    }

    // Forwarded flows
    @Test
    public void whenPaymentDataSetThenStartRyCScreen() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference preference = new CheckoutPreference.Builder()
                .setSite(Sites.ARGENTINA)
                .addItem(new Item("Item", BigDecimal.TEN))
                .build();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        presenter.setCheckoutPreference(preference);
        presenter.setPaymentDataInput(paymentData);

        provider.setCampaignsResponse(Discounts.getCampaigns());
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        presenter.initialize();

        assertTrue(!view.showingPaymentMethodSelection);
        assertTrue(view.showingReviewAndConfirm);
    }

    @Test
    public void whenPaymentDataSetAndReviewAndConfirmDisabledThenStartRyCScreenOnStartButSkipLater() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .disableReviewAndConfirmScreen()
                .build();

        CheckoutPreference preference = new CheckoutPreference.Builder()
                .setSite(Sites.ARGENTINA)
                .addItem(new Item("Item", BigDecimal.TEN))
                .build();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        presenter.setCheckoutPreference(preference);
        presenter.setPaymentDataInput(paymentData);
        presenter.setFlowPreference(flowPreference);
        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE);

        provider.setCampaignsResponse(Discounts.getCampaigns());
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        presenter.initialize();

        //Starts in RyC
        assertTrue(view.showingReviewAndConfirm);

        //User changes paymentMethod
        presenter.changePaymentMethod();

        //Starts payment method selection
        assertTrue(view.showingPaymentMethodSelection);

        String dummyId = "anotherId";
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(dummyId);
        presenter.onPaymentMethodSelectionResponse(paymentMethod, null, null, null, null, null, null);

        //Presenter skips RyC, responds payment data
        assertTrue(view.paymentDataFinalResponse.getPaymentMethod().getId().equals(dummyId));
    }

    @Test
    public void whenPaymentResultSetThenStartResultScreen() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference preference = new CheckoutPreference.Builder()
                .setSite(Sites.ARGENTINA)
                .addItem(new Item("Item", BigDecimal.TEN))
                .build();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentData(paymentData)
                .setPaymentStatus(Payment.StatusCodes.STATUS_APPROVED)
                .build();

        presenter.setCheckoutPreference(preference);
        presenter.setPaymentResultInput(paymentResult);

        provider.setCampaignsResponse(Discounts.getCampaigns());
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        presenter.initialize();

        assertTrue(!view.showingPaymentMethodSelection);
        assertTrue(!view.showingReviewAndConfirm);
        assertTrue(view.showingPaymentResult);
    }

    @Test
    public void whenPaymentResultSetAndUserLeavesScreenThenRespondWithoutPayment() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference preference = new CheckoutPreference.Builder()
                .setSite(Sites.ARGENTINA)
                .addItem(new Item("Item", BigDecimal.TEN))
                .build();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentData(paymentData)
                .setPaymentStatus(Payment.StatusCodes.STATUS_APPROVED)
                .build();

        presenter.setCheckoutPreference(preference);
        presenter.setPaymentResultInput(paymentResult);

        provider.setCampaignsResponse(Discounts.getCampaigns());
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        presenter.initialize();
        assertTrue(view.showingPaymentResult);

        presenter.onPaymentResultResponse();

        assertTrue(view.finishedCheckoutWithoutPayment);
    }

    // Payment recovery flow
    @Test
    public void ifPaymentRecoveryRequiredThenStartPaymentRecoveryFlow() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference("dummy_id");

        Payer payer = new Payer();
        payer.setEmail("unemail@gmail.com");
        preference.setPayer(payer);

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getCallForAuthPayment());

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();

        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();
        assertTrue(view.showingPaymentResult);
        presenter.onPaymentResultCancel(PaymentResult.RECOVER_PAYMENT);
        assertTrue(view.showingPaymentRecoveryFlow);
        assertEquals(view.paymentRecoveryRequested.getPaymentMethod().getId(), paymentMethod.getId());
    }

    @Test
    public void onTokenRecoveryFlowOkResponseThenCreatePayment() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference("dummy_id");
        Payer payer = new Payer();
        payer.setEmail("unemail@gmail.com");
        preference.setPayer(payer);

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getCallForAuthPayment());

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();

        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();
        assertTrue(view.showingPaymentResult);
        presenter.onPaymentResultCancel(PaymentResult.RECOVER_PAYMENT);
        assertTrue(view.showingPaymentRecoveryFlow);
        assertEquals(view.paymentRecoveryRequested.getPaymentMethod().getId(), paymentMethod.getId());

        presenter.onCardFlowResponse(paymentMethod, issuer, payerCost, token, null);
        assertTrue(view.showingPaymentResult);

        assertTrue(paymentMethod.getId().equals(provider.paymentMethodPaid.getId()));
    }

    @Test
    public void ifPaymentRecoveryRequiredWithInvalidPaymentMethodThenShowError() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference("dummy_id");

        Payer payer = new Payer();
        payer.setEmail("unemail@gmail.com");
        preference.setPayer(payer);

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getCallForAuthPayment());

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.initialize();

        presenter.onPaymentMethodSelectionResponse(PaymentMethods
                .getPaymentMethodOff(), null, null, null, null, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();
        assertTrue(view.showingPaymentResult);
        presenter.onPaymentResultCancel(PaymentResult.RECOVER_PAYMENT);
        assertTrue(view.showingError);
    }

    //Backs
    @Test
    public void ifCheckoutInitiatedAndUserPressesBackCancelCheckout() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        presenter.setCheckoutPreference(preference);
        presenter.initialize();
        assertTrue(view.showingPaymentMethodSelection);
        presenter.onPaymentMethodSelectionCancel();
        assertTrue(view.checkoutCanceled);
    }

    @Test
    public void ifReviewAndConfirmShownAndUserPressesBackThenRestartPaymentMethodSelection() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        presenter.setCheckoutPreference(preference);
        presenter.initialize();
        assertTrue(view.showingPaymentMethodSelection);
        presenter.onPaymentMethodSelectionResponse(PaymentMethods.getPaymentMethodOff(), null, null, null, null, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onReviewAndConfirmCancel();
        assertTrue(view.showingPaymentMethodSelection);
    }

    @Test
    public void ifPaymentRecoveryShownAndUserPressesBackThenRestartPaymentMethodSelection() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getCallForAuthPayment());

        presenter.setCheckoutPreference(preference);

        presenter.initialize();
        assertTrue(view.showingPaymentMethodSelection);
        presenter.onPaymentMethodSelectionResponse(PaymentMethods.getPaymentMethodOnVisa(),
                Issuers.getIssuers().get(0),
                Installments.getInstallments().getPayerCosts().get(0), Tokens.getVisaToken(), null, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();
        assertTrue(view.showingPaymentResult);
        presenter.onPaymentResultCancel(PaymentResult.RECOVER_PAYMENT);
        assertTrue(view.showingPaymentRecoveryFlow);
        presenter.onCardFlowCancel();
        assertTrue(view.showingPaymentMethodSelection);
    }

    @Test
    public void ifPaymentMethodEditionRequestedAndUserPressesBackTwiceCancelCheckout() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        presenter.setCheckoutPreference(preference);

        presenter.initialize();
        assertTrue(view.showingPaymentMethodSelection);

        presenter.onPaymentMethodSelectionResponse(PaymentMethods.getPaymentMethodOff(), null, null, null, null, null, null);
        assertTrue(view.showingReviewAndConfirm);

        presenter.changePaymentMethod();
        assertTrue(view.showingPaymentMethodSelection);

        presenter.onPaymentMethodSelectionCancel();
        assertTrue(view.showingReviewAndConfirm);

        presenter.onReviewAndConfirmCancel();
        assertTrue(view.showingPaymentMethodSelection);

        presenter.onPaymentMethodSelectionCancel();
        assertTrue(view.checkoutCanceled);
    }

    //Payment tests
    @Test
    public void whenPaymentCreationRequestedThenGenerateTransactionId() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getApprovedPayment());

        presenter.setIdempotencyKeySeed("TEST");
        presenter.setCheckoutPreference(preference);
        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.initialize();

        //Payment method off, no issuer, installments or token
        presenter.onPaymentMethodSelectionResponse(PaymentMethods.getPaymentMethodOff(), null, null, null, null, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();

        assertTrue(provider.paymentRequested);
        assertTrue(!TextUtil.isEmpty(provider.transactionId));
    }

    @Test
    public void whenCustomerAvailableAndPaymentCreationRequestedThenCreatePaymentWithCustomerId() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getApprovedPayment());
        provider.setCustomerResponse(Customers.getCustomerWithCards());

        presenter.setIdempotencyKeySeed("TEST");
        presenter.setCheckoutPreference(preference);
        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.initialize();

        //Payment method off, no issuer, installments or token
        presenter.onPaymentMethodSelectionResponse(PaymentMethods.getPaymentMethodOff(), null, null, null, null, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();

        assertTrue(provider.paymentRequested);
        assertTrue(!TextUtil.isEmpty(provider.paymentCustomerId));
    }

    //Timer tests
    @Test
    public void ifTimeInFlowPreferenceSetThenStartCheckoutTimer() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        //Flow preference with timer
        FlowPreference flowPreference = new FlowPreference.Builder()
                .setCheckoutTimer(5)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        presenter.setCheckoutPreference(preference);
        presenter.setFlowPreference(flowPreference);

        Timer timer = new MockedTimer();
        presenter.setTimer(timer);
        presenter.initialize();

        assertTrue(timer.isTimerEnabled());
    }

    @Test
    public void ifTimeInFlowPreferenceNotSetThenDoNotStartCheckoutTimer() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        presenter.setCheckoutPreference(preference);

        Timer timer = new MockedTimer();
        presenter.setTimer(timer);
        presenter.initialize();

        assertFalse(timer.isTimerEnabled());
    }

    @Test
    public void ifPaymentResultApprovedSetAndTokenWithESCAndESCEnabledThenSaveESC() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("description", new BigDecimal(100)))
                .setSite(Sites.ARGENTINA)
                .setPayerAccessToken("ACCESS_TOKEN")
                .build();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        //Token with card Id and ESC
        Token token = Tokens.getTokenWithESC();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setIssuer(issuer);
        paymentData.setPayerCost(payerCost);
        paymentData.setToken(token);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .enableESC()
                .build();

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentData(paymentData)
                .setPaymentId(1234L)
                .setPaymentStatus(Payment.StatusCodes.STATUS_APPROVED)
                .setPaymentStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED)
                .build();

        presenter.setCheckoutPreference(checkoutPreference);
        presenter.setPaymentResultInput(paymentResult);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        assertTrue(provider.saveESCRequested);
    }

    @Test
    public void ifPaymentResultApprovedSetAndESCEnabledButTokenHasNoESCThenDontSaveESC() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("description", new BigDecimal(100)))
                .setSite(Sites.ARGENTINA)
                .setPayerAccessToken("ACCESS_TOKEN")
                .build();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        //Token without card id or ESC
        Token token = Tokens.getVisaToken();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setIssuer(issuer);
        paymentData.setPayerCost(payerCost);
        paymentData.setToken(token);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .enableESC()
                .build();

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentData(paymentData)
                .setPaymentId(1234L)
                .setPaymentStatus(Payment.StatusCodes.STATUS_APPROVED)
                .setPaymentStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED)
                .build();

        presenter.setCheckoutPreference(checkoutPreference);
        presenter.setPaymentResultInput(paymentResult);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        assertFalse(provider.saveESCRequested);
    }

    @Test
    public void ifPaymentResultApprovedSetAndESCEnabledThenShowPaymentResultScreen() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("description", new BigDecimal(100)))
                .setSite(Sites.ARGENTINA)
                .setPayerAccessToken("ACCESS_TOKEN")
                .build();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        //Token with card Id and ESC
        Token token = Tokens.getTokenWithESC();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setIssuer(issuer);
        paymentData.setPayerCost(payerCost);
        paymentData.setToken(token);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .enableESC()
                .build();

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentData(paymentData)
                .setPaymentId(1234L)
                .setPaymentStatus(Payment.StatusCodes.STATUS_APPROVED)
                .setPaymentStatusDetail(Payment.StatusCodes.STATUS_DETAIL_ACCREDITED)
                .build();

        presenter.setCheckoutPreference(checkoutPreference);
        presenter.setPaymentResultInput(paymentResult);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        assertTrue(view.showingPaymentResult);
    }

    @Test
    public void onCreatePaymentWithESCTokenErrorThenDeleteESC() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("description", new BigDecimal(100)))
                .setSite(Sites.ARGENTINA)
                .setPayerAccessToken("ACCESS_TOKEN")
                .build();

        provider.setCheckoutPreferenceResponse(checkoutPreference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        ApiException apiException = Payments.getInvalidESCPayment();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        provider.setPaymentResponse(mpException);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .enableESC()
                .disableReviewAndConfirmScreen()
                .build();

        presenter.setCheckoutPreference(checkoutPreference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getTokenWithESC();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertTrue(provider.paymentRequested);

        Cause cause = provider.failedResponse.getApiException().getCause().get(0);
        assertEquals(cause.getCode(), ApiException.ErrorCodes.INVALID_PAYMENT_WITH_ESC);
        assertTrue(provider.deleteESCRequested);
    }

    @Test
    public void onCreatePaymentWithESCTokenErrorThenRequestSecurityCode() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("description", new BigDecimal(100)))
                .setSite(Sites.ARGENTINA)
                .setPayerAccessToken("ACCESS_TOKEN")
                .build();

        provider.setCheckoutPreferenceResponse(checkoutPreference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        ApiException apiException = Payments.getInvalidESCPayment();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        provider.setPaymentResponse(mpException);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .enableESC()
                .disableReviewAndConfirmScreen()
                .build();

        presenter.setCheckoutPreference(checkoutPreference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getTokenWithESC();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertTrue(provider.paymentRequested);

        provider.paymentRequested = false;

        assertTrue(view.showingPaymentRecoveryFlow);
        PaymentRecovery paymentRecovery = view.paymentRecoveryRequested;
        assertTrue(paymentRecovery.isStatusDetailInvalidESC());
        assertTrue(paymentRecovery.isTokenRecoverable());

        //Response from Card Vault with new Token
        presenter.onCardFlowResponse(paymentMethod, issuer, payerCost, token, null);
        assertTrue(provider.paymentRequested);

        provider.setPaymentResponse(Payments.getApprovedPayment());
        assertNotNull(provider.paymentResponse);

    }

    @Test
    public void createPaymentWithESCTokenThenSaveESC() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("description", new BigDecimal(100)))
                .setSite(Sites.ARGENTINA)
                .setPayerAccessToken("ACCESS_TOKEN")
                .build();

        provider.setCheckoutPreferenceResponse(checkoutPreference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        provider.setPaymentResponse(Payments.getApprovedPayment());

        FlowPreference flowPreference = new FlowPreference.Builder()
                .enableESC()
                .disableReviewAndConfirmScreen()
                .build();

        presenter.setCheckoutPreference(checkoutPreference);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getTokenWithESC();

        Card mockedCard = Cards.getCard();
        mockedCard.setId("12345");
        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, mockedCard, null);

        //Response from Review And confirm
        assertTrue(provider.paymentRequested);
        assertNotNull(provider.paymentResponse);
        assertTrue(provider.saveESCRequested);
    }

    @Test
    public void ifPaymentResultInvalidESCSetAndESCEnabledThenDontSaveESC() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("description", new BigDecimal(100)))
                .setSite(Sites.ARGENTINA)
                .setPayerAccessToken("ACCESS_TOKEN")
                .build();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        //Token without card id or ESC
        Token token = Tokens.getVisaToken();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setIssuer(issuer);
        paymentData.setPayerCost(payerCost);
        paymentData.setToken(token);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .enableESC()
                .build();

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentData(paymentData)
                .setPaymentId(1234L)
                .setPaymentStatus(Payment.StatusCodes.STATUS_REJECTED)
                .setPaymentStatusDetail(Payment.StatusCodes.STATUS_DETAIL_INVALID_ESC)
                .build();

        presenter.setCheckoutPreference(checkoutPreference);
        presenter.setPaymentResultInput(paymentResult);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        assertFalse(provider.saveESCRequested);
    }

    @Test
    public void ifPaymentResultInvalidESCSetAndESCEnabledThenDeleteESCSaved() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("description", new BigDecimal(100)))
                .setSite(Sites.ARGENTINA)
                .setPayerAccessToken("ACCESS_TOKEN")
                .build();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        //Token with Card ID (because it was created with ESC enabled)
        Token token = Tokens.getTokenWithESC();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setIssuer(issuer);
        paymentData.setPayerCost(payerCost);
        paymentData.setToken(token);

        FlowPreference flowPreference = new FlowPreference.Builder()
                .enableESC()
                .build();

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentData(paymentData)
                .setPaymentId(1234L)
                .setPaymentStatus(Payment.StatusCodes.STATUS_REJECTED)
                .setPaymentStatusDetail(Payment.StatusCodes.STATUS_DETAIL_INVALID_ESC)
                .build();

        presenter.setCheckoutPreference(checkoutPreference);
        presenter.setPaymentResultInput(paymentResult);
        presenter.setFlowPreference(flowPreference);
        presenter.initialize();

        assertTrue(provider.deleteESCRequested);
    }

    @Test
    public void ifPayerDataCollectedAndPayerInPreferenceThenUseBothForPayment() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference("dummy_id");

        Payer preferencePayer = new Payer();
        preferencePayer.setEmail("unemail@gmail.com");
        preferencePayer.setAccessToken("AT");
        preference.setPayer(preferencePayer);

        String firstName = "FirstName";
        String lastName = "LastName";
        Identification identification = new Identification();
        identification.setType("cpf");
        identification.setNumber("111");

        Payer collectedPayer = new Payer();
        collectedPayer.setFirstName(firstName);
        collectedPayer.setLastName(lastName);
        collectedPayer.setIdentification(identification);

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getCallForAuthPayment());

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.initialize();

        presenter.onPaymentMethodSelectionResponse(PaymentMethods.getPaymentMethodOff(), null, null, null, null, null, collectedPayer);
        presenter.onPaymentConfirmation();

        Assert.assertEquals(provider.payerPosted.getEmail(), preferencePayer.getEmail());
        Assert.assertEquals(provider.payerPosted.getAccessToken(), preferencePayer.getAccessToken());
        Assert.assertEquals(provider.payerPosted.getFirstName(), firstName);
        Assert.assertEquals(provider.payerPosted.getLastName(), lastName);
        Assert.assertEquals(provider.payerPosted.getIdentification().getType(), identification.getType());
        Assert.assertEquals(provider.payerPosted.getIdentification().getNumber(), identification.getNumber());
    }

    @Test
    public void ifOnlyPayerFromPreferenceThenUseItForPayment() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference("dummy_id");

        Payer preferencePayer = new Payer();
        preferencePayer.setEmail("unemail@gmail.com");
        preferencePayer.setAccessToken("AT");
        preference.setPayer(preferencePayer);

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());
        provider.setPaymentResponse(Payments.getCallForAuthPayment());

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.initialize();

        presenter.onPaymentMethodSelectionResponse(PaymentMethods.getPaymentMethodOff(), null, null, null, null, null, null);
        presenter.onPaymentConfirmation();

        Assert.assertEquals(provider.payerPosted.getEmail(), preferencePayer.getEmail());
        Assert.assertEquals(provider.payerPosted.getAccessToken(), preferencePayer.getAccessToken());
    }

    @Test
    public void onIdentificationInvalidAndErrorShownThenGoBackToPaymentMethodSelection() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, without items
        CheckoutPreference preference = new CheckoutPreference("dummy_id");

        Payer preferencePayer = new Payer();
        preferencePayer.setEmail("unemail@gmail.com");
        preferencePayer.setAccessToken("AT");
        preference.setPayer(preferencePayer);

        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        ApiException apiException = Payments.getInvalidIdentificationPayment();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        provider.setPaymentResponse(mpException);

        presenter.setRequestedResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        presenter.setCheckoutPreference(preference);
        presenter.initialize();

        presenter.onErrorCancel(mpException);
        assertTrue(view.showingPaymentMethodSelection);
    }

    @Test
    public void createPaymentWithInvalidIdentificationThenShowError() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("description", new BigDecimal(100)))
                .setSite(Sites.ARGENTINA)
                .setPayerAccessToken("ACCESS_TOKEN")
                .build();

        provider.setCheckoutPreferenceResponse(checkoutPreference);
        provider.setPaymentMethodSearchResponse(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA());

        ApiException apiException = Payments.getInvalidIdentificationPayment();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        provider.setPaymentResponse(mpException);

        presenter.setCheckoutPreference(checkoutPreference);
        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getTokenWithESC();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, null, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertTrue(provider.paymentRequested);

        Cause cause = provider.failedResponse.getApiException().getCause().get(0);
        assertEquals(cause.getCode(), ApiException.ErrorCodes.INVALID_IDENTIFICATION_NUMBER);
        assertTrue(view.showingError);
    }

    @Test
    public void ifNotNewFlowThenDoNotTrackInit() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        //Real preference, with items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        presenter.setCheckoutPreference(preference);

        //With a PaymentData input
        presenter.setPaymentDataInput(paymentData);

        presenter.initialize();
        assertFalse(view.initTracked);
    }

    @Test
    public void ifNewFlowThenDoTrackInit() {
        MockedProvider provider = new MockedProvider();
        MockedView view = new MockedView();

        CheckoutPresenter presenter = new CheckoutPresenter();
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);

        //Real preference, with items
        CheckoutPreference preference = new CheckoutPreference.Builder()
                .addItem(new Item("id", BigDecimal.TEN))
                .setSite(Sites.ARGENTINA)
                .build();

        provider.setCheckoutPreferenceResponse(preference);
        presenter.setCheckoutPreference(preference);

        presenter.initialize();
        assertTrue(view.initTracked);
    }

    private class MockedView implements CheckoutView {

        private MercadoPagoError errorShown;
        private boolean showingError = false;
        private boolean showingPaymentMethodSelection = false;
        private boolean showingReviewAndConfirm = false;
        private boolean initTracked = false;
        private PaymentData paymentDataFinalResponse;
        private boolean showingPaymentResult = false;
        private boolean checkoutCanceled = false;
        private Payment paymentFinalResponse;
        private boolean finishedCheckoutWithoutPayment = false;
        private boolean showingPaymentRecoveryFlow = false;
        private PaymentRecovery paymentRecoveryRequested;

        @Override
        public void showError(MercadoPagoError error) {
            this.showingError = true;
            this.errorShown = error;
        }

        @Override
        public void showProgress() {

        }

        @Override
        public void hideProgress() {

        }

        @Override
        public void showReviewAndConfirm() {
            showingPaymentMethodSelection = false;
            showingReviewAndConfirm = true;
            showingPaymentResult = false;
            showingPaymentRecoveryFlow = false;
        }

        @Override
        public void showPaymentMethodSelection() {
            showingPaymentMethodSelection = true;
            showingReviewAndConfirm = false;
            showingPaymentResult = false;
            showingPaymentRecoveryFlow = false;
        }

        @Override
        public void startPaymentMethodEdition() {
            showingPaymentMethodSelection = true;
            showingReviewAndConfirm = false;
            showingPaymentResult = false;
            showingPaymentRecoveryFlow = false;
        }

        @Override
        public void showPaymentResult(PaymentResult paymentResult) {
            showingPaymentMethodSelection = false;
            showingReviewAndConfirm = false;
            showingPaymentResult = true;
            showingPaymentRecoveryFlow = false;
        }

        @Override
        public void backToReviewAndConfirm() {
            showingPaymentMethodSelection = false;
            showingReviewAndConfirm = true;
            showingPaymentResult = false;
            showingPaymentRecoveryFlow = false;
        }

        @Override
        public void backToPaymentMethodSelection() {
            showingPaymentMethodSelection = true;
            showingReviewAndConfirm = false;
            showingPaymentResult = false;
            showingPaymentRecoveryFlow = false;
        }

        @Override
        public void finishWithPaymentResult() {
            finishedCheckoutWithoutPayment = true;
        }

        @Override
        public void finishWithPaymentResult(Integer customResultCode) {

        }

        @Override
        public void finishWithPaymentResult(Payment payment) {
            paymentFinalResponse = payment;
        }

        @Override
        public void finishWithPaymentResult(Integer customResultCode, Payment payment) {

        }

        @Override
        public void finishWithPaymentDataResult(PaymentData paymentData, Boolean paymentMethodEdited) {
            paymentDataFinalResponse = paymentData;
        }

        @Override
        public void cancelCheckout() {
            checkoutCanceled = true;
        }

        @Override
        public void cancelCheckout(MercadoPagoError mercadoPagoError) {

        }

        @Override
        public void cancelCheckout(Integer customResultCode, PaymentData paymentData, Boolean paymentMethodEdited) {

        }

        @Override
        public void startPaymentRecoveryFlow(PaymentRecovery paymentRecovery) {
            paymentRecoveryRequested = paymentRecovery;
            showingPaymentRecoveryFlow = true;
            showingPaymentMethodSelection = false;
            showingReviewAndConfirm = false;
            showingPaymentResult = false;
        }

        @Override
        public void initializeMPTracker() {

        }

        @Override
        public void trackScreen() {
            initTracked = true;
        }

        @Override
        public void finishFromReviewAndConfirm() {

        }

        @Override
        public void showHook(Hook hook, int requestCode) {

        }

        @Override
        public void showPaymentProcessor() {

        }
    }

    public class MockedProvider implements CheckoutProvider {

        private boolean campaignsRequested = false;
        private boolean directDiscountRequested = false;
        private List<Campaign> campaigns;
        private boolean checkoutPreferenceRequested = false;
        private CheckoutPreference preference;
        private boolean paymentMethodSearchRequested = false;
        private PaymentMethodSearch paymentMethodSearchResponse;
        private Payment paymentResponse;
        private boolean paymentRequested;
        private Customer customerResponse;
        private boolean saveESCRequested = false;
        private boolean deleteESCRequested = false;

        private String transactionId;
        private String paymentCustomerId;
        private PaymentMethod paymentMethodPaid;
        private Payer payerPosted;

        private boolean shouldFail = false;
        private MercadoPagoError failedResponse;

        @Override
        public void getCheckoutPreference(String checkoutPreferenceId, OnResourcesRetrievedCallback<CheckoutPreference> onResourcesRetrievedCallback) {
            checkoutPreferenceRequested = true;
            onResourcesRetrievedCallback.onSuccess(preference);
        }

        @Override
        public void getDiscountCampaigns(OnResourcesRetrievedCallback<List<Campaign>> callback) {
            this.campaignsRequested = true;
            callback.onSuccess(campaigns);
        }

        @Override
        public void getDirectDiscount(BigDecimal amount, String payerEmail, OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
            this.directDiscountRequested = true;
            onResourcesRetrievedCallback.onSuccess(null);
        }

        @Override
        public void getPaymentMethodSearch(BigDecimal amount, List<String> excludedPaymentTypes, List<String> excludedPaymentMethods, Payer payer, Site site, OnResourcesRetrievedCallback<PaymentMethodSearch> onPaymentMethodSearchRetrievedCallback, OnResourcesRetrievedCallback<Customer> onCustomerRetrievedCallback) {
            this.paymentMethodSearchRequested = true;
            onPaymentMethodSearchRetrievedCallback.onSuccess(paymentMethodSearchResponse);
            if (customerResponse != null) {
                onCustomerRetrievedCallback.onSuccess(customerResponse);
            }
        }

        @Override
        public String getCheckoutExceptionMessage(CheckoutPreferenceException exception) {
            return null;
        }

        @Override
        public String getCheckoutExceptionMessage(IllegalStateException exception) {
            return null;
        }

        @Override
        public void createPayment(String transactionId, CheckoutPreference checkoutPreference, PaymentData paymentData, Boolean binaryMode, String customerId, OnResourcesRetrievedCallback<Payment> onResourcesRetrievedCallback) {
            this.paymentMethodPaid = paymentData.getPaymentMethod();
            this.transactionId = transactionId;
            this.paymentCustomerId = customerId;
            this.paymentRequested = true;
            this.payerPosted = paymentData.getPayer();
            if (shouldFail) {
                onResourcesRetrievedCallback.onFailure(failedResponse);
            } else {
                onResourcesRetrievedCallback.onSuccess(paymentResponse);
            }
        }

        @Override
        public boolean saveESC(String cardId, String value) {
            this.saveESCRequested = true;
            return saveESCRequested;
        }

        @Override
        public void deleteESC(String cardId) {
            this.deleteESCRequested = true;
        }

        public void setCampaignsResponse(List<Campaign> campaigns) {
            this.campaigns = campaigns;
        }

        public void setCheckoutPreferenceResponse(CheckoutPreference preference) {
            this.preference = preference;
        }

        public void setPaymentMethodSearchResponse(PaymentMethodSearch paymentMethodSearchResponse) {
            this.paymentMethodSearchResponse = paymentMethodSearchResponse;
        }

        public void setPaymentResponse(Payment paymentResponse) {
            this.paymentResponse = paymentResponse;
        }

        public void setCustomerResponse(Customer customerResponse) {
            this.customerResponse = customerResponse;
        }

        public void setPaymentResponse(MercadoPagoError error) {
            this.shouldFail = true;
            this.failedResponse = error;
        }
    }

    private class MockedTimer implements Timer {

        private boolean enabled = false;

        @Override
        public void start(long seconds) {
            enabled = true;
        }

        @Override
        public void stop() {

        }

        @Override
        public Boolean isTimerEnabled() {
            return enabled;
        }

        @Override
        public void setOnFinishListener(FinishListener listener) {

        }

        @Override
        public void finishCheckout() {

        }
    }
}
