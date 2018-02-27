package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.PayerCostsAdapter;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.controllers.CustomServicesHandler;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.core.MercadoPagoUI;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Site;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.presenters.InstallmentsPresenter;
import com.mercadopago.providers.InstallmentsProviderImpl;
import com.mercadopago.tracker.FlowHandler;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.utils.TrackingUtil;
import com.mercadopago.uicontrollers.FontCache;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.uicontrollers.discounts.DiscountRowView;
import com.mercadopago.uicontrollers.installments.InstallmentsReviewView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.InstallmentsActivityView;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by vaserber on 9/29/16.
 */

public class InstallmentsActivity extends MercadoPagoBaseActivity implements InstallmentsActivityView, TimerObserver {

    protected InstallmentsPresenter mPresenter;

    //Local vars
    protected String mPublicKey;
    protected String mPrivateKey;
    protected boolean mActivityActive;

    protected String mDefaultBaseURL;
    protected String mMerchantDiscountBaseURL;
    protected String mMerchantGetDiscountURI;
    protected Map<String, String> mDiscountAdditionalInfo;

    //View controls
    protected PayerCostsAdapter mPayerCostsAdapter;
    protected RecyclerView mInstallmentsRecyclerView;

    //ViewMode
    protected boolean mLowResActive;

    //Low Res View
    protected Toolbar mLowResToolbar;
    protected MPTextView mLowResTitleToolbar;

    //Normal View
    protected CollapsingToolbarLayout mCollapsingToolbar;
    protected AppBarLayout mAppBar;
    protected FrameLayout mCardContainer;
    protected FrameLayout mInstallmentsReview;
    protected Toolbar mNormalToolbar;
    protected FrontCardView mFrontCardView;
    protected MPTextView mTimerTextView;
    protected FrameLayout mDiscountFrameLayout;

    private MPTextView mNoInstallmentsRateTextView;
    private LinearLayout mNoInstallmentsRate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPresenter();
        getActivityParameters();

        setMerchantInfo();

        mPresenter.attachView(this);
        mPresenter.attachResourcesProvider(new InstallmentsProviderImpl(this, mPublicKey, mPrivateKey, mDefaultBaseURL,
                mMerchantDiscountBaseURL, mMerchantGetDiscountURI, mDiscountAdditionalInfo));

        mActivityActive = true;
        analyzeLowRes();
        setContentView();
        initializeControls();
        initializeView();

        mPresenter.initialize();
    }

    private void createPresenter() {
        mPresenter = new InstallmentsPresenter();
    }

    private void getActivityParameters() {
        mPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPrivateKey = getIntent().getStringExtra("payerAccessToken");

        mPresenter.setSite(JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("site"), Site.class));
        mPresenter.setPaymentMethod(JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class));
        mPresenter.setIssuer(JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("issuer"), Issuer.class));
        mPresenter.setCardInfo(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("cardInfo"), CardInfo.class));

        BigDecimal amount = null;
        if (this.getIntent().getStringExtra("amount") != null) {
            amount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        }

        mPresenter.setAmount(amount);

        List<PayerCost> payerCosts;
        try {
            Type listType = new TypeToken<List<PayerCost>>() {
            }.getType();
            payerCosts = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("payerCosts"), listType);
        } catch (Exception ex) {
            payerCosts = null;
        }

        mPresenter.setPayerCosts(payerCosts);
        mPresenter.setPaymentPreference(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentPreference"), PaymentPreference.class));
        mPresenter.setDiscount(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("discount"), Discount.class));
        mPresenter.setDiscountEnabled(this.getIntent().getBooleanExtra("discountEnabled", true));
        mPresenter.setDirectDiscountEnabled(this.getIntent().getBooleanExtra("directDiscountEnabled", true));
        mPresenter.setInstallmentsReviewEnabled(this.getIntent().getBooleanExtra("installmentsReviewEnabled", true));
        mPresenter.setPayerEmail(this.getIntent().getStringExtra("payerEmail"));
    }

    private void setMerchantInfo() {
        if (CustomServicesHandler.getInstance().getServicePreference() != null) {
            mDefaultBaseURL = CustomServicesHandler.getInstance().getServicePreference().getDefaultBaseURL();
            mMerchantDiscountBaseURL = CustomServicesHandler.getInstance().getServicePreference().getGetMerchantDiscountBaseURL();
            mMerchantGetDiscountURI = CustomServicesHandler.getInstance().getServicePreference().getGetMerchantDiscountURI();
            mDiscountAdditionalInfo = CustomServicesHandler.getInstance().getServicePreference().getGetDiscountAdditionalInfo();
        }
    }

    public void analyzeLowRes() {
        if (mPresenter.isRequiredCardDrawn()) {
            this.mLowResActive = ScaleUtil.isLowRes(this);
        } else {
            this.mLowResActive = true;
        }
    }

    public void setContentView() {
        if (mLowResActive) {
            setContentViewLowRes();
        } else {
            setContentViewNormal();
        }
    }

    public void setContentViewLowRes() {
        setContentView(R.layout.mpsdk_activity_installments_lowres);
    }

    public void setContentViewNormal() {
        setContentView(R.layout.mpsdk_activity_installments_normal);
    }

    private void initializeControls() {
        mInstallmentsRecyclerView = (RecyclerView) findViewById(R.id.mpsdkActivityInstallmentsView);
        mTimerTextView = (MPTextView) findViewById(R.id.mpsdkTimerTextView);

        if (mLowResActive) {
            initializeLowResControls();
        } else {
            initializeNormalControls();
        }

        mDiscountFrameLayout = (FrameLayout) findViewById(R.id.mpsdkDiscount);
        mDiscountFrameLayout.setVisibility(View.VISIBLE);
        mInstallmentsReview = (FrameLayout) findViewById(R.id.mpsdkInstallmentsReview);
    }

    private void initializeLowResControls() {
        mLowResToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
        mLowResTitleToolbar = (MPTextView) findViewById(R.id.mpsdkTitle);

        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            Toolbar.LayoutParams marginParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginParams.setMargins(0, 0, 0, 6);
            mLowResTitleToolbar.setLayoutParams(marginParams);
            mLowResTitleToolbar.setTextSize(19);
            mTimerTextView.setTextSize(17);
        }

        mLowResToolbar.setVisibility(View.VISIBLE);
    }

    private void initializeNormalControls() {
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.mpsdkCollapsingToolbar);
        mAppBar = (AppBarLayout) findViewById(R.id.mpsdkInstallmentesAppBar);
        mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityCardContainer);
        mNormalToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
        mNormalToolbar.setVisibility(View.VISIBLE);
    }

    private void initializeView() {
        loadViews();
        hideHeader();
        showTimer();
    }

    protected void trackScreen() {
        MPTrackingContext mTrackingContext = new MPTrackingContext.Builder(this, mPublicKey)
                .setCheckoutVersion(BuildConfig.VERSION_NAME)
                .build();

        ScreenViewEvent event = new ScreenViewEvent.Builder()
                .setFlowId(FlowHandler.getInstance().getFlowId())
                .setScreenId(TrackingUtil.SCREEN_ID_INSTALLMENTS)
                .setScreenName(TrackingUtil.SCREEN_NAME_CARD_FORM_INSTALLMENTS)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_METHOD_ID, mPresenter.getPaymentMethod().getId())
                .build();

        mTrackingContext.trackEvent(event);
    }

    public void loadViews() {
        if (mLowResActive) {
            loadLowResViews();
        } else {
            loadNormalViews();
        }
    }

    @Override
    public void warnAboutBankInterests() {
        mNoInstallmentsRate = (LinearLayout) findViewById(R.id.mpsdkNoInstallmentsRate);
        mNoInstallmentsRate.setVisibility(View.VISIBLE);
        mNoInstallmentsRateTextView = (MPTextView) findViewById(R.id.mpsdkNoInstallmentsRateTextView);
        mNoInstallmentsRateTextView.setVisibility(View.VISIBLE);
        mNoInstallmentsRateTextView.setText(R.string.mpsdk_interest_label);
    }

    public void loadLowResViews() {
        loadToolbarArrow(mLowResToolbar);
        mLowResTitleToolbar.setText(getString(R.string.mpsdk_card_installments_title));

        if (FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            mLowResTitleToolbar.setTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
        }
    }

    public void loadNormalViews() {
        loadToolbarArrow(mNormalToolbar);
        mNormalToolbar.setTitle(getString(R.string.mpsdk_card_installments_title));
        setCustomFontNormal();

        mFrontCardView = new FrontCardView(this, CardRepresentationModes.SHOW_FULL_FRONT_ONLY);
        mFrontCardView.setSize(CardRepresentationModes.MEDIUM_SIZE);
        mFrontCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        if (mPresenter.getCardInfo() != null) {
            mFrontCardView.setCardNumberLength(mPresenter.getCardNumberLength());
            mFrontCardView.setLastFourDigits(mPresenter.getCardInfo().getLastFourDigits());
        }
        mFrontCardView.inflateInParent(mCardContainer, true);
        mFrontCardView.initializeControls();
        mFrontCardView.draw();
        mFrontCardView.enableEditingCardNumber();
    }

    private void loadToolbarArrow(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInstallmentsReviewVisible()) {
                        hideInstallmentsReviewView();
                        showInstallmentsRecyclerView();

                        mPresenter.initializeDiscountRow();
                    } else {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mPresenter.getDiscount()));
                        returnIntent.putExtra("discountEnabled", JsonUtil.getInstance().toJson(mPresenter.getDiscountEnabled()));
                        returnIntent.putExtra("directDiscountEnabled", mPresenter.getDirectDiscountEnabled());
                        setResult(RESULT_CANCELED, returnIntent);
                        finish();
                    }
                }
            });
        }
    }

    private void setCustomFontNormal() {
        if (FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            mCollapsingToolbar.setCollapsedTitleTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
            mCollapsingToolbar.setExpandedTitleTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
        }
    }

    private void hideHeader() {
        if (mLowResActive) {
            mLowResToolbar.setVisibility(View.GONE);
        } else {
            mNormalToolbar.setTitle("");
        }
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            CheckoutTimer.getInstance().addObserver(this);
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    @Override
    public void showHeader() {
        if (mLowResActive) {
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mNormalToolbar.setTitle(getString(R.string.mpsdk_card_installments_title));
            setCustomFontNormal();
        }
    }

    private void initializeAdapter(OnSelectedCallback<Integer> onSelectedCallback) {
        mPayerCostsAdapter = new PayerCostsAdapter(this, mPresenter.getSite(), onSelectedCallback);
        initializeAdapterListener(mPayerCostsAdapter, mInstallmentsRecyclerView);
    }

    private void initializeAdapterListener(RecyclerView.Adapter adapter, RecyclerView view) {
        view.setAdapter(adapter);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mPresenter.onItemSelected(position);
                    }
                }));
    }

    @Override
    public void showError(MercadoPagoError error, String requestOrigin) {
        if (error.isApiException()) {
            showApiException(error.getApiException(), requestOrigin);
        } else {
            ErrorUtil.startErrorActivity(this, error, mPublicKey);
        }
    }

    public void showApiException(ApiException apiException, String requestOrigin) {
        if (mActivityActive) {
            ApiUtil.showApiExceptionError(this, apiException, mPublicKey, requestOrigin);
        }
    }

    @Override
    public void onTimeChanged(String timeToShow) {
        mTimerTextView.setText(timeToShow);
    }

    @Override
    public void onFinish() {
        setResult(MercadoPagoCheckout.TIMER_FINISHED_RESULT_CODE);
        this.finish();
    }

    @Override
    public void showInstallments(List<PayerCost> payerCostList, OnSelectedCallback<Integer> onSelectedCallback) {
        //We track after evaluating default installments or autoselected installments
        trackScreen();
        initializeAdapter(onSelectedCallback);
        mPayerCostsAdapter.addResults(payerCostList);
    }

    @Override
    public void showLoadingView() {
        mInstallmentsRecyclerView.setVisibility(View.GONE);
        mDiscountFrameLayout.setVisibility(View.GONE);

        LayoutUtil.showProgressLayout(this);
    }

    @Override
    public void hideLoadingView() {
        mInstallmentsRecyclerView.setVisibility(View.VISIBLE);
        mDiscountFrameLayout.setVisibility(View.VISIBLE);

        LayoutUtil.showRegularLayout(this);
    }

    @Override
    public void finishWithResult(PayerCost payerCost) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(payerCost));
        returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mPresenter.getDiscount()));
        returnIntent.putExtra("discountEnabled", JsonUtil.getInstance().toJson(mPresenter.getDiscountEnabled()));
        returnIntent.putExtra("directDiscountEnabled", mPresenter.getDirectDiscountEnabled());
        setResult(RESULT_OK, returnIntent);
        finish();
        animateTransitionSlideInSlideOut();
    }

    public void animateTransitionSlideInSlideOut() {
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    @Override
    public void onBackPressed() {
        if (isInstallmentsReviewVisible()) {
            hideInstallmentsReviewView();
            showInstallmentsRecyclerView();

            mPresenter.initializeDiscountRow();
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("backButtonPressed", true);
            returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mPresenter.getDiscount()));
            returnIntent.putExtra("discountEnabled", JsonUtil.getInstance().toJson(mPresenter.getDiscountEnabled()));
            returnIntent.putExtra("directDiscountEnabled", mPresenter.getDirectDiscountEnabled());
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mPresenter.recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
        } else if (requestCode == MercadoPagoComponents.Activities.DISCOUNTS_REQUEST_CODE) {
            resolveDiscountRequest(resultCode, data);
        } else {
            setResult(resultCode, data);
            finish();
        }
    }

    protected void resolveDiscountRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (mPresenter.getDiscount() == null) {
                Discount discount = JsonUtil.getInstance().fromJson(data.getStringExtra("discount"), Discount.class);
                mPresenter.onDiscountReceived(discount);
            }
        }
    }

    @Override
    public void startDiscountFlow(BigDecimal transactionAmount) {
        MercadoPagoComponents.Activities.DiscountsActivityBuilder mercadoPagoBuilder = new MercadoPagoComponents.Activities.DiscountsActivityBuilder();

        mercadoPagoBuilder.setActivity(this)
                .setMerchantPublicKey(mPublicKey)
                .setPayerEmail(mPresenter.getPayerEmail())
                .setAmount(transactionAmount)
                .setDiscount(mPresenter.getDiscount())
                .setDirectDiscountEnabled(mPresenter.getDirectDiscountEnabled());

        if (mPresenter.getDiscount() == null) {
            mercadoPagoBuilder.setDirectDiscountEnabled(false);
        } else {
            mercadoPagoBuilder.setDiscount(mPresenter.getDiscount());
        }

        mercadoPagoBuilder.startActivity();
    }

    @Override
    public void showDiscountRow(BigDecimal transactionAmount) {
        MercadoPagoUI.Views.DiscountRowViewBuilder discountRowViewBuilder = new MercadoPagoUI.Views.DiscountRowViewBuilder();

        discountRowViewBuilder.setContext(this)
                .setDiscount(mPresenter.getDiscount())
                .setTransactionAmount(transactionAmount)
                .setCurrencyId(mPresenter.getSite().getCurrencyId());

        if (isInstallmentsReviewVisible() && mPresenter.getDiscount() == null) {
            discountRowViewBuilder.setDiscountEnabled(false);
        } else {
            discountRowViewBuilder.setDiscountEnabled(mPresenter.getDiscountEnabled());
        }

        DiscountRowView discountRowView = discountRowViewBuilder.build();

        discountRowView.inflateInParent(mDiscountFrameLayout, true);
        discountRowView.initializeControls();
        discountRowView.draw();
        discountRowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInstallmentsRecyclerOnClickEnabled() || isInstallmentsReviewOnClickEnabled()) {
                    mPresenter.initializeDiscountActivity();
                }
            }
        });
    }

    private Boolean isInstallmentsRecyclerOnClickEnabled() {
        return !isInstallmentsReviewVisible() && mPresenter.getDiscountEnabled();
    }

    private Boolean isInstallmentsReviewOnClickEnabled() {
        return isInstallmentsReviewVisible() && mPresenter.getDiscount() != null && mPresenter.getDiscountEnabled();
    }

    @Override
    public void initInstallmentsReviewView(final PayerCost payerCost) {
        InstallmentsReviewView installmentsReviewView = new MercadoPagoUI.Views.InstallmentsReviewViewBuilder()
                .setContext(this)
                .setCurrencyId(mPresenter.getSite().getCurrencyId())
                .setPayerCost(payerCost)
                .build();

        installmentsReviewView.inflateInParent(mInstallmentsReview, true);
        installmentsReviewView.initializeControls();
        installmentsReviewView.draw();
        installmentsReviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishWithResult(payerCost);
            }
        });
    }

    @Override
    public void hideInstallmentsRecyclerView() {
        mInstallmentsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showInstallmentsRecyclerView() {
        mInstallmentsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showInstallmentsReviewView() {
        mInstallmentsReview.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideInstallmentsReviewView() {
        mInstallmentsReview.setVisibility(View.GONE);
    }

    private Boolean isInstallmentsReviewVisible() {
        return mInstallmentsReview.getVisibility() == View.VISIBLE;
    }
}
