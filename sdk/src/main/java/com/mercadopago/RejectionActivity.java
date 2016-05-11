package com.mercadopago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mercadopago.model.Payment;
import com.mercadopago.views.MPTextView;

import static android.text.TextUtils.isEmpty;

public class RejectionActivity extends AppCompatActivity {

    // Controls
    protected MPTextView mPaymentMethodRejectedDescription;

    // Activity parameters
    protected String mMerchantPublicKey;

    protected Payment mPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rejection);

        getActivityParameters();
        initializeControls();
        setLayoutData();
    }

    //TODO setear el comportamiento del botón
    //TODO agregar el set del botón de aliExpress
    protected void setLayoutData() {
        if (mPayment != null) {
            setPaymentMethodRejection();

        }
        else {
            //TODO validar si mandamos RESULT_CANCELED
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    protected void setPaymentMethodRejection(){
        if (mPayment.getCard() != null && !isEmpty(mPayment.getCard().getPaymentMethod().getId())){
            mPaymentMethodRejectedDescription.setText(getString(R.string.mpsdk_title_rejection) + mPayment.getCard().getPaymentMethod().getId());
        }
        else{
            mPaymentMethodRejectedDescription.setVisibility(View.GONE);
        }
    }

    protected void initializeControls() {
        mPaymentMethodRejectedDescription = (MPTextView) findViewById(R.id.paymentMethodRejectedDescription);
    }

    protected void getActivityParameters() {
        mPayment = (Payment) this.getIntent().getSerializableExtra("payment");
    }

}
