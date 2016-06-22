package com.mercadopago.examples.services;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mercadopago.examples.R;
import com.mercadopago.examples.services.step1.Step1Activity;
import com.mercadopago.examples.services.step2.Step2Activity;
import com.mercadopago.examples.services.step3.Step3Activity;
import com.mercadopago.examples.services.step4.Step4Activity;
import com.mercadopago.examples.services.step5.Step5Activity;

public class ServicesExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_example);
    }

    public void runStep1(View view) {

        runStep(new Step1Activity());
    }

    public void runStep2(View view) {

        runStep(new Step2Activity());
    }

    public void runStep3(View view) {

        runStep(new Step3Activity());
    }

    public void runStep4(View view) {

        runStep(new Step4Activity());
    }

    public void runStep5(View view) {

        runStep(new Step5Activity());
    }

    private void runStep(Activity activity) {

        Intent exampleIntent = new Intent(this, activity.getClass());
        startActivity(exampleIntent);
    }
}
