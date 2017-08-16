package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.Site;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;

import static android.text.TextUtils.isEmpty;

public class PaymentResultActivity extends Activity {

    public static final String DISCOUNT_BUNDLE = "mDiscount";
    public static final String DISCOUNT_ENABLED_BUNDLE = "mDiscountEnabled";
    public static final String MERCHANT_PUBLIC_KEY_BUNDLE = "mMerchantPublicKey";
    public static final String CONGRATS_DISPLAY_BUNDLE = "mCongratsDisplay";
    public static final String PAYMENT_RESULT_BUNDLE = "mPaymentResult";
    public static final String SITE_BUNDLE = "mSite";
    public static final String AMOUNT_BUNDLE = "mAmount";
    public static final String PAYMENT_RESULT_SCREEN_PREFERENCE_BUNDLE = "mPaymentResultScreenPreference";

    protected Discount mDiscount;
    protected Boolean mDiscountEnabled;
    private String mMerchantPublicKey;
    protected Integer mCongratsDisplay;
    protected PaymentResult mPaymentResult;
    protected Site mSite;
    protected BigDecimal mAmount;
    protected PaymentResultScreenPreference mPaymentResultScreenPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getActivityParameters();
            try {
                validateActivityParameters();
                onValidStart();
            } catch (IllegalStateException exception) {
                onInvalidStart(exception.getMessage());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DISCOUNT_BUNDLE, JsonUtil.getInstance().toJson(mDiscount));
        outState.putBoolean(DISCOUNT_ENABLED_BUNDLE, mDiscountEnabled);
        outState.putString(MERCHANT_PUBLIC_KEY_BUNDLE, mMerchantPublicKey);
        outState.putInt(CONGRATS_DISPLAY_BUNDLE, mCongratsDisplay);
        outState.putString(PAYMENT_RESULT_BUNDLE, JsonUtil.getInstance().toJson(mPaymentResult));
        outState.putString(SITE_BUNDLE, JsonUtil.getInstance().toJson(mSite));
        if (mAmount != null) {
            outState.putString(AMOUNT_BUNDLE, mAmount.toString());
        }
        outState.putString(PAYMENT_RESULT_SCREEN_PREFERENCE_BUNDLE, JsonUtil.getInstance().toJson(mPaymentResultScreenPreference));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mDiscount = JsonUtil.getInstance().fromJson(savedInstanceState.getString(DISCOUNT_BUNDLE), Discount.class);
        mDiscountEnabled = savedInstanceState.getBoolean(DISCOUNT_ENABLED_BUNDLE);
        mMerchantPublicKey = savedInstanceState.getString(MERCHANT_PUBLIC_KEY_BUNDLE);
        mCongratsDisplay = savedInstanceState.getInt(CONGRATS_DISPLAY_BUNDLE, -1);
        mPaymentResult = JsonUtil.getInstance().fromJson(savedInstanceState.getString(PAYMENT_RESULT_BUNDLE), PaymentResult.class);
        mSite = JsonUtil.getInstance().fromJson(savedInstanceState.getString(SITE_BUNDLE), Site.class);
        if (savedInstanceState.getString(AMOUNT_BUNDLE) != null) {
            mAmount = new BigDecimal(savedInstanceState.getString(AMOUNT_BUNDLE));
        }
        mPaymentResultScreenPreference = JsonUtil.getInstance().fromJson(savedInstanceState.getString(PAYMENT_RESULT_SCREEN_PREFERENCE_BUNDLE), PaymentResultScreenPreference.class);
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mCongratsDisplay = getIntent().getIntExtra("congratsDisplay", -1);
        mDiscount = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("discount"), Discount.class);
        mDiscountEnabled = getIntent().getExtras().getBoolean("discountEnabled", true);
        mPaymentResult = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentResult"), PaymentResult.class);
        mPaymentResultScreenPreference = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentResultScreenPreference"), PaymentResultScreenPreference.class);
        mSite = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("site"), Site.class);
        if (getIntent().getStringExtra("amount") != null) {
            mAmount = new BigDecimal(getIntent().getStringExtra("amount"));
        }
    }

    protected void validateActivityParameters() throws IllegalStateException {
        if (mMerchantPublicKey == null) {
            throw new IllegalStateException("merchant public key is null");
        }
        if (mPaymentResult == null) {
            throw new IllegalStateException("payment result is null");
        } else if (mPaymentResult.getPaymentData() == null) {
            throw new IllegalStateException("payment data is null");
        }
        if (!isStatusValid()) {
            throw new IllegalStateException("payment not does not have status");
        }
    }

    protected void onValidStart() {
        if (mPaymentResult.getPaymentStatusDetail() != null && mPaymentResult.getPaymentStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_WAITING_PAYMENT)) {
            startInstructionsActivity();
        } else if (mPaymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS) ||
                mPaymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_PENDING)) {
            startPendingActivity();
        } else if (checkCongratsPaymentTypeResult()) {
            startCardPaymentTypeResult();
        }
    }

    private boolean checkCongratsPaymentTypeResult() {
        return MercadoPagoUtil.isCard(mPaymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId()) ||
                mPaymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId().equals(PaymentTypes.ACCOUNT_MONEY);
    }

    private void startCardPaymentTypeResult() {
        if (mPaymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_APPROVED)) {
            startCongratsActivity();
        } else if (mPaymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_REJECTED)) {
            if (isStatusDetailValid() && mPaymentResult.getPaymentStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE)) {
                startCallForAuthorizeActivity();
            } else {
                startRejectionActivity();
            }
        } else {
            ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), false, mMerchantPublicKey);
        }
    }

    private void startInstructionsActivity() {
        new MercadoPagoComponents.Activities.InstructionsActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPaymentResult(mPaymentResult)
                .setSite(mSite)
                .setAmount(mAmount)
                .setPaymentResultScreenPreference(mPaymentResultScreenPreference)
                .startActivity();
    }

    private void startCongratsActivity() {
        new MercadoPagoComponents.Activities.CongratsActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setDiscountEnabled(mDiscountEnabled)
                .setCongratsDisplay(mCongratsDisplay)
                .setPaymentResult(mPaymentResult)
                .setPaymentResultScreenPreference(mPaymentResultScreenPreference)
                .setSite(mSite)
                .setAmount(mAmount)
                .startActivity();
    }

    private void startCallForAuthorizeActivity() {
        new MercadoPagoComponents.Activities.CallForAuthorizeActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPaymentResult(mPaymentResult)
                .setPaymentResultScreenPreference(mPaymentResultScreenPreference)
                .setSite(mSite)
                .startActivity();
    }

    private void startPendingActivity() {
        new MercadoPagoComponents.Activities.PendingActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPaymentResult(mPaymentResult)
                .setPaymentResultScreenPreference(mPaymentResultScreenPreference)
                .startActivity();
    }

    private void startRejectionActivity() {
        new MercadoPagoComponents.Activities.RejectionActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPaymentResult(mPaymentResult)
                .setPaymentResultScreenPreference(mPaymentResultScreenPreference)
                .startActivity();
    }

    private Boolean isStatusValid() {
        return !isEmpty(mPaymentResult.getPaymentStatus());
    }

    private Boolean isStatusDetailValid() {
        return !isEmpty(mPaymentResult.getPaymentStatusDetail());
    }

    protected void onInvalidStart(String errorMessage) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), errorMessage, false, mMerchantPublicKey);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == MercadoPagoCheckout.TIMER_FINISHED_RESULT_CODE) {
            resolveTimerObserverResult(resultCode);
        } else if (requestCode == MercadoPagoComponents.Activities.CONGRATS_REQUEST_CODE) {
            finishWithOkResult(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.PENDING_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.REJECTION_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.CALL_FOR_AUTHORIZE_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.INSTRUCTIONS_REQUEST_CODE) {
            finishWithOkResult(resultCode, data);
        } else {
            finishWithCancelResult(data);
        }
    }

    private void resolveTimerObserverResult(int resultCode) {
        setResult(resultCode);
        finish();
    }

    private void resolveRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED && data != null) {
            finishWithCancelResult(data);
        } else {
            finishWithOkResult(resultCode, data);
        }
    }

    private void finishWithCancelResult(Intent data) {
        setResult(RESULT_CANCELED, data);
        finish();
    }

    private void finishWithOkResult(int resultCode, Intent data) {
        setResult(resultCode, data);
        finish();
    }
}
