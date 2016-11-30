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
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.views.CardVaultActivityView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 10/12/16.
 */

public class CardVaultPresenter {

    private Context mContext;
    private CardVaultActivityView mView;
    private FailureRecovery mFailureRecovery;
    protected MercadoPago mMercadoPago;
    private String mBin;

    //Activity parameters
    protected PaymentRecovery mPaymentRecovery;
    protected PaymentPreference mPaymentPreference;
    protected List<PaymentMethod> mPaymentMethodList;
    protected Site mSite;
    protected Boolean mInstallmentsEnabled;
    protected String mPublicKey;
    protected BigDecimal mAmount;

    //Activity result
    protected PaymentMethod mPaymentMethod;
    protected PayerCost mPayerCost;
    protected Issuer mIssuer;

    //Card Info
    protected CardInfo mCardInfo;
    protected Token mToken;
    protected CardToken mCardToken;
    protected Card mCard;

    public CardVaultPresenter(Context context) {
        this.mContext = context;
    }

    public void setView(CardVaultActivityView view) {
        this.mView = view;
    }

    public void setPaymentRecovery(PaymentRecovery paymentRecovery) {
        this.mPaymentRecovery = paymentRecovery;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.mPaymentPreference = paymentPreference;
    }

    public void setPaymentMethodList(List<PaymentMethod> paymentMethodList) {
        this.mPaymentMethodList = paymentMethodList;
    }

    public void setSite(Site site) {
        this.mSite = site;
    }

    public void setInstallmentsEnabled(Boolean installmentsEnabled) {
        this.mInstallmentsEnabled = installmentsEnabled;
    }

    public void setCard(Card card) {
        this.mCard = card;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

    public void setAmount(BigDecimal amount) {
        this.mAmount = amount;
    }

    private void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    public Issuer getIssuer() {
        return mIssuer;
    }

    public void setIssuer(Issuer mIssuer) {
        this.mIssuer = mIssuer;
    }

    public Token getToken() {
        return mToken;
    }

    public void setToken(Token mToken) {
        this.mToken = mToken;
    }

    public PaymentMethod getPaymentMethod() {
        return mPaymentMethod;
    }

    public void setPaymentMethod(PaymentMethod mPaymentMethod) {
        this.mPaymentMethod = mPaymentMethod;
    }

    public PayerCost getPayerCost() {
        return mPayerCost;
    }

    public void setPayerCost(PayerCost mPayerCost) {
        this.mPayerCost = mPayerCost;
    }

    public BigDecimal getAmount() {
        return mAmount;
    }

    public PaymentRecovery getPaymentRecovery() {
        return mPaymentRecovery;
    }

    public PaymentPreference getPaymentPreference() {
        return mPaymentPreference;
    }

    public List<PaymentMethod> getPaymentMethodList() {
        return mPaymentMethodList;
    }

    public Site getSite() {
        return mSite;
    }

    public Card getCard() {
        return mCard;
    }

    public String getPublicKey() {
        return mPublicKey;
    }

    public CardToken getCardToken() {
        return mCardToken;
    }

    public void setCardToken(CardToken mCardToken) {
        this.mCardToken = mCardToken;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.mCardInfo = cardInfo;
        if (mCardInfo == null) {
            mBin = "";
        } else {
            mBin = mCardInfo.getFirstSixDigits();
        }
    }

    public CardInfo getCardInfo() {
        return mCardInfo;
    }

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(mPaymentMethod, mBin);
    }

    public void checkStartInstallmentsActivity() {
        if (installmentsRequired()) {
            mView.startInstallmentsActivity();
            mView.overrideTranstitionHold();
        } else {
            createToken();
        }
    }

    public void checkStartIssuersActivity() {
        if (mIssuer == null) {
            mView.startIssuersActivity();
        } else {
            checkStartInstallmentsActivity();
        }
    }

    public boolean installmentsRequired() {
        return mInstallmentsEnabled;
    }

    public void validateActivityParameters() {
        if (mPublicKey == null) {
            mView.onInvalidStart("public key not set");
        } else if (mInstallmentsEnabled && (mSite == null || mAmount == null)) {
            mView.onInvalidStart("missing site or amount");
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

    public void createToken() {
        mMercadoPago.createToken(mCardToken, new Callback<Token>() {
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
                        createToken();
                    }
                });
                mView.showApiExceptionError(apiException);
            }
        });
    }

    public void createToken(final SavedCardToken savedCardToken) {
        mMercadoPago.createToken(savedCardToken, new Callback<Token>() {
            @Override
            public void success(Token token) {
                mToken = token;
            }

            @Override
            public void failure(ApiException apiException) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        createToken(savedCardToken);
                    }
                });
                mView.showApiExceptionError(apiException);
            }
        });
    }
}
