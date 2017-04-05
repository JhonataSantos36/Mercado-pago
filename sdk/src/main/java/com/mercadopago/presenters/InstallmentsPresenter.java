package com.mercadopago.presenters;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.core.MercadoPagoServices;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Site;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.TextUtil;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.views.InstallmentsActivityView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by vaserber on 9/29/16.
 */

public class InstallmentsPresenter {

    private InstallmentsActivityView mView;
    private Context mContext;
    private FailureRecovery mFailureRecovery;

    //Mercado Pago instance
    private MercadoPagoServices mMercadoPago;

    //Card Info
    private String mBin;
    private Long mIssuerId;

    //Activity parameters
    private String mPublicKey;
    private String mPrivateKey;
    private String mPayerEmail;
    private PaymentMethod mPaymentMethod;
    private Issuer mIssuer;
    private BigDecimal mAmount;
    private Site mSite;
    private List<PayerCost> mPayerCosts;
    private PaymentPreference mPaymentPreference;
    private CardInfo mCardInfo;
    private Discount mDiscount;
    private Boolean mDiscountEnabled;
    private Boolean mDirectDiscountEnabled;
    private String mMerchantBaseUrl;
    private String mMerchantDiscountUrl;
    private String mMerchantGetDiscountUri;
    private Map<String, String> mDiscountAdditionalInfo;
    private Boolean mInstallmentsReviewEnabled;


    public InstallmentsPresenter(Context context) {
        this.mContext = context;
    }

    public void setView(InstallmentsActivityView view) {
        this.mView = view;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
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

    public void setSite(Site site) {
        this.mSite = site;
    }

    public void setPayerCosts(List<PayerCost> payerCosts) {
        this.mPayerCosts = payerCosts;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.mPaymentPreference = paymentPreference;
    }

    private void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    public PaymentMethod getPaymentMethod() {
        return this.mPaymentMethod;
    }

    public Site getSite() {
        return mSite;
    }

    public String getPublicKey() {
        return mPublicKey;
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

    public List<PayerCost> getPayerCosts() {
        return mPayerCosts;
    }

    public Issuer getIssuer() {
        return mIssuer;
    }

    public void validateActivityParameters() throws IllegalStateException {
        if (mAmount == null || mSite == null) {
            mView.onInvalidStart("amount or site is null");
        } else if (mPayerCosts == null) {
            if (mIssuer == null) {
                mView.onInvalidStart("issuer is null");
            } else if (mPublicKey == null) {
                mView.onInvalidStart("public key not set");
            } else if (mPaymentMethod == null) {
                mView.onInvalidStart("payment method is null");
            } else {
                mView.onValidStart();
            }
        } else {
            mView.onValidStart();
        }
    }

    public boolean isCardInfoAvailable() {
        return mCardInfo != null && mPaymentMethod != null;
    }

    public void initializeMercadoPago() {
        mMercadoPago = new MercadoPagoServices.Builder()
                .setContext(mContext)
                .setPublicKey(mPublicKey)
                .setPrivateKey(mPrivateKey)
                .build();
    }

    private boolean werePayerCostsSet() {
        return mPayerCosts != null;
    }

    public void initialize() {
        if (mDiscountEnabled) {
            loadDiscount();
        } else {
            initializeDiscountRow();
            loadPayerCosts();
        }
    }

    private void loadDiscount() {
        if (mDirectDiscountEnabled && mDiscount == null) {
            if (isAmountValid()) {
                getDirectDiscount();
            } else {
                loadPayerCosts();
            }
        } else {
            initializeDiscountRow();
            loadPayerCosts();
        }
    }

    private Boolean isAmountValid() {
        return mAmount != null && mAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public void initializeDiscountActivity() {
        mView.startDiscountActivity(mAmount);
    }

    public void initializeDiscountRow() {
        mView.showDiscountRow(mAmount);
    }

    private void getDirectDiscount() {
        if (isMerchantServerDiscountsAvailable()) {
            getMerchantDirectDiscount();
        } else {
            getMPDirectDiscount();
        }
    }

    private void getMPDirectDiscount() {
        mMercadoPago.getDirectDiscount(mAmount.toString(), mPayerEmail, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                mDiscount = discount;
                getInstallmentsAsync();
                initializeDiscountRow();
            }

            @Override
            public void failure(ApiException apiException) {
                mDirectDiscountEnabled = false;
                initializeDiscountRow();
                loadPayerCosts();
            }
        });
    }

    private void getMerchantDirectDiscount() {
        String merchantDiscountUrl = getMerchantServerDiscountUrl();

        MerchantServer.getDirectDiscount(mAmount.toString(), mPayerEmail, mContext, merchantDiscountUrl, mMerchantGetDiscountUri, mDiscountAdditionalInfo, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                mDiscount = discount;
                getInstallmentsAsync();
                initializeDiscountRow();
            }

            @Override
            public void failure(ApiException apiException) {
                mDirectDiscountEnabled = false;
                initializeDiscountRow();
                loadPayerCosts();
            }
        });
    }

    public void onDiscountReceived(Discount discount) {
        setDiscount(discount);
        initializeDiscountRow();
        getInstallmentsAsync();
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

    public void setPrivateKey(String privateKey) {
        this.mPrivateKey = privateKey;
    }

    public void setDiscountEnabled(Boolean discountEnabled) {
        this.mDiscountEnabled = discountEnabled;
    }

    public void setMerchantDiscountBaseUrl(String merchantDiscountUrl) {
        this.mMerchantDiscountUrl = merchantDiscountUrl;
    }

    public String getMerchantDiscountBaseUrl() {
        return this.mMerchantDiscountUrl;
    }

    public void setMerchantGetDiscountUri(String merchantGetDiscountUri) {
        this.mMerchantGetDiscountUri = merchantGetDiscountUri;
    }

    public String getMerchantGetDiscountUri() {
        return mMerchantGetDiscountUri;
    }

    public Boolean getDiscountEnabled() {
        return this.mDiscountEnabled;
    }

    public void setMerchantBaseUrl(String merchantBaseUrl) {
        this.mMerchantBaseUrl = merchantBaseUrl;
    }

    public String getMerchantBaseUrl() {
        return this.mMerchantBaseUrl;
    }

    public void setDiscountAdditionalInfo(Map<String, String> discountAdditionalInfo) {
        this.mDiscountAdditionalInfo = discountAdditionalInfo;
    }

    public Map<String, String> getDiscountAdditionalInfo() {
        return this.mDiscountAdditionalInfo;
    }

    public void setInstallmentsReviewEnabled(Boolean installmentReviewEnabled) {
        this.mInstallmentsReviewEnabled = installmentReviewEnabled;
    }

    public Boolean getInstallmentReviewEnabled() {
        return this.mInstallmentsReviewEnabled;
    }

    public void setDirectDiscountEnabled(Boolean directDiscountEnabled) {
        this.mDirectDiscountEnabled = directDiscountEnabled;
    }

    public Boolean getDirectDiscountEnabled() {
        return mDirectDiscountEnabled;
    }

    private void loadPayerCosts() {
        if (werePayerCostsSet()) {
            resolvePayerCosts(mPayerCosts);
        } else {
            getInstallmentsAsync();
        }
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    private void getInstallmentsAsync() {
        if (mMercadoPago == null) return;
        mView.showLoadingView();
        mMercadoPago.getInstallments(mBin, getAmount(), mIssuerId, mPaymentMethod.getId(),
                new Callback<List<Installment>>() {
                    @Override
                    public void success(List<Installment> installments) {
                        mView.stopLoadingView();
                        if (installments.size() == 0) {
                            mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
                                    "no installments found for an issuer at InstallmentsActivity");
                        } else if (installments.size() == 1) {
                            resolvePayerCosts(installments.get(0).getPayerCosts());
                        } else {
                            mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
                                    "multiple installments found for an issuer at InstallmentsActivity");
                        }
                    }

                    @Override
                    public void failure(ApiException apiException) {
                        mView.stopLoadingView();
                        setFailureRecovery(new FailureRecovery() {
                            @Override
                            public void recover() {
                                getInstallmentsAsync();
                            }
                        });
                        mView.showApiExceptionError(apiException);
                    }
                });
    }

    private void resolvePayerCosts(List<PayerCost> payerCosts) {
        PayerCost defaultPayerCost = mPaymentPreference.getDefaultInstallments(payerCosts);
        mPayerCosts = mPaymentPreference.getInstallmentsBelowMax(payerCosts);

        if (defaultPayerCost != null) {
            mView.finishWithResult(defaultPayerCost);
        } else if (mPayerCosts.isEmpty()) {
            mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
                    "no payer costs found at InstallmentsActivity");
        } else if (mPayerCosts.size() == 1) {
            mView.finishWithResult(payerCosts.get(0));
        } else {
            mView.showHeader();
            mView.initializeInstallments(mPayerCosts);
        }
        mView.stopLoadingView();
    }

    public void onItemSelected(int position) {
        PayerCost selectedPayerCost = mPayerCosts.get(position);
        if (isInstallmentsReviewEnabled() && isInstallmentsReviewRequired(selectedPayerCost)) {
            mView.hideInstallmentsRecyclerView();
            mView.showInstallmentsReviewView();

            initializeDiscountRow();
            mView.initInstallmentsReviewView(selectedPayerCost);
        } else {
            mView.finishWithResult(selectedPayerCost);
        }
    }

    private Boolean isInstallmentsReviewEnabled() {
        return mInstallmentsReviewEnabled != null && mInstallmentsReviewEnabled;
    }

    private Boolean isInstallmentsReviewRequired(PayerCost payerCost) {
        return payerCost != null && payerCost.getCFTPercent() != null && payerCost.getTEAPercent() != null;
    }

    private boolean isMerchantServerDiscountsAvailable() {
        return !TextUtil.isEmpty(getMerchantServerDiscountUrl()) && !TextUtil.isEmpty(mMerchantGetDiscountUri);
    }

    private String getMerchantServerDiscountUrl() {
        String merchantBaseUrl;

        if (TextUtil.isEmpty(mMerchantDiscountUrl)) {
            merchantBaseUrl = this.mMerchantBaseUrl;
        } else {
            merchantBaseUrl = this.mMerchantDiscountUrl;
        }

        return merchantBaseUrl;
    }
}
