package com.mercadopago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mercadopago.model.Payment;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;

import static android.text.TextUtils.isEmpty;

public class CongratsActivity extends AppCompatActivity {

    // Controls
    protected MPTextView mPayerEmailDescription;
    protected MPTextView mLastFourDigitsTextView;
    protected MPTextView mInstallmentsDescription;
    protected MPTextView mTotalAmountDescription;
    protected MPTextView mPaymentIdDescription;
    protected MPTextView mActionDescription;
    protected MPTextView mExitOfCongrat;

    // Activity parameters
    protected Payment mPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrats);

        getActivityParameters();
        initializeControls();
        setLayoutData();
    }

    protected void getActivityParameters(){
        mPayment = (Payment) getIntent().getExtras().getSerializable("payment");
    }


    protected void initializeControls() {
        mPayerEmailDescription = (MPTextView) findViewById(R.id.payerEmailDescription);
        mLastFourDigitsTextView = (MPTextView) findViewById(R.id.lastFourDigitsDescription);
        mInstallmentsDescription = (MPTextView) findViewById(R.id.installmentsDescription);
        mTotalAmountDescription = (MPTextView) findViewById(R.id.totalAmountDescription);
        mPaymentIdDescription = (MPTextView) findViewById(R.id.paymentIdDescription);
        mActionDescription = (MPTextView) findViewById(R.id.actionDescription);
        mExitOfCongrat = (MPTextView) findViewById(R.id.exitOfCongrat);
    }

    //TODO agregar el set del botón de aliExpress
    protected void setLayoutData() {
        if (mPayment != null) {
            setPaymentEmailDescription();
            setLastFourDigitsCard();
            setInstallmentsDescription();
            setTotalAmountDescription();
            setPaymentDescription();
        }
        else {
            //TODO validar si mandamos RESULT_CANCELED
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    protected void setPaymentDescription() {
        if(mPayment.getId() != null && mPayment.getId() >= 0){
            mPaymentIdDescription.setText(getString(R.string.mpsdk_payment_id_description) + mPayment.getId());
        }
        else{
            mPaymentIdDescription.setVisibility(View.GONE);
        }
    }

    private void setTotalAmountDescription() {
        //TODO agregar un if para imprimir "sin intereses"
        if(mPayment.getTransactionAmount() != null && mPayment.getTransactionAmount().compareTo(BigDecimal.ZERO) >= 0){
            mTotalAmountDescription.setText((mPayment.getTransactionAmount()).toString());
        }
        else{
            mTotalAmountDescription.setVisibility(View.GONE);
        }
    }

    protected void setInstallmentsDescription() {
        if (mPayment.getInstallments() != null && mPayment.getInstallments() >= 0){

            CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionAmount(), mPayment.getCurrencyId(),"100.87",true,true);
        }
        else {
            mInstallmentsDescription.setVisibility(View.GONE);
        }
    }

    protected void setLastFourDigitsCard() {
        if (mPayment.getCard() != null && !isEmpty(mPayment.getCard().getLastFourDigits()) && !isEmpty(mPayment.getCard().getPaymentMethod().getId())){
            mLastFourDigitsTextView.setText(getString(R.string.mpsdk_last_digits_label) + mPayment.getCard().getLastFourDigits());
            mLastFourDigitsTextView.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(this, mPayment.getCard().getPaymentMethod().getId()), 0, 0, 0);
        }
        else{
            mLastFourDigitsTextView.setVisibility(View.GONE);
        }
    }

    protected void setPaymentEmailDescription() {
        if(mPayment.getPayer() != null && !isEmpty(mPayment.getPayer().getEmail())) {
            mPayerEmailDescription.setText(mPayment.getPayer().getEmail());
        } else {
            mActionDescription.setVisibility(View.GONE);
            mPayerEmailDescription.setVisibility(View.GONE);
        }
    }


}
