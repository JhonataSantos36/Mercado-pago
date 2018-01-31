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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.adapters.ReviewablesAdapter;
import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.controllers.CustomReviewablesHandler;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.ReviewSubscriber;
import com.mercadopago.model.Reviewable;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.tracker.FlowHandler;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.utils.TrackingUtil;
import com.mercadopago.util.ColorsUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;

import java.util.List;

import static android.text.TextUtils.isEmpty;

public class PendingActivity extends MercadoPagoBaseActivity implements TimerObserver, ReviewSubscriber {

    //Controls
    protected MPTextView mTimerTextView;
    protected MPTextView mPendingTitle;
    protected MPTextView mPendingSubtitle;
    protected MPTextView mExitButtonText;
    protected MPTextView mContentTitleTextView;
    protected MPTextView mContentTextView;
    protected FrameLayout mChangePaymentMethodButton;
    protected ImageView mHeaderIcon;
    protected RecyclerView mReviewables;
    protected FrameLayout mSecondaryExitButton;
    protected MPTextView mSecondaryExitTextView;
    protected ViewGroup mTitleBackground;

    //Params
    protected String mMerchantPublicKey;
    protected PaymentResult mPaymentResult;
    protected PaymentData mPaymentData;
    protected PaymentResultScreenPreference mPaymentResultScreenPreference;

    protected String mPaymentStatus;
    protected String mPaymentStatusDetail;
    protected Activity mActivity;

    //Local values
    private boolean mBackPressedOnce;

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
        mPaymentResult = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentResult"), PaymentResult.class);
        mPaymentResultScreenPreference = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentResultScreenPreference"), PaymentResultScreenPreference.class);
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
        setContentView(R.layout.mpsdk_activity_pending);
    }

    protected void initializeControls() {
        mTitleBackground = (ViewGroup) findViewById(R.id.mpsdkTitleBackground);
        mPendingTitle = (MPTextView) findViewById(R.id.mpsdkPendingTitle);
        mPendingSubtitle = (MPTextView) findViewById(R.id.mpsdkPendingSubtitle);
        mExitButtonText = (MPTextView) findViewById(R.id.mpsdkExitButtonPending);
        mContentTitleTextView = (MPTextView) findViewById(R.id.mpsdkContentTitle);
        mContentTextView = (MPTextView) findViewById(R.id.mpsdkContentText);
        mChangePaymentMethodButton = (FrameLayout) findViewById(R.id.mpsdkPendingOptionButton);
        mHeaderIcon = (ImageView) findViewById(R.id.mpsdkHeaderIcon);
        mTimerTextView = (MPTextView) findViewById(R.id.mpsdkTimerTextView);
        mReviewables = (RecyclerView) findViewById(R.id.mpsdkReviewablesRecyclerView);
        mSecondaryExitButton = (FrameLayout) findViewById(R.id.mpsdkPendingSecondaryExitButton);
        mSecondaryExitTextView = (MPTextView) findViewById(R.id.mpsdkPendingSecondaryExitButtonText);
        mExitButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult();
            }
        });
        mChangePaymentMethodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPendingOptionButton();
            }
        });
    }

    private void initializeReviewablesRecyclerView() {
        mReviewables.setNestedScrollingEnabled(false);
        mReviewables.setLayoutManager(new LinearLayoutManager(this));
        mReviewables.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
    }

    protected void onValidStart() {
        initializePaymentData();
        trackScreen();
        setPaymentResultScreenPreferenceData();
        setPaymentResultScreenWithoutPreferenceData();
        showTimer();
        showReviewables();
    }

    private void initializePaymentData() {
        mPaymentStatus = mPaymentResult.getPaymentStatus();
        mPaymentStatusDetail = mPaymentResult.getPaymentStatusDetail();
        mPaymentData = mPaymentResult.getPaymentData();
    }

    protected void trackScreen() {
        MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(this, mMerchantPublicKey)
                .setCheckoutVersion(BuildConfig.VERSION_NAME)
                .build();


        ScreenViewEvent.Builder builder = new ScreenViewEvent.Builder()
                .setFlowId(FlowHandler.getInstance().getFlowId())
                .setScreenId(TrackingUtil.SCREEN_ID_PAYMENT_RESULT_PENDING)
                .setScreenName(TrackingUtil.SCREEN_NAME_PAYMENT_RESULT_PENDING)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_IS_EXPRESS, TrackingUtil.IS_EXPRESS_DEFAULT_VALUE)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_STATUS, mPaymentStatus)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_STATUS_DETAIL, mPaymentStatusDetail)
                .addMetaData(TrackingUtil.METADATA_PAYMENT_ID, String.valueOf(mPaymentResult.getPaymentId()));

        if (mPaymentData != null && mPaymentData.getPaymentMethod() != null) {
            if (mPaymentData.getPaymentMethod().getPaymentTypeId() != null) {
                builder.addMetaData(TrackingUtil.METADATA_PAYMENT_TYPE_ID, mPaymentData.getPaymentMethod().getPaymentTypeId());
            }
            if (mPaymentData.getPaymentMethod().getId() != null) {
                builder.addMetaData(TrackingUtil.METADATA_PAYMENT_METHOD_ID, mPaymentData.getPaymentMethod().getId());
            }
        }
        if (mPaymentData != null && mPaymentData.getIssuer() != null && mPaymentData.getIssuer().getId() != null) {
            builder.addMetaData(TrackingUtil.METADATA_ISSUER_ID, String.valueOf(mPaymentData.getIssuer().getId()));
        }

        ScreenViewEvent event = builder.build();
        mpTrackingContext.trackEvent(event);
    }

    private void setPaymentResultScreenPreferenceData() {
        if (mPaymentResultScreenPreference != null) {
            if (mPaymentResultScreenPreference.getPendingTitle() == null) {
                setDefaultPendingTitle();
            } else {
                mPendingTitle.setText(mPaymentResultScreenPreference.getPendingTitle());
            }
            if (mPaymentResultScreenPreference.getPendingSubtitle() == null) {
                setDefaultPendingSubtitle();
            } else {
                mPendingSubtitle.setText(mPaymentResultScreenPreference.getPendingSubtitle());
                mPendingSubtitle.setVisibility(View.VISIBLE);
            }
            if (mPaymentResultScreenPreference.getExitButtonTitle() == null) {
                setDefaultExitButtonText();
            } else {
                mExitButtonText.setText(mPaymentResultScreenPreference.getExitButtonTitle());
            }
            if (mPaymentResultScreenPreference.isPendingContentTitleEnabled()) {
                if (mPaymentResultScreenPreference.getPendingContentTitle() == null) {
                    setDefaultPendingContentTitle();
                } else {
                    mContentTitleTextView.setText(mPaymentResultScreenPreference.getPendingContentTitle());
                }
            } else {
                hidePendingContentTitle();
            }
            if (mPaymentResultScreenPreference.isPendingContentTextEnabled()) {
                if (mPaymentResultScreenPreference.getPendingContentText() == null) {
                    setDefaultPendingContentText();
                } else {
                    mContentTextView.setText(mPaymentResultScreenPreference.getPendingContentText());
                }
            } else {
                hidePendingContentText();
            }
            if (mPaymentResultScreenPreference.getPendingIcon() != null) {
                Drawable image = ContextCompat.getDrawable(this, mPaymentResultScreenPreference.getPendingIcon());
                mHeaderIcon.setImageDrawable(image);
            }
            if (!mPaymentResultScreenPreference.isPendingSecondaryExitButtonEnabled() ||
                    mPaymentResultScreenPreference.getSecondaryPendingExitButtonTitle() == null ||
                    (mPaymentResultScreenPreference.getSecondaryPendingExitResultCode() == null
                            && !CallbackHolder.getInstance().hasPaymentResultCallback(CallbackHolder.PENDING_PAYMENT_RESULT_CALLBACK))) {
                hideChangePaymentMethodButton();
                hideSecondaryExitButton();
            } else {
                hideChangePaymentMethodButton();
                mSecondaryExitTextView.setText(mPaymentResultScreenPreference.getSecondaryPendingExitButtonTitle());
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

    private void setSecondaryExitButtonListener() {
        mSecondaryExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Deprecate
                if (CallbackHolder.getInstance().hasPaymentResultCallback(CallbackHolder.PENDING_PAYMENT_RESULT_CALLBACK)) {
                    CallbackHolder.getInstance().getPaymentResultCallback(CallbackHolder.PENDING_PAYMENT_RESULT_CALLBACK).onResult(mPaymentResult);
                    finishWithOkResult();
                } else {
                    finishWithResult(mPaymentResultScreenPreference.getSecondaryPendingExitResultCode());
                }
            }
        });
    }

    private void finishWithResult(Integer resultCode) {
        Intent intent = new Intent();
        intent.putExtra("resultCode", resultCode);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setPaymentResultScreenWithoutPreferenceData() {
        if (mPaymentResultScreenPreference == null) {
            setDefaultPendingTitle();
            setDefaultPendingSubtitle();
            setDefaultExitButtonText();
            setDefaultPendingContentTitle();
            setDefaultPendingContentText();
            hideSecondaryExitButton();
        }
    }

    private void hideSecondaryExitButton() {
        mSecondaryExitButton.setVisibility(View.GONE);
        mSecondaryExitTextView.setVisibility(View.GONE);
    }

    private void hideChangePaymentMethodButton() {
        mChangePaymentMethodButton.setVisibility(View.GONE);
    }

    private void setDefaultPendingTitle() {
        mPendingTitle.setText(getResources().getString(R.string.mpsdk_title_pending));
    }

    private void setDefaultPendingSubtitle() {
        mPendingSubtitle.setVisibility(View.GONE);
    }

    private void setDefaultExitButtonText() {
        mExitButtonText.setText(getResources().getString(R.string.mpsdk_text_continue));
    }

    private void setDefaultPendingContentTitle() {
        mContentTitleTextView.setText(getResources().getString(R.string.mpsdk_what_can_do));
    }

    private void setDefaultPendingContentText() {
        if (isStatusDetailValid()) {
            if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_CONTINGENCY)) {
                mContentTextView.setText(getString(R.string.mpsdk_subtitle_pending_contingency));
            } else if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_REVIEW_MANUAL)) {
                mContentTextView.setText(getString(R.string.mpsdk_subtitle_pending_review_manual));
            }
        } else {
            hidePendingContentText();
        }
    }

    public void showReviewables() {
        List<Reviewable> customReviewables = retrieveCustomReviewables();
        ReviewablesAdapter reviewablesAdapter = new ReviewablesAdapter(customReviewables);
        mReviewables.setAdapter(reviewablesAdapter);
    }

    private List<Reviewable> retrieveCustomReviewables() {
        List<Reviewable> customReviewables = CustomReviewablesHandler.getInstance().getPendingReviewables();

        for (Reviewable reviewable : customReviewables) {
            reviewable.setReviewSubscriber(this);
        }

        return customReviewables;
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


    private void hidePendingContentText() {
        mContentTextView.setVisibility(View.GONE);
    }

    private void hidePendingContentTitle() {
        mContentTitleTextView.setVisibility(View.GONE);
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            CheckoutTimer.getInstance().addObserver(this);
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    protected void onInvalidStart(String errorMessage) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), errorMessage, false, mMerchantPublicKey);
    }

    private Boolean isStatusDetailValid() {
        return !isEmpty(mPaymentStatusDetail);
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

    public void onClickPendingOptionButton() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("nextAction", PaymentResult.SELECT_OTHER_PAYMENT_METHOD);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            setResult(RESULT_CANCELED, data);
            finish();
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
}
