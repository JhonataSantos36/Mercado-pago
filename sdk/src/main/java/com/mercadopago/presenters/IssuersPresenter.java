package com.mercadopago.presenters;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.providers.IssuersProvider;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.views.IssuersActivityView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 10/11/16.
 */

public class IssuersPresenter extends MvpPresenter<IssuersActivityView, IssuersProvider> {

    //Local vars
    private PaymentMethod mPaymentMethod;
    private List<Issuer> mIssuers;
    private CardInfo mCardInfo;
    private FailureRecovery mFailureRecovery;

    //Card Info
    private String mBin = "";

    public void initialize() {
        if (wereIssuersSet()) {
            resolveIssuers(mIssuers);
        } else {
            getIssuersAsync();
        }
    }

    private boolean wereIssuersSet() {
        return mIssuers != null;
    }

    private void resolveIssuers(List<Issuer> issuers) {
        if (issuers == null) {
            issuers = new ArrayList<>();
        }

        mIssuers = issuers;

        if (mIssuers.isEmpty()) {
            getView().showError(getResourcesProvider().getEmptyIssuersError(), "");
        } else if (mIssuers.size() == 1) {
            getView().finishWithResult(issuers.get(0));
        } else {
            getView().showHeader();
            getView().showIssuers(issuers, getDpadSelectionCallback());
        }
    }

    private void getIssuersAsync() {
        getView().showLoadingView();

        getResourcesProvider().getIssuers(mPaymentMethod.getId(), mBin, new TaggedCallback<List<Issuer>>(ApiUtil.RequestOrigin.GET_ISSUERS) {
            @Override
            public void onSuccess(List<Issuer> issuers) {
                getView().stopLoadingView();
                resolveIssuers(issuers);
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                getView().stopLoadingView();

                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getIssuersAsync();
                    }
                });

                getView().showError(error, ApiUtil.RequestOrigin.GET_ISSUERS);
            }
        });
    }

    private OnSelectedCallback<Integer> getDpadSelectionCallback() {
        return new OnSelectedCallback<Integer>() {
            @Override
            public void onSelected(Integer position) {
                onItemSelected(position);
            }
        };
    }

    public void onItemSelected(int position) {
        getView().finishWithResult(mIssuers.get(position));
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        mPaymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
        return mPaymentMethod;
    }

    public void setIssuers(List<Issuer> issuers) {
        mIssuers = issuers;
    }

    public CardInfo getCardInfo() {
        return mCardInfo;
    }

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        mFailureRecovery = failureRecovery;
    }

    public FailureRecovery getFailureRecovery() {
        return mFailureRecovery;
    }

    public void setCardInfo(CardInfo cardInfo) {
        mCardInfo = cardInfo;

        if (mCardInfo != null) {
            mBin = mCardInfo.getFirstSixDigits();
        }
    }

    public String getBin() {
        return mBin;
    }

    public boolean isRequiredCardDrawn() {
        return mCardInfo != null && mPaymentMethod != null;
    }
}
