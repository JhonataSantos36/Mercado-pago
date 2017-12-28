package com.mercadopago.examples.services;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mercadopago.customviews.MPButton;
import com.mercadopago.examples.R;
import com.mercadopago.examples.services.step1.Step1Activity;
import com.mercadopago.examples.services.step2.Step2Activity;
import com.mercadopago.examples.services.step3.Step3Activity;
import com.mercadopago.examples.services.step4.Step4Activity;

public class ServicesExampleActivity extends AppCompatActivity {

    private MPButton mStep1Button;
    private MPButton mStep2Button;
    private MPButton mStep3Button;
    private MPButton mStep4Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_example);
        mStep1Button = findViewById(R.id.buttonStep1);
        mStep2Button = findViewById(R.id.buttonStep2);
        mStep3Button = findViewById(R.id.buttonStep3);
        mStep4Button = findViewById(R.id.buttonStep4);

        mStep1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runStep1();
            }
        });
        mStep2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runStep2();
            }
        });
        mStep3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runStep3();
            }
        });
        mStep4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runStep4();
            }
        });
    }

    public void runStep1() {

        runStep(new Step1Activity());
    }

    public void runStep2() {

        runStep(new Step2Activity());
    }

    public void runStep3() {

        runStep(new Step3Activity());
    }

    public void runStep4() {

        runStep(new Step4Activity());
    }

    private void runStep(Activity activity) {

        Intent exampleIntent = new Intent(this, activity.getClass());
        startActivity(exampleIntent);
    }
}
