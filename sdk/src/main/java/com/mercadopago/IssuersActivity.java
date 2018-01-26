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
import com.mercadopago.adapters.IssuersAdapter;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.presenters.IssuersPresenter;
import com.mercadopago.providers.IssuersProviderImpl;
import com.mercadopago.tracker.FlowHandler;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.utils.TrackingUtil;
import com.mercadopago.uicontrollers.FontCache;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.IssuersActivityView;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by vaserber on 10/11/16.
 */

public class IssuersActivity extends MercadoPagoBaseActivity implements IssuersActivityView, TimerObserver {

    protected IssuersPresenter mPresenter;

    // Local vars
    protected boolean mActivityActive;
    protected String mPublicKey;
    protected String mPrivateKey;

    protected IssuersAdapter mIssuersAdapter;
    protected RecyclerView mIssuersRecyclerView;

    //ViewMode
    protected boolean mLowResActive;

    //Low Res View
    protected Toolbar mLowResToolbar;
    protected MPTextView mLowResTitleToolbar;
    protected MPTextView mTimerTextView;

    //Normal View
    protected CollapsingToolbarLayout mCollapsingToolbar;
    protected AppBarLayout mAppBar;
    protected FrameLayout mCardContainer;
    protected Toolbar mNormalToolbar;
    protected FrontCardView mFrontCardView;
    protected ViewGroup mProgressLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPresenter();
        getActivityParameters();

        mPresenter.attachView(this);
        mPresenter.attachResourcesProvider(new IssuersProviderImpl(this, mPublicKey, mPrivateKey));

        mActivityActive = true;

        analyzeLowRes();
        setContentView();
        initializeControls();

        initialize();
        mPresenter.initialize();
    }

    protected void createPresenter() {
        mPresenter = new IssuersPresenter();
    }

    private void getActivityParameters() {
        mPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPrivateKey = getIntent().getStringExtra("payerAccessToken");

        List<Issuer> issuers;
        try {
            Type listType = new TypeToken<List<Issuer>>() {
            }.getType();
            issuers = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("issuers"), listType);
        } catch (Exception ex) {
            issuers = null;
        }

        mPresenter.setPaymentMethod(JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class));
        mPresenter.setCardInfo(JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("cardInfo"), CardInfo.class));
        mPresenter.setIssuers(issuers);
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

    private void setContentViewLowRes() {
        setContentView(R.layout.mpsdk_activity_issuers_lowres);
    }

    private void setContentViewNormal() {
        setContentView(R.layout.mpsdk_activity_issuers_normal);
    }

    private void initializeControls() {
        mIssuersRecyclerView = findViewById(R.id.mpsdkActivityIssuersView);
        mTimerTextView = findViewById(R.id.mpsdkTimerTextView);
        mProgressLayout = findViewById(R.id.mpsdkProgressLayout);

        if (mLowResActive) {
            initializeLowResControls();
        } else {
            initializeNormalControls();
        }

    }

    private void initializeLowResControls() {
        mLowResToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
        mLowResTitleToolbar = (MPTextView) findViewById(R.id.mpsdkTitle);

        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            Toolbar.LayoutParams marginParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginParams.setMargins(0, 0, 0, 0);
            mLowResTitleToolbar.setLayoutParams(marginParams);
            mLowResTitleToolbar.setTextSize(17);
            mTimerTextView.setTextSize(15);
        }

        mLowResToolbar.setVisibility(View.VISIBLE);
    }

    private void initializeNormalControls() {
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.mpsdkCollapsingToolbar);
        mAppBar = (AppBarLayout) findViewById(R.id.mpsdkIssuersAppBar);
        mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityCardContainer);
        mNormalToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
        mNormalToolbar.setVisibility(View.VISIBLE);
    }

    private void initialize() {
        loadViews();
        hideHeader();
        showTimer();
        trackScreen();
    }


    protected void trackScreen() {
        MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(this, mPublicKey)
                .setCheckoutVersion(BuildConfig.VERSION_NAME)
                .setTrackingStrategy(TrackingUtil.BATCH_STRATEGY)
                .build();

        ScreenViewEvent event = new ScreenViewEvent.Builder()
                .setFlowId(FlowHandler.getInstance().getFlowId())
                .setScreenId(TrackingUtil.SCREEN_ID_ISSUERS)
                .setScreenName(TrackingUtil.SCREEN_NAME_CARD_FORM_ISSUERS)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_TYPE_ID, mPresenter.getPaymentMethod().getPaymentTypeId())
                .addMetaData(TrackingUtil.METADATA_PAYMENT_METHOD_ID, mPresenter.getPaymentMethod().getId())
                .build();

        mpTrackingContext.trackEvent(event);
    }

    private void loadViews() {
        if (mLowResActive) {
            loadLowResViews();
        } else {
            loadNormalViews();
        }
    }

    private void loadLowResViews() {
        loadToolbarArrow(mLowResToolbar);
        mLowResTitleToolbar.setText(mPresenter.getResourcesProvider().getCardIssuersTitle());

        if (FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            mLowResTitleToolbar.setTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
        }
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
                    finish();
                }
            });
        }
    }

    private void loadNormalViews() {
        loadToolbarArrow(mNormalToolbar);
        mNormalToolbar.setTitle(mPresenter.getResourcesProvider().getCardIssuersTitle());
        setCustomFontNormal();

        mFrontCardView = new FrontCardView(this, CardRepresentationModes.SHOW_FULL_FRONT_ONLY);
        mFrontCardView.setSize(CardRepresentationModes.MEDIUM_SIZE);
        mFrontCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        if (mPresenter.getCardInfo() != null) {
            mFrontCardView.setCardNumberLength(mPresenter.getCardInfo().getCardNumberLength());
            mFrontCardView.setLastFourDigits(mPresenter.getCardInfo().getLastFourDigits());
        }
        mFrontCardView.inflateInParent(mCardContainer, true);
        mFrontCardView.initializeControls();
        mFrontCardView.draw();
        mFrontCardView.enableEditingCardNumber();
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

    private void initializeAdapter(OnSelectedCallback<Integer> onSelectedCallback) {
        mIssuersAdapter = new IssuersAdapter(this, onSelectedCallback);
        initializeAdapterListener(mIssuersAdapter, mIssuersRecyclerView);
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
    public void showHeader() {
        if (mLowResActive) {
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mNormalToolbar.setTitle(mPresenter.getResourcesProvider().getCardIssuersTitle());
            setCustomFontNormal();
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mPresenter.recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
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
    public void showIssuers(List<Issuer> issuersList, OnSelectedCallback<Integer> onSelectedCallback) {
        initializeAdapter(onSelectedCallback);
        mIssuersAdapter.addResults(issuersList);
        stopLoadingView();
    }

    @Override
    public void showLoadingView() {
        mIssuersRecyclerView.setVisibility(View.GONE);
        mProgressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoadingView() {
        mIssuersRecyclerView.setVisibility(View.VISIBLE);
        mProgressLayout.setVisibility(View.GONE);
    }

    @Override
    public void finishWithResult(Issuer issuer) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
        setResult(RESULT_OK, returnIntent);
        finish();
        animateTransitionSlideInSlideOut();
    }

    public void animateTransitionSlideInSlideOut() {
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
