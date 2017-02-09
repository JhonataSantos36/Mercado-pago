package com.mercadopago;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;

import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentResultAction;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;

import static android.text.TextUtils.isEmpty;

public class RejectionActivity extends MercadoPagoActivity implements TimerObserver {

    //Controls
    protected MPTextView mTimerTextView;
    protected MPTextView mRejectionTitle;
    protected MPTextView mRejectionSubtitle;
    protected MPTextView mKeepBuyingButton;
    protected MPTextView mRejectionOptionButtonText;
    protected FrameLayout mRejectionOptionButton;

    //Params
    protected Payment mPayment;
    protected PaymentMethod mPaymentMethod;
    protected String mMerchantPublicKey;

    //Local values
    private boolean mBackPressedOnce;
    private String mNextAction;

    @Override
    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPayment = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("payment"), Payment.class);
        mPaymentMethod = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentMethod"), PaymentMethod.class);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mMerchantPublicKey == null) {
            throw new IllegalStateException("merchant public key not set");
        }
        if (mPayment == null) {
            throw new IllegalStateException("payment not set");
        }
        if (mPaymentMethod == null) {
            throw new IllegalStateException("payment method not set");
        }
    }

    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("REJECTED", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, getActivity());
        setContentView(R.layout.mpsdk_activity_rejection);
    }

    @Override
    protected void initializeControls() {
        mRejectionTitle = (MPTextView) findViewById(R.id.mpsdkRejectionTitle);
        mRejectionSubtitle = (MPTextView) findViewById(R.id.mpsdkRejectionSubtitle);
        mRejectionOptionButtonText = (MPTextView) findViewById(R.id.mpsdkRejectionOptionButtonText);
        mRejectionOptionButton = (FrameLayout) findViewById(R.id.mpsdkRejectionOptionButton);
        mKeepBuyingButton = (MPTextView) findViewById(R.id.mpsdkKeepBuyingRejection);
        mKeepBuyingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult();
            }
        });

        mTimerTextView = (MPTextView) findViewById(R.id.mpsdkTimerTextView);
    }

    @Override
    protected void onValidStart() {
        showTimer();
        loadTitles();
    }

    private void loadTitles() {
        if (isStatusDetailValid() && isPaymentMethodValid()) {
            if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_OTHER_REASON)) {
                String titleMessage = String.format(getString(R.string.mpsdk_title_other_reason_rejection), mPaymentMethod.getName());
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_text_select_other_rejection));
            } else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT)) {
                String titleMessage = String.format(getString(R.string.mpsdk_text_insufficient_amount), mPaymentMethod.getName());
                mRejectionTitle.setText(titleMessage);

                if (isCardPaymentTypeCreditCard()) {
                    mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_insufficient_amount_credit_card));
                } else {
                    mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_insufficient_amount));
                }
            } else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT)) {
                String titleMessage = String.format(getString(R.string.mpsdk_title_other_reason_rejection), mPaymentMethod.getName());
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_duplicated_payment));
            } else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED)) {
                String titleMessage = String.format(getString(R.string.mpsdk_text_active_card), mPaymentMethod.getName());
                mRejectionTitle.setText(titleMessage);
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_card_disabled));
            } else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_REJECTED_HIGH_RISK)) {
                mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_high_risk));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_high_risk));
            } else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS)) {
                mRejectionTitle.setText(getString(R.string.mpsdk_title_rejection_max_attempts));
                mRejectionSubtitle.setText(getString(R.string.mpsdk_subtitle_rejection_max_attempts));
            } else if (isPaymentStatusDetailRecoverable()) {
                mRejectionTitle.setText(getBadFillTitle());
                setBadFillTextButtons();
            } else {
                mRejectionTitle.setText(R.string.mpsdk_title_bad_filled_other);
                mRejectionSubtitle.setVisibility(View.GONE);
            }
        } else {
            ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), false);
        }
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            CheckoutTimer.getInstance().addObserver(this);
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    private String getBadFillTitle() {
        return String.format(getString(R.string.mpsdk_text_some_card_data_is_incorrect), mPaymentMethod.getName());
    }

    @Override
    protected void onInvalidStart(String errorMessage) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), errorMessage, false);
    }

    private Boolean isStatusDetailValid() {
        return !isEmpty(mPayment.getStatusDetail());
    }

    private Boolean isPaymentMethodValid() {
        return isPaymentMethodIdValid() && !isEmpty(mPaymentMethod.getName()) && !isEmpty(mPaymentMethod.getPaymentTypeId());
    }

    private Boolean isPaymentMethodIdValid() {
        return !isEmpty(mPaymentMethod.getId()) && mPayment.getPaymentMethodId().equals(mPaymentMethod.getId());
    }

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private boolean isCardPaymentTypeCreditCard() {
        return !mPaymentMethod.getPaymentTypeId().isEmpty() && mPaymentMethod.getPaymentTypeId().equals("credit_card");
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("REJECTION", "BACK_PRESSED", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

        if (mBackPressedOnce) {
            finishWithOkResult();
        } else {
            Snackbar.make(mKeepBuyingButton, getString(R.string.mpsdk_press_again_to_leave), Snackbar.LENGTH_LONG).show();
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
        mRejectionSubtitle.setVisibility(View.GONE);
        mRejectionOptionButtonText.setText(getString(R.string.mpsdk_text_enter_again));
        mKeepBuyingButton.setText(getString(R.string.mpsdk_text_cancel_payment_and_continue));
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
        return mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER) ||
                mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER) ||
                mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE) ||
                mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE);
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
