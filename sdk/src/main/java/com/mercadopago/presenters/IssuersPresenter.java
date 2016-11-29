package com.mercadopago.presenters;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.views.IssuersActivityView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 10/11/16.
 */

public class IssuersPresenter {

    private IssuersActivityView mView;
    private Context mContext;
    private FailureRecovery mFailureRecovery;

    //Mercado Pago instance
    private MercadoPago mMercadoPago;

    //Card Info
    private String mBin;

    //Activity parameters
    private String mPublicKey;
    private PaymentMethod mPaymentMethod;
    private List<Issuer> mIssuers;
    private PaymentPreference mPaymentPreference;
    protected CardInfo mCardInfo;

    public IssuersPresenter(Context context) {
        this.mContext = context;
    }

    public void setView(IssuersActivityView view) {
        this.mView = view;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
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

    public void setIssuers(List<Issuer> issuers) {
        this.mIssuers = issuers;
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

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(mPaymentMethod, mBin);
    }

    public void validateActivityParameters() throws IllegalStateException {
        if (mPaymentMethod == null) {
            mView.onInvalidStart("payment method is null");
        } else if (mIssuers == null) {
            if (mPublicKey == null) {
                mView.onInvalidStart("public key not set");
            } else {
                mView.onValidStart();
            }
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

    public void loadIssuers() {
        if (wereIssuersSet()) {
            resolveIssuers(mIssuers);
        } else {
            getIssuersAsync();
        }
    }

    private boolean wereIssuersSet() {
        return mIssuers != null;
    }

    private void getIssuersAsync() {
        if (mMercadoPago == null) return;
        mView.showLoadingView();
        mMercadoPago.getIssuers(mPaymentMethod.getId(), mBin,
            new Callback<List<Issuer>>() {
                @Override
                public void success(List<Issuer> issuers) {
                    mView.stopLoadingView();
                    if (issuers == null) {
                        issuers = new ArrayList<Issuer>();
                    }
                    resolveIssuers(issuers);
                }

                @Override
                public void failure(ApiException apiException) {
                    mView.stopLoadingView();
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getIssuersAsync();
                        }
                    });
                    mView.showApiExceptionError(apiException);
                }
            });
    }

    protected void resolveIssuers(List<Issuer> issuers) {
        mIssuers = issuers;
        if (mIssuers.isEmpty()) {
            mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
                    "no issuers found at IssuersActivity");
        } else if (mIssuers.size() == 1) {
            mView.finishWithResult(issuers.get(0));
        } else {
            mView.showHeader();
            mView.initializeIssuers(issuers);
        }
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    public void onItemSelected(int position) {
        mView.finishWithResult(mIssuers.get(position));
    }
}
