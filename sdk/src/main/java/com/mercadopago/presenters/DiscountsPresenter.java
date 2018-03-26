package com.mercadopago.presenters;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Discount;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.providers.DiscountsProvider;
import com.mercadopago.views.DiscountsActivityView;

import java.math.BigDecimal;

/**
 * Created by mromar on 11/29/16.
 */

public class DiscountsPresenter extends MvpPresenter<DiscountsActivityView, DiscountsProvider> {

    private DiscountsActivityView mDiscountsView;

    //Activity parameters
    private String mPublicKey;
    private String mPayerEmail;
    private BigDecimal mTransactionAmount;
    private Discount mDiscount;
    private Boolean mDirectDiscountEnabled;

    @Override
    public void attachView(DiscountsActivityView discountsView) {
        mDiscountsView = discountsView;
    }

    public void initialize() {
        if (mDiscount == null) {
            initDiscountFlow();
        } else {
            mDiscountsView.drawSummary();
        }
    }

    private void initDiscountFlow() {
        if (mDirectDiscountEnabled && isTransactionAmountValid()) {
            mDiscountsView.hideDiscountSummary();
            getDirectDiscount();
        } else {
            mDiscountsView.requestDiscountCode();
        }
    }

    private Boolean isTransactionAmountValid() {
        return mTransactionAmount != null && mTransactionAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    private void getDirectDiscount() {
        getResourcesProvider().getDirectDiscount(mTransactionAmount.toString(), mPayerEmail, new OnResourcesRetrievedCallback<Discount>() {
            @Override
            public void onSuccess(Discount discount) {
                mDiscount = discount;
                mDiscountsView.drawSummary();
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                mDiscountsView.requestDiscountCode();
            }
        });
    }

    private void getCodeDiscount(final String discountCode) {
        mDiscountsView.showProgressBar();

        getResourcesProvider().getCodeDiscount(mTransactionAmount.toString(), mPayerEmail, discountCode, new OnResourcesRetrievedCallback<Discount>() {
            @Override
            public void onSuccess(Discount discount) {
                mDiscountsView.setSoftInputModeSummary();
                mDiscountsView.hideKeyboard();
                mDiscountsView.hideProgressBar();

                mDiscount = discount;
                mDiscount.setCouponCode(discountCode);
                mDiscountsView.drawSummary();
            }

            @Override
            public void onFailure(MercadoPagoError error) {
                mDiscountsView.hideProgressBar();
                if(error.isApiException()) {
                    String errorMessage = getResourcesProvider().getApiErrorMessage(error.getApiException().getError());
                    mDiscountsView.showCodeInputError(errorMessage);
                } else {
                    mDiscountsView.showCodeInputError(getResourcesProvider().getStandardErrorMessage());
                }
            }
        });
    }

    public void validateDiscountCodeInput(String discountCode) {
        if (isTransactionAmountValid()) {
            if (isEmpty(discountCode)) {
                mDiscountsView.showEmptyDiscountCodeError();
            } else {
                getCodeDiscount(discountCode);
            }
        } else {
            mDiscountsView.finishWithCancelResult();
        }
    }

    public Discount getDiscount() {
        return mDiscount;
    }

    public void setMerchantPublicKey(String publicKey) {
        mPublicKey = publicKey;
    }

    public void setPayerEmail(String payerEmail) {
        mPayerEmail = payerEmail;
    }

    public void setDiscount(Discount discount) {
        mDiscount = discount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        mTransactionAmount = transactionAmount;
    }

    public void setDirectDiscountEnabled(Boolean directDiscountEnabled) {
        mDirectDiscountEnabled = directDiscountEnabled;
    }

    public Boolean getDirectDiscountEnabled() {
        return mDirectDiscountEnabled;
    }

    public String getCurrencyId() {
        return mDiscount.getCurrencyId();
    }

    public BigDecimal getTransactionAmount() {
        return mTransactionAmount;
    }

    public BigDecimal getCouponAmount() {
        return mDiscount.getCouponAmount();
    }

    public String getPublicKey() {
        return mPublicKey;
    }

    private boolean isEmpty(String discountCode) {
        return discountCode == null || discountCode.isEmpty();
    }
}
