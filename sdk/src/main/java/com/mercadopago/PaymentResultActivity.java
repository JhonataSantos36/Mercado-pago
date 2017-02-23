package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mercadopago.constants.PaymentTypes;
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
import static com.mercadopago.core.MercadoPagoComponents.Activities.CONGRATS_REQUEST_CODE;

public class PaymentResultActivity extends Activity {

    public static final int RESULT_SILENT_OK = 3;

    protected Discount mDiscount;
    protected String mMerchantPublicKey;
    protected Integer mCongratsDisplay;
    protected PaymentResult mPaymentResult;
    protected Site mSite;
    protected BigDecimal mAmount;
    protected PaymentResultScreenPreference mPaymentResultScreenPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityParameters();

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
        mPaymentResult = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentResult"), PaymentResult.class);
        mPaymentResultScreenPreference = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentResultScreenPreference"), PaymentResultScreenPreference.class);
        mSite = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("site"), Site.class);
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
        if (!isStatusValid()) {
            throw new IllegalStateException("payment not does not have status");
        }
    }

    protected void onValidStart() {
        if(mPaymentResult.getPaymentStatusDetail() != null && mPaymentResult.getPaymentStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_WAITING_PAYMENT)) {
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
            ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), false);
        }
    }

    private void startInstructionsActivity() {
        new MercadoPagoComponents.Activities.InstructionsActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPaymentResult(mPaymentResult)
                .setSite(mSite)
                .setAmount(mAmount)
                .startActivity();
    }

    private void startCongratsActivity() {
        new MercadoPagoComponents.Activities.CongratsActivityBuilder()
                .setMerchantPublicKey(mMerchantPublicKey)
                .setActivity(this)
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
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), errorMessage, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPagoComponents.Activities.CONGRATS_REQUEST_CODE) {
            finishWithOkResult(resultCode);
        } else if (requestCode == MercadoPagoComponents.Activities.PENDING_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.REJECTION_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.CALL_FOR_AUTHORIZE_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == MercadoPagoComponents.Activities.INSTRUCTIONS_REQUEST_CODE) {
            finishWithOkResult(resultCode);
        } else {
            finishWithCancelResult(data);
        }
    }

    private void resolveRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED && data != null) {
            finishWithCancelResult(data);
        } else {
            finishWithOkResult(resultCode);
        }
    }

    private void finishWithCancelResult(Intent data) {
        setResult(RESULT_CANCELED, data);
        finish();
    }

    private void finishWithOkResult(int resultCode) {
        Intent paymentResultIntent = new Intent();
        setResult(resultCode, paymentResultIntent);
        finish();
    }
}
