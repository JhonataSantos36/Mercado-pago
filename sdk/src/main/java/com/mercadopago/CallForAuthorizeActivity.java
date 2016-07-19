package com.mercadopago;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;

import static android.text.TextUtils.isEmpty;

public class CallForAuthorizeActivity extends MercadoPagoActivity {

    //Controls
    protected MPTextView mCallForAuthTitle;
    protected MPTextView mAuthorizedPaymentMethod;
    protected MPTextView mSelectOtherPaymentMethod;
    protected MPTextView mExit;

    //Params
    protected Payment mPayment;
    protected PaymentMethod mPaymentMethod;
    protected String mMerchantPublicKey;

    //Local values
    private boolean mBackPressedOnce;

    @Override
    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mPayment = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("payment"), Payment.class);
        mPaymentMethod = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentMethod"), PaymentMethod.class);
    }

    @Override
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
    }

    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("CALL_FOR_AUTHORIZE", "2", mMerchantPublicKey, "MLA", "1.0", getActivity());

        setContentView(R.layout.mpsdk_activity_call_for_authorize);
    }

    @Override
    protected void initializeControls(){
        mCallForAuthTitle = (MPTextView) findViewById(R.id.mpsdkCallForAuthorizeTitle);
        mAuthorizedPaymentMethod = (MPTextView) findViewById(R.id.mpsdkAuthorizedPaymentMethod);
        mAuthorizedPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("retry", true);
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        mSelectOtherPaymentMethod = (MPTextView) findViewById(R.id.mpsdkSelectOtherPaymentMethodByCallForAuthorize);
        mSelectOtherPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("selectOther", true);
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        mExit = (MPTextView) findViewById(R.id.mpsdkExitCallForAuthorize);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithOkResult();
            }
        });
    }

    @Override
    protected void onValidStart(){
        setDescription();
        setAuthorized();
    }

    @Override
    protected void onInvalidStart(String errorMessage){
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), errorMessage,false);
    }

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void setAuthorized(){
        if (isPaymentMethodValid()){
            String message = getString(R.string.mpsdk_text_authorized_call_for_authorize) + " " + mPaymentMethod.getName() + " " + getString(R.string.mpsdk_text_and_he_authorized);
            mAuthorizedPaymentMethod.setText(message);
        }
        else{
            mAuthorizedPaymentMethod.setVisibility(View.GONE);
        }
    }

    private void setDescription() {
        if (isPaymentMethodValid() && isCurrencyIdValid() && isTotalPaidAmountValid()){
            StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.mpsdk_title_activity_call_for_authorize));
            sb.append(" " + mPaymentMethod.getName() + " ");
            sb.append(getString(R.string.mpsdk_text_the_payment) + " ");
            sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId()));
            sb.append(" " + getString(R.string.mpsdk_text_to_mercado_pago));

            mCallForAuthTitle.setText(CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(),
                    mPayment.getCurrencyId(), sb.toString(), true, true));
        }
        else {
            mCallForAuthTitle.setText(getString(R.string.mpsdk_error_title_activity_call_for_authorize));
        }
    }

    private Boolean isPaymentMethodValid(){
        return isPaymentMethodIdValid() && !isEmpty(mPaymentMethod.getName()) && !isEmpty(mPaymentMethod.getPaymentTypeId());
    }

    private Boolean isPaymentMethodIdValid(){
        return !isEmpty(mPaymentMethod.getId()) && mPayment.getPaymentMethodId().equals(mPaymentMethod.getId());
    }

    private Boolean isCurrencyIdValid(){
        return !isEmpty(mPayment.getCurrencyId()) && CurrenciesUtil.isValidCurrency(mPayment.getCurrencyId());
    }

    private Boolean isTotalPaidAmountValid(){
        return mPayment.getTransactionDetails() != null && mPayment.getTransactionDetails().getTotalPaidAmount() != null
                && (mPayment.getTransactionDetails().getTotalPaidAmount().compareTo(BigDecimal.ZERO))>0;
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("CALL_FOR_AUTHORIZE", "BACK_PRESSED", "2", mMerchantPublicKey, "MLA", "1.0", this);

        if(mBackPressedOnce) {
            finishWithOkResult();
        }
        else {
            Snackbar.make(mExit, getString(R.string.mpsdk_press_again_to_leave), Snackbar.LENGTH_LONG).show();
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
}
