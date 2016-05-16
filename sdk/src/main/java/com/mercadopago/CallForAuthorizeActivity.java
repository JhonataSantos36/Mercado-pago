package com.mercadopago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;

import com.mercadopago.model.Payment;
import com.mercadopago.util.CurrenciesUtil;
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
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    protected void setAuthorized(){
        if (mPayment.getCard() != null && mPayment.getPaymentMethodId() != null &&
                mPayment.getCard().getPaymentMethod().getName() != null && !isEmpty(mPayment.getCard().getPaymentMethod().getName())){

            String message = "Ya hablé con " + mPayment.getCard().getPaymentMethod().getName() + "y me autorizó";
            mAuthorized.setText(message);
        }
        else{
            mAuthorized.setVisibility(View.GONE);
        }
    }

    protected void setDescription() {
        if (mPayment.getCard() != null && mPayment.getCard().getPaymentMethod() != null &&
                mPayment.getCard().getPaymentMethod().getName() != null &&
                !isEmpty(mPayment.getCard().getPaymentMethod().getName()) &&
                mPayment.getTransactionDetails() != null &&
                mPayment.getTransactionDetails().getTotalPaidAmount() != null ){//&& mPayment.getTransactionDetails().getTotalPaidAmount().compareTo(BigDecimal.ZERO) >= 0){

            String titlePaymentMethodMessage = getString(R.string.mpsdk_title_activity_call_for_authorize) + " " + mPayment.getCard().getPaymentMethod().getName() + " el";
            String titlePaymentAmount = "pago de " + getInstallmentsText() + " a" ;

            mAuthorizeDescription.setText(titlePaymentMethodMessage);
            mPaymentAmountDescription.setText(titlePaymentAmount);
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

    //TODO agregar al set paymentAmount
    private Spanned getInstallmentsText() {
        StringBuffer sb = new StringBuffer();
        sb.append(mPayment.getInstallments());
        sb.append(" ");
        sb.append("de");
        sb.append(" ");
        sb.append(CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getInstallmentAmount(), mPayment.getCurrencyId()));
        return CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getInstallmentAmount(),
                mPayment.getCurrencyId(), sb.toString(), true, true);
    }
}
