package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;

import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.PaymentResultAction;
import com.mercadopago.model.Site;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.TextUtils;

import java.math.BigDecimal;

import static android.text.TextUtils.isEmpty;

public class CallForAuthorizeActivity extends MercadoPagoBaseActivity implements TimerObserver {

    //Controls
    protected MPTextView mTimerTextView;
    protected MPTextView mCallForAuthTitle;
    protected MPTextView mAuthorizedPaymentMethod;
    protected MPTextView mKeepBuyingButton;
    protected FrameLayout mPayWithOtherPaymentMethodButton;

    //Params
    protected String mMerchantPublicKey;
    protected PaymentResult mPaymentResult;
    protected Site mSite;
    protected Activity mActivity;

    //Local values
    private boolean mBackPressedOnce;
    private String mNextAction;
    private String mPaymentMethodName;
    private String mCurrencyId;
    private BigDecimal mTotalAmount;
    private String mPaymentTypeId;
    private String mPaymentMethodId;
    private PaymentResultScreenPreference mPaymentResultScreenPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityParameters();
        setContentView();
        initializeControls();
        customizeViews();
        mActivity = this;
        try {
            validateActivityParameters();
            onValidStart();
        } catch (IllegalStateException exception) {
            onInvalidStart(exception.getMessage());
        }
    }

    private void customizeViews() {
        if (mPaymentResultScreenPreference != null && !TextUtils.isEmpty(mPaymentResultScreenPreference.getExitButtonTitle())) {
            mKeepBuyingButton.setText(mPaymentResultScreenPreference.getExitButtonTitle());
        }
    }

    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPaymentResult = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentResult"), PaymentResult.class);
        mPaymentResultScreenPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentResultScreenPreference"), PaymentResultScreenPreference.class);
        mSite = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("site"), Site.class);
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
        MPTracker.getInstance().trackScreen("CALL_FOR_AUTHORIZE", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);
        setContentView(R.layout.mpsdk_activity_call_for_authorize);
    }

    protected void initializeControls() {
        mCallForAuthTitle = (MPTextView) findViewById(R.id.mpsdkCallForAuthorizeTitle);
        mAuthorizedPaymentMethod = (MPTextView) findViewById(R.id.mpsdkAuthorizedPaymentMethod);
        mAuthorizedPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MPTracker.getInstance().trackEvent("CALL_FOR_AUTHORIZE", "RECOVER_TOKEN", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, mActivity);

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
                MPTracker.getInstance().trackEvent("CALL_FOR_AUTHORIZE", "SELECT_OTHER_PAYMENT_METHOD", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, mActivity);

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

    protected void onValidStart() {
        initializePaymentData();
        showTimer();
        setDescription();
        setAuthorized();
    }

    private void initializePaymentData() {
        PaymentData paymentData = mPaymentResult.getPaymentData();
        if (paymentData != null) {
            if (paymentData.getPaymentMethod() != null) {
                mPaymentMethodName = paymentData.getPaymentMethod().getName();
                mPaymentTypeId = paymentData.getPaymentMethod().getPaymentTypeId();
                mPaymentMethodId = paymentData.getPaymentMethod().getId();
            }
            if (paymentData.getPayerCost() != null) {
                mTotalAmount = paymentData.getPayerCost().getTotalAmount();
            }
        }
        if (mSite != null) {
            mCurrencyId = mSite.getCurrencyId();
        }
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

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void setAuthorized() {
        if (isPaymentMethodValid()) {
            String message = String.format(getString(R.string.mpsdk_text_authorized_call_for_authorize), mPaymentMethodName);
            mAuthorizedPaymentMethod.setText(message);
        } else {
            mAuthorizedPaymentMethod.setVisibility(View.GONE);
        }
    }

    private void setDescription() {
        if (isPaymentMethodValid() && isCurrencyIdValid() && isTotalPaidAmountValid()) {
            String totalPaidAmount = CurrenciesUtil.formatNumber(mTotalAmount, mCurrencyId);
            String titleWithFormat = String.format(getString(R.string.mpsdk_title_activity_call_for_authorize), "<br>" + mPaymentMethodName, "<br>" + totalPaidAmount);

            mCallForAuthTitle.setText(CurrenciesUtil.formatCurrencyInText(mTotalAmount, mCurrencyId, titleWithFormat, true, true));
        } else {
            mCallForAuthTitle.setText(getString(R.string.mpsdk_error_title_activity_call_for_authorize));
        }
    }

    private Boolean isPaymentMethodValid() {
        return isPaymentMethodIdValid() && !isEmpty(mPaymentMethodName) && !isEmpty(mPaymentTypeId);
    }

    private Boolean isPaymentMethodIdValid() {
        return !isEmpty(mPaymentMethodId);
    }

    private Boolean isCurrencyIdValid() {
        return !isEmpty(mCurrencyId) && CurrenciesUtil.isValidCurrency(mCurrencyId);
    }

    private Boolean isTotalPaidAmountValid() {
        return mTotalAmount != null
                && (mTotalAmount.compareTo(BigDecimal.ZERO)) > 0;
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
        setResult(MercadoPagoCheckout.TIMER_FINISHED_RESULT_CODE);
        this.finish();
    }
}
