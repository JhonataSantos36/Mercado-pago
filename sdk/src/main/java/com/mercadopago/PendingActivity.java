package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.adapters.ReviewablesAdapter;
import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.controllers.CustomReviewablesHandler;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.PaymentResultAction;
import com.mercadopago.model.ReviewSubscriber;
import com.mercadopago.model.Reviewable;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.preferences.PaymentResultScreenPreference;
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

    //Params
    protected String mMerchantPublicKey;
    protected PaymentResult mPaymentResult;
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
        MPTracker.getInstance().trackScreen("RESULT_PENDING", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);
        setContentView(R.layout.mpsdk_activity_pending);
    }

    protected void initializeControls() {
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
                finishWithOkResult(true);
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
        setPaymentResultScreenPreferenceData();
        setPaymentResultScreenWithoutPreferenceData();
        showTimer();
        showReviewables();
    }

    private void initializePaymentData() {
        mPaymentStatus = mPaymentResult.getPaymentStatus();
        mPaymentStatusDetail = mPaymentResult.getPaymentStatusDetail();
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
            if (mPaymentResultScreenPreference.getPendingIconName() != null) {
                Drawable image = ContextCompat.getDrawable(this, mPaymentResultScreenPreference.getPendingIconName());
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
        }
    }

    private void setSecondaryExitButtonListener() {
        mSecondaryExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Deprecate
                if(CallbackHolder.getInstance().hasPaymentResultCallback(CallbackHolder.PENDING_PAYMENT_RESULT_CALLBACK)) {
                    CallbackHolder.getInstance().getPaymentResultCallback(CallbackHolder.PENDING_PAYMENT_RESULT_CALLBACK).onResult(mPaymentResult);
                    finishWithOkResult(false);
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
    public void changeRequired(Reviewable reviewable) {
        if (reviewable.getPaymentResultReviewableCallback() != null) {
            reviewable.getPaymentResultReviewableCallback().onChangeRequired(mPaymentResult);
        }
        finishWithOkResult(false);
    }

    @Override
    public void changeRequired(Integer resultCode) {
        Intent intent = new Intent();
        intent.putExtra("resultCode", resultCode);
        intent.putExtra("paymentResult", JsonUtil.getInstance().toJson(mPaymentResult));
        setResult(RESULT_OK, intent);
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
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), errorMessage, false);
    }

    private Boolean isStatusDetailValid() {
        return !isEmpty(mPaymentStatusDetail);
    }

    private void finishWithOkResult(boolean notifyOk) {
        Intent returnIntent = new Intent();
        int resultCode = notifyOk ? RESULT_OK : PaymentResultActivity.RESULT_SILENT_OK;
        setResult(resultCode, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("PENDING", "BACK_PRESSED", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

        if (mBackPressedOnce) {
            finishWithOkResult(true);
        } else {
            Snackbar.make(mExitButtonText, getString(R.string.mpsdk_press_again_to_leave), Snackbar.LENGTH_LONG).show();
            mBackPressedOnce = true;
            resetBackPressedOnceIn(4000);
        }
    }

    public void onClickPendingOptionButton(View view) {
        MPTracker.getInstance().trackEvent("PENDING", "SELECT_OTHER_PAYMENT_METHOD", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("nextAction", PaymentResultAction.SELECT_OTHER_PAYMENT_METHOD);
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
        this.finish();
    }
}
