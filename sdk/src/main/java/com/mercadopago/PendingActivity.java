package com.mercadopago;

import android.content.Intent;
import android.view.View;

import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.MPTextView;

import static android.text.TextUtils.isEmpty;

public class PendingActivity extends MercadoPagoActivity {

    //Controls
    protected MPTextView mPendingSubtitle;
    protected MPTextView mExit;

    //Params
    protected Payment mPayment;
    protected PaymentMethod mPaymentMethod;

    @Override
    protected void getActivityParameters() {
        super.getActivityParameters();
        mPayment = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("payment"), Payment.class);
        mPaymentMethod = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentMethod"), PaymentMethod.class);
    }

    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("PENDING", "3", getMerchantPublicKey(), "MLA", "1.0", getActivity());

        setTheme(R.style.Theme_InfoMercadoPagoTheme_NoActionbar);
        setContentView(R.layout.mpsdk_activity_pending);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if(getMerchantPublicKey() == null) {
            throw new IllegalStateException("merchant public key not set");
        }
        if(mPayment == null) {
            throw new IllegalStateException("payment not set");
        }
        if(mPaymentMethod == null) {
            throw new IllegalStateException("payment method not set");
        }
    }

    @Override
    protected void initializeControls(){
        mPendingSubtitle = (MPTextView) findViewById(R.id.mpsdkPendingSubtitle);
        mExit = (MPTextView) findViewById(R.id.mpsdkExitPending);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult();
            }
        });
    }

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onValidStart(){
        if (isStatusDetailValid()) {
            if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_CONTINGENCY)) {
                mPendingSubtitle.setText(getString(R.string.mpsdk_subtitle_pending_contingency));
            }
            else if (mPayment.getStatusDetail().equals(Payment.StatusCodes.STATUS_DETAIL_PENDING_REVIEW_MANUAL)) {
                //TODO review subtitle
                mPendingSubtitle.setText(getString(R.string.mpsdk_subtitle_pending_contingency));
            }
        } else {
            mPendingSubtitle.setVisibility(View.GONE);
        }
    }

    private Boolean isStatusDetailValid(){
        return !isEmpty(mPayment.getStatusDetail());
    }

    @Override
    protected void onInvalidStart(String message){
        ErrorUtil.startErrorActivity(this, message, false);
    }
}
