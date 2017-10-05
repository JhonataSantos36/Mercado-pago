package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Card;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.Token;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.FlowPreference;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.presenters.CheckoutPresenter;
import com.mercadopago.providers.CheckoutProvider;
import com.mercadopago.providers.CheckoutProviderImpl;
import com.mercadopago.px_tracking.utils.TrackingUtil;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.px_tracking.MPTracker;
import com.mercadopago.px_tracking.model.ScreenViewEvent;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.TextUtil;
import com.mercadopago.views.CheckoutView;

import java.math.BigDecimal;

public class CheckoutActivity extends MercadoPagoBaseActivity implements CheckoutView {

    private static final String MERCHANT_PUBLIC_KEY_BUNDLE = "mMerchantPublicKey";
    private static final String DECORATION_PREFERENCE_BUNDLE = "mDecorationPreference";
    private static final String SERVICE_PREFERENCE_BUNDLE = "mServicePreference";
    private static final String RESULT_CODE_BUNDLE = "mRequestedResultCode";
    private static final String PRESENTER_BUNDLE = "mCheckoutPresenter";

    //Parameters
    protected String mMerchantPublicKey;
    protected String mPrivateKey;

    //Local vars
    protected CheckoutPresenter mCheckoutPresenter;

    protected DecorationPreference mDecorationPreference;
    protected Integer mRequestedResultCode;
    protected Intent mCustomDataBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mCheckoutPresenter = new CheckoutPresenter();
            getActivityParameters();
            configurePresenter();
            setContentView(R.layout.mpsdk_activity_checkout);
            decorate();
            mCheckoutPresenter.initialize();
        }
    }

    private void configurePresenter() {
        CheckoutProvider provider = new CheckoutProviderImpl(this, mMerchantPublicKey, mPrivateKey, mCheckoutPresenter.getServicePreference(), mCheckoutPresenter.isESCEnabled());
        mCheckoutPresenter.attachResourcesProvider(provider);
        mCheckoutPresenter.attachView(this);
        mCheckoutPresenter.setIdempotencyKeySeed(mMerchantPublicKey);
        mCheckoutPresenter.setTimer(CheckoutTimer.getInstance());
    }

    private void decorate() {
        if (mDecorationPreference != null) {
            mDecorationPreference.activateFont(this);
            if (mDecorationPreference.hasColors()) {
                setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
            }
        }
    }

    protected void getActivityParameters() {

        CheckoutPreference checkoutPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("checkoutPreference"), CheckoutPreference.class);
        PaymentResultScreenPreference paymentResultScreenPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentResultScreenPreference"), PaymentResultScreenPreference.class);
        ReviewScreenPreference reviewScreenPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("reviewScreenPreference"), ReviewScreenPreference.class);
        FlowPreference flowPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("flowPreference"), FlowPreference.class);

        Boolean binaryMode = this.getIntent().getBooleanExtra("binaryMode", false);

        Discount discount = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("discount"), Discount.class);
        Boolean directDiscountEnabled = this.getIntent().getBooleanExtra("directDiscountEnabled", true);

        PaymentData paymentDataInput = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentData"), PaymentData.class);
        PaymentResult paymentResultInput = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentResult"), PaymentResult.class);

        mRequestedResultCode = this.getIntent().getIntExtra("resultCode", 0);

        ServicePreference servicePreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("servicePreference"), ServicePreference.class);

        mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mPrivateKey = checkoutPreference.getPayer() != null ? checkoutPreference.getPayer().getAccessToken() : "";

        mCheckoutPresenter.setCheckoutPreference(checkoutPreference);
        mCheckoutPresenter.setPaymentResultScreenPreference(paymentResultScreenPreference);
        mCheckoutPresenter.setReviewScreenPreference(reviewScreenPreference);
        mCheckoutPresenter.setFlowPreference(flowPreference);
        mCheckoutPresenter.setBinaryMode(binaryMode);
        mCheckoutPresenter.setDiscount(discount);
        mCheckoutPresenter.setDirectDiscountEnabled(directDiscountEnabled);
        mCheckoutPresenter.setPaymentDataInput(paymentDataInput);
        mCheckoutPresenter.setPaymentResultInput(paymentResultInput);
        mCheckoutPresenter.setRequestedResult(mRequestedResultCode);
        mCheckoutPresenter.setServicePreference(servicePreference);
    }

    @Override
    public void initializeMPTracker() {
        //Initialize tracker before creating a token
        MPTracker.getInstance().initTracker(mMerchantPublicKey, mCheckoutPresenter.getCheckoutPreference().getSite().getId(), BuildConfig.VERSION_NAME, getApplicationContext());
    }

    @Override
    public void trackScreen() {
        MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(this, mMerchantPublicKey)
                .setCheckoutVersion(BuildConfig.VERSION_NAME)
                .setTrackingStrategy(TrackingUtil.BATCH_STRATEGY)
                .build();
        ScreenViewEvent event = new ScreenViewEvent.Builder()
                .setScreenId(TrackingUtil.SCREEN_ID_CHECKOUT)
                .setScreenName(TrackingUtil.SCREEN_NAME_CHECKOUT)
                .build();
        mpTrackingContext.clearExpiredTracks();
        mpTrackingContext.trackEvent(event);
    }

    @Override
    public void finishFromReviewAndConfirm() {
        setResult(MercadoPagoCheckout.PAYMENT_METHOD_CHANGED_REQUESTED);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PRESENTER_BUNDLE, JsonUtil.getInstance().toJson(mCheckoutPresenter));

        outState.putString(MERCHANT_PUBLIC_KEY_BUNDLE, mMerchantPublicKey);
        outState.putString(DECORATION_PREFERENCE_BUNDLE, JsonUtil.getInstance().toJson(mDecorationPreference));
        outState.putInt(RESULT_CODE_BUNDLE, mRequestedResultCode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mMerchantPublicKey = savedInstanceState.getString(MERCHANT_PUBLIC_KEY_BUNDLE);
            mRequestedResultCode = savedInstanceState.getInt(RESULT_CODE_BUNDLE, 0);
            mDecorationPreference = JsonUtil.getInstance().fromJson(savedInstanceState.getString(DECORATION_PREFERENCE_BUNDLE), DecorationPreference.class);
            mCheckoutPresenter = JsonUtil.getInstance().fromJson(savedInstanceState.getString(PRESENTER_BUNDLE), CheckoutPresenter.class);
            configurePresenter();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            resolveErrorRequest(resultCode, data);
        } else if (resultCode == MercadoPagoCheckout.TIMER_FINISHED_RESULT_CODE) {
            resolveTimerObserverResult(resultCode);
        } else if (requestCode == MercadoPagoComponents.Activities.PAYMENT_VAULT_REQUEST_CODE) {
            resolvePaymentVaultRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.PAYMENT_RESULT_REQUEST_CODE) {
            resolvePaymentResultRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.CARD_VAULT_REQUEST_CODE) {
            resolveCardVaultRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.REVIEW_AND_CONFIRM_REQUEST_CODE) {
            resolveReviewAndConfirmRequest(resultCode, data);
        }
    }

    private void resolveTimerObserverResult(int resultCode) {
        setResult(resultCode);
        finish();
    }

    private void resolveReviewAndConfirmRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mCheckoutPresenter.onPaymentConfirmation();
        } else if (resultCode == ReviewAndConfirmActivity.RESULT_CHANGE_PAYMENT_METHOD) {
            mCheckoutPresenter.changePaymentMethod();
        } else if (resultCode == ReviewAndConfirmActivity.RESULT_CANCEL_PAYMENT) {
            if (data != null && data.getIntExtra("resultCode", 0) != 0) {
                Integer customResultCode = data.getIntExtra("resultCode", 0);
                PaymentData paymentData = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentData"), PaymentData.class);
                mCustomDataBundle = data;
                mCheckoutPresenter.onCustomReviewAndConfirmResponse(customResultCode, paymentData);
            } else {
                MercadoPagoError mercadoPagoError = null;
                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    mercadoPagoError = JsonUtil.getInstance().fromJson(data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
                }
                if (mercadoPagoError == null) {
                    mCheckoutPresenter.onReviewAndConfirmCancelPayment();
                } else {
                    mCheckoutPresenter.onReviewAndConfirmError(mercadoPagoError);
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            mCheckoutPresenter.onReviewAndConfirmCancel();
        }
    }

    protected void resolveCardVaultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Discount discount = JsonUtil.getInstance().fromJson(data.getStringExtra("discount"), Discount.class);
            Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            PayerCost payerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

            mCheckoutPresenter.onCardFlowResponse(paymentMethod, issuer, payerCost, token, discount);
        } else {
            MercadoPagoError mercadoPagoError = (data == null || data.getStringExtra("mercadoPagoError") == null) ? null :
                    JsonUtil.getInstance().fromJson(data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
            if (mercadoPagoError == null) {
                mCheckoutPresenter.onCardFlowCancel();
            } else {
                mCheckoutPresenter.onCardFlowError(mercadoPagoError);
            }
        }
    }

    private void resolvePaymentVaultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Discount discount = JsonUtil.getInstance().fromJson(data.getStringExtra("discount"), Discount.class);
            Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            PayerCost payerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            Card card = JsonUtil.getInstance().fromJson(data.getStringExtra("card"), Card.class);
            Payer payer = JsonUtil.getInstance().fromJson(data.getStringExtra("payer"), Payer.class);
            PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethodSearch"), PaymentMethodSearch.class);
            if (paymentMethodSearch != null) {
                mCheckoutPresenter.setPaymentMethodSearch(paymentMethodSearch);
            }

            mCheckoutPresenter.onPaymentMethodSelectionResponse(paymentMethod, issuer, payerCost, token, discount, card, payer);
        } else if (isErrorResult(data)) {
            MercadoPagoError mercadoPagoError = JsonUtil.getInstance().fromJson(data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
            mCheckoutPresenter.onPaymentMethodSelectionError(mercadoPagoError);
        } else {
            mCheckoutPresenter.onPaymentMethodSelectionCancel();
        }
    }

    private boolean isErrorResult(Intent data) {
        return data != null && !TextUtil.isEmpty(data.getStringExtra("mercadoPagoError"));
    }

    @Override
    public void showReviewAndConfirm() {

        MercadoPagoComponents.Activities.ReviewAndConfirmBuilder builder = new MercadoPagoComponents.Activities.ReviewAndConfirmBuilder()
                .setActivity(this)
                .setMerchantPublicKey(mMerchantPublicKey)
                .setSite(mCheckoutPresenter.getCheckoutPreference().getSite())
                .setReviewScreenPreference(mCheckoutPresenter.getReviewScreenPreference())
                .setPaymentMethod(mCheckoutPresenter.getSelectedPaymentMethod())
                .setIssuer(mCheckoutPresenter.getIssuer())
                .setPayerCost(mCheckoutPresenter.getSelectedPayerCost())
                .setAmount(mCheckoutPresenter.getCheckoutPreference().getAmount())
                .setSite(mCheckoutPresenter.getCheckoutPreference().getSite())
                .setEditionEnabled(!mCheckoutPresenter.isUniquePaymentMethod())
                .setDiscountEnabled(mCheckoutPresenter.isDiscountEnabled())
                .setItems(mCheckoutPresenter.getCheckoutPreference().getItems())
                .setDecorationPreference(mDecorationPreference)
                .setTermsAndConditionsEnabled(!isUserLogged());

        if (mCheckoutPresenter.isDiscountEnabled() && mCheckoutPresenter.isDiscountValid()) {
            builder.setDiscount(mCheckoutPresenter.getDiscount());
        }

        if (MercadoPagoUtil.isCard(mCheckoutPresenter.getSelectedPaymentMethod().getPaymentTypeId())) {
            builder.setToken(mCheckoutPresenter.getCreatedToken());
        } else if (!PaymentTypes.ACCOUNT_MONEY.equals(mCheckoutPresenter.getSelectedPaymentMethod().getPaymentTypeId())) {
            PaymentMethodSearchItem paymentMethodSearchItem = mCheckoutPresenter.getPaymentMethodSearch().getSearchItemByPaymentMethod(mCheckoutPresenter.getSelectedPaymentMethod());
            String searchItemComment = paymentMethodSearchItem.getComment();
            String searchItemDescription = paymentMethodSearchItem.getDescription();
            builder.setPaymentMethodCommentInfo(searchItemComment);
            builder.setPaymentMethodDescriptionInfo(searchItemDescription);
        }
        builder.startActivity();
    }

    @Override
    public void backToReviewAndConfirm() {
        showReviewAndConfirm();
        animateBackFromPaymentEdition();
    }

    @Override
    public void backToPaymentMethodSelection() {
        animateBackToPaymentMethodSelection();
        showPaymentMethodSelection();
    }

    @Override
    public void showPaymentMethodSelection() {
        new MercadoPagoComponents.Activities.PaymentVaultActivityBuilder()
                .setActivity(this)
                .setMerchantPublicKey(mMerchantPublicKey)
                .setDecorationPreference(mDecorationPreference)
                .setPayerAccessToken(mPrivateKey)
                .setPayerEmail(mCheckoutPresenter.getCheckoutPreference().getPayer().getEmail())
                .setSite(mCheckoutPresenter.getCheckoutPreference().getSite())
                .setAmount(mCheckoutPresenter.getCheckoutPreference().getAmount())
                .setPaymentMethodSearch(mCheckoutPresenter.getPaymentMethodSearch())
                .setDiscount(mCheckoutPresenter.getDiscount())
                .setInstallmentsEnabled(true)
                .setDiscountEnabled(mCheckoutPresenter.isDiscountEnabled())
                .setDirectDiscountEnabled(mCheckoutPresenter.isDirectDiscountEnabled())
                .setInstallmentsReviewEnabled(mCheckoutPresenter.isInstallmentsReviewScreenEnabled())
                .setPaymentPreference(mCheckoutPresenter.getCheckoutPreference().getPaymentPreference())
                .setShowBankDeals(mCheckoutPresenter.getShowBankDeals())
                .setMaxSavedCards(mCheckoutPresenter.getMaxSavedCardsToShow())
                .setShowAllSavedCardsEnabled(mCheckoutPresenter.shouldShowAllSavedCards())
                .setESCEnabled(mCheckoutPresenter.isESCEnabled())
                .startActivity();
    }

    @Override
    public void showPaymentResult(PaymentResult paymentResult) {
        BigDecimal amount = mCheckoutPresenter.getCreatedPayment() == null ? mCheckoutPresenter.getCheckoutPreference().getAmount() : mCheckoutPresenter.getCreatedPayment().getTransactionAmount();

        new MercadoPagoComponents.Activities.PaymentResultActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setPayerAccessToken(mPrivateKey)
                .setActivity(this)
                .setPaymentResult(paymentResult)
                .setDiscount(mCheckoutPresenter.getDiscount())
                .setDiscountEnabled(mCheckoutPresenter.isDiscountEnabled())
                .setCongratsDisplay(mCheckoutPresenter.getCongratsDisplay())
                .setSite(mCheckoutPresenter.getCheckoutPreference().getSite())
                .setPaymentResultScreenPreference(mCheckoutPresenter.getPaymentResultScreenPreference())
                .setAmount(amount)
                .setServicePreference(mCheckoutPresenter.getServicePreference())
                .startActivity();
    }

    private boolean isUserLogged() {
        return mCheckoutPresenter.getCheckoutPreference().getPayer() != null && !TextUtil.isEmpty(mCheckoutPresenter.getCheckoutPreference().getPayer().getAccessToken());
    }

    private void resolvePaymentResultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED && data != null) {
            String nextAction = data.getStringExtra("nextAction");
            mCheckoutPresenter.onPaymentResultCancel(nextAction);
        } else {
            if (data != null && data.hasExtra("resultCode")) {
                Integer finalResultCode = data.getIntExtra("resultCode", MercadoPagoCheckout.PAYMENT_RESULT_CODE);
                mCustomDataBundle = data;
                mCheckoutPresenter.onCustomPaymentResultResponse(finalResultCode);
            } else {
                mCheckoutPresenter.onPaymentResultResponse();
            }
        }
    }

    @Override
    public void finishWithPaymentResult() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void finishWithPaymentResult(Payment payment) {
        Intent data = new Intent();
        data.putExtra("payment", JsonUtil.getInstance().toJson(payment));
        setResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE, data);
        finish();
    }

    @Override
    public void finishWithPaymentResult(Integer paymentResultCode) {
        Intent intent = new Intent();
        if (mCustomDataBundle != null) {
            intent.putExtras(mCustomDataBundle);
        }
        setResult(paymentResultCode, intent);
        finish();
    }

    public void finishWithPaymentResult(Integer resultCode, Payment payment) {
        Intent intent = new Intent();
        if (mCustomDataBundle != null) {
            intent.putExtras(mCustomDataBundle);
        }
        intent.putExtra("payment", JsonUtil.getInstance().toJson(payment));
        setResult(resultCode, intent);
        finish();
    }

    @Override
    public void finishWithPaymentDataResult(PaymentData paymentData, Boolean paymentMethodEdited) {
        Intent intent = new Intent();
        intent.putExtra("paymentMethodChanged", paymentMethodEdited);
        intent.putExtra("paymentData", JsonUtil.getInstance().toJson(paymentData));
        setResult(mRequestedResultCode, intent);
        finish();
    }

    @Override
    public void cancelCheckout(MercadoPagoError mercadoPagoError) {
        Intent intent = new Intent();
        intent.putExtra("mercadoPagoError", JsonUtil.getInstance().toJson(mercadoPagoError));
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void cancelCheckout() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void startPaymentRecoveryFlow(PaymentRecovery paymentRecovery) {
        PaymentPreference paymentPreference = mCheckoutPresenter.getCheckoutPreference().getPaymentPreference();

        if (paymentPreference == null) {
            paymentPreference = new PaymentPreference();
        }

        paymentPreference.setDefaultPaymentTypeId(mCheckoutPresenter.getSelectedPaymentMethod().getPaymentTypeId());

        new MercadoPagoComponents.Activities.CardVaultActivityBuilder()
                .setActivity(this)
                .setMerchantPublicKey(mMerchantPublicKey)
                .setPayerAccessToken(mPrivateKey)
                .setPaymentPreference(paymentPreference)
                .setDecorationPreference(mDecorationPreference)
                .setAmount(mCheckoutPresenter.getCheckoutPreference().getAmount())
                .setSite(mCheckoutPresenter.getCheckoutPreference().getSite())
                .setInstallmentsEnabled(true)
                .setAcceptedPaymentMethods(mCheckoutPresenter.getPaymentMethodSearch().getPaymentMethods())
                .setInstallmentsReviewEnabled(mCheckoutPresenter.isInstallmentsReviewScreenEnabled())
                .setDiscount(mCheckoutPresenter.getDiscount())
                .setDiscountEnabled(mCheckoutPresenter.isDiscountEnabled())
                .setDirectDiscountEnabled(mCheckoutPresenter.isDirectDiscountEnabled())
                .setPaymentRecovery(paymentRecovery)
                .setShowBankDeals(mCheckoutPresenter.getShowBankDeals())
                .setESCEnabled(mCheckoutPresenter.isESCEnabled())
                .setCard(mCheckoutPresenter.getSelectedCard())
                .startActivity();

        animatePaymentMethodSelection();
    }

    @Override
    public void startPaymentMethodEdition() {
        showPaymentMethodSelection();
        animatePaymentMethodSelection();
    }

    private void animatePaymentMethodSelection() {
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    private void resolveErrorRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mCheckoutPresenter.recoverFromFailure();
        } else {
            MercadoPagoError mercadoPagoError = data.getStringExtra("mercadoPagoError") == null ? null :
                    JsonUtil.getInstance().fromJson(data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
            mCheckoutPresenter.onErrorCancel(mercadoPagoError);
        }
    }

    private void animateBackFromPaymentEdition() {
        overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
    }

    private void animateBackToPaymentMethodSelection() {
        overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
    }

    @Override
    public void cancelCheckout(Integer resultCode, PaymentData paymentData, Boolean paymentMethodEdited) {
        Intent intent = new Intent();
        if (mCustomDataBundle != null) {
            intent.putExtras(mCustomDataBundle);
        }
        intent.putExtra("paymentMethodChanged", paymentMethodEdited);
        intent.putExtra("paymentData", JsonUtil.getInstance().toJson(paymentData));
        setResult(resultCode, intent);
        this.finish();
    }

    @Override
    public void showError(MercadoPagoError error) {
        ErrorUtil.startErrorActivity(this, error, mMerchantPublicKey);
    }

    @Override
    public void showProgress() {
        LayoutUtil.showProgressLayout(this);
    }
}