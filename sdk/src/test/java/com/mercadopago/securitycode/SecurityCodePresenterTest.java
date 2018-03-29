package com.mercadopago.securitycode;

import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.lite.exceptions.CardTokenException;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.mocks.Cards;
import com.mercadopago.mocks.Issuers;
import com.mercadopago.mocks.PayerCosts;
import com.mercadopago.mocks.PaymentMethods;
import com.mercadopago.mocks.Tokens;
import com.mercadopago.lite.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.lite.model.Issuer;
import com.mercadopago.lite.model.PayerCost;
import com.mercadopago.lite.model.Payment;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.lite.model.SavedCardToken;
import com.mercadopago.lite.model.SavedESCCardToken;
import com.mercadopago.lite.model.SecurityCode;
import com.mercadopago.lite.model.Token;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.presenters.SecurityCodePresenter;
import com.mercadopago.providers.SecurityCodeProvider;
import com.mercadopago.util.TextUtil;
import com.mercadopago.utils.MVPStructure;
import com.mercadopago.views.SecurityCodeActivityView;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by marlanti on 7/18/17.
 */

public class SecurityCodePresenterTest {

    private static final String PAYMENT_METHOD_NOT_SET = "payment_method_not_set";
    private static final String CARD_AND_TOKEN_NOT_SET = "card_and_token_not_set";
    private static final String CARD_AND_TOKEN_SET_WITHOUT_RECOVERY = "card_and_token_set_without_recovery";
    private static final String CARD_INFO_NOT_SET = "card_info_not_set";
    private static final String ERROR_SECURITY_CODE = "error_security_code";
    private static final int CARD_TOKEN_INVALID_SECURITY_CODE = 9;

    @Test
    public void showErrorWhenInvalidParameters() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();
        mvp.getPresenter().setCardInfo(new CardInfo(Tokens.getVisaToken()));

        mvp.getPresenter().initialize();

        assertTrue(mvp.getProvider().standardErrorMessageGotten);
        assertTrue(isErrorShown(mvp.getView()));
        assertFalse(mvp.getView().initializeDone);
        assertEquals(mvp.getProvider().errorMessage, CARD_AND_TOKEN_NOT_SET);
    }

    @Test
    public void ifPaymentMethodNotSetShowError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        mvp.getPresenter().setCard(Cards.getCard());

        mvp.getPresenter().initialize();

        assertTrue(mvp.getProvider().standardErrorMessageGotten);
        assertTrue(isErrorShown(mvp.getView()));
        assertFalse(mvp.getView().initializeDone);
        assertEquals(mvp.getProvider().errorMessage, PAYMENT_METHOD_NOT_SET);
    }

    @Test
    public void ifCardAndTokenNotSetShowError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        mvp.getPresenter().initialize();

        assertTrue(mvp.getProvider().standardErrorMessageGotten);
        assertTrue(isErrorShown(mvp.getView()));
        assertFalse(mvp.getView().initializeDone);
        assertEquals(mvp.getProvider().errorMessage, CARD_AND_TOKEN_NOT_SET);
    }

    @Test
    public void ifCardAndTokenWithoutRecoverySetShowError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        mvp.getPresenter().setCard(Cards.getCard());
        mvp.getPresenter().setToken(Tokens.getToken());

        mvp.getPresenter().initialize();

        assertTrue(mvp.getProvider().standardErrorMessageGotten);
        assertTrue(isErrorShown(mvp.getView()));
        assertFalse(mvp.getView().initializeDone);
        assertEquals(mvp.getProvider().errorMessage, CARD_AND_TOKEN_SET_WITHOUT_RECOVERY);
    }

    @Test
    public void ifCardInfoNotSetThenShowError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        mvp.getPresenter().setCard(Cards.getCard());
        mvp.getPresenter().setPaymentMethod(PaymentMethods.getPaymentMethodOnMaster());

        mvp.getPresenter().initialize();

        assertTrue(mvp.getProvider().standardErrorMessageGotten);
        assertTrue(isErrorShown(mvp.getView()));
        assertFalse(mvp.getView().initializeDone);
        assertEquals(mvp.getProvider().errorMessage, CARD_INFO_NOT_SET);
    }

    @Test
    public void ifCardAndTokenWithRecoverySetDontShowError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPM = PaymentMethods.getPaymentMethodOnVisa();

        mvp.getPresenter().setCard(Cards.getCard());
        mvp.getPresenter().setToken(Tokens.getTokenWithESC());
        mvp.getPresenter().setPaymentRecovery(getPaymentRecoveryForESC(mockedPM));
        mvp.getPresenter().setPaymentMethod(mockedPM);
        mvp.getPresenter().setCardInfo(new CardInfo(Cards.getCard()));

        mvp.getPresenter().initialize();

        assertFalse(mvp.getProvider().standardErrorMessageGotten);
        assertFalse(isErrorShown(mvp.getView()));
        assertTrue(mvp.getView().initializeDone);
    }

    @Test
    public void whenInitializedThenSetInputMaxLength() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();

        mvp.getPresenter().setCard(Cards.getCard());
        mvp.getPresenter().setToken(Tokens.getTokenWithESC());
        mvp.getPresenter().setPaymentRecovery(getPaymentRecoveryForESC(mockedPaymentMethod));
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(Cards.getCard()));

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();
        assertEquals(mvp.getView().maxLenght, mockedPaymentMethod.getSettings().get(0).getSecurityCode().getLength());
    }

    @Test
    public void whenInitializedWithoutSecurityCodeSettingsThenSetDefaultInputMaxLength() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMasterWithoutSecurityCodeSettings();
        Card mockedCard = Cards.getCardWithoutSecurityCodeSettings();

        mvp.getPresenter().setCard(mockedCard);
        mvp.getPresenter().setToken(Tokens.getTokenWithESC());
        mvp.getPresenter().setPaymentRecovery(getPaymentRecoveryForESC(mockedPaymentMethod));
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedCard));

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();
        assertEquals(mvp.getView().maxLenght, SecurityCodePresenter.CARD_DEFAULT_SECURITY_CODE_LENGTH);
    }

    @Test
    public void onCallForAuthRecoveryCloneToken() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        Token mockedToken = Tokens.getToken();

        mvp.getPresenter().setToken(mockedToken);
        mvp.getPresenter().setPaymentRecovery(getPaymentRecoveryForCallForAuth(mockedPaymentMethod));
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedToken));

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();

        //Input for security code
        mvp.getProvider().setCloneTokenResponse(mockedToken);
        mvp.getProvider().setPutSecurityCodeResponse(mockedToken);
        mvp.getPresenter().saveSecurityCode("123");
        mvp.getPresenter().validateSecurityCodeInput();
        assertEquals(mvp.getProvider().successfulCloneTokenResponse, mockedToken);
    }

    @Test
    public void onCallForAuthRecoveryCloneTokenThenPutSecurityCode() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        Token mockedToken = Tokens.getToken();

        mvp.getPresenter().setToken(mockedToken);
        mvp.getPresenter().setPaymentRecovery(getPaymentRecoveryForCallForAuth(mockedPaymentMethod));
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedToken));

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();

        //Input for security code
        mvp.getProvider().setCloneTokenResponse(mockedToken);
        mvp.getPresenter().saveSecurityCode("123");
        assertEquals(mvp.getProvider().successfulCloneTokenResponse, mockedToken);

        mvp.getProvider().setPutSecurityCodeResponse(mockedToken);
        mvp.getPresenter().putSecurityCode();
        assertEquals(mvp.getProvider().successfulPutSecurityCodeResponse, mockedToken);
    }

    @Test
    public void onCallForAuthRecoveryCloneTokenError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        Token mockedToken = Tokens.getToken();

        mvp.getPresenter().setToken(mockedToken);
        mvp.getPresenter().setPaymentRecovery(getPaymentRecoveryForCallForAuth(mockedPaymentMethod));
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedToken));

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();

        //Input for security code
        ApiException apiException = Tokens.getInvalidCloneToken();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        mvp.getProvider().setCloneTokenResponse(mpException);

        mvp.getPresenter().saveSecurityCode("123");
        mvp.getPresenter().validateSecurityCodeInput();
        assertEquals(mvp.getProvider().failedResponse, mpException);
        assertTrue(mvp.getView().errorState);
    }

    @Test
    public void onCallForAuthRecoveryPutSecurityCodeAndGetError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        Token mockedToken = Tokens.getToken();

        mvp.getPresenter().setToken(mockedToken);
        mvp.getPresenter().setPaymentRecovery(getPaymentRecoveryForCallForAuth(mockedPaymentMethod));
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedToken));

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();

        //Input for security code
        mvp.getProvider().setCloneTokenResponse(mockedToken);
        mvp.getPresenter().saveSecurityCode("123");
        assertEquals(mvp.getProvider().successfulCloneTokenResponse, mockedToken);

        ApiException apiException = Tokens.getInvalidCloneToken();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        mvp.getProvider().setPutSecurityCodeResponse(mpException);

        mvp.getPresenter().putSecurityCode();
        assertEquals(mvp.getProvider().failedResponse, mpException);
        assertTrue(mvp.getView().errorState);
    }

    @Test
    public void onCloneTokenAndSecurityCodeInputIsNotValidThenShowError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        Token mockedToken = Tokens.getToken();

        mvp.getPresenter().setToken(mockedToken);
        mvp.getPresenter().setPaymentRecovery(getPaymentRecoveryForCallForAuth(mockedPaymentMethod));
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedToken));

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();

        //Input for security code
        mvp.getProvider().shouldFailSecurityCodeValidation = true;
        mvp.getProvider().setCloneTokenResponse(mockedToken);
        mvp.getPresenter().saveSecurityCode("1");
        mvp.getPresenter().validateSecurityCodeInput();

        assertTrue(mvp.getView().errorState);
        assertNotNull(mvp.getView().cardTokenErrorCode);
    }

    @Test
    public void onSaveCardWithESCEnabledThenCreateESCToken() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        Card mockedCard = Cards.getCard();

        mvp.getPresenter().setCard(mockedCard);
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedCard));

        //ESC enabled
        mvp.getProvider().enableESC(true);

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();

        //Input for security code
        Token mockedToken = Tokens.getTokenWithESC();
        mvp.getProvider().setCreateTokenWithEscResponse(mockedToken);
        mvp.getPresenter().saveSecurityCode("123");
        mvp.getPresenter().validateSecurityCodeInput();
        assertEquals(mvp.getProvider().successfulcreateESCTokenResponse, mockedToken);
    }

    @Test
    public void onSaveCardWithESCEnabledThenCreateESCTokenHasError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        Card mockedCard = Cards.getCard();
      
        mvp.getPresenter().setCard(mockedCard);
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedCard));

        //ESC enabled
        mvp.getProvider().enableESC(true);

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();

        //Input for security code
        ApiException apiException = Tokens.getInvalidTokenWithESC();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        mvp.getProvider().setPutSecurityCodeResponse(mpException);

        mvp.getProvider().setCreateTokenWithEscResponse(mpException);
        mvp.getPresenter().saveSecurityCode("123");
        mvp.getPresenter().validateSecurityCodeInput();
        assertEquals(mvp.getProvider().failedResponse, mpException);
        assertTrue(mvp.getView().errorState);
    }

    @Test
    public void onESCRecoverFromPaymentThenCreateESCToken() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        Token mockedToken = Tokens.getTokenWithESC();
        Card mockedCard = Cards.getCard();
        PaymentRecovery paymentRecovery = getPaymentRecoveryForESC(mockedPaymentMethod);

        mvp.getPresenter().setCard(mockedCard);
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedCard));
        mvp.getPresenter().setToken(mockedToken);
        mvp.getPresenter().setPaymentRecovery(paymentRecovery);

        //ESC enabled
        mvp.getProvider().enableESC(true);

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();

        //Input for security code
        mvp.getProvider().setCreateTokenWithEscResponse(mockedToken);
        mvp.getPresenter().saveSecurityCode("123");
        mvp.getPresenter().validateSecurityCodeInput();
        assertEquals(mvp.getProvider().successfulcreateESCTokenResponse, mockedToken);
    }

    @Test
    public void onSavedCardWithoutESCEnabledThenCreateToken() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        Card mockedCard = Cards.getCard();

        mvp.getPresenter().setCard(mockedCard);
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedCard));

        //Disable ESC
        mvp.getProvider().enableESC(false);

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();

        //Input for security code
        Token mockedToken = Tokens.getToken();
        mvp.getProvider().setCreateTokenWithSavedCardTokenResponse(mockedToken);
        mvp.getPresenter().saveSecurityCode("123");
        mvp.getPresenter().validateSecurityCodeInput();
        assertEquals(mvp.getProvider().successfulcreateTokenWithSavedCardTokenResponse, mockedToken);
    }

    @Test
    public void onSavedCardWithoutESCEnabledThenCreateTokenHasError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        Card mockedCard = Cards.getCard();

        mvp.getPresenter().setCard(mockedCard);
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedCard));

        //Disable ESC
        mvp.getProvider().enableESC(false);

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();

        //Input for security code
        ApiException apiException = Tokens.getInvalidCreateToken();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        mvp.getProvider().setCreateTokenWithSavedCardTokenResponse(mpException);

        mvp.getPresenter().saveSecurityCode("123");
        mvp.getPresenter().validateSecurityCodeInput();
        assertEquals(mvp.getProvider().failedResponse, mpException);
    }

    @Test
    public void onESCRecoverFromPaymentWithPaymentResultIntegrationThenCreateESCToken() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure();

        PaymentMethod mockedPaymentMethod = PaymentMethods.getPaymentMethodOnMaster();
        Token mockedToken = Tokens.getTokenWithESC();
        PaymentRecovery paymentRecovery = getPaymentRecoveryForESC(mockedPaymentMethod);

        //With wallet integration, with payment result with invalid esc in payment
        //we dont have a card, we only have a token in payment data
        mvp.getPresenter().setPaymentMethod(mockedPaymentMethod);
        mvp.getPresenter().setCardInfo(new CardInfo(mockedToken));
        mvp.getPresenter().setToken(mockedToken);
        mvp.getPresenter().setPaymentRecovery(paymentRecovery);

        //ESC enabled
        mvp.getProvider().enableESC(true);

        mvp.getPresenter().initialize();
        mvp.getPresenter().initializeSecurityCodeSettings();

        //Input for security code
        mvp.getProvider().setCreateTokenWithEscResponse(mockedToken);
        mvp.getPresenter().saveSecurityCode("123");
        mvp.getPresenter().validateSecurityCodeInput();
        assertEquals(mvp.getProvider().successfulcreateESCTokenResponse, mockedToken);
    }

    private boolean isErrorShown(SecurityCodeMockedView view) {
        return !TextUtil.isEmpty(view.errorMessage);
    }

    private PaymentRecovery getPaymentRecoveryForESC(PaymentMethod paymentMethod) {
        Token mockedToken = Tokens.getTokenWithESC();
        PayerCost mockedPayerCost = PayerCosts.getPayerCost();
        Issuer mockedIssuer = Issuers.getIssuerMLA();
        return new PaymentRecovery(mockedToken, paymentMethod, mockedPayerCost, mockedIssuer, Payment.StatusCodes.STATUS_REJECTED, Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC);
    }

    private PaymentRecovery getPaymentRecoveryForCallForAuth(PaymentMethod paymentMethod) {
        Token mockedToken = Tokens.getToken();
        PayerCost mockedPayerCost = PayerCosts.getPayerCost();
        Issuer mockedIssuer = Issuers.getIssuerMLA();
        return new PaymentRecovery(mockedToken, paymentMethod, mockedPayerCost, mockedIssuer, Payment.StatusCodes.STATUS_REJECTED, Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);
    }

    private MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> getMVPStructure() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvpStructure = new MVPStructure<>();

        SecurityCodeMockedView view = new SecurityCodeMockedView();

        SecurityCodePresenter presenter = new SecurityCodePresenter();
        presenter.attachView(view);
        SecurityCodeMockedProvider provider = new SecurityCodeMockedProvider();
        presenter.attachResourcesProvider(provider);

        mvpStructure.setPresenter(presenter);
        mvpStructure.setProvider(provider);
        mvpStructure.setView(view);

        return mvpStructure;
    }

    private class SecurityCodeMockedProvider implements SecurityCodeProvider {

        private boolean isEscEnabled;
        private boolean standardErrorMessageGotten = false;
        private String errorMessage;
        private boolean putSecurityCodeShouldFail = false;
        private boolean cloneTokenShouldFail = false;
        private boolean createTokenWithSavedCardTokenShouldFail = false;
        private boolean createESCTokenShouldFail = false;
        private MercadoPagoError failedResponse;
        private Token successfulCloneTokenResponse;
        private Token successfulPutSecurityCodeResponse;
        private Token successfulcreateTokenWithSavedCardTokenResponse;
        private Token successfulcreateESCTokenResponse;
        private boolean shouldFailSecurityCodeValidation = false;

        public void enableESC(boolean enable) {
            this.isEscEnabled = enable;
        }

        public String getStandardErrorMessageGotten() {
            this.standardErrorMessageGotten = true;
            return "We are going to fix it. Try later.";
        }

        @Override
        public String getTokenAndCardNotSetMessage() {
            this.errorMessage = CARD_AND_TOKEN_NOT_SET;
            return errorMessage;
        }

        @Override
        public String getTokenAndCardWithoutRecoveryCantBeBothSetMessage() {
            this.errorMessage = CARD_AND_TOKEN_SET_WITHOUT_RECOVERY;
            return errorMessage;
        }

        @Override
        public String getPaymentMethodNotSetMessage() {
            this.errorMessage = PAYMENT_METHOD_NOT_SET;
            return errorMessage;
        }

        @Override
        public String getCardInfoNotSetMessage() {
            this.errorMessage = CARD_INFO_NOT_SET;
            return errorMessage;
        }

        public void setCloneTokenResponse(MercadoPagoError exception) {
            cloneTokenShouldFail = true;
            failedResponse = exception;
        }

        public void setCloneTokenResponse(Token token) {
            cloneTokenShouldFail = false;
            successfulCloneTokenResponse = token;
        }

        @Override
        public void cloneToken(String tokenId, TaggedCallback<Token> taggedCallback) {
            if (cloneTokenShouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulCloneTokenResponse);
            }
        }

        public void setPutSecurityCodeResponse(MercadoPagoError exception) {
            putSecurityCodeShouldFail = true;
            failedResponse = exception;
        }

        public void setPutSecurityCodeResponse(Token token) {
            putSecurityCodeShouldFail = false;
            successfulPutSecurityCodeResponse = token;
        }

        @Override
        public void putSecurityCode(String securityCode, String tokenId, TaggedCallback<Token> taggedCallback) {
            if (putSecurityCodeShouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulPutSecurityCodeResponse);
            }
        }

        public void setCreateTokenWithSavedCardTokenResponse(MercadoPagoError exception) {
            createTokenWithSavedCardTokenShouldFail = true;
            failedResponse = exception;
        }

        public void setCreateTokenWithSavedCardTokenResponse(Token token) {
            createTokenWithSavedCardTokenShouldFail = false;
            successfulcreateTokenWithSavedCardTokenResponse = token;
        }

        @Override
        public void createToken(SavedCardToken savedCardToken, TaggedCallback<Token> taggedCallback) {
            if (createTokenWithSavedCardTokenShouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulcreateTokenWithSavedCardTokenResponse);
            }
        }

        public void setCreateTokenWithEscResponse(MercadoPagoError exception) {
            createESCTokenShouldFail = true;
            failedResponse = exception;
        }

        public void setCreateTokenWithEscResponse(Token token) {
            createESCTokenShouldFail = false;
            successfulcreateESCTokenResponse = token;
        }

        @Override
        public void createToken(SavedESCCardToken savedESCCardToken, TaggedCallback<Token> taggedCallback) {
            if (createESCTokenShouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulcreateESCTokenResponse);
            }
        }

        @Override
        public boolean isESCEnabled() {
            return isEscEnabled;
        }


        @Override
        public void validateSecurityCodeFromToken(String mSecurityCode, PaymentMethod mPaymentMethod, String firstSixDigits) throws CardTokenException {
            if (shouldFailSecurityCodeValidation) {
                throw new CardTokenException(CARD_TOKEN_INVALID_SECURITY_CODE);
            }
        }

        @Override
        public void validateSecurityCodeFromToken(String mSecurityCode) {

        }

        @Override
        public void validateSecurityCodeFromToken(SavedCardToken savedCardToken, Card card) throws CardTokenException {
            if (shouldFailSecurityCodeValidation) {
                throw new CardTokenException(CARD_TOKEN_INVALID_SECURITY_CODE);
            }
        }

    }


    private class SecurityCodeMockedView implements SecurityCodeActivityView {
        private boolean screenTracked = false;
        private boolean loadingViewShown = false;
        private boolean finishWithResult = false;
        private boolean initializeDone = false;
        private boolean backSecurityCodeShown = false;
        private boolean frontSecurityCodeShown = false;
        private String errorMessage;
        private MercadoPagoError error;
        private boolean timerShown = false;
        private Integer maxLenght;
        private boolean errorState = false;
        private Integer cardTokenErrorCode;

        @Override
        public void initialize() {
            initializeDone = true;
        }


        @Override
        public void setSecurityCodeInputMaxLength(int length) {
            this.maxLenght = length;
        }

        @Override
        public void showError(MercadoPagoError error, String requestOrigin) {
            this.error = error;
            this.errorMessage = error.getMessage();
            this.errorState = true;
        }

        @Override
        public void showApiExceptionError(ApiException exception, String requestOrigin) {
        }

        @Override
        public void setErrorView(CardTokenException exception) {
            this.cardTokenErrorCode = exception.getErrorCode();
            this.errorState = true;
        }

        @Override
        public void clearErrorView() {
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
        public void showTimer() {
            this.timerShown = true;
        }

        @Override
        public void finishWithResult() {
            this.finishWithResult = true;
        }

        @Override
        public void trackScreen() {
            screenTracked = true;
        }

        @Override
        public void showBackSecurityCodeCardView() {
        }

        @Override
        public void showFrontSecurityCodeCardView() {
        }

    }
}
