package com.mercadopago;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.PaymentCallback;
import com.mercadopago.callbacks.PaymentDataCallback;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.CustomServiceHandler;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.core.MercadoPagoServices;
import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.exceptions.ExceptionHandler;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentBody;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.PaymentResultAction;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.FlowPreference;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.TextUtil;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

/**
 * Created by vaserber on 2/1/17.
 */

public class CheckoutActivity extends MercadoPagoBaseActivity {
    private static final String CHECKOUT_PREFERENCE_BUNDLE = "mCheckoutPreference";
    private static final String PAYMENT_METHOD_SEARCH_BUNDLE = "mPaymentMethodSearch";
    private static final String SAVED_CARDS_BUNDLE = "mSavedCards";
    //Parameters
    protected CheckoutPreference mCheckoutPreference;
    protected String mMerchantPublicKey;
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;
    protected String mMerchantAccessToken;
    protected Integer mCongratsDisplay;

    //Local vars
    protected MercadoPagoServices mMercadoPagoServices;
    protected PaymentMethodSearch mPaymentMethodSearch;

    protected Activity mActivity;

    protected String mTransactionId;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected Token mCreatedToken;
    protected Payment mCreatedPayment;
    protected Site mSite;

    protected boolean mPaymentMethodEditionRequested;

    protected PaymentRecovery mPaymentRecovery;
    protected Discount mDiscount;
    protected String mCustomerId;
    protected Boolean mBinaryModeEnabled;
    protected Boolean mDiscountEnabled;
    protected List<Card> mSavedCards;
    protected DecorationPreference mDecorationPreference;
    protected ServicePreference mServicePreference;
    protected FlowPreference mFlowPreference;

    protected PaymentDataCallback mPaymentDataCallback;
    protected PaymentData mPaymentDataInput;
    protected FailureRecovery mFailureRecovery;
    protected ReviewScreenPreference mReviewScreenPreference;
    protected Integer mMaxSavedCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityParameters();
        if (mDecorationPreference != null) {
            mDecorationPreference.activateFont(this);
            if (mDecorationPreference.hasColors()) {
                setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
            }
        }
        setContentView(R.layout.mpsdk_activity_checkout);
        mActivity = this;
        try {
            validateActivityParameters();
            onValidStart();
        } catch (IllegalStateException exception) {
            onInvalidStart(exception.getMessage());
        }
    }

    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPaymentDataInput = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentData"), PaymentData.class);
        mCongratsDisplay = this.getIntent().getIntExtra("congratsDisplay", -1);
        mBinaryModeEnabled = this.getIntent().getBooleanExtra("binaryMode", false);
        mMaxSavedCards = this.getIntent().getIntExtra("maxSavedCards", 0);
        mDiscount = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("discount"), Discount.class);
        mDiscountEnabled = this.getIntent().getBooleanExtra("discountEnabled", true);

        mServicePreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("servicePreference"), ServicePreference.class);
        mCheckoutPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("checkoutPreference"), CheckoutPreference.class);
        mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        mReviewScreenPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("reviewScreenPreference"), ReviewScreenPreference.class);
        mFlowPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("flowPreference"), FlowPreference.class);
    }

    protected void onValidStart() {

        if (CallbackHolder.getInstance().hasPaymentDataCallback()) {
            mPaymentDataCallback = CallbackHolder.getInstance().getPaymentDataCallback();
        } else {
            mPaymentDataCallback = new PaymentDataCallback() {
                @Override
                public void onSuccess(PaymentData paymentData) {
                    createPayment();
                }

                @Override
                public void onCancel() {
                    CallbackHolder.getInstance().getPaymentCallback().onCancel();
                }

                @Override
                public void onFailure(MercadoPagoError error) {
                    CallbackHolder.getInstance().getPaymentCallback().onFailure(error);
                }
            };
        }

        mMercadoPagoServices = new MercadoPagoServices.Builder()
                .setContext(this)
                .setPublicKey(mMerchantPublicKey)
                .build();

        showProgressBar();

        if (mCheckoutPreference.getId() != null) {
            getCheckoutPreference();
        } else {
            try {
                validatePreference();
                initializeCheckout();

            } catch (CheckoutPreferenceException e) {
                String errorMessage = ExceptionHandler.getErrorMessage(mActivity, e);
                ErrorUtil.startErrorActivity(mActivity, errorMessage, false);
            }
        }
    }

    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), message, false);
    }

    private void getCheckoutPreference() {
        showProgressBar();

        mMercadoPagoServices.getPreference(mCheckoutPreference.getId(), new Callback<CheckoutPreference>() {
            @Override
            public void success(CheckoutPreference checkoutPreference) {
                mCheckoutPreference = checkoutPreference;
                try {
                    validatePreference();
                    initializeCheckout();

                } catch (CheckoutPreferenceException e) {
                    String errorMessage = ExceptionHandler.getErrorMessage(mActivity, e);
                    ErrorUtil.startErrorActivity(mActivity, errorMessage, false);
                }
            }

            @Override
            public void failure(ApiException apiException) {
                ApiUtil.showApiExceptionError(mActivity, apiException);
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getCheckoutPreference();
                    }
                });
            }
        });
    }

    private void validatePreference() throws CheckoutPreferenceException {
        mCheckoutPreference.validate();
    }

    private void initializeCheckout() {
        mSite = mCheckoutPreference.getSite();
        getPaymentMethodSearch();
    }

    protected void validateActivityParameters() throws IllegalStateException {
        if (isEmpty(mMerchantPublicKey)) {
            throw new IllegalStateException("public key not set");
        } else if (mCheckoutPreference == null) {
            throw new IllegalStateException("preference not set");
        }
        //TODO validate paymentDataInput integrity
    }

    protected void getPaymentMethodSearch() {
        showProgressBar();
        mMercadoPagoServices.getPaymentMethodSearch(mCheckoutPreference.getAmount(), mCheckoutPreference.getExcludedPaymentTypes(), mCheckoutPreference.getExcludedPaymentMethods(), mCheckoutPreference.getPayer(), new Callback<PaymentMethodSearch>() {
            @Override
            public void success(PaymentMethodSearch paymentMethodSearch) {
                mPaymentMethodSearch = paymentMethodSearch;
                if (!mPaymentMethodSearch.hasSavedCards() && isMerchantServerInfoAvailable()) {
                    getCustomerAsync();
                } else {
                    startFlow();
                }
            }

            @Override
            public void failure(ApiException apiException) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getPaymentMethodSearch();
                    }
                });
                ApiUtil.showApiExceptionError(mActivity, apiException);

            }
        });
    }

    private boolean isMerchantServerInfoAvailable() {
        return mServicePreference != null && mServicePreference.hasGetCustomerURL();
    }

    private void getCustomerAsync() {
        showProgressBar();
        CustomServiceHandler.getCustomer(this, mServicePreference.getGetCustomerURL(), mServicePreference.getGetCustomerURI(), mServicePreference.getGetCustomerAdditionalInfo(), new Callback<Customer>() {
            @Override
            public void success(Customer customer) {
                if (customer != null) {
                    mCustomerId = customer.getId();
                    mSavedCards = mCheckoutPreference.getPaymentPreference() == null ? customer.getCards() : mCheckoutPreference.getPaymentPreference().getValidCards(customer.getCards());
                }
                getDiscountAsync();
            }

            @Override
            public void failure(ApiException apiException) {
                getDiscountAsync();
            }
        });
    }

    private void getDiscountAsync() {

        mMercadoPagoServices.getDirectDiscount(mCheckoutPreference.getAmount().toString(), mCheckoutPreference.getPayer().getEmail(), new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                mDiscount = discount;
                startFlow();
            }

            @Override
            public void failure(ApiException apiException) {
                startFlow();
            }
        });
    }

    private void showProgressBar() {
        LayoutUtil.showProgressLayout(this);
    }

    private void startFlow() {
        if (mPaymentDataInput == null) {
            startPaymentVaultActivity();
            overridePendingTransition(R.anim.mpsdk_fade_in_seamless, R.anim.mpsdk_fade_out_seamless);
        } else {
            mSelectedIssuer = mPaymentDataInput.getIssuer();
            mSelectedPayerCost = mPaymentDataInput.getPayerCost();
            mCreatedToken = mPaymentDataInput.getToken();
            mSelectedPaymentMethod = mPaymentDataInput.getPaymentMethod();
            showReviewAndConfirm();
        }
    }

    protected void startPaymentVaultActivity() {

        new MercadoPagoComponents.Activities.PaymentVaultActivityBuilder()
                .setActivity(this)
                .setMerchantPublicKey(mMerchantPublicKey)
                .setPayerEmail(mCheckoutPreference.getPayer().getEmail())
                .setSite(mSite)
                .setAmount(mCheckoutPreference.getAmount())
                .setPaymentMethodSearch(mPaymentMethodSearch)
                .setDiscount(mDiscount)
                .setDiscountEnabled(mDiscountEnabled)
                .setPaymentPreference(mCheckoutPreference.getPaymentPreference())
                .setDecorationPreference(mDecorationPreference)
                .setCards(mSavedCards)
                .setMaxSavedCards(mMaxSavedCards)
                .setPayerAccessToken(mCheckoutPreference.getPayer().getAccessToken())
                .startActivity();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CHECKOUT_PREFERENCE_BUNDLE, JsonUtil.getInstance().toJson(mCheckoutPreference));
        outState.putString(PAYMENT_METHOD_SEARCH_BUNDLE, JsonUtil.getInstance().toJson(mPaymentMethodSearch));
        outState.putString(SAVED_CARDS_BUNDLE, JsonUtil.getInstance().toJson(mSavedCards));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCheckoutPreference = JsonUtil.getInstance().fromJson(savedInstanceState.getString(CHECKOUT_PREFERENCE_BUNDLE), CheckoutPreference.class);
            mPaymentMethodSearch = JsonUtil.getInstance().fromJson(savedInstanceState.getString(PAYMENT_METHOD_SEARCH_BUNDLE), PaymentMethodSearch.class);
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Card>>() {
                }.getType();
                mSavedCards = gson.fromJson(savedInstanceState.getString(SAVED_CARDS_BUNDLE), listType);
            } catch (Exception ex) {
                mSavedCards = null;
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPagoComponents.Activities.PAYMENT_VAULT_REQUEST_CODE) {
            resolvePaymentVaultRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.PAYMENT_RESULT_REQUEST_CODE) {
            resolvePaymentResultRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.CARD_VAULT_REQUEST_CODE) {
            resolveCardVaultRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.REVIEW_AND_CONFIRM_REQUEST_CODE) {
            resolveReviewAndConfirmRequest(resultCode);
        } else {
            resolveErrorRequest(resultCode, data);
        }
    }

    private void resolveReviewAndConfirmRequest(int resultCode) {
        if (resultCode == RESULT_OK) {
            resolvePaymentDataCallback();
        } else if (resultCode == ReviewAndConfirmActivity.RESULT_CHANGE_PAYMENT_METHOD) {
            changePaymentMethod();
        } else if (resultCode == ReviewAndConfirmActivity.RESULT_CANCEL_PAYMENT) {
            mPaymentDataCallback.onCancel();
            this.finish();
        } else {
            animateBackToPaymentMethodSelection();
            startPaymentVaultActivity();
        }
    }

    private void resolvePaymentDataCallback() {
        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(mSelectedPaymentMethod);
        paymentData.setIssuer(mSelectedIssuer);
        paymentData.setToken(mCreatedToken);
        paymentData.setPayerCost(mSelectedPayerCost);
        paymentData.setDiscount(mDiscount);

        boolean hasToFinishActivity = false;
        if (CallbackHolder.getInstance().hasPaymentDataCallback()) {
            hasToFinishActivity = true;
        }
        mPaymentDataCallback.onSuccess(paymentData);
        if (hasToFinishActivity) {
            this.finish();
        }
    }

    protected void resolveCardVaultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mSelectedIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            mSelectedPayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            mCreatedToken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            mSelectedPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

            if (mPaymentRecovery != null && mPaymentRecovery.isTokenRecoverable()) {
                createPayment();
            } else {
                checkFlowWithPaymentMethodSelected();
            }
        } else {
            if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                MPTracker.getInstance().trackEvent("CARD_VAULT", "CANCELED", "3", mMerchantPublicKey, mCheckoutPreference.getSite().getId(), BuildConfig.VERSION_NAME, this);
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                this.finish();
            } else {
                startPaymentVaultActivity();
            }
        }
    }

    private void resolvePaymentVaultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mDiscount = JsonUtil.getInstance().fromJson(data.getStringExtra("discount"), Discount.class);
            mSelectedIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            mSelectedPayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            mCreatedToken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            mSelectedPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            checkFlowWithPaymentMethodSelected();
        } else {
            if (!mPaymentMethodEditionRequested) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                this.finish();
            } else {
                showReviewAndConfirm();
                animateBackFromPaymentEdition();
            }
        }
    }

    private void checkFlowWithPaymentMethodSelected() {
        if (isReviewAndConfirmEnabled()) {
            showReviewAndConfirm();
        } else {
            resolvePaymentDataCallback();
        }
    }

    private void showReviewAndConfirm() {
        MPTracker.getInstance().trackScreen("REVIEW_AND_CONFIRM", "3", mMerchantPublicKey, mCheckoutPreference.getSite().getId(), BuildConfig.VERSION_NAME, this);

        mPaymentMethodEditionRequested = false;

        MercadoPagoComponents.Activities.ReviewAndConfirmBuilder builder = new MercadoPagoComponents.Activities.ReviewAndConfirmBuilder()
                .setActivity(this)
                .setReviewScreenPreference(mReviewScreenPreference)
                .setPaymentMethod(mSelectedPaymentMethod)
                .setPayerCost(mSelectedPayerCost)
                .setAmount(mCheckoutPreference.getAmount())
                .setSite(mSite)
                .setDecorationPreference(mDecorationPreference)
                .setEditionEnabled(!isUniquePaymentMethod())
                .setDiscount(mDiscount)
                .setItems(mCheckoutPreference.getItems())
                .setTermsAndConditionsEnabled(!isUserLogged());

        if (MercadoPagoUtil.isCard(mSelectedPaymentMethod.getPaymentTypeId())) {
            builder.setToken(mCreatedToken);
        } else if (!PaymentTypes.ACCOUNT_MONEY.equals(mSelectedPaymentMethod.getPaymentTypeId())) {
            String searchItemComment = mPaymentMethodSearch.getSearchItemByPaymentMethod(mSelectedPaymentMethod).getComment();
            builder.setExtraPaymentMethodInfo(searchItemComment);
        }
        builder.startActivity();
    }

    private boolean isUserLogged() {
        return mCheckoutPreference.getPayer() != null && !TextUtil.isEmpty(mCheckoutPreference.getPayer().getAccessToken());
    }

    private void resolvePaymentResultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED && data != null) {
            String nextAction = data.getStringExtra("nextAction");
            if (!isEmpty(nextAction)) {
                if (nextAction.equals(PaymentResultAction.SELECT_OTHER_PAYMENT_METHOD)) {
                    startPaymentVaultActivity();
                }
                if (nextAction.equals(PaymentResultAction.RECOVER_PAYMENT)) {
                    createPaymentRecovery();
                    startCardVaultActivity();
                }
            }
        } else {
            finishWithPaymentResult();
        }
    }

    private void createPaymentRecovery() {
        try {
            mPaymentRecovery = new PaymentRecovery(mCreatedToken, mCreatedPayment, mSelectedPaymentMethod, mSelectedPayerCost, mSelectedIssuer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private void startCardVaultActivity() {
        PaymentPreference paymentPreference = mCheckoutPreference.getPaymentPreference();

        if (paymentPreference == null) {
            paymentPreference = new PaymentPreference();
        }

        paymentPreference.setDefaultPaymentTypeId(mSelectedPaymentMethod.getPaymentTypeId());

        new MercadoPagoComponents.Activities.CardVaultActivityBuilder()
                .setActivity(this)
                .setMerchantPublicKey(mMerchantPublicKey)
                .setPaymentPreference(paymentPreference)
                .setDecorationPreference(mDecorationPreference)
                .setAmount(mCheckoutPreference.getAmount())
                .setSite(mSite)
                .setInstallmentsEnabled(true)
                .setDiscount(mDiscount)
                .setDiscountEnabled(mDiscountEnabled)
                .setAcceptedPaymentMethods(mPaymentMethodSearch.getPaymentMethods())
                .setPaymentRecovery(mPaymentRecovery)
                .startActivity();

        animatePaymentMethodSelection();
    }

    private void animatePaymentMethodSelection() {
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    private void resolveErrorRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            recoverFromFailure();
        } else if (noUserInteractionReached() || !isReviewAndConfirmEnabled()) {
            setResult(RESULT_CANCELED, data);
            this.finish();
        } else {
            showReviewAndConfirm();
        }
    }

    private boolean isReviewAndConfirmEnabled() {
        return mFlowPreference == null || mFlowPreference.isReviewAndConfirmScreenEnabled();
    }

    private boolean noUserInteractionReached() {
        return mSelectedPaymentMethod == null;
    }

    private void finishWithPaymentResult() {
        PaymentCallback paymentCallback = CallbackHolder.getInstance().getPaymentCallback();
        paymentCallback.onSuccess(mCreatedPayment);
        this.finish();
    }

    private void changePaymentMethod() {
        if (!isUniquePaymentMethod()) {
            mPaymentMethodEditionRequested = true;
            startPaymentVaultActivity();
            overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
        }
    }

    private void animateBackFromPaymentEdition() {
        overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
    }

    private void animateBackToPaymentMethodSelection() {
        overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
    }

    private boolean isUniquePaymentMethod() {
        return mPaymentMethodSearch != null && mPaymentMethodSearch.getGroups().size() == 1
                && mPaymentMethodSearch.getGroups().get(0).isPaymentMethod()
                && (mSavedCards == null || mSavedCards.isEmpty());
    }

    protected void createPayment() {

        if (mServicePreference != null && mServicePreference.hasCreatePaymentURL()) {

            Map<String, Object> paymentInfoMap = new HashMap<>();
            paymentInfoMap.putAll(mServicePreference.getCreatePaymentAdditionalInfo());

            PaymentData paymentData = new PaymentData();
            paymentData.setPaymentMethod(mSelectedPaymentMethod);
            paymentData.setPayerCost(mSelectedPayerCost);
            paymentData.setIssuer(mSelectedIssuer);
            paymentData.setDiscount(mDiscount);
            paymentData.setToken(mCreatedToken);
            paymentData.setPaymentMethod(mSelectedPaymentMethod);

            String paymentDataJson = JsonUtil.getInstance().toJson(paymentData);

            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, String> paymentDataMap = new Gson().fromJson(paymentDataJson, type);

            paymentInfoMap.putAll(paymentDataMap);

            CustomServiceHandler.createPayment(this, getTransactionID(), mServicePreference.getCreatePaymentURL(), mServicePreference.getCreatePaymentURI(), paymentInfoMap, new Callback<Payment>() {
                @Override
                public void success(Payment payment) {
                    mCreatedPayment = payment;
                    checkStartPaymentResultActivity(payment);
                    cleanTransactionId();
                }

                @Override
                public void failure(ApiException apiException) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            createPayment();
                        }
                    });
                    resolvePaymentFailure(apiException);
                }
            });
        } else {
            PaymentBody paymentBody = createPaymentBody();
            mMercadoPagoServices.createPayment(paymentBody, new Callback<Payment>() {
                @Override
                public void success(Payment payment) {
                    mCreatedPayment = payment;
                    checkStartPaymentResultActivity(payment);
                    cleanTransactionId();
                }

                @Override
                public void failure(ApiException apiException) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            createPayment();
                        }
                    });
                    resolvePaymentFailure(apiException);
                }
            });
        }
    }

    private PaymentBody createPaymentBody() {
        PaymentBody paymentIntent = new PaymentBody();
        paymentIntent.setPrefId(mCheckoutPreference.getId());
        paymentIntent.setPublicKey(mMerchantPublicKey);
        paymentIntent.setPaymentMethodId(mSelectedPaymentMethod.getId());
        paymentIntent.setBinaryMode(mBinaryModeEnabled);
        Payer payer = mCheckoutPreference.getPayer();
        if (!TextUtils.isEmpty(mCustomerId) && MercadoPagoUtil.isCard(mSelectedPaymentMethod.getPaymentTypeId())) {
            payer.setId(mCustomerId);
        }

        paymentIntent.setPayer(payer);

        if (mCreatedToken != null) {
            paymentIntent.setTokenId(mCreatedToken.getId());
        }
        if (mSelectedPayerCost != null) {
            paymentIntent.setInstallments(mSelectedPayerCost.getInstallments());
        }
        if (mSelectedIssuer != null) {
            paymentIntent.setIssuerId(mSelectedIssuer.getId());
        }

        mTransactionId = getTransactionID();

        if (mDiscount != null) {
            paymentIntent.setCampaignId(mDiscount.getId().intValue());
            paymentIntent.setCouponAmount(mDiscount.getCouponAmount().floatValue());

            if (!isEmpty(mDiscount.getCouponCode())) {
                paymentIntent.setCouponCode(mDiscount.getCouponCode());
            }
        }

        paymentIntent.setTransactionId(mTransactionId);
        return paymentIntent;
    }

    private String getTransactionID() {
        String transactionId;
        if (!existsTransactionId() || !MercadoPagoUtil.isCard(mSelectedPaymentMethod.getPaymentTypeId())) {
            transactionId = createNewTransactionId();
        } else {
            transactionId = mTransactionId;
        }
        return transactionId;
    }

    private void checkStartPaymentResultActivity(Payment payment) {
        if (hasToSkipPaymentResultActivity(payment)) {
            finishWithPaymentResult();
        } else {
            startPaymentResultActivity();
        }
    }

    private boolean hasToSkipPaymentResultActivity(Payment payment) {
        return mCongratsDisplay == 0 && (payment != null) && (!isEmpty(payment.getStatus())) &&
                (payment.getStatus().equals(Payment.StatusCodes.STATUS_APPROVED));
    }

    private void startPaymentResultActivity() {
        new MercadoPagoComponents.Activities.PaymentResultActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(mActivity)
                .setPayment(mCreatedPayment)
                .setDiscount(mDiscount)
                .setPaymentMethod(mSelectedPaymentMethod)
                .setCongratsDisplay(mCongratsDisplay)
                .startActivity();
    }

    private String createNewTransactionId() {
        return String.valueOf(mMerchantPublicKey + Calendar.getInstance().getTimeInMillis()) + String.valueOf(Math.round(Math.random()));
    }

    private boolean existsTransactionId() {
        return mTransactionId != null;
    }

    private void cleanTransactionId() {
        mTransactionId = null;
    }

    private void resolvePaymentFailure(ApiException apiException) {
        if (apiException.getStatus() != null) {
            String serverErrorFirstDigit = String.valueOf(ApiUtil.StatusCodes.INTERNAL_SERVER_ERROR).substring(0, 1);

            if (apiException.getStatus() == ApiUtil.StatusCodes.PROCESSING) {
                startPaymentInProcessActivity();
                cleanTransactionId();
            } else if (String.valueOf(apiException.getStatus()).startsWith(serverErrorFirstDigit)) {

                ApiUtil.showApiExceptionError(this, apiException);
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        createPayment();
                    }
                });
            } else if (apiException.getStatus() == ApiUtil.StatusCodes.BAD_REQUEST) {

                MercadoPagoError error = new MercadoPagoError(apiException);
                ErrorUtil.startErrorActivity(this, error);
            } else {
                ApiUtil.showApiExceptionError(this, apiException);
            }
        } else {
            ApiUtil.showApiExceptionError(this, apiException);
        }
    }

    private void startPaymentInProcessActivity() {
        mCreatedPayment = new Payment();
        mCreatedPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        mCreatedPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_PENDING_CONTINGENCY);
        startPaymentResultActivity();
    }

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    protected void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }
}
