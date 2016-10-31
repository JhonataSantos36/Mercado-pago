package com.mercadopago.presenters;

import android.content.Context;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInformation;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
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
    protected Card mCard;
    protected String mPublicKey;
    protected BigDecimal mAmount;
    protected CardInformation mCardInfo;

    //Activity result
    protected Token mToken;
    protected PaymentMethod mPaymentMethod;
    protected PayerCost mPayerCost;
    protected Issuer mIssuer;

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

    public void setCardInformation() {
        if(mCard == null && mToken != null) {
            setCardInformation(mToken);
        } else if (mCard != null) {
            setCardInformation(mCard);
        }
    }

    private void setCardInformation(CardInformation cardInformation) {
        this.mCardInfo = cardInformation;
        if (mCardInfo == null) {
            mBin = "";
        } else {
            mBin = mCardInfo.getFirstSixDigits();
        }
    }

    public CardInformation getCardInformation() {
        return mCardInfo;
    }

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(mPaymentMethod, mBin);
    }

    public void checkStartInstallmentsActivity() {
        if (installmentsRequired()) {
            mView.startInstallmentsActivity();
        } else {
            mView.finishWithResult();
        }
    }

    private boolean installmentsRequired() {
        return mInstallmentsEnabled && (mPaymentRecovery == null || !mPaymentRecovery.isTokenRecoverable());
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

}
