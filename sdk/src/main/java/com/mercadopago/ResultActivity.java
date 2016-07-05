package com.mercadopago;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;

import static android.text.TextUtils.isEmpty;

public class ResultActivity extends AppCompatActivity {

    //Params
    protected Payment mPayment;
    protected PaymentMethod mPaymentMethod;

    //TODO revisar mActivityActive
    private boolean mActivityActive;
    protected DecorationPreference mDecorationPreference;
    private Activity mActivity;
    private String mMerchantPublicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityParameters();
        setActivity();
        mActivityActive = true;

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

    private void setActivity() {
        mActivity = this;
    }

    protected String getMerchantPublicKey() {
        return mMerchantPublicKey;
    }

    protected void validateActivityParameters() throws IllegalStateException {
        if(mPayment == null) {
            throw new IllegalStateException("payment not set");
        }
        if(mPaymentMethod == null) {
            throw new IllegalStateException("payment method not set");
        }
    }

    protected void onValidStart(){
        if (MercadoPagoUtil.isCardPaymentType(mPaymentMethod.getPaymentTypeId())) {
            startCardPaymentTypeResult();
        }
        else {
            MPTracker.getInstance().trackPaymentId(mPaymentMethod.getId(), "3", getMerchantPublicKey(), "MLA", "1.0", this);
            startInstructionsActivity();
        }
    }

    private void startCardPaymentTypeResult(){
        if (mPayment != null && isPaymentMethodValid() && isStatusValid()) {
            if (mPayment.getStatus().equals(Payment.StatusCodes.STATUS_APPROVED)) {
                startCongratsActivity();
            } else if (mPayment.getStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS)) {
                startPendingActivity();
            } else if (mPayment.getStatus().equals(Payment.StatusCodes.STATUS_REJECTED)) {
                if (isStatusDetailValid()) {
                    if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE)) {
                        startCallForAuthorizeActivity();
                    } else {
                        startRejectionActivity();
                    }
                } else {
                    showError();
                }
            } else {
                showError();
            }
        } else {
            showError();
        }
    }

    private void startInstructionsActivity(){
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(getMerchantPublicKey())
                .setActivity(mActivity)
                .setPayment(mPayment)
                .setPaymentMethod(mPaymentMethod)
                .startInstructionsActivity();
    }

    private void startCongratsActivity(){
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(getMerchantPublicKey())
                .setActivity(mActivity)
                .setPayment(mPayment)
                .setPaymentMethod(mPaymentMethod)
                .startCongratsActivity();
    }

    private void startCallForAuthorizeActivity(){
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(getMerchantPublicKey())
                .setActivity(mActivity)
                .setPayment(mPayment)
                .setPaymentMethod(mPaymentMethod)
                .startCallForAuthorizeActivity();
    }

    private void startPendingActivity(){
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(getMerchantPublicKey())
                .setActivity(mActivity)
                .setPayment(mPayment)
                .setPaymentMethod(mPaymentMethod)
                .startPendingActivity();
    }

    private void startRejectionActivity(){
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(getMerchantPublicKey())
                .setActivity(mActivity)
                .setPayment(mPayment)
                .setPaymentMethod(mPaymentMethod)
                .startRejectionActivity();
    }

    //TODO lanzar exception
    private void showError(){
        if (mPayment==null){
            ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_payment_error), false);
        }
        else {
            ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_payment_method_error), false);
        }

    }

    private Boolean isStatusDetailValid(){
        return !isEmpty(mPayment.getStatusDetail());
    }

    private Boolean isPaymentMethodValid(){
        return mPaymentMethod != null && isPaymentMethodIdValid() && !isEmpty(mPaymentMethod.getName());
    }

    private Boolean isPaymentMethodIdValid(){
        return !isEmpty(mPaymentMethod.getId()) && mPayment.getPaymentMethodId().equals(mPaymentMethod.getId());
    }

    private Boolean isStatusValid(){
        return !isEmpty(mPayment.getStatus());
    }

    protected void onInvalidStart(String message){
        ErrorUtil.startErrorActivity(this, message, false);
    }

}
