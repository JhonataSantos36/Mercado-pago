package com.mercadopago.examples.Step8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.examples.R;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;

import java.math.BigDecimal;

public class F2Activity extends AppCompatActivity {

    private String mMerchantPublicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f2);
        mMerchantPublicKey = "APP_USR-5a399d42-6015-4f6a-8ff8-dd7d368068f8";
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            if(resultCode == RESULT_CANCELED && data.hasExtra("mpException")) {
                MPException mpException = (MPException) data.getSerializableExtra("mpException");
                Toast.makeText(this, mpException.getMessage()+ ". "+ mpException.getErrorDetail(), Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == MercadoPago.INSTRUCTIONS_REQUEST_CODE) {
            if(resultCode == RESULT_CANCELED && data != null && data.hasExtra("mpException")) {
                MPException mpException = (MPException) data.getSerializableExtra("mpException");
                Toast.makeText(this, mpException.getMessage()+ ". "+ mpException.getErrorDetail(), Toast.LENGTH_LONG).show();
            }
        }

    }
    public void submitForm(View view) {
        PaymentMethodSearch paymentMethodSearch = new PaymentMethodSearch();

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPaymentMethodSearch(paymentMethodSearch)
                .setPublicKey(mMerchantPublicKey)
                .setAmount(new BigDecimal(100))
                .startPaymentVaultActivity();
    }

    public void getInstructions(View view) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("bapropagos");
        paymentMethod.setPaymentTypeId("atm");

        Payment payment = new Payment();
        payment.setId((long)1826446924);


        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPayment(payment)
                .setPaymentMethod(paymentMethod)
                .setPublicKey(mMerchantPublicKey)
                .setAmount(new BigDecimal(100))
                .startInstructionsActivity();
    }
}
