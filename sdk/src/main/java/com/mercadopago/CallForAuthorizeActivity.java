package com.mercadopago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mercadopago.model.Payment;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;

import static android.text.TextUtils.isEmpty;

public class CallForAuthorizeActivity extends AppCompatActivity {

    // Controls
    protected MPTextView mAuthorizeDescription;
    protected MPTextView mPaymentAmountDescription;
    protected MPTextView mAuthorized;
    protected MPTextView mSelectPaymentMethod;
    protected MPTextView mExitOfCallForAuthorize;

    // Activity parameters
    protected Payment mPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_for_authorize);

        getActivityParameters();
        initializeControls();
        setLayoutData();
    }

    //TODO setear el select de otro método de pago
    //TODO agregar el set del botón de aliExpress
    protected void setLayoutData() {
        if (mPayment != null) {
            setDescription();
            setAuthorized();
        }
        else {
            //TODO validar si mandamos RESULT_CANCELED
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    protected void setAuthorized(){
        if (mPayment.getCard() != null && !isEmpty(mPayment.getCard().getPaymentMethod().getId())){
            mAuthorized.setText(getString(R.string.mpsdk_text_authorized_call_for_authorize) + mPayment.getCard().getPaymentMethod().getId());
        }
        else{
            mAuthorized.setVisibility(View.GONE);
        }
    }

    protected void setDescription() {
        if (mPayment.getCard() != null && !isEmpty(mPayment.getCard().getPaymentMethod().getId()) &&
                mPayment.getTransactionAmount() != null && mPayment.getTransactionAmount().compareTo(BigDecimal.ZERO) >= 0){

            //TODO concatenar el amount con el string
            mAuthorizeDescription.setText(getString(R.string.mpsdk_title_activity_call_for_authorize) + mPayment.getCard().getPaymentMethod().getId());
            mPaymentAmountDescription.setText(getString(R.string.mpsdk_title_amount_call_for_authorize) + mPayment.getTransactionAmount());
        }
        else {
            mAuthorizeDescription.setVisibility(View.GONE);
            mPaymentAmountDescription.setVisibility(View.GONE);
        }
    }

    protected void getActivityParameters() {
        mPayment = (Payment) this.getIntent().getSerializableExtra("payment");
    }

    protected void initializeControls() {
        mAuthorizeDescription = (MPTextView) findViewById(R.id.authorizeDescription);
        mPaymentAmountDescription = (MPTextView) findViewById(R.id.paymentAmountDescription);
        mAuthorized = (MPTextView) findViewById(R.id.authorized);
        mSelectPaymentMethod = (MPTextView) findViewById(R.id.selectPaymentMethod);
        mExitOfCallForAuthorize = (MPTextView) findViewById(R.id.exitOfCallForAuthorize);
    }
}
