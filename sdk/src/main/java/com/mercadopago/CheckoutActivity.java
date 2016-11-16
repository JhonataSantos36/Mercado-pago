package com.mercadopago;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.ReviewPaymentAdapter;
import com.mercadopago.adapters.ReviewProductAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnChangePaymentMethodCallback;
import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.customviews.MPButton;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.exceptions.ExceptionHandler;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentIntent;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.PaymentResultAction;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.uicontrollers.ViewControllerFactory;
import com.mercadopago.uicontrollers.payercosts.PayerCostViewController;
import com.mercadopago.uicontrollers.paymentmethods.PaymentMethodViewController;
import com.mercadopago.uicontrollers.reviewandconfirm.ReviewSummaryView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class CheckoutActivity extends MercadoPagoActivity {

    private static final String CHECKOUT_PREFERENCE_BUNDLE = "mCheckoutPreference";
    private static final String PAYMENT_METHOD_SEARCH_BUNDLE = "mPaymentMethodSearch";
    private static final String SAVED_CARDS_BUNDLE = "mSavedCards";
    //Parameters
    protected String mCheckoutPreferenceId;
    protected CheckoutPreference mCheckoutPreference;
    protected String mMerchantPublicKey;
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;
    protected String mMerchantAccessToken;

    //Local vars
    protected MercadoPago mMercadoPago;
    protected PaymentMethodSearch mPaymentMethodSearch;

    protected Long mTransactionId;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected Token mCreatedToken;
    protected Payment mCreatedPayment;
    protected Site mSite;

    protected String mPurchaseTitle;

    protected boolean mPaymentMethodEditionRequested;

    protected PaymentMethodViewController mPaymentMethodRow;
    protected PayerCostViewController mPayerCostRow;

    protected PaymentRecovery mPaymentRecovery;

    protected OnChangePaymentMethodCallback mChangePaymentMethodCallback;
    protected OnConfirmPaymentCallback mConfirmCallback;

    //Controls
    protected Toolbar mToolbar;
    protected MPTextView mTermsAndConditionsTextView;
    protected MPTextView mCancelTextView;
    protected MPTextView mTotalAmountTextView;
    protected MPButton mPayButton;
    protected RelativeLayout mPayerCostLayout;
    protected Boolean mBackPressedOnce;
    protected Snackbar mSnackbar;
    protected FrameLayout mConfirmButton;
    protected FrameLayout mCancelButton;
    protected LinearLayout mTermsAndConditionsButton;
    protected MPTextView mConfirmTextButton;
    protected List<Card> mSavedCards;

    protected FrameLayout mSnackbarContainer;

    //Payments list (for many payment methods)
    protected List<PaymentMethod> mPaymentMethodList;
    protected List<CardInfo> mCardInfoList;
    protected List<String> mCurrencies;
    protected List<PayerCost> mPayerCostList;
    protected List<BigDecimal> mTotalAmountList;
    protected List<PaymentMethodSearchItem> mPaymentMethodSearchList;

    //View
    protected ReviewPaymentAdapter mReviewPaymentAdapter;
    protected RecyclerView mReviewPaymentRecyclerView;
    protected ReviewProductAdapter mReviewProductAdapter;
    protected RecyclerView mReviewProductRecyclerView;
    protected CollapsingToolbarLayout mCollapsingToolbar;
    protected AppBarLayout mAppBar;
    protected ProgressBar mProgressBar;
    protected FrameLayout mReviewSummaryContainer;
    protected NestedScrollView mScrollView;
    protected String mCustomerId;

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_collapsing_checkout);
    }

    @Override
    protected void getActivityParameters() {
        //TODO modified
        //mMerchantPublicKey = "TEST-9eb0be69-329a-417f-9dd5-aad772a4d50b";//getIntent().getStringExtra("merchantPublicKey");
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");

        mMerchantBaseUrl = this.getIntent().getStringExtra("merchantBaseUrl");
        mMerchantGetCustomerUri = this.getIntent().getStringExtra("merchantGetCustomerUri");
        mMerchantAccessToken = this.getIntent().getStringExtra("merchantAccessToken");
        //TODO modified
        //mCheckoutPreferenceId = "137787120-2853c5cb-388b-49f1-824c-759366965aef";//this.getIntent().getStringExtra("checkoutPreferenceId");
        mCheckoutPreferenceId = this.getIntent().getStringExtra("checkoutPreferenceId");
    }

    @Override
    protected void initializeControls() {
        //Review views
        mReviewPaymentRecyclerView = (RecyclerView) findViewById(R.id.mpsdkReviewPaymentRecyclerView);
        mReviewProductRecyclerView = (RecyclerView) findViewById(R.id.mpsdkReviewProductRecyclerView);
        mReviewPaymentRecyclerView.setNestedScrollingEnabled(false);
        mReviewProductRecyclerView.setNestedScrollingEnabled(false);
        mReviewSummaryContainer = (FrameLayout) findViewById(R.id.mpsdkReviewSummaryContainer);
        mConfirmButton = (FrameLayout) findViewById(R.id.mpsdkReviewSummaryConfirmButton);
        mConfirmTextButton = (MPTextView) findViewById(R.id.mpsdkConfirmText);
        mCancelButton = (FrameLayout) findViewById(R.id.mpsdkReviewCancelButton);
        mCancelTextView = (MPTextView) findViewById(R.id.mpsdkCancelText);
        mTermsAndConditionsButton = (LinearLayout) findViewById(R.id.mpsdkCheckoutTermsAndConditions);
        mTermsAndConditionsTextView = (MPTextView) findViewById(R.id.mpsdkReviewTermsAndConditions);

        mSnackbarContainer  = (FrameLayout) findViewById(R.id.mpsdkSnackBarContainer);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mScrollView = (NestedScrollView) findViewById(R.id.mpsdkReviewScrollView);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.mpsdkCollapsingToolbar);
        mAppBar = (AppBarLayout) findViewById(R.id.mpsdkCheckoutAppBar);
        mToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
        mAppBar.setVisibility(View.GONE);
        mScrollView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.VISIBLE);

        mChangePaymentMethodCallback = new OnChangePaymentMethodCallback() {
            @Override
            public void onChangePaymentMethodSelected() {
                changePaymentMethod();
            }
        };

        mConfirmCallback = new OnConfirmPaymentCallback() {
            @Override
            public void confirmPayment() {
                createPayment();
            }
        };

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPayment();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });

        mTermsAndConditionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTermsAndConditionsActivity();
            }
        });

        decorateButtons();
    }

    @Override
    protected void onValidStart() {
        mBackPressedOnce = false;
        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(mMerchantPublicKey)
                .build();

        getCheckoutPreference();
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), message, false);
    }

    private void decorateButtons() {
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mConfirmButton.setBackgroundColor(mDecorationPreference.getBaseColor());
            if (mDecorationPreference.isDarkFontEnabled()) {
                mConfirmTextButton.setTextColor(mDecorationPreference.getDarkFontColor(this));
            }
            mCancelTextView.setTextColor(mDecorationPreference.getBaseColor());
            mTermsAndConditionsTextView.setTextColor(mDecorationPreference.getBaseColor());
        }
    }

    private void getCheckoutPreference() {
        showProgressBar();
        mMercadoPago.getPreference(mCheckoutPreferenceId, new Callback<CheckoutPreference>() {
            @Override
            public void success(CheckoutPreference checkoutPreference) {
                mCheckoutPreference = checkoutPreference;

                try {
                    if (isActivityActive()) {
                        validatePreference();
                        initializeCheckout();
                    }
                } catch (CheckoutPreferenceException e) {
                    String errorMessage = ExceptionHandler.getErrorMessage(getActivity(), e);
                    ErrorUtil.startErrorActivity(getActivity(), errorMessage, false);
                }
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getCheckoutPreference();
                        }
                    });
                }
            }
        });
    }

    private void validatePreference() throws CheckoutPreferenceException {
        mCheckoutPreference.validate();
        if (!mCheckoutPreference.getId().equals(mCheckoutPreferenceId)) {
            throw new CheckoutPreferenceException(CheckoutPreferenceException.PREF_ID_NOT_MATCHING_REQUESTED);
        }
    }

    private void initializeCheckout() {
        mSite = new Site(mCheckoutPreference.getSiteId(), mCheckoutPreference.getItems().get(0).getCurrencyId());
        getPaymentMethodSearch();
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (isEmpty(mMerchantPublicKey)) {
            throw new IllegalStateException("public key not set");
        } else if (isEmpty(mCheckoutPreferenceId)) {
            throw new IllegalStateException("preference id not set");
        }
    }

    protected void startTermsAndConditionsActivity() {
        Intent termsAndConditionsIntent = new Intent(this, TermsAndConditionsActivity.class);
        termsAndConditionsIntent.putExtra("siteId", mCheckoutPreference.getSiteId());
        termsAndConditionsIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(mDecorationPreference));
        startActivity(termsAndConditionsIntent);
    }

    protected void getPaymentMethodSearch() {

        showProgressBar();
        mMercadoPago.getPaymentMethodSearch(mCheckoutPreference.getAmount(), mCheckoutPreference.getExcludedPaymentTypes(), mCheckoutPreference.getExcludedPaymentMethods(), mCheckoutPreference.getPayer(), false, new Callback<PaymentMethodSearch>() {
            @Override
            public void success(PaymentMethodSearch paymentMethodSearch) {
                mPaymentMethodSearch = paymentMethodSearch;
                if (!mPaymentMethodSearch.hasSavedCards() && isMerchantServerInfoAvailable()) {
                    getCustomerAsync();
                } else if (isActivityActive()) {
                    startPaymentVaultActivity();
                }
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getPaymentMethodSearch();
                        }
                    });
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                }
            }
        });
    }

    private boolean isMerchantServerInfoAvailable() {
        return !isEmpty(mMerchantBaseUrl) && !isEmpty(mMerchantGetCustomerUri) && !isEmpty(mMerchantAccessToken);
    }

    private void getCustomerAsync() {
        showProgressBar();
        MerchantServer.getCustomer(this, mMerchantBaseUrl, mMerchantGetCustomerUri, mMerchantAccessToken, new Callback<Customer>() {
            @Override
            public void success(Customer customer) {
                if(customer != null) {
                    mCustomerId = customer.getId();
                    mSavedCards = mCheckoutPreference.getPaymentPreference() == null ? customer.getCards() : mCheckoutPreference.getPaymentPreference().getValidCards(customer.getCards());
                }
                startPaymentVaultActivity();
            }

            @Override
            public void failure(ApiException apiException) {
                startPaymentVaultActivity();
            }
        });
    }

    protected void startPaymentVaultActivity() {
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .setSite(mSite)
                .setAmount(mCheckoutPreference.getAmount())
                .setPaymentMethodSearch(mPaymentMethodSearch)
                .setPaymentPreference(mCheckoutPreference.getPaymentPreference())
                .setDecorationPreference(mDecorationPreference)
                .setCards(mSavedCards)
                .startPaymentVaultActivity();
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

    private void showProgressBar() {
        mScrollView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void stopProgressBar() {
        mScrollView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            resolvePaymentVaultRequest(resultCode, data);
        } else if (requestCode == MercadoPago.PAYMENT_RESULT_REQUEST_CODE) {
            resolvePaymentResultRequest(resultCode, data);
        } else if (requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) {
            resolveInstallmentsRequest(resultCode, data);
        } else if (requestCode == MercadoPago.CARD_VAULT_REQUEST_CODE) {
            resolveCardVaultRequest(resultCode, data);
        } else {
            resolveErrorRequest(resultCode, data);
        }
    }

    protected void resolveCardVaultRequest(int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            mSelectedIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            mSelectedPayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            mCreatedToken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            mSelectedPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

            if (mPaymentRecovery != null && mPaymentRecovery.isTokenRecoverable()){
                createPayment();
            }
            else{
                MPTracker.getInstance().trackScreen("REVIEW_AND_CONFIRM", "3", mMerchantPublicKey, mCheckoutPreference.getSiteId(), BuildConfig.VERSION_NAME, this);
                showReviewAndConfirm();
                stopProgressBar();
            }
        } else {
            if (data != null && data.getStringExtra("mpException") != null) {
                Intent returnIntent = new Intent();
                MPTracker.getInstance().trackEvent("CARD_VAULT", "CANCELED", "3", mMerchantPublicKey, mCheckoutPreference.getSiteId(), BuildConfig.VERSION_NAME, this);
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            } else {
                startPaymentVaultActivity();
            }
        }
    }

    private void resolvePaymentVaultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            mSelectedIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            mSelectedPayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            mCreatedToken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            mSelectedPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            MPTracker.getInstance().trackScreen("REVIEW_AND_CONFIRM", "3", mMerchantPublicKey, mCheckoutPreference.getSiteId(), BuildConfig.VERSION_NAME, this);
            showReviewAndConfirm();
            stopProgressBar();
        } else {
            if (!mPaymentMethodEditionRequested) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            } else {
                animateBackFromPaymentEdition();
            }
        }
    }

    private void resolvePaymentResultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED && data != null) {
            String nextAction = data.getStringExtra("nextAction");
            if (!isEmpty(nextAction)){
                if (nextAction.equals(PaymentResultAction.SELECT_OTHER_PAYMENT_METHOD)){
                    startPaymentVaultActivity();
                }
                if (nextAction.equals(PaymentResultAction.RECOVER_PAYMENT)){
                    createPaymentRecovery();
                    startCardVaultActivity();
                }
            }
        } else {
            finishWithPaymentResult();
        }
    }

    private void createPaymentRecovery() {
        try{
            mPaymentRecovery = new PaymentRecovery(mCreatedToken, mCreatedPayment, mSelectedPaymentMethod, mSelectedPayerCost, mSelectedIssuer);
        }
        catch (Exception e){
            throw new IllegalStateException(e.getMessage());
        }
    }

    private void startCardVaultActivity() {

        PaymentPreference paymentPreference = mCheckoutPreference.getPaymentPreference();

        if (paymentPreference == null) {
            paymentPreference = new PaymentPreference();
        }

        paymentPreference.setDefaultPaymentTypeId(mSelectedPaymentMethod.getPaymentTypeId());

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentPreference(paymentPreference)
                .setDecorationPreference(mDecorationPreference)
                .setAmount(mCheckoutPreference.getAmount())
                .setSite(mSite)
                .setInstallmentsEnabled(true)
                .setSupportedPaymentMethods(mPaymentMethodSearch.getPaymentMethods())
                .setPaymentRecovery(mPaymentRecovery)
                .startCardVaultActivity();

        animatePaymentMethodSelection();
    }

    private void animatePaymentMethodSelection() {
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    private void resolveErrorRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            recoverFromFailure();
        } else if (noUserInteractionReached()) {
            setResult(RESULT_CANCELED, data);
            finish();
        } else {
            stopProgressBar();
        }
    }

    private boolean noUserInteractionReached() {
        return mSelectedPaymentMethod == null;
    }

    protected void resolveInstallmentsRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            mSelectedPayerCost = JsonUtil.getInstance().fromJson(bundle.getString("payerCost"), PayerCost.class);
            drawPayerCostRow();
            setAmountLabel();
        }
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    private void finishWithPaymentResult() {
        Intent paymentResultIntent = new Intent();
        paymentResultIntent.putExtra("payment", JsonUtil.getInstance().toJson(mCreatedPayment));
        setResult(RESULT_OK, paymentResultIntent);
        finish();
    }

    private void createPaymentMethodList() {
        //TODO for the future, for many payment methods
        mPaymentMethodList = new ArrayList<>();
        mPaymentMethodList.add(mSelectedPaymentMethod);
    }

    private void createCardInfoList() {
        mCardInfoList = new ArrayList<>();

        if (mCreatedToken != null && CardInfo.canCreateCardInfo(mCreatedToken)) {
            mCardInfoList.add(new CardInfo(mCreatedToken));
        } else if (mSavedCards != null && !mSavedCards.isEmpty()) {
            for (Card card: mSavedCards) {
                mCardInfoList.add(new CardInfo(card));
            }
        }
    }

    private void createCurrenciesList() {
        mCurrencies = new ArrayList<>();
        mCurrencies.add(mCheckoutPreference.getItems().get(0).getCurrencyId());
    }

    private void createPayerCostsList() {
        //info for cards
        mPayerCostList = new ArrayList<>();
        mPayerCostList.add(mSelectedPayerCost);
    }

    private void createTotalAmountList() {
        //info for off payment methods
        mTotalAmountList = new ArrayList<>();
        mTotalAmountList.add(mCheckoutPreference.getAmount());
    }

    private void createPaymentMethodSearchList() {
        mPaymentMethodSearchList = new ArrayList<>();
        for (PaymentMethod pm: mPaymentMethodList) {
            PaymentMethodSearchItem item = mPaymentMethodSearch.getSearchItemByPaymentMethod(pm);
            mPaymentMethodSearchList.add(item);
        }
    }

    private void showReviewAndConfirm() {
        createPaymentMethodList();
        createCardInfoList();
        createCurrenciesList();
        createPayerCostsList();
        createTotalAmountList();
        createPaymentMethodSearchList();

        initializeToolbar();
        setToolbarTitle();
        showScrollView();
        drawSummary();
        drawProductList();
        drawPaymentMethodList();
    }

    private void showScrollView() {
        mScrollView.setVisibility(View.VISIBLE);
    }

    private void drawSummary() {
        mReviewSummaryContainer.removeAllViews();
        ReviewSummaryView summaryView = new ReviewSummaryView(this, mCheckoutPreference.getItems().get(0).getCurrencyId(),
                mCheckoutPreference.getAmount(), mSelectedPayerCost, mSelectedPaymentMethod, null, null,
                mConfirmCallback, mDecorationPreference);
        summaryView.inflateInParent(mReviewSummaryContainer, true);
        summaryView.initializeControls();
        summaryView.drawSummary();
    }

    private void setToolbarTitle() {
        mProgressBar.setVisibility(View.GONE);
        mAppBar.setVisibility(View.VISIBLE);
        mCollapsingToolbar.setTitle(getString(R.string.mpsdk_activity_checkout_title));
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mCollapsingToolbar.setExpandedTitleColor(mDecorationPreference.getBaseColor());
            mCollapsingToolbar.setCollapsedTitleTextColor(mDecorationPreference.getBaseColor());
        } else {
            mCollapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.mpsdk_background_blue));
            mCollapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.mpsdk_background_blue));
        }
    }

    private void initializeToolbar() {
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Drawable upArrow = mToolbar.getNavigationIcon();
        if (upArrow != null && getSupportActionBar() != null) {
            if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
                upArrow.setColorFilter(mDecorationPreference.getBaseColor(), PorterDuff.Mode.SRC_ATOP);
            } else {
                upArrow.setColorFilter(ContextCompat.getColor(this, R.color.mpsdk_background_blue), PorterDuff.Mode.SRC_ATOP);
            }
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void drawPaymentMethodList() {
        initializeReviewPaymentAdapter();
    }

    private void drawProductList() {
        initializeReviewProductAdapter();
    }

    private void initializeReviewPaymentAdapter() {
        mReviewPaymentAdapter = new ReviewPaymentAdapter(this, mPaymentMethodList, mCardInfoList,
                mPayerCostList, mCurrencies, mTotalAmountList, mPaymentMethodSearchList, mSite, mChangePaymentMethodCallback,
                isUniquePaymentMethod(), mDecorationPreference);
        mReviewPaymentRecyclerView.setAdapter(mReviewPaymentAdapter);
        mReviewPaymentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void changePaymentMethod() {
        if (!isUniquePaymentMethod()) {
            mPaymentMethodEditionRequested = true;
            startPaymentVaultActivity();
            overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
        }
    }

    private void initializeReviewProductAdapter() {
        mReviewProductAdapter = new ReviewProductAdapter(this, mCheckoutPreference.getItems(), mCurrencies);
        mReviewProductRecyclerView.setAdapter(mReviewProductAdapter);
        mReviewProductRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void drawPayerCostRow() {
        mPayerCostLayout.removeAllViews();

        if (mSelectedPayerCost != null && mCheckoutPreference != null) {
            mPaymentMethodRow.showSeparator();

            mPayerCostRow = ViewControllerFactory.getPayerCostEditionViewController(this, mCheckoutPreference.getItems().get(0).getCurrencyId());
            mPayerCostRow.inflateInParent(mPayerCostLayout, true);
            mPayerCostRow.initializeControls();
            mPayerCostRow.drawPayerCost(mSelectedPayerCost);
            mPayerCostRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startInstallmentsActivity();
                }
            });
        }
    }

    public void startInstallmentsActivity() {
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(mCheckoutPreference.getMaxInstallments());

        new MercadoPago.StartActivityBuilder()
                .setActivity(getActivity())
                .setPublicKey(mMerchantPublicKey)
                .setPaymentMethod(mSelectedPaymentMethod)
                .setAmount(mCheckoutPreference.getAmount())
                .setToken(mCreatedToken)
                .setIssuer(mSelectedIssuer)
                .setSite(mSite)
                .setPaymentPreference(paymentPreference)
                .setDecorationPreference(mDecorationPreference)
                .startInstallmentsActivity();

        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    private void setAmountLabel() {
        mTotalAmountTextView.setText(getAmountLabel());
    }

    private Spanned getAmountLabel() {
        BigDecimal totalAmount = getTotalAmount();
        String currencyId = mCheckoutPreference.getItems().get(0).getCurrencyId();
        String amountText = CurrenciesUtil.formatNumber(totalAmount, currencyId);

        StringBuilder totalAmountTextBuilder = new StringBuilder();
        totalAmountTextBuilder.append(getString(R.string.mpsdk_payment_amount_to_pay));
        totalAmountTextBuilder.append(" ");
        totalAmountTextBuilder.append(amountText);

        return CurrenciesUtil.formatCurrencyInText(totalAmount, currencyId, totalAmountTextBuilder.toString(), true, true);
    }

    private BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal(0);
        if (mSelectedPayerCost != null) {
            amount = amount.add(mSelectedPayerCost.getTotalAmount());
        } else {
            amount = mCheckoutPreference.getAmount();
        }
        return amount;
    }

    private void animateBackFromPaymentEdition() {
        overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
    }

    private boolean isUniquePaymentMethod() {
        return mPaymentMethodSearch != null && mPaymentMethodSearch.getGroups().size() == 1
                && mPaymentMethodSearch.getGroups().get(0).isPaymentMethod()
                && (mSavedCards == null || mSavedCards.isEmpty());
    }

    protected void createPayment() {
        mScrollView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        PaymentIntent paymentIntent = createPaymentIntent();

        mMercadoPago.createPayment(paymentIntent, new Callback<Payment>() {
            @Override
            public void success(Payment payment) {
                mCreatedPayment = payment;
                startPaymentResultActivity();
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

    private PaymentIntent createPaymentIntent() {
        PaymentIntent paymentIntent = new PaymentIntent();
        paymentIntent.setPrefId(mCheckoutPreference.getId());
        paymentIntent.setPublicKey(mMerchantPublicKey);
        paymentIntent.setPaymentMethodId(mSelectedPaymentMethod.getId());
        Payer payer = new Payer();
        payer.setEmail(mCheckoutPreference.getPayer().getEmail());
        if(TextUtils.isEmpty(mCustomerId)) {
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

        if (!existsTransactionId() || !MercadoPagoUtil.isCard(mSelectedPaymentMethod.getPaymentTypeId())) {
            mTransactionId = createNewTransactionId();
        }

        paymentIntent.setTransactionId(mTransactionId);
        return paymentIntent;
    }

    private void startPaymentResultActivity() {
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(mMerchantPublicKey)
                .setActivity(getActivity())
                .setPayment(mCreatedPayment)
                .setPaymentMethod(mSelectedPaymentMethod)
                .startPaymentResultActivity();
    }

    private Long createNewTransactionId() {
        return Calendar.getInstance().getTimeInMillis() + Math.round(Math.random()) * Math.round(Math.random());
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

                MPException mpException = new MPException(apiException);
                ErrorUtil.startErrorActivity(this, mpException);
            } else {
                ApiUtil.showApiExceptionError(this, apiException);
            }
        } else {
            ApiUtil.showApiExceptionError(this, apiException);
        }
        stopProgressBar();
    }

    private void startPaymentInProcessActivity() {
        mCreatedPayment = new Payment();
        mCreatedPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        mCreatedPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_PENDING_CONTINGENCY);
        startPaymentResultActivity();
    }

    public void onCancelClicked() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mPaymentMethodSearch == null || isUniquePaymentMethod()) {
            onCancelClicked();
        } else if (mBackPressedOnce) {
            MPTracker.getInstance().trackEvent("CHECKOUT", "BACK_PRESSED", "3", mMerchantPublicKey, mCheckoutPreference.getSiteId(), BuildConfig.VERSION_NAME, this);

            mSnackbar.dismiss();
            mPaymentMethodEditionRequested = false;
            startPaymentVaultActivity();
            animateBackToPaymentVault();
        } else {
            mSnackbar = Snackbar.make(mSnackbarContainer, getString(R.string.mpsdk_press_again_confirm), Snackbar.LENGTH_LONG);
            mSnackbar.show();
            mBackPressedOnce = true;
            resetBackPressedOnceIn(4000);
        }
    }

    private void animateBackToPaymentVault() {
        overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
    }

    private void resetBackPressedOnceIn(final int mills) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(mills);
                    mBackPressedOnce = false;
                } catch (InterruptedException e) {
                    //Do nothing
                }
            }
        }).start();
    }
}
