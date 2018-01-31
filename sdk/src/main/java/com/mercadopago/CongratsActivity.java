package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.adapters.ReviewablesAdapter;
import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.constants.ContentLocation;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.controllers.CustomReviewablesHandler;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.ReviewSubscriber;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.tracker.FlowHandler;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.utils.TrackingUtil;
import com.mercadopago.uicontrollers.discounts.DiscountRowView;
import com.mercadopago.util.ColorsUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.InstallmentsUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.text.TextUtils.isEmpty;

public class CongratsActivity extends MercadoPagoBaseActivity implements ReviewSubscriber {

    // Controls
    protected MPTextView mPaymentMethodDescription;
    protected MPTextView mInstallmentsDescription;
    protected MPTextView mAmountDescription;
    protected MPTextView mInterestAmountDescription;
    protected MPTextView mInstallmentsTotalAmountDescription;
    protected MPTextView mPaymentIdDescription;
    protected MPTextView mPaymentIdDescriptionNumber;
    protected MPTextView mPayerEmailTextView;
    protected MPTextView mPaymentStatementDescriptionTextView;
    protected MPTextView mCongratulationsTitle;
    protected MPTextView mCongratulationsSubtitle;
    protected MPTextView mNoInstallmentsRateTextView;
    protected View mTopEmailSeparator;
    protected View mBottomEmailSeparator;
    protected ImageView mPaymentMethodImage;
    protected MPTextView mExitButtonText;
    protected FrameLayout mDiscountFrameLayout;
    protected Activity mActivity;
    protected RecyclerView mBottomReviewables;
    protected FrameLayout mSecondaryExitButton;
    protected MPTextView mSecondaryExitTextView;
    protected ImageView mHeaderIcon;

    // Activity parameters
    protected String mMerchantPublicKey;
    protected Discount mDiscount;
    protected Boolean mDiscountEnabled;
    protected PaymentResult mPaymentResult;

    //Local values
    private boolean mBackPressedOnce;
    private Integer mCongratsDisplay;

    //Data from PaymentResult
    private BigDecimal mTotalAmount;
    private String mCurrencyId;
    private int mInstallments;
    private BigDecimal mInstallmentsAmount;
    private BigDecimal mInstallmentsRate;
    private String mStatementDescription;
    private Long mPaymentId;
    private String mLastFourDigits;
    private String mPaymentMethodId;
    private String mPaymentMethodName;
    private String mPaymentTypeId;
    private String mPayerEmail;
    private Site mSite;
    private Issuer mIssuer;
    private BigDecimal mAmount;
    private PaymentResultScreenPreference mPaymentResultScreenPreference;
    private ViewGroup mTitleBackground;
    private RecyclerView mTopReviewables;
    private ServicePreference mServicePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityParameters();
        setContentView();
        initializeControls();
        initializeReviewablesRecyclerView();
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
        mCongratsDisplay = getIntent().getIntExtra("congratsDisplay", -1);
        mDiscount = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("discount"), Discount.class);
        mDiscountEnabled = getIntent().getExtras().getBoolean("discountEnabled", true);
        mPaymentResult = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentResult"), PaymentResult.class);
        mSite = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("site"), Site.class);
        mPaymentResultScreenPreference = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentResultScreenPreference"), PaymentResultScreenPreference.class);
        mServicePreference = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("servicePreference"), ServicePreference.class);
        if (getIntent().getStringExtra("amount") != null) {
            mAmount = new BigDecimal(getIntent().getStringExtra("amount"));
        }
    }

    protected void validateActivityParameters() throws IllegalStateException {
        if (mMerchantPublicKey == null) {
            throw new IllegalStateException("merchant public key not set");
        }
        if (mPaymentResult == null) {
            throw new IllegalStateException("payment result not set");
        }
    }

    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_congrats);
    }

    protected void initializeControls() {
        mPayerEmailTextView = (MPTextView) findViewById(R.id.mpsdkPayerEmail);
        mPaymentMethodDescription = (MPTextView) findViewById(R.id.mpsdkPaymentMethodDescription);
        mInstallmentsDescription = (MPTextView) findViewById(R.id.mpsdkInstallmentsDescription);
        mAmountDescription = (MPTextView) findViewById(R.id.mpsdkAmountDescription);
        mInterestAmountDescription = (MPTextView) findViewById(R.id.mpsdkInterestAmountDescription);
        mInstallmentsTotalAmountDescription = (MPTextView) findViewById(R.id.mpsdkInstallmentsTotalAmountDescription);
        mPaymentIdDescription = (MPTextView) findViewById(R.id.mpsdkPaymentIdDescription);
        mPaymentIdDescriptionNumber = (MPTextView) findViewById(R.id.mpsdkPaymentIdDescriptionNumber);
        mPaymentStatementDescriptionTextView = (MPTextView) findViewById(R.id.mpsdkStateDescription);
        mCongratulationsTitle = (MPTextView) findViewById(R.id.mpsdkCongratulationsTitle);
        mCongratulationsSubtitle = (MPTextView) findViewById(R.id.mpsdkCongratulationsSubtitle);
        mTopEmailSeparator = findViewById(R.id.mpsdkTopEmailSeparator);
        mBottomEmailSeparator = findViewById(R.id.mpsdkBottomEmailSeparator);
        mPaymentMethodImage = (ImageView) findViewById(R.id.mpsdkPaymentMethodImage);
        mExitButtonText = (MPTextView) findViewById(R.id.mpsdkExitButtonCongrats);
        mDiscountFrameLayout = (FrameLayout) findViewById(R.id.mpsdkDiscount);
        mBottomReviewables = (RecyclerView) findViewById(R.id.mpsdkReviewablesRecyclerView);
        mTopReviewables = (RecyclerView) findViewById(R.id.mpsdkTopReviewablesRecyclerView);
        mSecondaryExitButton = (FrameLayout) findViewById(R.id.mpsdkCongratsSecondaryExitButton);
        mSecondaryExitTextView = (MPTextView) findViewById(R.id.mpsdkCongratsSecondaryExitButtonText);
        mTitleBackground = (ViewGroup) findViewById(R.id.mpsdkTitleBackground);
        mHeaderIcon = (ImageView) findViewById(R.id.mpsdkIcon);

        mDiscountFrameLayout.setVisibility(View.GONE);
        mExitButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishWithOkResult();
            }
        });
    }

    private void initializeReviewablesRecyclerView() {
        mBottomReviewables.setNestedScrollingEnabled(false);
        mBottomReviewables.setLayoutManager(new LinearLayoutManager(this));
        mBottomReviewables.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        mTopReviewables.setNestedScrollingEnabled(false);
        mTopReviewables.setLayoutManager(new LinearLayoutManager(this));
        mTopReviewables.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
    }

    protected void onValidStart() {
        initializePaymentData();
        trackScreen();
        setPaymentResultScreenPreferenceData();
        setPaymentResultScreenWithoutPreferenceData();
        setPaymentEmailDescription();
        setPaymentStatementDescription();
        setDisplayTime();
        showBottomReviewables();
        showTopReviewables();
    }

    private void initializePaymentData() {
        PaymentData paymentData = mPaymentResult.getPaymentData();
        mStatementDescription = mPaymentResult.getStatementDescription();
        mPaymentId = mPaymentResult.getPaymentId();
        mPayerEmail = mPaymentResult.getPayerEmail();
        if (paymentData != null) {
            PaymentMethod paymentMethod = paymentData.getPaymentMethod();
            mDiscount = paymentData.getDiscount();
            mIssuer = paymentData.getIssuer();

            if (paymentData.getPayerCost() != null) {
                mTotalAmount = paymentData.getPayerCost().getTotalAmount();
                mInstallments = paymentData.getPayerCost().getInstallments();
                mInstallmentsAmount = paymentData.getPayerCost().getInstallmentAmount();
                mInstallmentsRate = paymentData.getPayerCost().getInstallmentRate();
            } else {
                mTotalAmount = mAmount;
            }
            if (paymentData.getToken() != null) {
                mLastFourDigits = paymentData.getToken().getLastFourDigits();
            }
            if (paymentMethod != null) {
                mPaymentMethodId = paymentMethod.getId();
                mPaymentMethodName = paymentMethod.getName();
                mPaymentTypeId = paymentMethod.getPaymentTypeId();
            }
        }
        if (mSite != null) {
            mCurrencyId = mSite.getCurrencyId();
        }
    }

    protected void trackScreen() {
        final MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(this, mMerchantPublicKey)
                .setCheckoutVersion(BuildConfig.VERSION_NAME)
                .build();

        final ScreenViewEvent.Builder builder = new ScreenViewEvent.Builder()
                .setFlowId(FlowHandler.getInstance().getFlowId())
                .setScreenId(TrackingUtil.SCREEN_ID_PAYMENT_RESULT_APPROVED)
                .setScreenName(TrackingUtil.SCREEN_NAME_PAYMENT_RESULT_APPROVED)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_IS_EXPRESS, TrackingUtil.IS_EXPRESS_DEFAULT_VALUE)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_TYPE_ID, mPaymentTypeId)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_METHOD_ID, mPaymentMethodId)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_STATUS, mPaymentResult.getPaymentStatus())
                .addMetaData(TrackingUtil.METADATA_PAYMENT_STATUS_DETAIL, mPaymentResult.getPaymentStatusDetail())
                .addMetaData(TrackingUtil.METADATA_PAYMENT_ID, String.valueOf(mPaymentResult.getPaymentId()));

        if (mIssuer != null && mIssuer.getId() != null) {
            builder.addMetaData(TrackingUtil.METADATA_ISSUER_ID, String.valueOf(mIssuer.getId()));
        }

        ScreenViewEvent event = builder.build();
        mpTrackingContext.trackEvent(event);
    }

    protected void onInvalidStart(String errorMessage) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), errorMessage, false, mMerchantPublicKey);
    }

    private void setPaymentResultScreenPreferenceData() {
        if (mPaymentResultScreenPreference != null) {
            if (mPaymentResultScreenPreference.getApprovedTitle() == null) {
                setDefaultCongratulationsTitle();
            } else {
                mCongratulationsTitle.setText(mPaymentResultScreenPreference.getApprovedTitle());
            }
            if (mPaymentResultScreenPreference.getApprovedSubtitle() == null) {
                setDefaultCongratulationsSubtitle();
            } else {
                mCongratulationsSubtitle.setText(mPaymentResultScreenPreference.getApprovedSubtitle());
                mCongratulationsSubtitle.setVisibility(View.VISIBLE);
            }
            if (mPaymentResultScreenPreference.getExitButtonTitle() == null) {
                setDefaultExitButtonTitle();
            } else {
                mExitButtonText.setText(mPaymentResultScreenPreference.getExitButtonTitle());
            }
            if (mPaymentResultScreenPreference.isApprovedReceiptEnabled()) {
                showReceipt();
            } else {
                hideReceipt();
            }
            if (mPaymentResultScreenPreference.isApprovedAmountEnabled()) {
                showAmount();
            } else {
                hideAmount();
            }
            if (mPaymentResultScreenPreference.isApprovedPaymentMethodInfoEnabled()) {
                showPaymentMethod();
            } else {
                hidePaymentMethod();
            }
            if (mPaymentResultScreenPreference.getApprovedIcon() != null) {
                Drawable image = ContextCompat.getDrawable(this, mPaymentResultScreenPreference.getApprovedIcon());

                mHeaderIcon.setImageDrawable(image);
            }
            if (!mPaymentResultScreenPreference.isCongratsSecondaryExitButtonEnabled() ||
                    mPaymentResultScreenPreference.getSecondaryCongratsExitButtonTitle() == null ||
                    (mPaymentResultScreenPreference.getSecondaryCongratsExitResultCode() == null
                            && !CallbackHolder.getInstance().hasPaymentResultCallback(CallbackHolder.CONGRATS_PAYMENT_RESULT_CALLBACK))) {
                hideSecondaryExitButton();
            } else {
                mSecondaryExitTextView.setText(mPaymentResultScreenPreference.getSecondaryCongratsExitButtonTitle());
                mSecondaryExitButton.setVisibility(View.VISIBLE);
                mSecondaryExitTextView.setVisibility(View.VISIBLE);
                setSecondaryExitButtonListener();
            }
            if (mPaymentResultScreenPreference.hasTitleBackgroundColor()) {
                mTitleBackground.setBackgroundColor(mPaymentResultScreenPreference.getTitleBackgroundColor());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int darkerTintColor = ColorsUtil.darker(mPaymentResultScreenPreference.getTitleBackgroundColor());
                    ColorsUtil.tintStatusBar(this, darkerTintColor);
                }
            }
        }
    }

    private void setPaymentResultScreenWithoutPreferenceData() {
        if (mPaymentResultScreenPreference == null) {
            setDefaultCongratulationsTitle();
            setDefaultCongratulationsSubtitle();
            setDefaultExitButtonTitle();
            showReceipt();
            showAmount();
            showPaymentMethod();
            hideSecondaryExitButton();
        }
    }

    private void setSecondaryExitButtonListener() {
        mSecondaryExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult(mPaymentResultScreenPreference.getSecondaryCongratsExitResultCode());
            }
        });
    }

    private void finishWithResult(Integer resultCode) {
        Intent intent = new Intent();
        intent.putExtra("resultCode", resultCode);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void hideSecondaryExitButton() {
        mSecondaryExitButton.setVisibility(View.GONE);
        mSecondaryExitTextView.setVisibility(View.GONE);
    }

    private void setDefaultCongratulationsTitle() {
        mCongratulationsTitle.setText(getResources().getString(R.string.mpsdk_title_approved_payment));
    }

    private void setDefaultCongratulationsSubtitle() {
        mCongratulationsSubtitle.setVisibility(View.GONE);
    }

    private void setDefaultExitButtonTitle() {
        mExitButtonText.setText(getResources().getString(R.string.mpsdk_text_continue));
    }

    public void showBottomReviewables() {
        List<Reviewable> customReviewables = retrieveBottomCustomReviewables();
        ReviewablesAdapter reviewablesAdapter = new ReviewablesAdapter(customReviewables);
        mBottomReviewables.setAdapter(reviewablesAdapter);
    }

    public void showTopReviewables() {
        List<Reviewable> customReviewables = retrieveTopCustomReviewables();
        ReviewablesAdapter reviewablesAdapter = new ReviewablesAdapter(customReviewables);
        mTopReviewables.setAdapter(reviewablesAdapter);
    }

    private List<Reviewable> retrieveTopCustomReviewables() {
        if (CustomReviewablesHandler.getInstance().hasCongratsReviewables(ContentLocation.TOP)) {
            List<Reviewable> customReviewables = CustomReviewablesHandler.getInstance().getCongratsReviewables(ContentLocation.TOP);

            for (Reviewable reviewable : customReviewables) {
                reviewable.setReviewSubscriber(this);
            }

            return customReviewables;
        } else {
            return new ArrayList<>();
        }
    }

    private List<Reviewable> retrieveBottomCustomReviewables() {
        if (CustomReviewablesHandler.getInstance().hasCongratsReviewables(ContentLocation.BOTTOM)) {
            List<Reviewable> customReviewables = CustomReviewablesHandler.getInstance().getCongratsReviewables(ContentLocation.BOTTOM);

            for (Reviewable reviewable : customReviewables) {
                reviewable.setReviewSubscriber(this);
            }

            return customReviewables;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void changeRequired(Integer resultCode, Bundle data) {
        Intent intent = new Intent();
        if (data != null) {
            intent.putExtras(data);
        }
        intent.putExtra("resultCode", resultCode);
        intent.putExtra("paymentResult", JsonUtil.getInstance().toJson(mPaymentResult));
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showReceipt() {
        if (isPaymentIdValid()) {
            String message = getString(R.string.mpsdk_payment_id_description_number, String.valueOf(mPaymentId));
            mPaymentIdDescriptionNumber.setText(message);
        } else {
            hideReceipt();
        }
    }

    private void hideReceipt() {
        mPaymentIdDescription.setVisibility(View.GONE);
        mPaymentIdDescriptionNumber.setVisibility(View.GONE);
    }

    private void setPaymentStatementDescription() {
        if (mStatementDescription == null) {
            mPaymentStatementDescriptionTextView.setVisibility(View.GONE);
        } else {
            String description = String.format(getResources().getString(R.string.mpsdk_text_state_account_activity_congrats), mStatementDescription);
            mPaymentStatementDescriptionTextView.setText(description);
            mPaymentStatementDescriptionTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setInterestAmountDescription() {
        setTotalAmountDescription();


        if (hasInterests()) {
            mInterestAmountDescription.setVisibility(View.GONE);
        } else {
            if (!InstallmentsUtil.shouldWarnAboutBankInterests(mSite)) {
                mInterestAmountDescription.setText(getString(R.string.mpsdk_zero_rate));
                mInstallmentsDescription.setVisibility(View.VISIBLE);
            } else {
                mInterestAmountDescription.setVisibility(View.GONE);
                warnAboutBankInterests();
            }
        }

    }

    private void warnAboutBankInterests() {
        mNoInstallmentsRateTextView = (MPTextView) findViewById(R.id.mpsdkNoInstallmentsRateTextView);
        mNoInstallmentsRateTextView.setVisibility(View.VISIBLE);
        mNoInstallmentsRateTextView.setText(R.string.mpsdk_interest_label);
    }

    private void setDiscountRow() {
        if (mDiscountEnabled && mDiscount != null) {
            showDiscountRow(mAmount);
        }
    }

    public void showDiscountRow(BigDecimal transactionAmount) {
        DiscountRowView discountRowView = new MercadoPagoComponents.Views.DiscountRowViewBuilder()
                .setContext(this)
                .setDiscount(mDiscount)
                .setTransactionAmount(transactionAmount)
                .setCurrencyId(mCurrencyId)
                .setShowArrow(false)
                .setShowSeparator(false)
                .build();

        mDiscountFrameLayout.setVisibility(View.VISIBLE);
        discountRowView.inflateInParent(mDiscountFrameLayout, true);
        discountRowView.initializeControls();
        discountRowView.draw();
    }

    private void setTotalAmountDescription() {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        sb.append(CurrenciesUtil.formatNumber(mTotalAmount, mCurrencyId));

        sb.append(")");
        Spanned spannedFullAmountText = CurrenciesUtil.formatCurrencyInText(mTotalAmount,
                mCurrencyId, sb.toString(), false, true);

        mInstallmentsTotalAmountDescription.setVisibility(View.VISIBLE);
        mInstallmentsTotalAmountDescription.setText(spannedFullAmountText);
    }

    private boolean hasInterests() {

        return mInstallments > 1 && !mInstallmentsRate.equals(BigDecimal.ZERO);
    }

    private void showAmount() {
        if (mPaymentTypeId == null) {
            hideAmount();
        } else {
            setDiscountRow();
            if (MercadoPagoUtil.isCard(mPaymentTypeId)) {
                setInstallmentsDescription();
                mAmountDescription.setVisibility(View.GONE);
            } else if (mPaymentTypeId.equals(PaymentTypes.ACCOUNT_MONEY)) {
                StringBuffer sb = new StringBuffer();
                if (mDiscount != null) {
                    mTotalAmount = mDiscount.getAmountWithDiscount(mTotalAmount);
                }
                sb.append(CurrenciesUtil.formatNumber(mTotalAmount, mCurrencyId));
                mAmountDescription.setText(CurrenciesUtil.formatCurrencyInText(mTotalAmount,
                        mCurrencyId, sb.toString(), false, true));
                mAmountDescription.setVisibility(View.VISIBLE);
                mInstallmentsDescription.setVisibility(View.GONE);
                mInterestAmountDescription.setVisibility(View.GONE);
                mInstallmentsTotalAmountDescription.setVisibility(View.GONE);
            }
        }
    }

    private void hideAmount() {
        mInstallmentsDescription.setVisibility(View.GONE);
        mInterestAmountDescription.setVisibility(View.GONE);
        mInstallmentsTotalAmountDescription.setVisibility(View.GONE);
        mAmountDescription.setVisibility(View.GONE);
    }

    private void showPaymentMethod() {
        if (mPaymentTypeId != null && MercadoPagoUtil.isCard(mPaymentTypeId) && isLastFourDigitsValid() &&
                isPaymentMethodValid()) {
            setPaymentMethodImage();
            String message = getString(R.string.mpsdk_last_digits_label) + " " + mLastFourDigits;
            mPaymentMethodDescription.setText(message);
        } else if (mPaymentTypeId != null && mPaymentTypeId.equals(PaymentTypes.ACCOUNT_MONEY)) {
            mPaymentMethodImage.setImageResource(R.drawable.mpsdk_mercadopago);
            mPaymentMethodImage.setVisibility(View.VISIBLE);
            mPaymentMethodDescription.setText(getResources().getString(R.string.mpsdk_account_money_description));
        } else {
            hidePaymentMethod();
        }
    }

    private void hidePaymentMethod() {
        mPaymentMethodDescription.setVisibility(View.GONE);
        mPaymentMethodImage.setVisibility(View.GONE);
    }

    private void setInstallmentsDescription() {
        if (isInstallmentQuantityValid() && isInstallmentAmountValid() && isTotalPaidAmountValid() && CurrenciesUtil.isValidCurrency(mCurrencyId)) {
            if (mInstallments > 1) {
                mInstallmentsDescription.setText(getInstallmentsText());
                setInterestAmountDescription();
            } else {
                //Installments quantity 0 or 1
                StringBuilder sb = new StringBuilder();

                sb.append(CurrenciesUtil.formatNumber(mTotalAmount, mCurrencyId));
                Spanned spannedInstallmentsText = CurrenciesUtil.formatCurrencyInText(mTotalAmount,
                        mCurrencyId, sb.toString(), false, true);

                mInstallmentsTotalAmountDescription.setVisibility(View.GONE);
                mInstallmentsDescription.setText(spannedInstallmentsText);
                mInterestAmountDescription.setVisibility(View.GONE);
            }
        } else {
            hideAmount();
        }
    }

    private void setDisplayTime() {
        if (mCongratsDisplay > 0) {
            startCountdown(mCongratsDisplay);
        }
    }

    private void startCountdown(Integer seconds) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finishWithOkResult();
            }
        }, seconds * 1000);
    }

    private void setPaymentMethodImage() {
        int resourceId = MercadoPagoUtil.getPaymentMethodIcon(this, mPaymentMethodId);
        if (resourceId != 0) {
            mPaymentMethodImage.setImageResource(resourceId);
        } else {
            mPaymentMethodImage.setVisibility(View.GONE);
        }
    }

    private void setPaymentEmailDescription() {
        if (isPayerEmailValid() && mServicePreference.shouldShowEmailConfirmationCell()) {
            String subtitle = String.format(getString(R.string.mpsdk_subtitle_action_activity_congrats), mPayerEmail);
            mPayerEmailTextView.setText(subtitle);
        } else {
            mPayerEmailTextView.setVisibility(View.GONE);
            mTopEmailSeparator.setVisibility(View.GONE);
            mBottomEmailSeparator.setVisibility(View.GONE);
        }
    }

    private Boolean isPaymentIdValid() {
        return mPaymentId != null && mPaymentId >= 0;
    }

    private Boolean isTotalPaidAmountValid() {
        return mTotalAmount != null && (mTotalAmount.compareTo(BigDecimal.ZERO)) > 0;
    }

    private Boolean isInstallmentAmountValid() {
        return mInstallmentsAmount != null && mInstallmentsAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    private Boolean isInstallmentQuantityValid() {
        return mInstallments >= 0;
    }

    private Boolean isPayerEmailValid() {
        return !isEmpty(mPayerEmail);
    }

    private Boolean isPaymentMethodValid() {
        return isPaymentMethodIdValid() && !isEmpty(mPaymentMethodName);
    }

    private Boolean isPaymentMethodIdValid() {
        return !isEmpty(mPaymentMethodId);
    }

    private Boolean isLastFourDigitsValid() {
        return !isEmpty(mLastFourDigits);
    }

    private Spanned getInstallmentsText() {
        StringBuffer sb = new StringBuffer();
        sb.append(mInstallments);
        sb.append(" ");
        sb.append(getString(R.string.mpsdk_installments_by));
        sb.append(" ");
        sb.append(CurrenciesUtil.formatNumber(mInstallmentsAmount, mCurrencyId));
        return CurrenciesUtil.formatCurrencyInText(mInstallmentsAmount,
                mCurrencyId, sb.toString(), false, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedOnce) {
            finishWithOkResult();
        } else {
            Snackbar.make(mExitButtonText, getString(R.string.mpsdk_press_again_to_leave), Snackbar.LENGTH_LONG).show();
            mBackPressedOnce = true;
            resetBackPressedOnceIn(4000);
        }
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

    public Discount getDiscount() {
        return mDiscount;
    }
}
