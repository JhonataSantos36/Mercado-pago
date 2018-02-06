package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.mercadopago.core.Settings;
import com.mercadopago.examples.R;
import com.mercadopago.examples.checkout.CheckoutExampleActivity;
import com.mercadopago.tracking.constants.TrackingEnvironments;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .build());
        Settings.trackingEnvironment = TrackingEnvironments.STAGING;
    }

    public void runCheckoutExample(View view) {

        runStep(new CheckoutExampleActivity());
    }

    private void runStep(Activity activity) {

        Intent exampleIntent = new Intent(this, activity.getClass());
        startActivity(exampleIntent);
    }
}