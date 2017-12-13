package com.mercadopago.examples.services.step1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mercadopago.ExampleActivity;
import com.mercadopago.constants.PaymentMethods;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.customviews.MPButton;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.model.Token;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.util.ArrayList;
import java.util.List;

public class Step1Activity extends ExampleActivity {

    private MPButton mSubmitButton;

    protected List<String> mExcludedPaymentTypeIds = new ArrayList<String>() {{
        add(PaymentTypes.TICKET);
        add(PaymentTypes.DIGITAL_CURRENCY);
    }};

    protected List<String> mExcludedPaymentMethodIds = new ArrayList<String>() {{
        add(PaymentMethods.ARGENTINA.VISA);
    }};
    protected Activity mActivity;
    protected PaymentPreference mPaymentPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);
        mActivity = this;
        mPaymentPreference = new PaymentPreference();
        mPaymentPreference.setExcludedPaymentTypeIds(mExcludedPaymentTypeIds);
        mPaymentPreference.setExcludedPaymentMethodIds(mExcludedPaymentMethodIds);
        mSubmitButton = findViewById(R.id.btnSimpleFlow);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSimpleForm();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Set payment method
                PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

                // Call new cards activity
                ExamplesUtils.startCardActivity(this, ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY_EXAMPLES_SERVICE, paymentMethod);
            } else {

                if ((data != null) && (data.getStringExtra("apiException") != null)) {
                    ApiException apiException = JsonUtil.getInstance().fromJson(data.getStringExtra("apiException"), ApiException.class);
                    Toast.makeText(getApplicationContext(), apiException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == ExamplesUtils.CARD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
                PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
                // Create payment
                ExamplesUtils.createPayment(this, token.getId(),
                        1, null, paymentMethod, null);

            } else {

                if (data != null) {
                    if (data.getStringExtra("apiException") != null) {
                        ApiException apiException = JsonUtil.getInstance().fromJson(data.getStringExtra("apiException"), ApiException.class);
                        Toast.makeText(getApplicationContext(), apiException.getMessage(), Toast.LENGTH_LONG).show();

                    } else if (data.getBooleanExtra("backButtonPressed", false)) {
                        new MercadoPago.StartActivityBuilder()
                                .setActivity(this)
                                .setPublicKey(ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY_EXAMPLES_SERVICE)
                                .setPaymentPreference(mPaymentPreference)
                                .startPaymentMethodsActivity();
                    }
                }
            }
        } else if (requestCode == MercadoPago.PAYMENT_RESULT_REQUEST_CODE) {
            LayoutUtil.showRegularLayout(this);
        }
    }

    private void submitSimpleForm() {

        // Call payment methods activity
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY_EXAMPLES_SERVICE)
                .setPaymentPreference(mPaymentPreference)
                .setShowBankDeals(false)
                .startPaymentMethodsActivity();
    }

}