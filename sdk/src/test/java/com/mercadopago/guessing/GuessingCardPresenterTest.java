package com.mercadopago.guessing;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.exceptions.CardTokenException;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.mocks.BankDeals;
import com.mercadopago.mocks.Cards;
import com.mercadopago.mocks.DummyCard;
import com.mercadopago.mocks.IdentificationTypes;
import com.mercadopago.mocks.Issuers;
import com.mercadopago.mocks.PayerCosts;
import com.mercadopago.mocks.PaymentMethods;
import com.mercadopago.mocks.Tokens;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.presenters.GuessingCardPresenter;
import com.mercadopago.providers.GuessingCardProvider;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.uicontrollers.card.CardView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.utils.CardTestUtils;
import com.mercadopago.views.GuessingCardActivityView;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by vaserber on 8/24/17.
 */

public class GuessingCardPresenterTest {

    @Test
    public void ifPublicKeyNotSetThenShowMissingPublicKeyError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.initialize();

        assertEquals(MockedProvider.MISSING_PUBLIC_KEY, mockedView.errorShown.getMessage());
    }

    @Test
    public void ifPublicKeySetThenCheckValidStart() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        assertTrue(mockedView.validStart);
    }

    @Test
    public void ifPaymentRecoverySetThenSaveCardholderNameAndIdentification() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        Token mockedToken = Tokens.getToken();
        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        PayerCost mockedPayerCost = PayerCosts.getPayerCost();
        Issuer mockedIssuer = Issuers.getIssuerMLA();
        String paymentStatus = Payment.StatusCodes.STATUS_REJECTED;
        String paymentStatusDetail = Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE;
        PaymentRecovery mockedPaymentRecovery = new PaymentRecovery(mockedToken, mockedPaymentMethod, mockedPayerCost, mockedIssuer, paymentStatus, paymentStatusDetail);

        presenter.setPublicKey("mockedPublicKey");
        presenter.setPaymentRecovery(mockedPaymentRecovery);

        presenter.initialize();

        assertTrue(mockedView.validStart);
        assertEquals(presenter.getCardholderName(), mockedPaymentRecovery.getToken().getCardHolder().getName());
        assertEquals(presenter.getIdentificationNumber(), mockedPaymentRecovery.getToken().getCardHolder().getIdentification().getNumber());
        assertEquals(mockedView.savedCardholderName, mockedPaymentRecovery.getToken().getCardHolder().getName());
        assertEquals(mockedView.savedIdentificationNumber, mockedPaymentRecovery.getToken().getCardHolder().getIdentification().getNumber());
    }

    @Test
    public void ifPaymentMethodListSetWithOnePaymentMethodThenSelectIt() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnVisa());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertTrue(mockedView.paymentMethodSet);
    }

    @Test
    public void ifPaymentMethodListSetIsEmptyThenShowError() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertFalse(mockedView.paymentMethodSet);
        assertTrue(mockedView.invalidPaymentMethod);
        assertTrue(mockedView.multipleErrorViewShown);
    }

    @Test
    public void ifPaymentMethodListSetWithTwoOptionsThenAskForPaymentType() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnVisa());
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnDebit());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertTrue(presenter.hasToShowPaymentTypes());

    }

    @Test
    public void ifPaymentMethodListSetWithTwoOptionsThenChooseFirstOne() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnVisa());
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnDebit());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertTrue(mockedView.paymentMethodSet);
        assertEquals(presenter.getPaymentMethod().getId(), mockedGuessedPaymentMethods.get(0).getId());
    }

    @Test
    public void ifPaymentMethodSetAndDeletedThenClearConfiguration() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnVisa());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertTrue(mockedView.paymentMethodSet);

        presenter.setPaymentMethod(null);

        assertEquals(presenter.getSecurityCodeLength(), GuessingCardPresenter.CARD_DEFAULT_SECURITY_CODE_LENGTH);
        assertEquals(presenter.getSecurityCodeLocation(), CardView.CARD_SIDE_BACK);
        assertTrue(presenter.isSecurityCodeRequired());
        assertEquals(presenter.getSavedBin().length(), 0);
    }

    @Test
    public void ifPaymentMethodSetAndDeletedThenClearViews() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnVisa());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertTrue(mockedView.paymentMethodSet);

        presenter.resolvePaymentMethodCleared();

        assertFalse(mockedView.errorState);
        assertTrue(mockedView.cardNumberLengthDefault);
        assertTrue(mockedView.cardNumberMaskDefault);
        assertTrue(mockedView.securityCodeInputErased);
        assertTrue(mockedView.clearCardView);
    }

    @Test
    public void ifPaymentMethodSetHasIdentificationTypeRequiredThenShowIdentificationView() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodOnVisa());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertTrue(mockedView.paymentMethodSet);
        assertTrue(presenter.isIdentificationNumberRequired());
        assertTrue(mockedView.identificationTypesInitialized);
    }

    @Test
    public void ifPaymentMethodSetDoesntHaveIdentificationTypeRequiredThenHideIdentificationView() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodWithIdNotRequired());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_CORDIAL);

        assertTrue(mockedView.paymentMethodSet);
        assertFalse(presenter.isIdentificationNumberRequired());
        assertFalse(mockedView.identificationTypesInitialized);
        assertTrue(mockedView.hideIdentificationInput);
    }

    @Test
    public void initializeGuessingFormWithPaymentMethodListFromCardVault() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");
        presenter.setPaymentMethodList(paymentMethodList);

        presenter.initialize();

        assertTrue(mockedView.showInputContainer);
        assertTrue(mockedView.initializeGuessingForm);
        assertTrue(mockedView.initializeGuessingListeners);
    }

    @Test
    public void ifBankDealsNotEnabledThenHideBankDeals() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");
        presenter.setPaymentMethodList(paymentMethodList);
        presenter.setShowBankDeals(false);

        presenter.initialize();

        assertTrue(mockedView.hideBankDeals);
    }

    @Test
    public void ifGetPaymentMethodFailsThenShowErrorMessage() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        ApiException apiException = PaymentMethods.getDoNotFindPaymentMethodsException();
        MercadoPagoError mpException = new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_PAYMENT_METHODS);
        provider.setPaymentMethodsResponse(mpException);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        assertTrue(provider.failedResponse.getApiException().getError().equals(MockedProvider.PAYMENT_METHODS_NOT_FOUND));
    }

    @Test
    public void ifPaymentTypeSetAndTwoPaymentMethodssThenChooseByPaymentType() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLM();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultPaymentTypeId(PaymentTypes.DEBIT_CARD);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");
        presenter.setPaymentPreference(paymentPreference);

        presenter.initialize();

        PaymentMethodGuessingController controller = new PaymentMethodGuessingController(
                paymentMethodList, PaymentTypes.DEBIT_CARD, null);
        List<PaymentMethod> paymentMethodsWithExclusionsList = controller.guessPaymentMethodsByBin(Cards.MOCKED_BIN_MASTER);

        presenter.resolvePaymentMethodListSet(paymentMethodsWithExclusionsList, Cards.MOCKED_BIN_MASTER);

        assertEquals(paymentMethodsWithExclusionsList.size(), 1);
        assertEquals(presenter.getPaymentMethod().getId(), "debmaster");
        assertFalse(presenter.hasToShowPaymentTypes());
    }

    @Test
    public void ifSecurityCodeSettingsAreWrongThenHideSecurityCodeView() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        mockedGuessedPaymentMethods.add(PaymentMethods.getPaymentMethodWithWrongSecurityCodeSettings());

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertTrue(mockedView.paymentMethodSet);
        assertTrue(mockedView.hideSecurityCodeInput);
    }

    @Test
    public void ifPaymentMethodSettingsAreEmptyThenShowErrorMessage() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        List<PaymentMethod> mockedGuessedPaymentMethods = new ArrayList<>();
        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        mockedPaymentMethod.setSettings(null);
        mockedGuessedPaymentMethods.add(mockedPaymentMethod);

        presenter.resolvePaymentMethodListSet(mockedGuessedPaymentMethods, Cards.MOCKED_BIN_VISA);

        assertEquals(MockedProvider.SETTING_NOT_FOUND_FOR_BIN, mockedView.errorShown.getMessage());

    }

    @Test
    public void ifGetIdentificationTypesFailsThenShowErrorMessage() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        ApiException apiException = IdentificationTypes.getDoNotFindIdentificationTypesException();
        MercadoPagoError mpException = new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES);
        provider.setIdentificationTypesResponse(mpException);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        assertTrue(provider.failedResponse.getApiException().getError().equals(MockedProvider.IDENTIFICATION_TYPES_NOT_FOUND));
    }

    @Test
    public void ifGetIdentificationTypesIsEmptyThenShowErrorMessage() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypes = new ArrayList<>();
        provider.setIdentificationTypesResponse(identificationTypes);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        presenter.resolvePaymentMethodListSet(paymentMethodList, Cards.MOCKED_BIN_VISA);

        assertEquals(MockedProvider.MISSING_IDENTIFICATION_TYPES, mockedView.errorShown.getMessage());

    }

    @Test
    public void ifBankDealsNotEmptyThenShowThem() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypes = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypes);

        List<BankDeal> bankDeals = BankDeals.getBankDealsListMLA();
        provider.setBankDealsResponse(bankDeals);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        presenter.resolvePaymentMethodListSet(paymentMethodList, Cards.MOCKED_BIN_VISA);

        assertTrue(mockedView.bankDealsShown);

    }

    @Test
    public void ifCardNumberSetThenValidateItAndSaveItInCardToken() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);

        boolean valid = presenter.validateCardNumber();

        assertTrue(valid);
        assertEquals(presenter.getCardToken().getCardNumber(), card.getCardNumber());
    }

    @Test
    public void ifCardholderNameSetThenValidateItAndSaveItInCardToken() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.saveCardholderName(CardTestUtils.DUMMY_CARDHOLDER_NAME);

        boolean valid = presenter.validateCardName();

        assertTrue(valid);
        assertEquals(presenter.getCardToken().getCardholder().getName(), CardTestUtils.DUMMY_CARDHOLDER_NAME);
    }

    @Test
    public void ifCardExpiryDateSetThenValidateItAndSaveItInCardToken() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.saveCardholderName(CardTestUtils.DUMMY_CARDHOLDER_NAME);
        presenter.saveExpiryMonth(CardTestUtils.DUMMY_EXPIRY_MONTH);
        presenter.saveExpiryYear(CardTestUtils.DUMMY_EXPIRY_YEAR_SHORT);

        boolean valid = presenter.validateExpiryDate();

        assertTrue(valid);
        assertEquals(presenter.getCardToken().getExpirationMonth(), Integer.valueOf(CardTestUtils.DUMMY_EXPIRY_MONTH));
        assertEquals(presenter.getCardToken().getExpirationYear(), Integer.valueOf(CardTestUtils.DUMMY_EXPIRY_YEAR_LONG));
    }

    @Test
    public void ifCardSecurityCodeSetThenValidateItAndSaveItInCardToken() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.saveCardholderName(CardTestUtils.DUMMY_CARDHOLDER_NAME);
        presenter.saveExpiryMonth(CardTestUtils.DUMMY_EXPIRY_MONTH);
        presenter.saveExpiryYear(CardTestUtils.DUMMY_EXPIRY_YEAR_SHORT);
        presenter.saveSecurityCode(card.getSecurityCode());

        boolean validCardNumber = presenter.validateCardNumber();
        boolean validSecurityCode = presenter.validateSecurityCode();

        assertTrue(validCardNumber && validSecurityCode);
        assertEquals(presenter.getCardToken().getSecurityCode(), card.getSecurityCode());
    }

    @Test
    public void ifIdentificationNumberSetThenValidateItAndSaveItInCardToken() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        Identification identification = new Identification();
        presenter.setIdentification(identification);
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.saveCardholderName(CardTestUtils.DUMMY_CARDHOLDER_NAME);
        presenter.saveExpiryMonth(CardTestUtils.DUMMY_EXPIRY_MONTH);
        presenter.saveExpiryYear(CardTestUtils.DUMMY_EXPIRY_YEAR_SHORT);
        presenter.saveSecurityCode(card.getSecurityCode());
        presenter.saveIdentificationNumber(CardTestUtils.DUMMY_IDENTIFICATION_NUMBER_DNI);
        presenter.saveIdentificationType(IdentificationTypes.getIdentificationType());

        boolean valid = presenter.validateIdentificationNumber();

        assertTrue(valid);
        assertEquals(presenter.getCardToken().getCardholder().getIdentification().getNumber(), CardTestUtils.DUMMY_IDENTIFICATION_NUMBER_DNI);
    }

    @Test
    public void ifCardDataSetAndValidThenCreateToken() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        List<Issuer> issuerList = Issuers.getIssuersListMLA();
        provider.setIssuersResponse(issuerList);

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        Token mockedtoken = Tokens.getToken();
        provider.setTokenResponse(mockedtoken);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        Identification identification = new Identification();
        presenter.setIdentification(identification);
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");

        presenter.initialize();

        DummyCard card = CardTestUtils.getDummyCard("master");
        presenter.saveCardNumber(card.getCardNumber());
        presenter.setPaymentMethod(mockedPaymentMethod);
        presenter.saveCardholderName(CardTestUtils.DUMMY_CARDHOLDER_NAME);
        presenter.saveExpiryMonth(CardTestUtils.DUMMY_EXPIRY_MONTH);
        presenter.saveExpiryYear(CardTestUtils.DUMMY_EXPIRY_YEAR_SHORT);
        presenter.saveSecurityCode(card.getSecurityCode());
        presenter.saveIdentificationNumber(CardTestUtils.DUMMY_IDENTIFICATION_NUMBER_DNI);
        presenter.saveIdentificationType(IdentificationTypes.getIdentificationType());

        boolean valid = presenter.validateCardNumber();
        valid = valid & presenter.validateCardName();
        valid = valid & presenter.validateExpiryDate();
        valid = valid & presenter.validateSecurityCode();
        valid = valid & presenter.validateIdentificationNumber();

        assertTrue(valid);

        presenter.checkFinishWithCardToken();

        presenter.resolveTokenRequest(mockedtoken);

        assertEquals(presenter.getToken(), mockedtoken);
    }

    @Test
    public void ifPaymentMethodExclusionSetAndUserSelectsItThenShowErrorMessage() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListMLA();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        //We exclude master
        List<String> excludedPaymentMethodIds = new ArrayList<>();
        excludedPaymentMethodIds.add("master");
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethodIds);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");
        presenter.setPaymentPreference(paymentPreference);

        presenter.initialize();

        //The user enters a master bin
        PaymentMethodGuessingController controller = presenter.getGuessingController();
        List<PaymentMethod> guessedPaymentMethods = controller.guessPaymentMethodsByBin(Cards.MOCKED_BIN_MASTER);

        presenter.resolvePaymentMethodListSet(guessedPaymentMethods, Cards.MOCKED_BIN_MASTER);

        //We show a red container showing the multiple available payment methods
        assertFalse(mockedView.paymentMethodSet);
        assertTrue(mockedView.invalidPaymentMethod);
        assertTrue(mockedView.multipleErrorViewShown);

        //The users deletes the bin master
        presenter.setPaymentMethod(null);
        presenter.resolvePaymentMethodCleared();

        //The red container disappears
        assertFalse(mockedView.multipleErrorViewShown);
        assertFalse(mockedView.invalidPaymentMethod);
    }

    @Test
    public void ifPaymentMethodExclusionSetAndUserSelectsItWithOnlyOnePMAvailableThenShowInfoMessage() {
        MockedView mockedView = new MockedView();
        MockedProvider provider = new MockedProvider();

        //We only have visa and master
        List<PaymentMethod> paymentMethodList = PaymentMethods.getPaymentMethodListWithTwoOptions();
        provider.setPaymentMethodsResponse(paymentMethodList);

        List<IdentificationType> identificationTypesList = IdentificationTypes.getIdentificationTypes();
        provider.setIdentificationTypesResponse(identificationTypesList);

        //We exclude master
        List<String> excludedPaymentMethodIds = new ArrayList<>();
        excludedPaymentMethodIds.add("master");
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethodIds);

        GuessingCardPresenter presenter = new GuessingCardPresenter();
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);

        presenter.setPublicKey("mockedPublicKey");
        presenter.setPaymentPreference(paymentPreference);

        presenter.initialize();

        PaymentMethodGuessingController controller = presenter.getGuessingController();
        List<PaymentMethod> guessedPaymentMethods = controller.guessPaymentMethodsByBin(Cards.MOCKED_BIN_MASTER);

        //Black info container shows the only available payment method
        assertTrue(mockedView.onlyOnePMErrorViewShown);
        assertEquals(mockedView.supportedPaymentMethodId, "visa");

        presenter.resolvePaymentMethodListSet(guessedPaymentMethods, Cards.MOCKED_BIN_MASTER);

        //When the user enters a master bin the container turns red
        assertFalse(mockedView.paymentMethodSet);
        assertTrue(mockedView.infoContainerTurnedRed);
        assertTrue(mockedView.invalidPaymentMethod);

        presenter.setPaymentMethod(null);
        presenter.resolvePaymentMethodCleared();

        //When the user deletes the input the container turns black again
        assertFalse(mockedView.infoContainerTurnedRed);
        assertTrue(mockedView.onlyOnePMErrorViewShown);
    }

    @Test
    public void whenAllGuessedPaymentMethodsShareTypeThenDoNotAskForPaymentType() {

        PaymentMethod creditCard1 = new PaymentMethod();
        creditCard1.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        PaymentMethod creditCard2 = new PaymentMethod();
        creditCard2.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(creditCard1);
        paymentMethodList.add(creditCard2);

        GuessingCardPresenter presenter = new GuessingCardPresenter();

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        assertFalse(shouldAskPaymentType);
    }

    @Test
    public void whenNotAllGuessedPaymentMethodsShareTypeThenDoAskForPaymentType() {

        PaymentMethod creditCard = new PaymentMethod();
        creditCard.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        PaymentMethod debitCard = new PaymentMethod();
        debitCard.setPaymentTypeId(PaymentTypes.DEBIT_CARD);

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(creditCard);
        paymentMethodList.add(debitCard);

        GuessingCardPresenter presenter = new GuessingCardPresenter();

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        assertTrue(shouldAskPaymentType);
    }

    @Test
    public void whenGuessedPaymentMethodsListIsNullThenPaymentMethodShouldBeUndefined() {

        List<PaymentMethod> paymentMethodList = null;

        GuessingCardPresenter presenter = new GuessingCardPresenter();

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        assertTrue(shouldAskPaymentType);
    }

    @Test
    public void whenGuessedPaymentMethodsListIsEmptyThenPaymentMethodShouldBeUndefined() {

        List<PaymentMethod> paymentMethodList = new ArrayList<>();

        GuessingCardPresenter presenter = new GuessingCardPresenter();

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        assertTrue(shouldAskPaymentType);
    }

    @Test
    public void whenUniquePaymentMethodGuessedThenPaymentMethodShouldDefined() {

        PaymentMethod creditCard = new PaymentMethod();
        creditCard.setPaymentTypeId(PaymentTypes.CREDIT_CARD);

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(creditCard);

        GuessingCardPresenter presenter = new GuessingCardPresenter();

        boolean shouldAskPaymentType = presenter.shouldAskPaymentType(paymentMethodList);
        assertFalse(shouldAskPaymentType);
    }

    private class MockedView implements GuessingCardActivityView {

        private MercadoPagoError errorShown;
        private CardTokenException cardTokenError;
        private boolean formDataErrorState;
        private boolean errorState;
        private boolean validStart;
        private boolean cardNumberLengthDefault;
        private boolean cardNumberMaskDefault;
        private boolean securityCodeInputErased;
        private boolean clearCardView;
        private boolean identificationTypesInitialized;
        private boolean hideIdentificationInput;
        private boolean showInputContainer;
        private boolean initializeGuessingForm;
        private boolean initializeGuessingListeners;
        private boolean hideBankDeals;
        private boolean hideSecurityCodeInput;
        private boolean bankDealsShown;
        private boolean paymentMethodSet;
        private boolean invalidPaymentMethod;
        private boolean multipleErrorViewShown;
        private boolean onlyOnePMErrorViewShown;
        private boolean infoContainerTurnedRed;
        private String supportedPaymentMethodId;
        private String savedCardholderName;
        private String savedIdentificationNumber;


        @Override
        public void setPaymentMethod(PaymentMethod paymentMethod) {

        }

        @Override
        public void clearSecurityCodeEditText() {
            securityCodeInputErased = true;
        }

        @Override
        public void clearCardNumberEditTextMask() {
            cardNumberMaskDefault = true;
        }

        @Override
        public void restoreBlackInfoContainerView() {
            onlyOnePMErrorViewShown = true;
            infoContainerTurnedRed = false;
        }

        @Override
        public void hideRedErrorContainerView(boolean withAnimation) {
            multipleErrorViewShown = false;
            invalidPaymentMethod = false;
        }

        @Override
        public void resolvePaymentMethodSet(PaymentMethod paymentMethod) {
            paymentMethodSet = true;
        }

        @Override
        public void clearErrorIdentificationNumber() {

        }

        @Override
        public void setSoftInputMode() {

        }

        @Override
        public void setErrorContainerListener() {

        }

        @Override
        public void setInvalidCardOnePaymentMethodErrorView() {
            invalidPaymentMethod = true;
            onlyOnePMErrorViewShown = true;
            infoContainerTurnedRed = true;
        }

        @Override
        public void setInvalidCardMultipleErrorView() {
            invalidPaymentMethod = true;
            multipleErrorViewShown = true;
        }

        @Override
        public void hideProgress() {

        }

        @Override
        public void finishCardFlow(PaymentMethod paymentMethod, Token token, Discount discount, Boolean directDiscountEnabled, Boolean discountEnabled, Issuer issuer, PayerCost payerCost) {

        }

        @Override
        public void finishCardFlow(PaymentMethod paymentMethod, Token token, Discount discount, Boolean directDiscountEnabled, Boolean discountEnabled, Issuer issuer, List<PayerCost> payerCosts) {

        }

        @Override
        public void showApiExceptionError(ApiException exception, String requestOrigin) {

        }

        @Override
        public void onInvalidStart(String message) {
            validStart = false;
        }

        @Override
        public void onValidStart() {
            validStart = true;
        }

        @Override
        public void hideExclusionWithOneElementInfoView() {

        }

        @Override
        public void showError(MercadoPagoError error, String requestOrigin) {
            errorShown = error;
        }

        @Override
        public void setContainerAnimationListeners() {

        }

        @Override
        public void setExclusionWithOneElementInfoView(PaymentMethod supportedPaymentMethod, boolean withAnimation) {
            onlyOnePMErrorViewShown = true;
            supportedPaymentMethodId = supportedPaymentMethod.getId();
        }

        @Override
        public void clearCardNumberInputLength() {
            cardNumberLengthDefault = true;
        }

        @Override
        public void clearErrorView() {
            errorState = false;
        }

        @Override
        public void checkClearCardView() {
            clearCardView = true;
        }

        @Override
        public void setBackButtonListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setCardNumberListeners(PaymentMethodGuessingController controller) {
            initializeGuessingListeners = true;
        }

        @Override
        public void setErrorSecurityCode() {

        }

        @Override
        public void setErrorCardNumber() {

        }

        @Override
        public void setErrorView(CardTokenException exception) {
            formDataErrorState = true;
            cardTokenError = exception;
        }

        @Override
        public void setErrorView(String mErrorState) {
            formDataErrorState = true;
        }

        @Override
        public void setSecurityCodeListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setIdentificationTypeListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setNextButtonListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setIdentificationNumberListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setSecurityCodeInputMaxLength(int length) {

        }

        @Override
        public void setSecurityCodeViewLocation(String location) {

        }

        @Override
        public void setIdentificationNumberRestrictions(String type) {

        }

        @Override
        public void setCardholderNameListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setExpiryDateListeners() {
            initializeGuessingListeners = true;
        }

        @Override
        public void setCardholderName(String cardholderName) {
            this.savedCardholderName = cardholderName;
        }

        @Override
        public void setCardNumberInputMaxLength(int length) {

        }

        @Override
        public void setErrorCardholderName() {

        }

        @Override
        public void setErrorExpiryDate() {

        }

        @Override
        public void setErrorIdentificationNumber() {

        }

        @Override
        public void setIdentificationNumber(String identificationNumber) {
            this.savedIdentificationNumber = identificationNumber;
        }

        @Override
        public void showDiscountRow(BigDecimal transactionAmount) {

        }

        @Override
        public void showIdentificationInput() {

        }

        @Override
        public void showSecurityCodeInput() {

        }

        @Override
        public void showInputContainer() {
            showInputContainer = true;
        }

        @Override
        public void showBankDeals() {
            bankDealsShown = true;
        }

        @Override
        public void hideBankDeals() {
            hideBankDeals = true;
        }

        @Override
        public void hideIdentificationInput() {
            hideIdentificationInput = true;
        }

        @Override
        public void hideSecurityCodeInput() {
            hideSecurityCodeInput = true;
        }

        @Override
        public void initializeTimer() {

        }

        @Override
        public void initializeIdentificationTypes(List<IdentificationType> identificationTypes) {
            identificationTypesInitialized = true;
        }

        @Override
        public void initializeTitle() {
            initializeGuessingForm = true;
        }

        @Override
        public void startDiscountActivity(BigDecimal transactionAmount) {

        }

        @Override
        public void finishCardFlow(PaymentMethod paymentMethod, Token token, Discount discount, Boolean directDiscountEnabled, Boolean discountEnabled,  List<Issuer> issuers) {

        }

        @Override
        public void askForPaymentType() {
        }

        @Override
        public void showFinishCardFlow() {

        }
    }

    private class MockedProvider implements GuessingCardProvider {

        private static final String MULTIPLE_INSTALLMENTS = "multiple installments";
        private static final String MISSING_INSTALLMENTS = "missing installments";
        private static final String MISSING_PAYER_COSTS = "missing payer costs";
        private static final String MISSING_PUBLIC_KEY = "missing public key";
        private static final String MISSING_IDENTIFICATION_TYPES = "missing identification types";
        private static final String INVALID_IDENTIFICATION_NUMBER = "invalid identification number";
        private static final String INVALID_EMPTY_NAME = "invalid empty name";
        private static final String INVALID_EXPIRY_DATE = "invalid expiry date";
        private static final String SETTING_NOT_FOUND_FOR_BIN = "setting not found for bin";
        private static final String PAYMENT_METHODS_NOT_FOUND = "payment methods not found error";
        private static final String IDENTIFICATION_TYPES_NOT_FOUND = "identification types not found error";
        private static final String INVALID_FIELD = "invalid field";

        private boolean shouldFail;
        private MercadoPagoError failedResponse;
        private List<Installment> successfulInstallmentsResponse;
        private List<IdentificationType> successfulIdentificationTypesResponse;
        private List<BankDeal> successfulBankDealsResponse;
        private Token successfulTokenResponse;
        private List<Issuer> successfulIssuersResponse;
        private Discount successfulDiscountResponse;
        private List<PaymentMethod> successfulPaymentMethodsResponse;

        public void setResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        public void setInstallmentsResponse(List<Installment> installmentList) {
            shouldFail = false;
            successfulInstallmentsResponse = installmentList;
        }

        public void setIdentificationTypesResponse(List<IdentificationType> identificationTypes) {
            shouldFail = false;
            successfulIdentificationTypesResponse = identificationTypes;
        }

        public void setIdentificationTypesResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        public void setBankDealsResponse(List<BankDeal> bankDeals) {
            shouldFail = false;
            successfulBankDealsResponse = bankDeals;
        }

        public void setTokenResponse(Token token) {
            shouldFail = false;
            successfulTokenResponse = token;
        }

        public void setIssuersResponse(List<Issuer> issuers) {
            shouldFail = false;
            successfulIssuersResponse = issuers;
        }

        public void setDiscountResponse(Discount discount) {
            shouldFail = false;
            successfulDiscountResponse = discount;
        }

        public void setPaymentMethodsResponse(List<PaymentMethod> paymentMethods) {
            shouldFail = false;
            successfulPaymentMethodsResponse = paymentMethods;
        }

        public void setPaymentMethodsResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        @Override
        public MPTrackingContext getTrackingContext() {
            return null;
        }

        @Override
        public String getMissingIdentificationTypesErrorMessage() {
            return MISSING_IDENTIFICATION_TYPES;
        }

        @Override
        public String getInvalidIdentificationNumberErrorMessage() {
            return INVALID_IDENTIFICATION_NUMBER;
        }

        @Override
        public String getInvalidEmptyNameErrorMessage() {
            return INVALID_EMPTY_NAME;
        }

        @Override
        public String getMissingPayerCostsErrorMessage() {
            return MISSING_PAYER_COSTS;
        }

        @Override
        public String getMissingInstallmentsForIssuerErrorMessage() {
            return MISSING_INSTALLMENTS;
        }

        @Override
        public String getInvalidExpiryDateErrorMessage() {
            return INVALID_EXPIRY_DATE;
        }

        @Override
        public String getMultipleInstallmentsForIssuerErrorMessage() {
            return MULTIPLE_INSTALLMENTS;
        }

        @Override
        public String getSettingNotFoundForBinErrorMessage() {
            return SETTING_NOT_FOUND_FOR_BIN;
        }

        @Override
        public String getMissingPublicKeyErrorMessage() {
            return MISSING_PUBLIC_KEY;
        }

        @Override
        public String getInvalidFieldErrorMessage() {
            return INVALID_FIELD;
        }

        @Override
        public void getInstallmentsAsync(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, OnResourcesRetrievedCallback<List<Installment>> onResourcesRetrievedCallback) {
            if (shouldFail) {
                onResourcesRetrievedCallback.onFailure(failedResponse);
            } else {
                onResourcesRetrievedCallback.onSuccess(successfulInstallmentsResponse);
            }
        }

        @Override
        public void getIdentificationTypesAsync(OnResourcesRetrievedCallback<List<IdentificationType>> onResourcesRetrievedCallback) {
            if (shouldFail) {
                onResourcesRetrievedCallback.onFailure(failedResponse);
            } else {
                onResourcesRetrievedCallback.onSuccess(successfulIdentificationTypesResponse);
            }
        }

        @Override
        public void getBankDealsAsync(OnResourcesRetrievedCallback<List<BankDeal>> onResourcesRetrievedCallback) {
            if (shouldFail) {
                onResourcesRetrievedCallback.onFailure(failedResponse);
            } else {
                onResourcesRetrievedCallback.onSuccess(successfulBankDealsResponse);
            }
        }

        @Override
        public void createTokenAsync(CardToken cardToken, OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback) {
            if (shouldFail) {
                onResourcesRetrievedCallback.onFailure(failedResponse);
            } else {
                onResourcesRetrievedCallback.onSuccess(successfulTokenResponse);
            }
        }

        @Override
        public void getIssuersAsync(String paymentMethodId, String bin, OnResourcesRetrievedCallback<List<Issuer>> onResourcesRetrievedCallback) {
            if (shouldFail) {
                onResourcesRetrievedCallback.onFailure(failedResponse);
            } else {
                onResourcesRetrievedCallback.onSuccess(successfulIssuersResponse);
            }
        }

        @Override
        public void getDirectDiscountAsync(String transactionAmount, String payerEmail, String merchantDiscountUrl, String merchantDiscountUri, Map<String, String> discountAdditionalInfo, OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
            if (shouldFail) {
                onResourcesRetrievedCallback.onFailure(failedResponse);
            } else {
                onResourcesRetrievedCallback.onSuccess(successfulDiscountResponse);
            }
        }

        @Override
        public void getMPDirectDiscount(String transactionAmount, String payerEmail, OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
            if (shouldFail) {
                onResourcesRetrievedCallback.onFailure(failedResponse);
            } else {
                onResourcesRetrievedCallback.onSuccess(successfulDiscountResponse);
            }
        }

        @Override
        public void getPaymentMethodsAsync(OnResourcesRetrievedCallback<List<PaymentMethod>> onResourcesRetrievedCallback) {
            if (shouldFail) {
                onResourcesRetrievedCallback.onFailure(failedResponse);
            } else {
                onResourcesRetrievedCallback.onSuccess(successfulPaymentMethodsResponse);
            }
        }
    }
}
