package com.mercadopago.presenters;

import android.content.Context;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SecurityCode;
import com.mercadopago.model.SecurityCodeIntent;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.uicontrollers.card.CardView;
import com.mercadopago.views.SecurityCodeActivityView;


/**
 * Created by vaserber on 10/26/16.
 */

public class SecurityCodePresenter {

    public static final int CARD_DEFAULT_SECURITY_CODE_LENGTH = 4;
    public static final int CARD_NUMBER_MAX_LENGTH = 16;

    private SecurityCodeActivityView mView;
    private Context mContext;
    private FailureRecovery mFailureRecovery;

    //Mercado Pago instance
    private MercadoPago mMercadoPago;

    //Card Info
    private int mSecurityCodeLength;
    private String mSecurityCodeLocation;
    private int mCardNumberLength;
    private String mSecurityCode;

    //Activity parameters
    private String mPublicKey;
    private PaymentMethod mPaymentMethod;
    private PaymentPreference mPaymentPreference;
    protected CardInfo mCardInfo;
    protected Card mCard;
    protected Token mToken;

    public SecurityCodePresenter(Context context) {
        this.mContext = context;
    }

    public void setView(SecurityCodeActivityView view) {
        this.mView = view;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
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

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.mPaymentPreference = paymentPreference;
    }

    private void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    public boolean isCardInfoAvailable() {
        return mCardInfo != null && mPaymentMethod != null;
    }

    public String getPublicKey() {
        return mPublicKey;
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

    public void validateActivityParameters() throws IllegalStateException {
        if (mToken == null && mCard == null) {
            mView.onInvalidStart("token and card can't both be null");
        } else if (mToken != null && mCard != null) {
            mView.onInvalidStart("can't set token and card at the same time");
        } else if (mPublicKey == null) {
            mView.onInvalidStart("public key not set");
        } else if (mPaymentMethod == null) {
            mView.onInvalidStart("payment method not set");
        } else {
            mView.onValidStart();
        }
    }

    public void initializeMercadoPago() {
        if (mPublicKey == null) return;
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mContext)
                .setKey(mPublicKey, MercadoPago.KEY_TYPE_PUBLIC)
                .build();
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
            mView.setSecurityCodeInputMaxLength(mSecurityCodeLength);
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
                savedCardToken.validateSecurityCode(mContext, mCard);
                createToken(savedCardToken);
            }
        } catch (Exception e) {
            mView.setErrorView(e.getMessage());
        }
    }

    private boolean validateSecurityCodeFromToken() {
        try {
            CardToken.validateSecurityCode(mContext, mSecurityCode, mPaymentMethod, mToken.getFirstSixDigits());
            mView.clearErrorView();
            return true;
        } catch (Exception e) {
            mView.setErrorView(e.getMessage());
            return false;
        }
    }

    private void cloneToken() {
        mView.showLoadingView();
        mMercadoPago.cloneToken(mToken.getId(), new Callback<Token>() {
            @Override
            public void success(Token token) {
                mToken = token;
                putSecurityCode();
            }

            @Override
            public void failure(ApiException apiException) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        cloneToken();
                    }
                });
                mView.stopLoadingView();
                mView.showApiExceptionError(apiException);
            }
        });
    }

    private void putSecurityCode() {
        SecurityCodeIntent securityCodeIntent = new SecurityCodeIntent();
        securityCodeIntent.setSecurityCode(mSecurityCode);

        mMercadoPago.putSecurityCode(mToken.getId(), securityCodeIntent, new Callback<Token>() {
            @Override
            public void success(Token token) {
                mToken = token;
                mView.finishWithResult();
            }

            @Override
            public void failure(ApiException apiException) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        cloneToken();
                    }
                });
                mView.stopLoadingView();
                mView.showApiExceptionError(apiException);
            }
        });
    }

    private void createToken(final SavedCardToken savedCardToken) {
        mView.showLoadingView();
        mMercadoPago.createToken(savedCardToken, new Callback<Token>() {
            @Override
            public void success(Token token) {
                mToken = token;
                mToken.setLastFourDigits(mCardInfo.getLastFourDigits());
                mView.finishWithResult();
            }

            @Override
            public void failure(ApiException apiException) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        createToken(savedCardToken);
                    }
                });
                mView.stopLoadingView();
                mView.showApiExceptionError(apiException);
            }
        });
    }


}
