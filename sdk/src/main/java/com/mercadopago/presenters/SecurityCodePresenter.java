package com.mercadopago.presenters;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SecurityCode;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.providers.SecurityCodeProvider;
import com.mercadopago.uicontrollers.card.CardView;
import com.mercadopago.util.TextUtils;
import com.mercadopago.views.SecurityCodeActivityView;


/**
 * Created by vaserber on 10/26/16.
 */

public class SecurityCodePresenter extends MvpPresenter<SecurityCodeActivityView, SecurityCodeProvider> {

    public static final int CARD_DEFAULT_SECURITY_CODE_LENGTH = 4;
    public static final int CARD_NUMBER_MAX_LENGTH = 16;
    private static final String TOKEN_AND_CARD_NOT_SET_MESSAGE = "token and card can't both be null";
    private static final String TOKEN_AND_CARD_SET_MESSAGE = "can't set token and card at the same time";
    private static final String PAYMENT_METHOD_NOT_SET = "payment method not set";

    private FailureRecovery mFailureRecovery;

    //Card Info
    private int mSecurityCodeLength;
    private String mSecurityCodeLocation;
    private int mCardNumberLength;
    private String mSecurityCode;

    //Activity parameters
    private PaymentMethod mPaymentMethod;
    protected CardInfo mCardInfo;
    protected Card mCard;
    protected Token mToken;


    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }


    public void setToken(Token token) {
        this.mToken = token;
    }

    public void setCard(Card card) {
        this.mCard = card;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.mCardInfo = cardInfo;
    }

    public CardInfo getCardInfo() {
        return mCardInfo;
    }

    private void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    public boolean isCardInfoAvailable() {
        return mCardInfo != null && mPaymentMethod != null;
    }


    public PaymentMethod getPaymentMethod() {
        return this.mPaymentMethod;
    }

    public Token getToken() {
        return this.mToken;
    }

    public Card getCard() {
        return this.mCard;
    }

    public int getSecurityCodeLength() {
        return this.mSecurityCodeLength;
    }

    public String getSecurityCodeLocation() {
        return this.mSecurityCodeLocation;
    }

    public int getCardNumberLength() {
        return this.mCardNumberLength;
    }

    public void validate() throws IllegalStateException {
        if (mToken == null && mCard == null) {
            throw new IllegalStateException(TOKEN_AND_CARD_NOT_SET_MESSAGE);
        }

        if (mToken != null && mCard != null) {
            throw new IllegalStateException(TOKEN_AND_CARD_SET_MESSAGE);
        }

        if (mPaymentMethod == null) {
            throw new IllegalStateException(PAYMENT_METHOD_NOT_SET);
        }
    }

    public void initialize() {
        try {
            validate();
            getView().initialize();
            getView().showTimer();
            getView().trackScreen();
        } catch (IllegalStateException exception) {
            String standardErrorMessage = getResourcesProvider().getStandardErrorMessageGotten();
            getView().showError(standardErrorMessage, exception.getMessage());
        }
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    public void initializeSecurityCodeSettings() {
        if (mCardInfo != null) {
            Setting setting = PaymentMethodGuessingController.getSettingByPaymentMethodAndBin(mPaymentMethod, mCardInfo.getFirstSixDigits());
            if (setting != null) {
                SecurityCode securityCode = setting.getSecurityCode();
                if (securityCode != null) {
                    mSecurityCodeLength = securityCode.getLength();
                    mSecurityCodeLocation = securityCode.getCardLocation();
                } else {
                    mSecurityCodeLength = CARD_DEFAULT_SECURITY_CODE_LENGTH;
                    mSecurityCodeLocation = CardView.CARD_SIDE_BACK;
                }
                if (setting.getCardNumber() != null) {
                    mCardNumberLength = setting.getCardNumber().getLength();
                } else {
                    mCardNumberLength = CARD_NUMBER_MAX_LENGTH;

                }
            }
            getView().setSecurityCodeInputMaxLength(mSecurityCodeLength);
        }
    }

    public void saveSecurityCode(String securityCode) {
        this.mSecurityCode = securityCode;
    }

    public void validateSecurityCodeInput() {
        try {
            if (mToken != null && validateSecurityCodeFromToken()) {
                cloneToken();
            } else if (mCard != null) {
                SavedCardToken savedCardToken = new SavedCardToken(mCard.getId(), mSecurityCode);
                getResourcesProvider().validateSecurityCodeFromToken(savedCardToken, mCard);
                createToken(savedCardToken);
            }
        } catch (Exception e) {
            getView().setErrorView(e.getMessage());
        }
    }

    private boolean validateSecurityCodeFromToken() {
        try {
            if (!TextUtils.isEmpty(mToken.getFirstSixDigits())) {
                getResourcesProvider().validateSecurityCodeFromToken(mSecurityCode, mPaymentMethod, mToken.getFirstSixDigits());
            } else {
                getResourcesProvider().validateSecurityCodeFromToken(mSecurityCode);
            }
            getView().clearErrorView();
            return true;
        } catch (Exception e) {
            getView().setErrorView(e.getMessage());
            return false;
        }
    }

    private void cloneToken() {
        getView().showLoadingView();

        getResourcesProvider().cloneToken(mToken.getId(), new OnResourcesRetrievedCallback<Token>() {
            @Override
            public void onSuccess(Token token) {
                mToken = token;
                putSecurityCode();
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        cloneToken();
                    }
                });
                getView().stopLoadingView();
                getView().showError(error.getMessage(), error.getErrorDetail());
            }
        });

    }

    private void putSecurityCode() {

        getResourcesProvider().putSecurityCode(mSecurityCode, mToken.getId(), new OnResourcesRetrievedCallback<Token>() {
            @Override
            public void onSuccess(Token token) {
                mToken = token;
                mToken.setLastFourDigits(mCardInfo.getLastFourDigits());
                getView().finishWithResult();
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        cloneToken();
                    }
                });
                getView().stopLoadingView();
                getView().showError(error.getMessage(), error.getErrorDetail());

            }
        });
    }

    private void createToken(final SavedCardToken savedCardToken) {
        getView().showLoadingView();

        getResourcesProvider().createToken(savedCardToken, new OnResourcesRetrievedCallback<Token>() {
            @Override
            public void onSuccess(Token token) {
                mToken = token;
                mToken.setLastFourDigits(mCardInfo.getLastFourDigits());
                getView().finishWithResult();
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        createToken(savedCardToken);
                    }
                });
                getView().stopLoadingView();
                getView().showError(error.getMessage(), error.getErrorDetail());
            }

        });
    }

    public void setSecurityCodeCardType() {
        if (getSecurityCodeLocation().equals(CardView.CARD_SIDE_BACK)) {
            getView().showBackSecurityCodeCardView();
        } else {
            getView().showFrontSecurityCodeCardView();
        }
    }

}
