package com.mercadopago;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Currency;
import com.mercadopago.model.Discount;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.presenters.DiscountsPresenter;
import com.mercadopago.providers.DiscountProviderImpl;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.views.DiscountsView;

import java.math.BigDecimal;

public class DiscountsActivity extends AppCompatActivity implements DiscountsView, TimerObserver {

    // Local vars
    protected DecorationPreference mDecorationPreference;
    protected MPTextView mTimerTextView;

    //View
    protected ProgressBar mProgressBar;
    protected FrameLayout mReviewDiscountSummaryContainer;
    protected FrameLayout mNextButton;
    protected FrameLayout mBackButton;
    protected FrameLayout mErrorContainer;
    protected FrameLayout mDiscountBackground;
    protected LinearLayout mDiscountCodeContainer;
    protected LinearLayout mDiscountLinearLayout;
    protected MPTextView mReviewSummaryTitle;
    protected MPTextView mReviewSummaryProductAmount;
    protected MPTextView mReviewSummaryDiscountAmount;
    protected MPTextView mReviewSummaryTotalAmount;
    protected MPTextView mErrorTextView;
    protected TextView mNextButtonText;
    protected TextView mBackButtonText;
    protected ImageView mCloseImage;
    protected MPEditText mDiscountCodeEditText;
    protected ScrollView mScrollView;
    protected Toolbar mToolbar;

    protected DiscountsPresenter mDiscountsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPresenter();
        getActivityParameters();
        initializePresenter();
        if (isCustomColorSet()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }

        setContentView();
        initializeControls();
        onValidStart();
    }

    private void initializePresenter() {
        try {
            DiscountProviderImpl discountProvider = new DiscountProviderImpl(this, mDiscountsPresenter.getPublicKey());
            mDiscountsPresenter.attachResourcesProvider(discountProvider);
            mDiscountsPresenter.attachView(this);
        } catch (IllegalStateException exception) {
            finishWithCancelResult();
        }
    }

    protected void createPresenter() {
        mDiscountsPresenter = new DiscountsPresenter();
    }

    protected void getActivityParameters() {
        mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);

        mDiscountsPresenter.setMerchantPublicKey(getIntent().getStringExtra("merchantPublicKey"));
        mDiscountsPresenter.setPayerEmail(this.getIntent().getStringExtra("payerEmail"));
        mDiscountsPresenter.setTransactionAmount(new BigDecimal(this.getIntent().getStringExtra("amount")));
        mDiscountsPresenter.setDiscount(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("discount"), Discount.class));
        mDiscountsPresenter.setDirectDiscountEnabled(this.getIntent().getBooleanExtra("directDiscountEnabled", true));
    }

    protected void setContentView() {
        setContentView(R.layout.activity_discounts);
    }

    protected void onValidStart() {
        showTimer();
        mDiscountsPresenter.initialize();
    }

    protected void initializeControls() {
        mDiscountLinearLayout = (LinearLayout) findViewById(R.id.mpsdkDiscountLinearLayout);
        mReviewDiscountSummaryContainer = (FrameLayout) findViewById(R.id.mpsdkReviewDiscountSummaryContainer);
        mDiscountBackground = (FrameLayout) findViewById(R.id.mpsdkDiscountBackground);
        mDiscountCodeContainer = (LinearLayout) findViewById(R.id.mpsdkDiscountCodeContainer);

        //Review discount summary
        mReviewSummaryTitle = (MPTextView) findViewById(R.id.mpsdkReviewSummaryTitle);
        mReviewSummaryProductAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryProductsAmount);
        mReviewSummaryDiscountAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryDiscountsAmount);
        mReviewSummaryTotalAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryTotalAmount);

        //Discount code input
        mDiscountCodeEditText = (MPEditText) findViewById(R.id.mpsdkDiscountCode);
        mDiscountCodeEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        mNextButton = (FrameLayout) findViewById(R.id.mpsdkNextButton);
        mBackButton = (FrameLayout) findViewById(R.id.mpsdkBackButton);
        mNextButtonText = (MPTextView) findViewById(R.id.mpsdkNextButtonText);
        mBackButtonText = (MPTextView) findViewById(R.id.mpsdkBackButtonText);

        mCloseImage = (ImageView) findViewById(R.id.mpsdkCloseImage);
        mErrorContainer = (FrameLayout) findViewById(R.id.mpsdkErrorContainer);
        mErrorTextView = (MPTextView) findViewById(R.id.mpsdkErrorTextView);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mScrollView = (ScrollView) findViewById(R.id.mpsdkScrollViewContainer);
        mTimerTextView = (MPTextView) findViewById(R.id.mpsdkTimerTextView);

        setListeners();
        fullScrollDown();
        initializeToolbar();
    }

    private void setListeners() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence discountCode = mDiscountCodeEditText.getText();
                mDiscountsPresenter.validateDiscountCodeInput(discountCode.toString());
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishWithResult();
            }
        });

        mDiscountCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                //Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                clearErrorView();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Nothing to do
            }
        });
    }

    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDiscountsPresenter.getDiscount() == null) {
                    onBackPressed();
                } else {
                    finishWithResult();
                }
            }
        });

        decorateToolbar();
    }

    private void decorateToolbar() {
        if (mToolbar != null && mDecorationPreference != null) {
            if (mDecorationPreference.hasColors()) {
                mToolbar.setBackgroundColor(mDecorationPreference.getBaseColor());
            }

            if (mDecorationPreference.isDarkFontEnabled()) {
                mCloseImage.setColorFilter(mDecorationPreference.getDarkFontColor(this), PorterDuff.Mode.SRC_ATOP);

                if (mTimerTextView != null) {
                    mTimerTextView.setTextColor(mDecorationPreference.getDarkFontColor(this));
                }
            }
            decorateUpArrow();
        }
    }

    protected void decorateUpArrow() {
        if (mDecorationPreference.isDarkFontEnabled()) {
            int darkFont = mDecorationPreference.getDarkFontColor(this);
            Drawable upArrow = mToolbar.getNavigationIcon();
            if (upArrow != null && getSupportActionBar() != null) {
                upArrow.setColorFilter(darkFont, PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            CheckoutTimer.getInstance().addObserver(this);
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    private boolean isCustomColorSet() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    @Override
    public void drawSummary() {
        MPTracker.getInstance().trackScreen("DISCOUNT_SUMMARY", "2", mDiscountsPresenter.getPublicKey(), BuildConfig.VERSION_NAME, this);

        mDiscountCodeContainer.setVisibility(View.GONE);
        mReviewDiscountSummaryContainer.setVisibility(View.VISIBLE);

        showSummaryTitle();
        showTransactionRow();
        showDiscountRow();
        showTotalRow();
        decorateSummary();
    }

    private void decorateSummary() {
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mReviewDiscountSummaryContainer.setBackgroundColor(mDecorationPreference.getBaseColor());
            if (mDecorationPreference.isDarkFontEnabled()) {
                mCloseImage.setColorFilter(mDecorationPreference.getDarkFontColor(this), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    private void showTotalRow() {
        if (isAmountValid(mDiscountsPresenter.getTransactionAmount()) && isDiscountCurrencyIdValid()) {
            mReviewSummaryTotalAmount.setText(getFormattedAmount(mDiscountsPresenter.getDiscount().getAmountWithDiscount(mDiscountsPresenter.getTransactionAmount()), mDiscountsPresenter.getDiscount().getCurrencyId()));
        } else {
            finishWithCancelResult();
        }
    }

    private void showDiscountRow() {
        StringBuilder discountAmountBuilder = new StringBuilder();
        Spanned discountAmount;

        if (isAmountValid(mDiscountsPresenter.getCouponAmount()) && isDiscountCurrencyIdValid()) {
            discountAmountBuilder.append("-");
            discountAmountBuilder.append(CurrenciesUtil.formatNumber(mDiscountsPresenter.getCouponAmount(), mDiscountsPresenter.getCurrencyId()));
            discountAmount = CurrenciesUtil.formatCurrencyInText(mDiscountsPresenter.getCouponAmount(), mDiscountsPresenter.getCurrencyId(), discountAmountBuilder.toString(), false, true);
            mReviewSummaryDiscountAmount.setText(discountAmount);
        } else {
            finishWithCancelResult();
        }
    }

    private void showTransactionRow() {
        if (isAmountValid(mDiscountsPresenter.getTransactionAmount()) && isDiscountCurrencyIdValid()) {
            mReviewSummaryProductAmount.setText(getFormattedAmount(mDiscountsPresenter.getTransactionAmount(), mDiscountsPresenter.getDiscount().getCurrencyId()));
        } else {
            finishWithCancelResult();
        }
    }

    private void showSummaryTitle() {
        if (isPercentOffValid()) {
            String title = mDiscountsPresenter.getDiscount().getPercentOff() + getString(R.string.mpsdk_percent_of_discount);
            mReviewSummaryTitle.setText(title);
        } else if (isAmountOffValid()){
            Currency currency = CurrenciesUtil.getCurrency(mDiscountsPresenter.getDiscount().getCurrencyId());
            String amount = currency.getSymbol() + mDiscountsPresenter.getDiscount().getAmountOff();

            StringBuilder title = new StringBuilder();

            title.append(amount);
            title.append(" ");
            title.append(getString(R.string.mpsdk_of_discount));

            mReviewSummaryTitle.setText(title);
        }
    }

    @Override
    public void requestDiscountCode() {
        MPTracker.getInstance().trackScreen("DISCOUNT_INPUT_CODE", "2", mDiscountsPresenter.getPublicKey(), BuildConfig.VERSION_NAME, this);

        mReviewDiscountSummaryContainer.setVisibility(View.GONE);
        mDiscountCodeContainer.setVisibility(View.VISIBLE);
        decorateDiscountCodeContainer();
        fullScrollDown();
    }

    public void decorateDiscountCodeContainer() {
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mDiscountBackground.setBackgroundColor(mDecorationPreference.getBaseColor());
            if (mDecorationPreference.isDarkFontEnabled()) {
                mCloseImage.setColorFilter(mDecorationPreference.getDarkFontColor(this), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public void showCodeInputError(String message) {
        mErrorContainer.setVisibility(View.VISIBLE);
        mErrorTextView.setText(message);
    }

    @Override
    public void showEmptyDiscountCodeError() {
        showCodeInputError(getString(R.string.mpsdk_do_not_enter_code));
    }

    @Override
    public void clearErrorView() {
        mErrorContainer.setVisibility(View.GONE);
        mErrorTextView.setText("");
    }

    private void fullScrollDown() {
        Runnable runnable = new Runnable() {
            public void run() {
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
        };
        mScrollView.post(runnable);
        runnable.run();
    }

    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mDiscountsPresenter.getDiscount()));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void finishWithCancelResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void hideKeyboard() {
        LayoutUtil.hideKeyboard(this);
    }

    @Override
    public void setSoftInputModeSummary() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private Spanned getFormattedAmount(BigDecimal amount, String currencyId) {
        String originalNumber = CurrenciesUtil.formatNumber(amount, currencyId);
        Spanned amountText = CurrenciesUtil.formatCurrencyInText(amount, currencyId, originalNumber, false, true);
        return amountText;
    }

    @Override
    public void onTimeChanged(String timeToShow) {
        mTimerTextView.setText(timeToShow);
    }

    @Override
    public void onFinish() {
        this.finish();
    }

    private Boolean isAmountOffValid() {
        return mDiscountsPresenter.getDiscount() != null && mDiscountsPresenter.getDiscount().getAmountOff() != null && mDiscountsPresenter.getDiscount().getAmountOff().compareTo(BigDecimal.ZERO) > 0;
    }

    private Boolean isPercentOffValid() {
        return mDiscountsPresenter.getDiscount() != null && mDiscountsPresenter.getDiscount().getPercentOff() != null && mDiscountsPresenter.getDiscount().getPercentOff().compareTo(BigDecimal.ZERO) > 0;
    }

    private Boolean isAmountValid(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private Boolean isDiscountCurrencyIdValid() {
        return mDiscountsPresenter.getDiscount() != null && mDiscountsPresenter.getDiscount().getCurrencyId() != null && CurrenciesUtil.isValidCurrency(mDiscountsPresenter.getDiscount().getCurrencyId());
    }
}
