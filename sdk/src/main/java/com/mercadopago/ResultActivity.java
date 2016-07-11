package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;

import static android.text.TextUtils.isEmpty;

public class ResultActivity extends AppCompatActivity {

    //Params
    protected Payment mPayment;
    protected PaymentMethod mPaymentMethod;

    private String mMerchantPublicKey;

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
        mPayment = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("payment"), Payment.class);
        mPaymentMethod = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentMethod"), PaymentMethod.class);
    }

    protected void validateActivityParameters() throws IllegalStateException {
        if(mMerchantPublicKey == null) {
            throw new IllegalStateException("merchant public key not set");
        }
        if(mPayment == null) {
            throw new IllegalStateException("payment not set");
        }
        if(mPaymentMethod == null) {
            throw new IllegalStateException("payment method not set");
        }
        if(!isStatusValid()) {
            throw new IllegalStateException("payment not does not have status");
        }
    }

    protected void onValidStart(){
        if(mPayment.getStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS)) {
            startPendingActivity();
        }
        else if (MercadoPagoUtil.isCardPaymentType(mPaymentMethod.getPaymentTypeId())) {
            startCardPaymentTypeResult();
        }
        else {
            MPTracker.getInstance().trackPaymentId(mPaymentMethod.getId(), "3", mMerchantPublicKey, "MLA", "1.0", this);
            startInstructionsActivity();
        }
    }

    private void startCardPaymentTypeResult(){
        if (isStatusDetailValid()) {
            if (mPayment.getStatus().equals(Payment.StatusCodes.STATUS_APPROVED)) {
                startCongratsActivity();
            } else if (mPayment.getStatus().equals(Payment.StatusCodes.STATUS_REJECTED)) {
                if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE)) {
                    startCallForAuthorizeActivity();
                } else {
                    startRejectionActivity();
                }
            } else {
                ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message),false);
            }
        } else {
            ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message),false);
        }
    }

    private void startInstructionsActivity(){
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPayment(mPayment)
                .setPaymentMethod(mPaymentMethod)
                .startInstructionsActivity();
    }

    private void startCongratsActivity(){
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPayment(mPayment)
                .setPaymentMethod(mPaymentMethod)
                .startCongratsActivity();
    }

    private void startCallForAuthorizeActivity(){
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPayment(mPayment)
                .setPaymentMethod(mPaymentMethod)
                .startCallForAuthorizeActivity();
    }

    private void startPendingActivity(){
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPayment(mPayment)
                .startPendingActivity();
    }

    private void startRejectionActivity(){
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(mMerchantPublicKey)
                .setActivity(this)
                .setPayment(mPayment)
                .setPaymentMethod(mPaymentMethod)
                .startRejectionActivity();
    }

    private Boolean isStatusValid(){
        return !isEmpty(mPayment.getStatus());
    }

    private Boolean isStatusDetailValid(){
        return !isEmpty(mPayment.getStatusDetail());
    }

    protected void onInvalidStart(String errorMessage){
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), errorMessage,false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {
            finishWithOkResult();
        }
        else if (requestCode == MercadoPago.PENDING_REQUEST_CODE) {
            finishWithOkResult();
        }
        else if (requestCode == MercadoPago.REJECTION_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        }
        else if (requestCode == MercadoPago.CALL_FOR_AUTHORIZE_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        }
        else if (requestCode == MercadoPago.INSTRUCTIONS_REQUEST_CODE) {
            finishWithOkResult();
        }
    }

    private void resolveRequest(int resultCode, Intent data){
        if (resultCode == RESULT_CANCELED && data != null) {
            finishWithCancelResult(data);
        } else {
            finishWithOkResult();
        }
    }

    private void finishWithCancelResult(Intent data) {
        setResult(RESULT_CANCELED, data);
        finish();
    }

    private void finishWithOkResult() {
        Intent paymentResultIntent = new Intent();
        setResult(RESULT_OK, paymentResultIntent);
        finish();
    }
}
