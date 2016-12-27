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
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;

import static android.text.TextUtils.isEmpty;

public class CallForAuthorizeActivity extends MercadoPagoActivity implements TimerObserver {

    //Controls
    protected MPTextView mTimerTextView;
    protected MPTextView mCallForAuthTitle;
    protected MPTextView mAuthorizedPaymentMethod;
    protected MPTextView mKeepBuyingButton;
    protected FrameLayout mPayWithOtherPaymentMethodButton;

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
        MPTracker.getInstance().trackScreen("CALL_FOR_AUTHORIZE", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);
        setContentView(R.layout.mpsdk_activity_call_for_authorize);
    }

    @Override
    protected void initializeControls() {
        mCallForAuthTitle = (MPTextView) findViewById(R.id.mpsdkCallForAuthorizeTitle);
        mAuthorizedPaymentMethod = (MPTextView) findViewById(R.id.mpsdkAuthorizedPaymentMethod);
        mAuthorizedPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MPTracker.getInstance().trackEvent("CALL_FOR_AUTHORIZE", "RECOVER_TOKEN", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, getActivity());

                Intent returnIntent = new Intent();
                mNextAction = PaymentResultAction.RECOVER_PAYMENT;
                returnIntent.putExtra("nextAction", mNextAction);
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        mPayWithOtherPaymentMethodButton = (FrameLayout) findViewById(R.id.mpsdkCallForAuthorizeOptionButton);
        mPayWithOtherPaymentMethodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MPTracker.getInstance().trackEvent("CALL_FOR_AUTHORIZE", "SELECT_OTHER_PAYMENT_METHOD", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, getActivity());

                Intent returnIntent = new Intent();
                mNextAction = PaymentResultAction.SELECT_OTHER_PAYMENT_METHOD;
                returnIntent.putExtra("nextAction", mNextAction);
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        mKeepBuyingButton = (MPTextView) findViewById(R.id.mpsdkKeepBuyingCallForAuthorize);
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
        setDescription();
        setAuthorized();
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

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void setAuthorized() {
        if (isPaymentMethodValid()) {
            String message = String.format(getString(R.string.mpsdk_text_authorized_call_for_authorize), mPaymentMethod.getName());
            mAuthorizedPaymentMethod.setText(message);
        } else {
            mAuthorizedPaymentMethod.setVisibility(View.GONE);
        }
    }

    private void setDescription() {
        if (isPaymentMethodValid() && isCurrencyIdValid() && isTotalPaidAmountValid()) {
            String totalPaidAmount = CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId());
            String titleWithFormat = String.format(getString(R.string.mpsdk_title_activity_call_for_authorize), "<br>" + mPaymentMethod.getName(), "<br>" + totalPaidAmount);

            mCallForAuthTitle.setText(CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                    mPayment.getCurrencyId(), titleWithFormat, true, true));
        } else {
            mCallForAuthTitle.setText(getString(R.string.mpsdk_error_title_activity_call_for_authorize));
        }
    }

    private Boolean isPaymentMethodValid() {
        return isPaymentMethodIdValid() && !isEmpty(mPaymentMethod.getName()) && !isEmpty(mPaymentMethod.getPaymentTypeId());
    }

    private Boolean isPaymentMethodIdValid() {
        return !isEmpty(mPaymentMethod.getId()) && mPayment.getPaymentMethodId().equals(mPaymentMethod.getId());
    }

    private Boolean isCurrencyIdValid() {
        return !isEmpty(mPayment.getCurrencyId()) && CurrenciesUtil.isValidCurrency(mPayment.getCurrencyId());
    }

    private Boolean isTotalPaidAmountValid() {
        return mPayment.getTransactionDetails() != null && mPayment.getTransactionDetails().getTotalPaidAmount() != null
                && (mPayment.getTransactionDetails().getTotalPaidAmount().compareTo(BigDecimal.ZERO)) > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("CALL_FOR_AUTHORIZE", "BACK_PRESSED", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

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

    @Override
    public void onTimeChanged(String timeToShow) {
        mTimerTextView.setText(timeToShow);
    }

    @Override
    public void onFinish() {
        this.finish();
    }
}
