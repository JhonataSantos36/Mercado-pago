package com.mercadopago;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.core.MercadoPagoContext;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentResultAction;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;

import static android.text.TextUtils.isEmpty;

public class PendingActivity extends MercadoPagoActivity implements TimerObserver {

    //Controls
    protected MPTextView mTimerTextView;
    protected MPTextView mPendingSubtitle;
    protected MPTextView mKeepBuying;

    //Params
    protected Payment mPayment;
    protected PaymentMethod mPaymentMethod;
    protected String mMerchantPublicKey;

    //Local values
    private boolean mBackPressedOnce;

    @Override
    protected void getActivityParameters() {
        mMerchantPublicKey = MercadoPagoContext.getInstance().getPublicKey();
        mPayment = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("payment"), Payment.class);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mMerchantPublicKey == null) {
            throw new IllegalStateException("merchant public key not set");
        }
        if (mPayment == null) {
            throw new IllegalStateException("payment not set");
        }
    }

    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("PENDING", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, getActivity());
        setContentView(R.layout.mpsdk_activity_pending);
    }

    @Override
    protected void initializeControls() {
        mPendingSubtitle = (MPTextView) findViewById(R.id.mpsdkPendingSubtitle);
        mKeepBuying = (MPTextView) findViewById(R.id.mpsdkKeepBuyingPending);
        mKeepBuying.setOnClickListener(new View.OnClickListener() {
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
        showSubtitle();
    }

    private void showSubtitle() {
        if (isStatusDetailValid()) {
            if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_CONTINGENCY)) {
                mPendingSubtitle.setText(getString(R.string.mpsdk_subtitle_pending_contingency));
            } else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_REVIEW_MANUAL)) {
                mPendingSubtitle.setText(getString(R.string.mpsdk_subtitle_pending_contingency));
            }
        } else {
            mPendingSubtitle.setVisibility(View.GONE);
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
    protected void onInvalidStart(String errorMessage) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), errorMessage, false);
    }

    private Boolean isStatusDetailValid() {
        return !isEmpty(mPayment.getStatusDetail());
    }

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("PENDING", "BACK_PRESSED", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

        if (mBackPressedOnce) {
            finishWithOkResult();
        } else {
            Snackbar.make(mKeepBuying, getString(R.string.mpsdk_press_again_to_leave), Snackbar.LENGTH_LONG).show();
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
