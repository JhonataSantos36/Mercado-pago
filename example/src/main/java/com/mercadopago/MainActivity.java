package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mercadopago.examples.R;
import com.mercadopago.examples.Step8.F2Activity;
import com.mercadopago.examples.step1.Step1Activity;
import com.mercadopago.examples.step2.Step2Activity;
import com.mercadopago.examples.step3.Step3Activity;
import com.mercadopago.examples.step4.Step4Activity;
import com.mercadopago.examples.step5.Step5Activity;
import com.mercadopago.examples.step6.Step6Activity;
import com.mercadopago.examples.step7.Step7Activity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void runStep6(View view) {

        runStep(new Step6Activity());
    }

    public void runStep7(View view) {
        runStep(new Step7Activity());
    }

    public void runStepF2(View view) {
        runStep(new F2Activity());
    }

    private void runStep(Activity activity) {

        Intent exampleIntent = new Intent(this, activity.getClass());
        startActivity(exampleIntent);
    }
}