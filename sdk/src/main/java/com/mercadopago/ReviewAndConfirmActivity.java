package com.mercadopago;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.mercadopago.adapters.ReviewablesAdapter;
import com.mercadopago.constants.Sites;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.presenters.ReviewAndConfirmPresenter;
import com.mercadopago.providers.ReviewAndConfirmProviderImpl;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.views.ReviewAndConfirmView;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mreverter on 2/1/17.
 */

public class ReviewAndConfirmActivity extends MercadoPagoBaseActivity implements TimerObserver, ReviewAndConfirmView {

    public static final int RESULT_CHANGE_PAYMENT_METHOD = 3;
    public static final int RESULT_CANCEL_PAYMENT = 4;

    //Controls

    protected CollapsingToolbarLayout mCollapsingToolbar;
    protected Toolbar mToolbar;
    protected AppBarLayout mAppBar;

    protected MPTextView mConfirmTextButton;
    protected MPTextView mCancelTextView;
    protected MPTextView mTermsAndConditionsTextView;
    protected MPTextView mTimerTextView;

    protected FrameLayout mConfirmButton;
    protected FrameLayout mCancelButton;

    protected NestedScrollView mScrollView;
    protected RecyclerView mReviewables;
    protected LinearLayout mTermsAndConditionsButton;

    protected ReviewAndConfirmPresenter mPresenter;
    protected DecorationPreference mDecorationPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDecorationPreference();
        createPresenter();
        getActivityParameters();
        mPresenter.attachView(this);
        mPresenter.attachResourcesProvider(new ReviewAndConfirmProviderImpl(this));

        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }
        setContentView(R.layout.mpsdk_activity_review_confirm);
        initializeControls();
        setListeners();
        showTimer();
        initializeReviewablesRecyclerView();
        mPresenter.initialize();
    }

    private void setListeners() {
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPayment();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPayment();
            }
        });
        mTermsAndConditionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTermsAndConditionsActivity();
            }
        });

    }

    private void createPresenter() {
        mPresenter = new ReviewAndConfirmPresenter();
    }

    private void getActivityParameters() {

        BigDecimal amount = new BigDecimal(getIntent().getStringExtra("amount"));
        Discount discount = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("discount"), Discount.class);
        PayerCost payerCost = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("payerCost"), PayerCost.class);
        CardInfo cardInfo = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("cardInfo"), CardInfo.class);
        PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        String extraPaymentMethodInfo = getIntent().getStringExtra("extraPaymentMethodInfo");
        Site site = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("site"), Site.class);
        List<Item> items;
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Item>>() {
            }.getType();
            items = gson.fromJson(this.getIntent().getStringExtra("items"), listType);
        } catch (Exception ex) {
            items = null;
        }

        mPresenter.setItems(items);
        mPresenter.setAmount(amount);
        mPresenter.setSite(site);
        mPresenter.setDiscount(discount);
        mPresenter.setPayerCost(payerCost);
        mPresenter.setCardInfo(cardInfo);
        mPresenter.setPaymentMethod(paymentMethod);
        mPresenter.setExtraPaymentMethodInfo(extraPaymentMethodInfo);

        mPresenter.setDecorationPreference(mDecorationPreference);
    }

    private void initializeControls() {
        mScrollView = (NestedScrollView) findViewById(R.id.mpsdkReviewScrollView);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.mpsdkCollapsingToolbar);
        mAppBar = (AppBarLayout) findViewById(R.id.mpsdkCheckoutAppBar);
        mToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
        mConfirmButton = (FrameLayout) findViewById(R.id.mpsdkCheckoutConfirmButton);
        mConfirmTextButton = (MPTextView) findViewById(R.id.mpsdkConfirmText);
        mCancelButton = (FrameLayout) findViewById(R.id.mpsdkReviewCancelButton);
        mCancelTextView = (MPTextView) findViewById(R.id.mpsdkCancelText);
        mTermsAndConditionsButton = (LinearLayout) findViewById(R.id.mpsdkCheckoutTermsAndConditions);
        mTermsAndConditionsTextView = (MPTextView) findViewById(R.id.mpsdkReviewTermsAndConditions);
        mTimerTextView = (MPTextView) findViewById(R.id.mpsdkTimerTextView);
        mReviewables = (RecyclerView) findViewById(R.id.mpsdkReviewablesRecyclerView);

        initializeToolbar();
        setToolbarTitle();
        decorateButtons();
    }

    private void initializeReviewablesRecyclerView() {
        mReviewables = (RecyclerView) findViewById(R.id.mpsdkReviewablesRecyclerView);
        mReviewables.setNestedScrollingEnabled(false);
        mReviewables.setLayoutManager(new LinearLayoutManager(this));
        mReviewables.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
    }

    private void setTimerColor() {
        if (mTimerTextView != null) {
            mTimerTextView.setTextColor(mDecorationPreference.getBaseColor());
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
                setTimerColor();
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

    private void setToolbarTitle() {
        LayoutUtil.showRegularLayout(this);
        mCollapsingToolbar.setTitle(getString(R.string.mpsdk_activity_checkout_title));
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mCollapsingToolbar.setExpandedTitleColor(mDecorationPreference.getBaseColor());
            mCollapsingToolbar.setCollapsedTitleTextColor(mDecorationPreference.getBaseColor());
        } else {
            mCollapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.mpsdk_background_blue));
            mCollapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.mpsdk_background_blue));
        }
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            CheckoutTimer.getInstance().addObserver(this);
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    private void startTermsAndConditionsActivity() {
        Intent termsAndConditionsIntent = new Intent(this, TermsAndConditionsActivity.class);
        termsAndConditionsIntent.putExtra("siteId", Sites.ARGENTINA.getId());
        termsAndConditionsIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(mDecorationPreference));
        startActivity(termsAndConditionsIntent);
    }

    private void getDecorationPreference() {
        if (getIntent().getStringExtra("decorationPreference") != null) {
            mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        }
    }

    protected boolean isCustomColorSet() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    protected int getCustomBaseColor() {
        return mDecorationPreference.getBaseColor();
    }

    protected boolean isDarkFontEnabled() {
        return mDecorationPreference != null && mDecorationPreference.isDarkFontEnabled();
    }

    protected int getDarkFontColor() {
        return mDecorationPreference.getDarkFontColor(this);
    }

    protected void decorate(Button button) {
        if (isCustomColorSet()) {
            button.setBackgroundColor(getCustomBaseColor());
        }

        if (isDarkFontEnabled()) {
            button.setTextColor(getDarkFontColor());
        }
    }

    protected void decorate(Toolbar toolbar) {
        if (toolbar != null) {
            if (isCustomColorSet()) {
                toolbar.setBackgroundColor(getCustomBaseColor());
            }
            decorateUpArrow(toolbar);
        }
    }

    protected void decorateUpArrow(Toolbar toolbar) {
        if (isDarkFontEnabled()) {
            int darkFont = getDarkFontColor();
            Drawable upArrow = toolbar.getNavigationIcon();
            if (upArrow != null && getSupportActionBar() != null) {
                upArrow.setColorFilter(darkFont, PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
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

    @Override
    public void onTimeChanged(String timeToShow) {
        mTimerTextView.setText(timeToShow);
    }

    @Override
    public void onFinish() {
        Intent intent = new Intent();
        setResult(RESULT_CANCEL_PAYMENT, intent);
        this.finish();
    }

    @Override
    public void showError(String message) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), message, false);
    }

    @Override
    public void showReviewables(List<Reviewable> reviewables) {
        ReviewablesAdapter reviewablesAdapter = new ReviewablesAdapter(reviewables);
        mReviewables.setAdapter(reviewablesAdapter);
    }

    @Override
    public void changePaymentMethod() {
        setResult(RESULT_CHANGE_PAYMENT_METHOD);
        finish();
    }

    @Override
    public void confirmPayment() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void cancelPayment() {
        setResult(RESULT_CANCEL_PAYMENT);
        finish();
    }

}
