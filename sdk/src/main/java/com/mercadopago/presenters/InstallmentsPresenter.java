package com.mercadopago.presenters;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.providers.InstallmentsProvider;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.InstallmentsUtil;
import com.mercadopago.views.InstallmentsActivityView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 9/29/16.
 */

public class InstallmentsPresenter extends MvpPresenter<InstallmentsActivityView, InstallmentsProvider> {

    private FailureRecovery mFailureRecovery;

    //Card Info
    private String mBin = "";
    private Long mIssuerId;

    //Activity parameters
    private String mPayerEmail;
    private PaymentMethod mPaymentMethod;
    private Issuer mIssuer;
    private BigDecimal mAmount;
    private List<PayerCost> mPayerCosts;
    private PaymentPreference mPaymentPreference;
    private CardInfo mCardInfo;
    private Discount mDiscount;
    private Boolean mDiscountEnabled = true;
    private Boolean mDirectDiscountEnabled = true;
    private Boolean mInstallmentsReviewEnabled;
    private Site mSite;

    public void initialize() {
        initializeDiscountRow();
        showSiteRelatedInformation();
        loadPayerCosts();
    }

    private void showSiteRelatedInformation() {
        if (InstallmentsUtil.shouldWarnAboutBankInterests(mSite)) {
            getView().warnAboutBankInterests();
        }
    }

    private void loadPayerCosts() {
        if (werePayerCostsSet()) {
            resolvePayerCosts(mPayerCosts);
        } else {
            getInstallmentsAsync();
        }
    }

    private boolean werePayerCostsSet() {
        return mPayerCosts != null;
    }

    private void resolvePayerCosts(List<PayerCost> payerCosts) {
        PayerCost defaultPayerCost = mPaymentPreference == null ? null : mPaymentPreference.getDefaultInstallments(payerCosts);
        mPayerCosts = mPaymentPreference == null ? payerCosts : mPaymentPreference.getInstallmentsBelowMax(payerCosts);

        if (defaultPayerCost != null) {
            getView().finishWithResult(defaultPayerCost);
        } else if (mPayerCosts.isEmpty()) {
            getView().showError(getResourcesProvider().getNoPayerCostFoundError(), "");
        } else if (mPayerCosts.size() == 1) {
            getView().finishWithResult(payerCosts.get(0));
        } else {
            getView().showHeader();
            getView().showInstallments(mPayerCosts, getDpadSelectionCallback());
            getView().hideLoadingView();
        }
    }

    private void getInstallmentsAsync() {
        getView().showLoadingView();

        getResourcesProvider().getInstallments(mBin, getAmount(), mIssuerId, mPaymentMethod.getId(), new OnResourcesRetrievedCallback<List<Installment>>() {
            @Override
            public void onSuccess(List<Installment> installments) {
                if (installments.size() == 0) {
                    getView().showError(getResourcesProvider().getNoInstallmentsFoundError(), "");
                } else if (installments.size() == 1) {
                    resolvePayerCosts(installments.get(0).getPayerCosts());
                } else {
                    getView().showError(getResourcesProvider().getMultipleInstallmentsFoundForAnIssuerError(), "");
                }
            }

            @Override
            public void onFailure(MercadoPagoError mercadoPagoError) {
                getView().hideLoadingView();

                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getInstallmentsAsync();
                    }
                });
                getView().showError(mercadoPagoError, ApiUtil.RequestOrigin.GET_INSTALLMENTS);
            }
        });
    }

    public void initializeDiscountRow() {
        getView().showDiscountRow(mAmount);
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.mCardInfo = cardInfo;
        if (mCardInfo != null) {
            mBin = mCardInfo.getFirstSixDigits();
        }
    }

    public CardInfo getCardInfo() {
        return mCardInfo;
    }

    public void setIssuer(Issuer issuer) {
        this.mIssuer = issuer;
        if (mIssuer != null) {
            this.mIssuerId = mIssuer.getId();
        }
    }

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(mPaymentMethod, mBin);
    }

    public void setAmount(BigDecimal amount) {
        this.mAmount = amount;
    }

    public void setPayerCosts(List<PayerCost> payerCosts) {
        this.mPayerCosts = payerCosts;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.mPaymentPreference = paymentPreference;
    }

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    public FailureRecovery getFailureRecovery() {
        return mFailureRecovery;
    }

    public PaymentMethod getPaymentMethod() {
        return this.mPaymentMethod;
    }

    public BigDecimal getAmount() {
        BigDecimal amount;

        if (!mDiscountEnabled || mDiscount == null || !isDiscountValid()) {
            amount = mAmount;
        } else {
            amount = mDiscount.getAmountWithDiscount(mAmount);
        }
        return amount;
    }

    private Boolean isDiscountValid() {
        return isAmountValid(mDiscount.getCouponAmount()) && isCampaignIdValid() && isDiscountCurrencyIdValid();
    }

    private Boolean isDiscountCurrencyIdValid() {
        return mDiscount != null && mDiscount.getCurrencyId() != null && CurrenciesUtil.isValidCurrency(mDiscount.getCurrencyId());
    }

    private Boolean isAmountValid(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private Boolean isCampaignIdValid() {
        return mDiscount.getId() != null;
    }

    public boolean isRequiredCardDrawn() {
        return mCardInfo != null && mPaymentMethod != null;
    }

    public void initializeDiscountActivity() {
        getView().startDiscountFlow(mAmount);
    }

    public void onDiscountReceived(Discount discount) {
        setDiscount(discount);
        initializeDiscountRow();
        getInstallmentsAsync();
    }

    private OnSelectedCallback<Integer> getDpadSelectionCallback() {
        return new OnSelectedCallback<Integer>() {
            @Override
            public void onSelected(Integer position) {
                onItemSelected(position);
            }
        };
    }

    public void setPayerEmail(String payerEmail) {
        this.mPayerEmail = payerEmail;
    }

    public Discount getDiscount() {
        return this.mDiscount;
    }

    public void setDiscount(Discount discount) {
        this.mDiscount = discount;
    }

    public String getPayerEmail() {
        return mPayerEmail;
    }

    public void setDiscountEnabled(Boolean discountEnabled) {
        this.mDiscountEnabled = discountEnabled;
    }

    public Boolean getDiscountEnabled() {
        return this.mDiscountEnabled;
    }

    public void setInstallmentsReviewEnabled(Boolean installmentReviewEnabled) {
        this.mInstallmentsReviewEnabled = installmentReviewEnabled;
    }

    public void setDirectDiscountEnabled(Boolean directDiscountEnabled) {
        this.mDirectDiscountEnabled = directDiscountEnabled;
    }

    public Boolean getDirectDiscountEnabled() {
        return mDirectDiscountEnabled;
    }

    public String getBin() {
        return mBin;
    }

    public Long getIssuerId() {
        return mIssuerId;
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    public void setSite(Site site) {
        this.mSite = site;
    }

    public Site getSite() {
        return this.mSite;
    }

    public void onItemSelected(int position) {
        PayerCost selectedPayerCost = mPayerCosts.get(position);
        if (isInstallmentsReviewEnabled() && isInstallmentsReviewRequired(selectedPayerCost)) {
            getView().hideInstallmentsRecyclerView();
            getView().showInstallmentsReviewView();

            initializeDiscountRow();
            getView().initInstallmentsReviewView(selectedPayerCost);
        } else {
            getView().finishWithResult(selectedPayerCost);
        }
    }

    private Boolean isInstallmentsReviewEnabled() {
        return mInstallmentsReviewEnabled != null && mInstallmentsReviewEnabled;
    }

    private Boolean isInstallmentsReviewRequired(PayerCost payerCost) {
        return payerCost != null && payerCost.getCFTPercent() != null;
    }
}
