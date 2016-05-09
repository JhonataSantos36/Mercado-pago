package com.mercadopago;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mercadopago.views.MPTextView;

public class CongratsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_congrats);



        MPTextView emailDescription = (MPTextView) findViewById(R.id.emailDescription);
        emailDescription.setText("matias.romar@mercadolibre.com");

        MPTextView paymentMethod = (MPTextView) findViewById(R.id.paymentMethod);
        paymentMethod.setText("9999");

        MPTextView installmentsDescription = (MPTextView) findViewById(R.id.installmentsDescription);
        installmentsDescription.setText("12 de 9999 (999999999)");

        MPTextView acquittanceDescription = (MPTextView) findViewById(R.id.acquittanceDescription);
        acquittanceDescription.setText("Comprobante 3333333333");

    }
}
