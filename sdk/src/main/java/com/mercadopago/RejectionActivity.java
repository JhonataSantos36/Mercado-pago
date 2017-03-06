package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.PaymentResultAction;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;

import static android.text.TextUtils.isEmpty;

public class RejectionActivity extends MercadoPagoBaseActivity implements TimerObserver {

    //Controls
    protected MPTextView mTimerTextView;
    protected MPTextView mRejectionTitle;
    protected MPTextView mRejectionSubtitle;
    protected MPTextView mRejectionContentTitle;
    protected MPTextView mRejectionContentText;
    protected MPTextView mExitButton;
    protected MPTextView mChangePaymentMethodButtonText;
    protected FrameLayout mChangePaymentMethodButton;
    protected ImageView mHeaderIcon;
    protected MPTextView mIconSubtext;
    protected FrameLayout mSecondaryExitButton;
    protected MPTextView mSecondaryExitTextView;
    //Params
    protected String mMerchantPublicKey;
    protected PaymentResult mPaymentResult;
    protected String mPaymentMethodName;
    protected String mPaymentStatusDetail;
    protected String mPaymentTypeId;
    protected Long mPaymentId;
    protected String mPaymentMethodId;
    protected Activity mActivity;
    protected PaymentResultScreenPreference mPaymentResultScreenPreference;


    //Local values
    private boolean mBackPressedOnce;
    private String mNextAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityParameters();
//        if (mDecorationPreference != null) {
//            mDecorationPreference.activateFont(this);
//            if (mDecorationPreference.hasColors()) {
//                setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
//            }
//        }
        setContentView();
        initializeControls();
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
        if (isStatusDetailMandatory() && mPaymentResult.getPaymentStatusDetail() == null) {
            throw new IllegalStateException("payment status detail not set");
        }
    }

    private boolean isStatusDetailMandatory() {
        return mPaymentResultScreenPreference == null || mPaymentResultScreenPreference.getRejectedTitle() == null;
    }

    protected void setContentView() {
        MPTracker.getInstance().trackScreen("RESULT", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);
        setContentView(R.layout.mpsdk_activity_rejection);
    }

    protected void initializeControls() {
        mRejectionTitle = (MPTextView) findViewById(R.id.mpsdkRejectionTitle);
        mRejectionSubtitle = (MPTextView) findViewById(R.id.mpsdkRejectionSubtitle);
        mRejectionContentTitle = (MPTextView) findViewById(R.id.mpsdkContentTitle);
        mRejectionContentText = (MPTextView) findViewById(R.id.mpsdkContentText);
        mChangePaymentMethodButtonText = (MPTextView) findViewById(R.id.mpsdkRejectionOptionButtonText);
        mChangePaymentMethodButton = (FrameLayout) findViewById(R.id.mpsdkChangePaymentMethodButton);
        mExitButton = (MPTextView) findViewById(R.id.mpsdkExitButtonRejection);
        mHeaderIcon = (ImageView) findViewById(R.id.mpsdkIcon);
        mIconSubtext = (MPTextView) findViewById(R.id.mpsdkIconSubtext);
        mSecondaryExitButton = (FrameLayout) findViewById(R.id.mpsdkRejectedSecondaryExitButton);
        mSecondaryExitTextView = (MPTextView) findViewById(R.id.mpsdkRejectedSecondaryExitButtonText);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult(true);
            }
        });

        mTimerTextView = (MPTextView) findViewById(R.id.mpsdkTimerTextView);
    }

    protected void onValidStart() {
        initializePaymentData();
        setPaymentResultScreenPreferenceData();
        setPaymentResultScreenWithoutPreferenceData();
        showTimer();
    }

    private void initializePaymentData() {
        mPaymentId = mPaymentResult.getPaymentId();
        mPaymentStatusDetail = mPaymentResult.getPaymentStatusDetail();
        PaymentData paymentData = mPaymentResult.getPaymentData();
        if (paymentData != null) {
            if (paymentData.getPaymentMethod() != null) {
                mPaymentMethodName = paymentData.getPaymentMethod().getName();
                mPaymentTypeId = paymentData.getPaymentMethod().getPaymentTypeId();
                mPaymentMethodId = paymentData.getPaymentMethod().getId();
            }
        }
    }

    private void setPaymentResultScreenPreferenceData() {
        if (mPaymentResultScreenPreference != null) {
            if (mPaymentResultScreenPreference.getRejectedTitle() == null) {
                setDefaultRejectedTitle();
            } else {
                mRejectionTitle.setText(mPaymentResultScreenPreference.getRejectedTitle());
            }
            if (mPaymentResultScreenPreference.getRejectedSubtitle() == null) {
                setDefaultRejectedSubtitle();
            } else {
                mRejectionSubtitle.setText(mPaymentResultScreenPreference.getRejectedSubtitle());
                mRejectionSubtitle.setVisibility(View.VISIBLE);
            }
            if (!mPaymentResultScreenPreference.isRejectedContentTitleEnabled()) {
                hideContentTitle();
            } else if (mPaymentResultScreenPreference.getRejectedContentTitle() == null) {
                setDefaultRejectedContentTitle();
            } else {
                mRejectionContentTitle.setText(mPaymentResultScreenPreference.getRejectedContentTitle());
            }
            if (!mPaymentResultScreenPreference.isRejectedContentTextEnabled()) {
                hideContentText();
            } else if (mPaymentResultScreenPreference.getRejectedContentText() == null) {
                setDefaultRejectedContentText();
            } else {
                mRejectionContentText.setText(mPaymentResultScreenPreference.getRejectedContentText());
            }
            if (mPaymentResultScreenPreference.isRejectedIconSubtextEnabled()) {
                if (mPaymentResultScreenPreference.getRejectedIconSubtext() == null) {
                    setDefaultRejectedIconSubtext();
                } else {
                    mIconSubtext.setText(mPaymentResultScreenPreference.getRejectedIconSubtext());
                }
            } else {
                hideIconSubtext();
            }
            if (mPaymentResultScreenPreference.getRejectedIconName() != null) {
                Drawable image = ContextCompat.getDrawable(this, mPaymentResultScreenPreference.getRejectedIconName());

                mHeaderIcon.setImageDrawable(image);

            }
            if (mPaymentResultScreenPreference.getExitButtonTitle() == null) {
                setDefaultExitButtonText();
            } else {
                mExitButton.setText(mPaymentResultScreenPreference.getExitButtonTitle());
            }
            if (!mPaymentResultScreenPreference.isRejectedSecondaryExitButtonEnabled() ||
                    mPaymentResultScreenPreference.getSecondaryRejectedExitButtonTitle() == null ||
                    (mPaymentResultScreenPreference.getSecondaryRejectedExitResultCode() == null
                            && !CallbackHolder.getInstance().hasPaymentResultCallback(CallbackHolder.REJECTED_PAYMENT_RESULT_CALLBACK))) {
                hideChangePaymentMethodButton();
                hideSecondaryExitButton();
            } else {
                hideChangePaymentMethodButton();
                mSecondaryExitTextView.setText(mPaymentResultScreenPreference.getSecondaryRejectedExitButtonTitle());
                mSecondaryExitButton.setVisibility(View.VISIBLE);
                mSecondaryExitTextView.setVisibility(View.VISIBLE);
                setSecondaryExitButtonListener();
            }
        }
    }

    private void setPaymentResultScreenWithoutPreferenceData() {
        if (mPaymentResultScreenPreference == null) {
            setDefaultRejectedTitle();
            setDefaultRejectedSubtitle();
            setDefaultRejectedContentTitle();
            setDefaultRejectedContentText();
            setDefaultExitButtonText();
            setDefaultRejectedIconSubtext();
            setDefaultSecondaryButton();
        }
    }

    private void setDefaultSecondaryButton() {
        mChangePaymentMethodButton.setVisibility(View.VISIBLE);
    }

    private void setSecondaryExitButtonListener() {
        mSecondaryExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO Deprecate
                if (CallbackHolder.getInstance().hasPaymentResultCallback(CallbackHolder.REJECTED_PAYMENT_RESULT_CALLBACK)) {
                    CallbackHolder.getInstance().getPaymentResultCallback(CallbackHolder.REJECTED_PAYMENT_RESULT_CALLBACK).onResult(mPaymentResult);
                    finishWithOkResult(false);
                } else {
                    finishWithResult(mPaymentResultScreenPreference.getSecondaryRejectedExitResultCode());
                }
            }
        });
    }

    private void hideChangePaymentMethodButton() {
        mChangePaymentMethodButton.setVisibility(View.GONE);
    }

    private void hideSecondaryExitButton() {
        mSecondaryExitButton.setVisibility(View.GONE);
        mSecondaryExitTextView.setVisibility(View.GONE);
    }

    private void setDefaultRejectedTitle() {
        if (isStatusDetailValid() && isPaymentMethodValid()) {
            if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_OTHER_REASON)) {
                String titleMessage = String.format(getString(R.string.mpsdk_title_other_reason_rejection), mPaymentMethodName);
                mRejectionTitle.setText(titleMessage);
            } else if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT)) {
                String titleMessage = String.format(getString(R.string.mpsdk_text_insufficient_amount), mPaymentMethodName);
                mRejectionTitle.setText(titleMessage);
            } else if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT)) {
                String titleMessage = String.format(getString(R.string.mpsdk_title_other_reason_rejection), mPaymentMethodName);
                mRejectionTitle.setText(titleMessage);
            } else if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED)) {
                String titleMessage = String.format(getString(R.string.mpsdk_text_active_card), mPaymentMethodName);
                mRejectionTitle.setText(titleMessage);
            } else if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_REJECTED_HIGH_RISK)) {
                mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_high_risk));
            } else if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS)) {
                mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_max_attempts));
            } else if (isPaymentStatusDetailRecoverable()) {
                mRejectionTitle.setText(String.format(getString(R.string.mpsdk_text_some_card_data_is_incorrect), mPaymentMethodName));
                setBadFillTextButtons();
            } else {
                mRejectionTitle.setText(R.string.mpsdk_title_bad_filled_other);
            }
        } else {
            ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), false);
        }
    }

    private void setDefaultRejectedSubtitle() {
        mRejectionSubtitle.setVisibility(View.GONE);
    }

    private void setDefaultRejectedContentTitle() {
        mRejectionContentTitle.setText(getResources().getString(R.string.mpsdk_what_can_do));
    }

    private void hideContentText() {
        mRejectionContentText.setVisibility(View.GONE);
    }

    private void hideIconSubtext() {
        mIconSubtext.setVisibility(View.GONE);
    }

    private void hideContentTitle() {
        mRejectionContentTitle.setVisibility(View.GONE);
    }

    private void setDefaultRejectedContentText() {
        if (isStatusDetailValid() && isPaymentMethodValid()) {
            if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_OTHER_REASON)) {
                mRejectionContentText.setText(getString(R.string.mpsdk_text_select_other_rejection));
            } else if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT)) {
                if (isCardPaymentTypeCreditCard()) {
                    mRejectionContentText.setText(getString(R.string.mpsdk_subtitle_rejection_insufficient_amount_credit_card));
                } else {
                    mRejectionContentText.setText(getString(R.string.mpsdk_subtitle_rejection_insufficient_amount));
                }
            } else if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT)) {
                mRejectionContentText.setText(getString(R.string.mpsdk_subtitle_rejection_duplicated_payment));
            } else if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED)) {
                mRejectionContentText.setText(getString(R.string.mpsdk_subtitle_rejection_card_disabled));
            } else if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_REJECTED_HIGH_RISK)) {
                mRejectionContentText.setText(getString(R.string.mpsdk_subtitle_rejection_high_risk));
            } else if (mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS)) {
                mRejectionContentText.setText(getString(R.string.mpsdk_subtitle_rejection_max_attempts));
            } else if (isPaymentStatusDetailRecoverable()) {
                mRejectionContentText.setVisibility(View.GONE);
            } else {
                mRejectionContentText.setVisibility(View.GONE);
            }
        } else {
            hideContentText();
        }
    }

    private void setDefaultRejectedIconSubtext() {
        mIconSubtext.setText(getResources().getString(R.string.mpsdk_rejection_title));
    }

    private void setDefaultExitButtonText() {
        mExitButton.setText(getResources().getString(R.string.mpsdk_text_continue));
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

    private Boolean isPaymentMethodValid() {
        return isPaymentMethodIdValid() && !isEmpty(mPaymentMethodName) && !isEmpty(mPaymentTypeId);
    }

    private Boolean isPaymentMethodIdValid() {
        return !isEmpty(mPaymentMethodId);
    }

    private void finishWithOkResult(boolean notifyOk) {
        Intent returnIntent = new Intent();
        int resultCode = notifyOk ? RESULT_OK : PaymentResultActivity.RESULT_SILENT_OK;
        setResult(resultCode, returnIntent);
        finish();
    }

    private void finishWithResult(Integer resultCode) {
        Intent intent = new Intent();
        intent.putExtra("resultCode", resultCode);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean isCardPaymentTypeCreditCard() {
        return !mPaymentTypeId.isEmpty() && mPaymentTypeId.equals("credit_card");
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("REJECTION", "BACK_PRESSED", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

        if (mBackPressedOnce) {
            finishWithOkResult(true);
        } else {
            Snackbar.make(mExitButton, getString(R.string.mpsdk_press_again_to_leave), Snackbar.LENGTH_LONG).show();
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

    private void setBadFillTextButtons() {
        mChangePaymentMethodButtonText.setText(getString(R.string.mpsdk_text_enter_again));
        mExitButton.setText(getString(R.string.mpsdk_text_cancel_payment_and_continue));
    }

    public void onClickRejectionOptionButton(View view) {
        if (isPaymentStatusDetailRecoverable()) {
            MPTracker.getInstance().trackEvent("REJECTION", "RECOVER_PAYMENT", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

            Intent returnIntent = new Intent();
            mNextAction = PaymentResultAction.RECOVER_PAYMENT;
            returnIntent.putExtra("nextAction", mNextAction);
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        } else {
            MPTracker.getInstance().trackEvent("REJECTION", "SELECT_OTHER_PAYMENT_METHOD", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

            Intent returnIntent = new Intent();
            mNextAction = PaymentResultAction.SELECT_OTHER_PAYMENT_METHOD;
            returnIntent.putExtra("nextAction", mNextAction);
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    private Boolean isPaymentStatusDetailRecoverable() {
        return mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER) ||
                mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER) ||
                mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE) ||
                mPaymentStatusDetail.equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE);
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
