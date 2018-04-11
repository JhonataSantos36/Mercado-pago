package com.mercadopago.cardvault;

import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.model.Sites;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.mocks.Cards;
import com.mercadopago.mocks.Installments;
import com.mercadopago.mocks.Issuers;
import com.mercadopago.mocks.PayerCosts;
import com.mercadopago.mocks.PaymentMethods;
import com.mercadopago.mocks.Tokens;
import com.mercadopago.model.Card;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.SavedESCCardToken;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.lite.preferences.PaymentPreference;
import com.mercadopago.presenters.CardVaultPresenter;
import com.mercadopago.providers.CardVaultProvider;
import com.mercadopago.views.CardVaultView;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by vaserber on 4/20/17.
 */

public class CardVaultPresenterTest {

    @Test
    public void ifInstallmentsEnabledNotSetThenDefaultValueIsTrue() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        assertTrue(presenter.isInstallmentsEnabled());
    }

    @Test
    public void ifInstallmentsEnabledAndSiteNotSetThenShowMissingSiteError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setAmount(new BigDecimal(100));

        presenter.initialize();

        assertEquals(MockedProvider.MISSING_SITE, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifInstallmentsEnabledAndAmountNotSetThenShowMissingAmountError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);

        presenter.initialize();

        assertEquals(MockedProvider.MISSING_AMOUNT, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifInstallmentsEnabledAndSavedCardSetThenGetInstallmentsForCard() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        List<PayerCost> expectedPayerCosts = presenter.getPayerCostList();
        List<PayerCost> mockedPayerCosts = installmentsList.get(0).getPayerCosts();

        assertEquals(expectedPayerCosts.size(), mockedPayerCosts.size());
        assertTrue(expectedPayerCosts.size() > 1);
        assertNull(presenter.getPayerCost());
    }

    @Test
    public void ifInstallmentsNotEnabledAndSavedCardSetThenDontGetInstallments() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());
        presenter.setInstallmentsEnabled(false);

        presenter.initialize();

        assertNull(presenter.getPayerCostList());
    }

    @Test
    public void ifInstallmentsForCardHasOnePayerCostThenSelectIt() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsListWithUniquePayerCost();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        List<PayerCost> expectedPayerCosts = presenter.getPayerCostList();
        assertTrue(expectedPayerCosts.size() == 1);
        assertNotNull(presenter.getPayerCost());
    }

    @Test
    public void ifInstallmentsForCardIsEmptyThenShowErrorMessage() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = new ArrayList<>();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        assertEquals(MockedProvider.MISSING_INSTALLMENTS, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifInstallmentsForCardHasMultiplePayerCostsThenShowErrorMessage() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsListWithMultiplePayerCost();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        assertEquals(MockedProvider.MULTIPLE_INSTALLMENTS, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifInstallmentsForCardFailsThenShowErrorMessage() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        ApiException apiException = Installments.getDoNotFindInstallmentsException();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        provider.setResponse(mpException);


        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        assertTrue(provider.failedResponse.getApiException().getError().equals(provider.INSTALLMENTS_NOT_FOUND_ERROR));

    }

    @Test
    public void ifInstallmentsEnabledAndSavedCardSetThenStartInstallmentsFlow() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        assertTrue(mockedView.installmentsFlowStarted);
    }

    @Test
    public void ifInstallmentsNotEnabledAndSavedCardSetThenStartSecurityCodeFlow() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());
        presenter.setInstallmentsEnabled(false);

        presenter.initialize();

        assertFalse(mockedView.installmentsFlowStarted);
        assertTrue(mockedView.securityCodeFlowStarted);
    }

    @Test
    public void ifPaymentPreferenceHasDefaultInstallmentsForSavedCardThenSelectIt() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        int mockedDefaultInstallment = 3;

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(mockedDefaultInstallment);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());
        presenter.setPaymentPreference(paymentPreference);

        presenter.initialize();

        assertNotNull(presenter.getPayerCost());
        assertTrue(presenter.getPayerCost().getInstallments() == mockedDefaultInstallment);
    }

    @Test
    public void ifPaymentPreferenceHasDefaultInstallmentsForSavedCardThenStartSecurityCodeFlow() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        int mockedDefaultInstallment = 3;

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(mockedDefaultInstallment);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());
        presenter.setPaymentPreference(paymentPreference);

        presenter.initialize();

        assertTrue(mockedView.securityCodeFlowStarted);
    }

    @Test
    public void ifInstallmentsForCardHasNoPayerCostsThenShowErrorMessage() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsListWithoutPayerCosts();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        assertEquals(MockedProvider.MISSING_PAYER_COSTS, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifPaymentRecoveryIsSetThenStartTokenRecoverableFlow() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        Token mockedToken = Tokens.getToken();
        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        PayerCost mockedPayerCost = PayerCosts.getPayerCost();
        Issuer mockedIssuer = Issuers.getIssuerMLA();
        String mockedPaymentStatus = Payment.StatusCodes.STATUS_REJECTED;
        String mockedPaymentStatusDeatil = Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE;

        PaymentRecovery mockedPaymentRecovery = new PaymentRecovery(mockedToken, mockedPaymentMethod, mockedPayerCost, mockedIssuer, mockedPaymentStatus, mockedPaymentStatusDeatil);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setPaymentRecovery(mockedPaymentRecovery);

        presenter.initialize();

        assertNotNull(presenter.getCardInfo());
        assertNotNull(presenter.getPaymentMethod());
        assertNotNull(presenter.getToken());
        assertTrue(mockedView.recoverableTokenFlowStarted);
        assertTrue(mockedView.securityCodeFlowStarted);
    }

    @Test
    public void ifNothingIsSetThenStartNewCardFlow() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();


        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));

        presenter.initialize();

        assertTrue(mockedView.guessingFlowStarted);
    }

    @Test
    public void whenNewCardDataAskedButNoIssuerResolvedThenAskForIssuer() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));

        presenter.initialize();

        Token mockedToken = Tokens.getToken();
        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        PayerCost mockedPayerCost = PayerCosts.getPayerCost();
        Issuer mockedIssuer = null;
        List<PayerCost> mockedPayerCostList = PayerCosts.getPayerCostList();
        List<Issuer> mockedIssuerList = Issuers.getIssuersListMLA();
        Discount mockedDiscount = null;
        Boolean directDiscountEnabled = false;
        Boolean discountEnabled = false;

        //Response from GuessingCardActivity, without an issuer selected
        presenter.resolveNewCardRequest(mockedPaymentMethod, mockedToken, directDiscountEnabled, discountEnabled, mockedPayerCost, mockedIssuer, mockedPayerCostList, mockedIssuerList, mockedDiscount);

        assertTrue(mockedView.issuerFlowStarted);
    }

    @Test
    public void whenNewCardDataAskedButNoPayerCostResolvedThenAskForInstallments() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));

        presenter.initialize();

        Token mockedToken = Tokens.getToken();
        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        PayerCost mockedPayerCost = null;
        Issuer mockedIssuer = Issuers.getIssuerMLA();
        List<PayerCost> mockedPayerCostList = PayerCosts.getPayerCostList();
        List<Issuer> mockedIssuerList = Issuers.getIssuersListMLA();
        Discount mockedDiscount = null;
        Boolean directDiscountEnabled = false;
        Boolean discountEnabled = false;

        //Response from GuessingCardActivity, with an issuer selected
        presenter.resolveNewCardRequest(mockedPaymentMethod, mockedToken, directDiscountEnabled, discountEnabled, mockedPayerCost, mockedIssuer, mockedPayerCostList, mockedIssuerList, mockedDiscount);

        assertFalse(mockedView.issuerFlowStarted);
        assertTrue(mockedView.installmentsFlowStarted);
    }

    @Test
    public void whenNewCardDataAskedAndIssuerAndPayerCostResolvedThenFinishWithResult() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));

        presenter.initialize();

        Token mockedToken = Tokens.getToken();
        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        PayerCost mockedPayerCost = PayerCosts.getPayerCost();
        Issuer mockedIssuer = Issuers.getIssuerMLA();
        List<PayerCost> mockedPayerCostList = PayerCosts.getPayerCostList();
        List<Issuer> mockedIssuerList = Issuers.getIssuersListMLA();
        Discount mockedDiscount = null;
        Boolean directDiscountEnabled = false;
        Boolean discountEnabled = false;

        //Response from GuessingCardActivity, with an issuer selected
        presenter.resolveNewCardRequest(mockedPaymentMethod, mockedToken, directDiscountEnabled, discountEnabled, mockedPayerCost, mockedIssuer, mockedPayerCostList, mockedIssuerList, mockedDiscount);

        assertFalse(mockedView.issuerFlowStarted);
        assertFalse(mockedView.installmentsFlowStarted);
        assertTrue(mockedView.finishedWithResult);
    }

    @Test
    public void onIssuerResolvedAndPayerCostNotResolvedThenAskForPayerCost() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));

        presenter.initialize();

        Token mockedToken = Tokens.getToken();
        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer mockedIssuer = Issuers.getIssuerMLA();
        List<PayerCost> mockedPayerCostList = PayerCosts.getPayerCostList();
        Boolean directDiscountEnabled = false;

        presenter.setToken(mockedToken);
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.setPayerCostsList(mockedPayerCostList);
        presenter.setDirectDiscountEnabled(directDiscountEnabled);

        //Response from IssuersActivity, with an issuer selected
        presenter.resolveIssuersRequest(mockedIssuer);

        assertTrue(mockedView.installmentsFlowStarted);
    }

    @Test
    public void whenPayerCostResolvedThenFinishWithResult() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));

        presenter.initialize();

        Token mockedToken = Tokens.getToken();
        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        PayerCost mockedPayerCost = PayerCosts.getPayerCost();
        Issuer mockedIssuer = Issuers.getIssuerMLA();
        Discount mockedDiscount = null;
        Boolean directDiscountEnabled = false;

        presenter.setToken(mockedToken);
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.setIssuer(mockedIssuer);
        presenter.setDirectDiscountEnabled(directDiscountEnabled);

        //Response from InstallmentsActivity, with payer cost selected
        presenter.resolveInstallmentsRequest(mockedPayerCost, mockedDiscount);

        assertTrue(mockedView.finishedWithResult);
    }

    @Test
    public void whenPayerCostResolvedAndSavedCardSetThenAskForSecurityCode() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        PayerCost mockedPayerCost = PayerCosts.getPayerCost();
        Discount mockedDiscount = null;

        //Response from InstallmentsActivity, with payer cost selected and saved card
        presenter.resolveInstallmentsRequest(mockedPayerCost, mockedDiscount);

        assertTrue(mockedView.securityCodeFlowStarted);
    }

    @Test
    public void whenSecurityCodeResolvedAndSavedCardSetThenFinishWithResult() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        Token mockedToken = Tokens.getToken();

        //Response from SecurityCodeActivity
        presenter.resolveSecurityCodeRequest(mockedToken);

        assertTrue(mockedView.finishedWithResult);
    }

    @Test
    public void onResponseCanceledThenCancelCardVault() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        presenter.onResultCancel();

        assertTrue(mockedView.cardVaultCanceled);
    }

    @Test
    public void whenSecurityCodeResolvedWithPaymentRecoverySetThenFinishWithResult() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        Token mockedToken = Tokens.getToken();
        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        PayerCost mockedPayerCost = PayerCosts.getPayerCost();
        Issuer mockedIssuer = Issuers.getIssuerMLA();
        String mockedPaymentStatus = Payment.StatusCodes.STATUS_REJECTED;
        String mockedPaymentStatusDetail = Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE;

        PaymentRecovery mockedPaymentRecovery = new PaymentRecovery(mockedToken, mockedPaymentMethod, mockedPayerCost, mockedIssuer, mockedPaymentStatus, mockedPaymentStatusDetail);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setPaymentRecovery(mockedPaymentRecovery);

        presenter.initialize();

        //Response from SecurityCodeActivity, with recoverable token
        presenter.resolveSecurityCodeRequest(mockedToken);

        assertNotNull(presenter.getPayerCost());
        assertNotNull(presenter.getIssuer());
        assertNotNull(presenter.getToken());
        assertTrue(mockedView.finishedWithResult);
    }

    @Test
    public void ifInstallmentsForCardFailsThenRecoverRequest() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        ApiException apiException = Installments.getDoNotFindInstallmentsException();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        provider.setResponse(mpException);


        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        assertTrue(mockedView.errorState);
        presenter.recoverFromFailure();

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        assertNotNull(installmentsList);

    }

    @Test
    public void onInstallmentsAskedThenAskForSecurityCodeWhenCardIdIsNotSaved() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();
        provider.setESCEnabled(true);

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        presenter.setCard(Cards.getCard());

        presenter.initialize();

        PayerCost mockedPayerCost = PayerCosts.getPayerCost();

        //Installments response
        presenter.resolveInstallmentsRequest(mockedPayerCost, null);
        assertTrue(mockedView.securityCodeFlowStarted);

        presenter.checkSecurityCodeFlow();
        assertTrue(mockedView.securityCodeActivityStarted);
    }

    @Test
    public void onInstallmentsAskedThenDontAskForSecurityCodeWhenCardIdIsSaved() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();
        provider.setESCEnabled(true);

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        Card mockedCard = Cards.getCard();
        mockedCard.setId("12345");
        presenter.setCard(mockedCard);

        presenter.initialize();

        PayerCost mockedPayerCost = PayerCosts.getPayerCost();

        //Installments response
        presenter.resolveInstallmentsRequest(mockedPayerCost, null);
        assertTrue(mockedView.securityCodeFlowStarted);

        Token mockedToken = Tokens.getTokenWithESC();
        provider.setResponse(mockedToken);

        presenter.checkSecurityCodeFlow();
        assertFalse(mockedView.securityCodeActivityStarted);

        assertEquals(provider.successfulTokenResponse.getId(), mockedToken.getId());
    }

    @Test
    public void onCreateTokenWithESCHasErrorThenAskForSecurityCode() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();
        provider.setESCEnabled(true);

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        Card mockedCard = Cards.getCard();
        mockedCard.setId("12345");
        presenter.setCard(mockedCard);

        presenter.initialize();

        PayerCost mockedPayerCost = PayerCosts.getPayerCost();

        //Installments response
        presenter.resolveInstallmentsRequest(mockedPayerCost, null);
        assertTrue(mockedView.securityCodeFlowStarted);

        //Set error with create token ESC
        ApiException apiException = Tokens.getInvalidTokenWithESC();
        provider.setResponse(new MercadoPagoError(apiException, ""));

        presenter.checkSecurityCodeFlow();
        assertTrue(mockedView.securityCodeActivityStarted);

        assertTrue(provider.deleteRequested);
        assertEquals(provider.cardIdDeleted, "12345");

    }

    @Test
    public void onCreateTokenWithESCHasErrorFingerprintThenAskForSecurityCode() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();
        provider.setESCEnabled(true);

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        Card mockedCard = Cards.getCard();
        mockedCard.setId("12345");
        presenter.setCard(mockedCard);

        presenter.initialize();

        PayerCost mockedPayerCost = PayerCosts.getPayerCost();

        //Installments response
        presenter.resolveInstallmentsRequest(mockedPayerCost, null);
        assertTrue(mockedView.securityCodeFlowStarted);

        //Set error with create token ESC
        ApiException apiException = Tokens.getInvalidTokenWithESCFingerprint();
        provider.setResponse(new MercadoPagoError(apiException, ""));

        presenter.checkSecurityCodeFlow();
        assertTrue(mockedView.securityCodeActivityStarted);

        assertTrue(provider.deleteRequested);
        assertEquals(provider.cardIdDeleted, "12345");

    }

    @Test
    public void onESCDisabledThenAskForSecurityCodeWhenCardIdIsSaved() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();
        //ESC disabled
        provider.setESCEnabled(false);

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        Card mockedCard = Cards.getCard();
        mockedCard.setId("12345");
        presenter.setCard(mockedCard);

        presenter.initialize();

        PayerCost mockedPayerCost = PayerCosts.getPayerCost();

        //Installments response
        presenter.resolveInstallmentsRequest(mockedPayerCost, null);
        assertTrue(mockedView.securityCodeFlowStarted);

        Token mockedToken = Tokens.getToken();
        provider.setResponse(mockedToken);

        presenter.checkSecurityCodeFlow();
        assertTrue(mockedView.securityCodeActivityStarted);

        assertEquals(provider.successfulTokenResponse.getId(), mockedToken.getId());
    }

    @Test
    public void onSavedCardWithESCSavedThenCreateTokenWithESC() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();
        provider.setESCEnabled(true);

        List<Installment> installmentsList = Installments.getInstallmentsList();
        provider.setResponse(installmentsList);

        CardVaultPresenter presenter = new CardVaultPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setSite(Sites.ARGENTINA);
        presenter.setAmount(new BigDecimal(100));
        Card mockedCard = Cards.getCard();
        mockedCard.setId("12345");
        presenter.setCard(mockedCard);

        //Set ESC to simulate it is saved
        presenter.setESC("12345678");

        presenter.initialize();

        PayerCost mockedPayerCost = PayerCosts.getPayerCost();
        Token mockedToken = Tokens.getTokenWithESC();

        //Installments response
        presenter.resolveInstallmentsRequest(mockedPayerCost, null);

        //Set error with create token ESC
        provider.setResponse(mockedToken);

        presenter.checkSecurityCodeFlow();
        assertEquals(provider.successfulTokenResponse.getId(), mockedToken.getId());
    }

    private class MockedProvider implements CardVaultProvider {

        private static final String MULTIPLE_INSTALLMENTS = "multiple installments";
        private static final String MISSING_INSTALLMENTS = "missing installments";
        private static final String MISSING_PAYER_COSTS = "missing payer costs";
        private static final String MISSING_AMOUNT = "missing amount";
        private static final String MISSING_PUBLIC_KEY = "missing public key";
        private static final String MISSING_SITE = "missing site";
        private static final String INSTALLMENTS_NOT_FOUND_ERROR = "installments not found error";

        private boolean shouldFail;
        private MercadoPagoError failedResponse;
        private List<Installment> successfulResponse;
        private Token successfulTokenResponse;
        private boolean escEnabled;
        private boolean deleteRequested;
        private String cardIdDeleted;

        public void setESCEnabled(boolean enabled) {
            this.escEnabled = enabled;
        }

        public void setResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        public void setResponse(List<Installment> installmentList) {
            shouldFail = false;
            successfulResponse = installmentList;
        }

        public void setResponse(Token token) {
            shouldFail = false;
            successfulTokenResponse = token;
        }

        @Override
        public String getMultipleInstallmentsForIssuerErrorMessage() {
            return MULTIPLE_INSTALLMENTS;
        }

        @Override
        public String getMissingInstallmentsForIssuerErrorMessage() {
            return MISSING_INSTALLMENTS;
        }

        @Override
        public String getMissingPayerCostsErrorMessage() {
            return MISSING_PAYER_COSTS;
        }

        @Override
        public String getMissingAmountErrorMessage() {
            return MISSING_AMOUNT;
        }

        @Override
        public String getMissingPublicKeyErrorMessage() {
            return MISSING_PUBLIC_KEY;
        }

        @Override
        public String getMissingSiteErrorMessage() {
            return MISSING_SITE;
        }

        @Override
        public void getInstallmentsAsync(String bin, Long issuerId, String paymentMethodId, BigDecimal amount, TaggedCallback<List<Installment>> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulResponse);
            }
        }

        @Override
        public void createESCTokenAsync(SavedESCCardToken escCardToken, TaggedCallback<Token> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulTokenResponse);
            }
        }

        @Override
        public String findESCSaved(String cardId) {
            if (escEnabled) {
                if (cardId.equals("12345")) {
                    return "12345";
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        public void deleteESC(String cardId) {
            deleteRequested = true;
            cardIdDeleted = cardId;
        }
    }

    private class MockedView implements CardVaultView {

        private MercadoPagoError errorShown;
        private List<Installment> installmentsShown;
        private boolean issuerFlowStarted;
        private boolean installmentsFlowStarted;
        private boolean securityCodeFlowStarted;
        private boolean guessingFlowStarted;
        private boolean recoverableTokenFlowStarted;
        private boolean finishedWithResult;
        private boolean cardVaultCanceled;
        private boolean errorState;
        private boolean securityCodeActivityStarted;

        @Override
        public void askForInstallments() {
            installmentsFlowStarted = true;
        }

        @Override
        public void askForInstallmentsFromIssuers() {
            installmentsFlowStarted = true;
        }

        @Override
        public void askForInstallmentsFromNewCard() {
            installmentsFlowStarted = true;
        }

        @Override
        public void askForSecurityCodeWithoutInstallments() {
            securityCodeFlowStarted = true;
        }

        @Override
        public void askForCardInformation() {
            guessingFlowStarted = true;
        }

        @Override
        public void askForSecurityCodeFromInstallments() {
            securityCodeFlowStarted = true;
        }

        @Override
        public void askForSecurityCodeFromTokenRecovery() {
            recoverableTokenFlowStarted = true;
            securityCodeFlowStarted = true;
        }

        @Override
        public void startIssuersActivity() {
            issuerFlowStarted = true;
        }

        @Override
        public void showApiExceptionError(ApiException exception, String requestOrigin) {
            //Do something
        }

        @Override
        public void showProgressLayout() {
            //Do something
        }

        @Override
        public void showError(MercadoPagoError mercadoPagoError, String requestOrigin) {
            errorShown = mercadoPagoError;
            errorState = true;
        }

        @Override
        public void finishWithResult() {
            finishedWithResult = true;
        }

        @Override
        public void cancelCardVault() {
            cardVaultCanceled = true;
        }

        @Override
        public void startSecurityCodeActivity(String reason) {
            securityCodeActivityStarted = true;
        }
    }
}
