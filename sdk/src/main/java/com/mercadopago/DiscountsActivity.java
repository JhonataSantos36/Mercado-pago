package com.mercadopago;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.controllers.CustomServicesHandler;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Currency;
import com.mercadopago.model.Discount;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.presenters.DiscountsPresenter;
import com.mercadopago.providers.DiscountProviderImpl;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.TextUtil;
import com.mercadopago.views.DiscountsActivityView;

import java.math.BigDecimal;
import java.util.Map;

public class DiscountsActivity extends AppCompatActivity implements DiscountsActivityView, TimerObserver {

    // Local vars
    protected MPTextView mTimerTextView;
    private String mMerchantBaseURL;
    private String mMerchantDiscountBaseURL;
    private String mMerchantGetDiscountURI;
    private Map<String, String> mDiscountAdditionalInfo;

    //View
    protected ProgressBar mProgressBar;
    protected FrameLayout mReviewDiscountSummaryContainer;
    protected FrameLayout mNextButton;
    protected FrameLayout mBackButton;
    protected FrameLayout mErrorContainer;
    protected FrameLayout mDiscountBackground;
    protected FrameLayout mCloseImageFrameLayout;
    protected LinearLayout mDiscountCodeContainer;
    protected LinearLayout mDiscountLinearLayout;
    protected MPTextView mReviewSummaryTitle;
    protected MPTextView mReviewSummaryProductAmount;
    protected MPTextView mReviewSummaryDiscountAmount;
    protected MPTextView mReviewSummaryDiscountLabel;
    protected MPTextView mReviewSummaryTotalAmount;
    protected MPTextView mErrorTextView;
    protected TextView mNextButtonText;
    protected TextView mBackButtonText;
    protected ImageView mCloseImage;
    protected MPEditText mDiscountCodeEditText;
    protected ScrollView mScrollView;
    protected Toolbar mToolbar;

    protected DiscountsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPresenter();
        getActivityParameters();
        initializePresenter();

        setContentView();
        setMerchantInfo();
        initializeControls();
        onValidStart();
    }

    private void initializePresenter() {
        try {
            DiscountProviderImpl discountProvider = new DiscountProviderImpl(this, mPresenter.getPublicKey(), mMerchantBaseURL, mMerchantDiscountBaseURL, mMerchantGetDiscountURI, mDiscountAdditionalInfo);
            mPresenter.attachResourcesProvider(discountProvider);
            mPresenter.attachView(this);
        } catch (IllegalStateException exception) {
            finishWithCancelResult();
        }
    }

    private void setMerchantInfo() {
        if (CustomServicesHandler.getInstance().getServicePreference() != null) {
            mMerchantBaseURL = CustomServicesHandler.getInstance().getServicePreference().getDefaultBaseURL();
            mMerchantDiscountBaseURL = CustomServicesHandler.getInstance().getServicePreference().getGetMerchantDiscountBaseURL();
            mMerchantGetDiscountURI = CustomServicesHandler.getInstance().getServicePreference().getGetMerchantDiscountURI();
            mDiscountAdditionalInfo = CustomServicesHandler.getInstance().getServicePreference().getGetDiscountAdditionalInfo();
        }
    }

    protected void createPresenter() {
        mPresenter = new DiscountsPresenter();
    }

    protected void getActivityParameters() {
        mPresenter.setMerchantPublicKey(getIntent().getStringExtra("merchantPublicKey"));
        mPresenter.setPayerEmail(this.getIntent().getStringExtra("payerEmail"));
        mPresenter.setTransactionAmount(new BigDecimal(this.getIntent().getStringExtra("amount")));
        mPresenter.setDiscount(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("discount"), Discount.class));
        mPresenter.setDirectDiscountEnabled(this.getIntent().getBooleanExtra("directDiscountEnabled", true));
    }

    protected void setContentView() {
        setContentView(R.layout.activity_discounts);
    }

    protected void onValidStart() {
        showTimer();
        mPresenter.initialize();
    }

    protected void initializeControls() {
        mDiscountLinearLayout = (LinearLayout) findViewById(R.id.mpsdkDiscountLinearLayout);
        mReviewDiscountSummaryContainer = (FrameLayout) findViewById(R.id.mpsdkReviewDiscountSummaryContainer);
        mDiscountBackground = (FrameLayout) findViewById(R.id.mpsdkDiscountBackground);
        mDiscountCodeContainer = (LinearLayout) findViewById(R.id.mpsdkDiscountCodeContainer);

        //Review discount summary
        mReviewSummaryTitle = (MPTextView) findViewById(R.id.mpsdkReviewSummaryTitle);
        mReviewSummaryProductAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryProductsAmount);
        mReviewSummaryDiscountLabel = (MPTextView) findViewById(R.id.mpsdkReviewSummaryDiscountLabel);
        mReviewSummaryDiscountAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryDiscountsAmount);
        mReviewSummaryTotalAmount = (MPTextView) findViewById(R.id.mpsdkReviewSummaryTotalAmount);

        //Discount code input
        mDiscountCodeEditText = (MPEditText) findViewById(R.id.mpsdkDiscountCode);
        mDiscountCodeEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        mNextButton = (FrameLayout) findViewById(R.id.mpsdkNextButton);
        mBackButton = (FrameLayout) findViewById(R.id.mpsdkBackButton);
        mNextButtonText = (MPTextView) findViewById(R.id.mpsdkNextButtonText);
        mBackButtonText = (MPTextView) findViewById(R.id.mpsdkBackButtonText);

        mCloseImageFrameLayout = (FrameLayout) findViewById(R.id.mpsdkCloseImageFrameLayout);
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
                mPresenter.validateDiscountCodeInput(discountCode.toString());
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mCloseImageFrameLayout.setOnClickListener(new View.OnClickListener() {
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
                if (mPresenter.getDiscount() == null) {
                    onBackPressed();
                } else {
                    finishWithResult();
                }
            }
        });

        decorateToolbar();
    }


    private void decorateToolbar() {

        mCloseImage.setColorFilter(getResources().getColor(R.color.mpsdk_toolbar_text), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.mpsdk_background));


        Drawable upArrow = mToolbar.getNavigationIcon();
        if (upArrow != null && getSupportActionBar() != null) {
            upArrow.setColorFilter(getResources().getColor(R.color.mpsdk_toolbar_text), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
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
        ErrorUtil.startErrorActivity(this, message, false, mPresenter.getPublicKey());
    }

    @Override
    public void drawSummary() {
        mDiscountCodeContainer.setVisibility(View.GONE);
        mReviewDiscountSummaryContainer.setVisibility(View.VISIBLE);

        showSummaryTitle();
        showTransactionRow();
        showDiscountRow();
        showTotalRow();
    }

    private void showTotalRow() {
        if (isAmountValid(mPresenter.getTransactionAmount()) && isDiscountCurrencyIdValid()) {
            mReviewSummaryTotalAmount.setText(getFormattedAmount(mPresenter.getDiscount().getAmountWithDiscount(mPresenter.getTransactionAmount()), mPresenter.getDiscount().getCurrencyId()));
        } else {
            finishWithCancelResult();
        }
    }

    private void showDiscountRow() {
        StringBuilder discountAmountBuilder = new StringBuilder();
        Spanned discountAmount;

        if (isAmountValid(mPresenter.getCouponAmount()) && isDiscountCurrencyIdValid()) {

            discountAmountBuilder.append("-");
            discountAmountBuilder.append(CurrenciesUtil.formatNumber(mPresenter.getCouponAmount(), mPresenter.getCurrencyId()));
            discountAmount = CurrenciesUtil.formatCurrencyInText(mPresenter.getCouponAmount(), mPresenter.getCurrencyId(), discountAmountBuilder.toString(), false, true);

            mReviewSummaryDiscountAmount.setText(discountAmount);
            if (!TextUtil.isEmpty(mPresenter.getDiscount().getConcept())) {
                mReviewSummaryDiscountLabel.setText(mPresenter.getDiscount().getConcept());
            }
        } else {
            finishWithCancelResult();
        }
    }

    private void showTransactionRow() {
        if (isAmountValid(mPresenter.getTransactionAmount()) && isDiscountCurrencyIdValid()) {
            mReviewSummaryProductAmount.setText(getFormattedAmount(mPresenter.getTransactionAmount(), mPresenter.getDiscount().getCurrencyId()));
        } else {
            finishWithCancelResult();
        }
    }

    private void showSummaryTitle() {
        if (isPercentOffValid()) {
            String title = mPresenter.getDiscount().getPercentOff() + getString(R.string.mpsdk_percent_of_discount);
            mReviewSummaryTitle.setText(title);
        } else if (isAmountOffValid()) {
            Currency currency = CurrenciesUtil.getCurrency(mPresenter.getDiscount().getCurrencyId());
            String amount = currency.getSymbol() + mPresenter.getDiscount().getAmountOff();
            StringBuilder title = new StringBuilder();

            title.append(amount);
            title.append(" ");
            title.append(getString(R.string.mpsdk_of_discount));

            mReviewSummaryTitle.setText(title);
        } else {
            mReviewSummaryTitle.setText(getString(R.string.mpsdk_discount));
        }
    }

    @Override
    public void requestDiscountCode() {
        mReviewDiscountSummaryContainer.setVisibility(View.GONE);
        mDiscountCodeContainer.setVisibility(View.VISIBLE);
        setStatusBarColor();
        fullScrollDown();
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.mpsdk_status_bar));
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
        returnIntent.putExtra("discount", JsonUtil.getInstance().toJson(mPresenter.getDiscount()));
        returnIntent.putExtra("directDiscountEnabled", mPresenter.getDirectDiscountEnabled());

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
        setResult(MercadoPagoCheckout.TIMER_FINISHED_RESULT_CODE);
        this.finish();
    }

    private Boolean isAmountOffValid() {
        return mPresenter.getDiscount() != null && mPresenter.getDiscount().getAmountOff() != null && mPresenter.getDiscount().getAmountOff().compareTo(BigDecimal.ZERO) > 0;
    }

    private Boolean isPercentOffValid() {
        return mPresenter.getDiscount() != null && mPresenter.getDiscount().getPercentOff() != null && mPresenter.getDiscount().getPercentOff().compareTo(BigDecimal.ZERO) > 0;
    }

    private Boolean isAmountValid(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private Boolean isDiscountCurrencyIdValid() {
        return mPresenter.getDiscount() != null && mPresenter.getDiscount().getCurrencyId() != null && CurrenciesUtil.isValidCurrency(mPresenter.getDiscount().getCurrencyId());
    }

    @Override
    public void hideDiscountSummary() {
        mReviewDiscountSummaryContainer.setVisibility(View.GONE);
    }
}
