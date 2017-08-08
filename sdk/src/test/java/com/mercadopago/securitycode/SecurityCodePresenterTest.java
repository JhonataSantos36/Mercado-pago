package com.mercadopago.securitycode;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SecurityCode;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.presenters.SecurityCodePresenter;
import com.mercadopago.providers.SecurityCodeProvider;
import com.mercadopago.util.TextUtil;
import com.mercadopago.utils.MVPStructure;
import com.mercadopago.views.SecurityCodeActivityView;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by marlanti on 7/18/17.
 */

public class SecurityCodePresenterTest {

    private static final String NO_PARAMETERS_SET = "no_parameters_set";
    private static final String ALL_PARAMETERS_SET = "all_parameters_set";
    private static final String PAYMENT_METHOD_NOT_SET = "payment_method_not_set";
    private static final String CARD_NOT_SET = "card_not_set";
    private static final String TOKEN_NOT_SET = "token_not_set";
    private static final String CARD_AND_TOKEN_NOT_SET = "card_and_token_not_set";

    //If someone adds a new parameter and forgets to test it.
    @Test
    public void showErrorWhenInvalidParameters() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure(NO_PARAMETERS_SET);

        SecurityCodeMockedProvider provider = mvp.getProvider();
        SecurityCodeMockedView view = mvp.getView();
        SecurityCodePresenter presenter = mvp.getPresenter();

        presenter.initialize();

        assertTrue(provider.standardErrorMessageGotten);
        assertTrue(isErrorShown(view));
        assertFalse(view.initializeDone);
    }

    @Test
    public void ifPaymentMethodNotSetShowError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure(PAYMENT_METHOD_NOT_SET);

        SecurityCodeMockedProvider provider = mvp.getProvider();
        SecurityCodeMockedView view = mvp.getView();
        SecurityCodePresenter presenter = mvp.getPresenter();

        presenter.initialize();

        assertTrue(provider.standardErrorMessageGotten);
        assertTrue(isErrorShown(view));
        assertFalse(view.initializeDone);
    }

    @Test
    public void ifCardAndTokenNotSetShowError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure(CARD_AND_TOKEN_NOT_SET);

        SecurityCodeMockedProvider provider = mvp.getProvider();
        SecurityCodeMockedView view = mvp.getView();
        SecurityCodePresenter presenter = mvp.getPresenter();

        presenter.initialize();

        assertTrue(provider.standardErrorMessageGotten);
        assertTrue(isErrorShown(view));
        assertFalse(view.initializeDone);
    }

    @Test
    public void ifCardAndTokenSetShowError() {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvp = getMVPStructure(ALL_PARAMETERS_SET);

        SecurityCodeMockedProvider provider = mvp.getProvider();
        SecurityCodeMockedView view = mvp.getView();
        SecurityCodePresenter presenter = mvp.getPresenter();

        presenter.initialize();

        assertTrue(provider.standardErrorMessageGotten);
        assertTrue(isErrorShown(view));
        assertFalse(view.initializeDone);
    }


    public boolean isErrorShown(SecurityCodeMockedView view) {
        return !TextUtil.isEmpty(view.errorMessage) && view.error != null;
    }

    public MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> getMVPStructure(String emptyParameter) {
        MVPStructure<SecurityCodePresenter, SecurityCodeMockedProvider, SecurityCodeMockedView, SecurityCode> mvpStructure = new MVPStructure<>();

        SecurityCodeMockedView view = new SecurityCodeMockedView();

        SecurityCodePresenter presenter = new SecurityCodePresenter();
        presenter.attachView(view);
        SecurityCodeMockedProvider provider = new SecurityCodeMockedProvider();
        presenter.attachResourcesProvider(provider);

        if (!emptyParameter.equals(NO_PARAMETERS_SET)) {
            if (!emptyParameter.equals(PAYMENT_METHOD_NOT_SET)) {
                PaymentMethod paymentMethod = new PaymentMethod();
                presenter.setPaymentMethod(paymentMethod);
            }
            if (!emptyParameter.equals(CARD_NOT_SET)) {
                Card card = new Card();
                presenter.setCard(card);
            }
            if (!emptyParameter.equals(TOKEN_NOT_SET)) {
                Token token = new Token();
                presenter.setToken(token);
            }
            if (!emptyParameter.equals(CARD_AND_TOKEN_NOT_SET)) {
                Card card = new Card();
                presenter.setCard(card);
                Token token = new Token();
                presenter.setToken(token);
            }
        }

        mvpStructure.setPresenter(presenter);
        mvpStructure.setProvider(provider);
        mvpStructure.setView(view);

        return mvpStructure;
    }

    private class SecurityCodeMockedProvider implements SecurityCodeProvider {

        private boolean standardErrorMessageGotten = false;

        public String getStandardErrorMessageGotten() {
            this.standardErrorMessageGotten = true;
            return "We are going to fix it. Try later.";
        }

        @Override
        public void cloneToken(String tokenId, OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback) {

        }

        @Override
        public void putSecurityCode(String securityCode, String tokenId, OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback) {

        }

        @Override
        public void createToken(SavedCardToken savedCardToken, OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback) {

        }

        @Override
        public void validateSecurityCodeFromToken(String mSecurityCode, PaymentMethod mPaymentMethod, String firstSixDigits) throws Exception {

        }

        @Override
        public void validateSecurityCodeFromToken(String mSecurityCode) {

        }

        @Override
        public void validateSecurityCodeFromToken(SavedCardToken savedCardToken, Card card) throws Exception {

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

        @Override
        public void initialize() {
            initializeDone = true;
        }


        @Override
        public void setSecurityCodeInputMaxLength(int length) {

        }

        @Override
        public void showError(MercadoPagoError error, String requestOrigin) {
            this.error = error;
            this.errorMessage = error.getMessage();
        }

        @Override
        public void showApiExceptionError(ApiException exception, String requestOrigin) {

        }

        @Override
        public void setErrorView(String message) {

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
