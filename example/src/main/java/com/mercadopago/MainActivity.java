package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mercadopago.examples.R;
import com.mercadopago.examples.checkout.CheckoutExampleActivity;
import com.mercadopago.examples.components.ComponentsExampleActivity;
import com.mercadopago.examples.services.ServicesExampleActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .build());
    }

    public void runCheckoutExample(View view) {

        runStep(new CheckoutExampleActivity());
    }

    public void runComponentsExample(View view) {

        runStep(new ComponentsExampleActivity());
    }

    public void runServicesExample(View view) {

        runStep(new ServicesExampleActivity());
    }

    private void runStep(Activity activity) {

        Intent exampleIntent = new Intent(this, activity.getClass());
        startActivity(exampleIntent);
    }
}