package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.core.Settings;
import com.mercadopago.example.R;
import com.mercadopago.utils.ExamplesUtils;
import com.mercadopago.tracking.constants.TrackingEnvironments;

import static com.mercadopago.utils.ExamplesUtils.resolveCheckoutResult;

public class CheckoutExampleActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private View mRegularLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .build());

        Settings.trackingEnvironment = TrackingEnvironments.STAGING;

        setContentView(R.layout.activity_checkout_example);
        mProgressBar = findViewById(R.id.progressBar);
        mRegularLayout = findViewById(R.id.regularLayout);

        View jsonConfigurationButton = findViewById(R.id.jsonConfigButton);
        View continueSimpleCheckout = findViewById(R.id.continueButton);
        View selectCheckoutButton = findViewById(R.id.select_checkout);

        jsonConfigurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJsonInput();
            }
        });

        continueSimpleCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContinueClicked();
            }
        });

        selectCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(CheckoutExampleActivity.this, SelectCheckoutActivity.class));
            }
        });
    }

    private void onContinueClicked() {
        MercadoPagoCheckout.Builder builder = ExamplesUtils.createBase(this);
        builder.startForPayment();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        resolveCheckoutResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        showRegularLayout();
    }

    private void showRegularLayout() {
        mProgressBar.setVisibility(View.GONE);
        mRegularLayout.setVisibility(View.VISIBLE);
    }

    private void startJsonInput() {
        Intent intent = new Intent(this, JsonSetupActivity.class);
        startActivityForResult(intent, MercadoPagoCheckout.CHECKOUT_REQUEST_CODE);
    }
}