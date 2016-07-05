package com.mercadopago;

import android.content.Intent;
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

    @Override
    protected void getActivityParameters() {
        super.getActivityParameters();
        mPayment = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("payment"), Payment.class);
        mPaymentMethod = JsonUtil.getInstance().fromJson(getIntent().getExtras().getString("paymentMethod"), PaymentMethod.class);
    }

    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("CALL_FOR_AUTHORIZE", "3", getMerchantPublicKey(), "MLA", "1.0", getActivity());

        setTheme(R.style.Theme_CallForAuthorizeMercadoPagoTheme_NoActionbar);
        setContentView(R.layout.mpsdk_activity_call_for_authorize);
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
        mSelectOtherPaymentMethod = (MPTextView) findViewById(R.id.mpsdkSelectOtherPaymentMethod);
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

    private void finishWithOkResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onValidStart(){
        setDescription();
        setAuthorized();
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
        return mPaymentMethod != null && isPaymentMethodIdValid() && !isEmpty(mPaymentMethod.getName());
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
    protected void onInvalidStart(String message){
        ErrorUtil.startErrorActivity(this, message, false);
    }
}
