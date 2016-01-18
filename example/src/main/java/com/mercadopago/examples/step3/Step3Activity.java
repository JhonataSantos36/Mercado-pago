package com.mercadopago.examples.step3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mercadopago.ExampleActivity;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.LayoutUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Step3Activity extends ExampleActivity {

    protected List<String> mExcludedPaymentTypes = new ArrayList<String>(){{
        add("ticket");
        add("atm");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ExamplesUtils.ADVANCED_VAULT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Long issuerId = (data.getStringExtra("issuerId") != null)
                        ? Long.parseLong(data.getStringExtra("issuerId")) : null;

                // Create payment
                ExamplesUtils.createPayment(this, data.getStringExtra("token"),
                        Integer.parseInt(data.getStringExtra("installments")),
                        issuerId, (PaymentMethod) data.getSerializableExtra("paymentMethod"), null);

            } else {

                if (data != null && data.getSerializableExtra("apiException") != null) {
                    ApiException apiException = (ApiException) data.getSerializableExtra("apiException");
                    Toast.makeText(getApplicationContext(), apiException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {

            LayoutUtil.showRegularLayout(this);
        }
    }

    public void submitSimpleForm(View view) {
        // Call final vault activity
        ExamplesUtils.startAdvancedVaultActivity(this, ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY,
                ExamplesUtils.DUMMY_MERCHANT_BASE_URL, ExamplesUtils.DUMMY_MERCHANT_GET_CUSTOMER_URI,
                ExamplesUtils.DUMMY_MERCHANT_ACCESS_TOKEN, new BigDecimal("20"), mExcludedPaymentTypes);
    }

    public void submitGuessingForm(View view) {
        // Call final vault activity
        ExamplesUtils.startAdvancedVaultActivityWithGuessing(this, ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY,
                ExamplesUtils.DUMMY_MERCHANT_BASE_URL, ExamplesUtils.DUMMY_MERCHANT_GET_CUSTOMER_URI,
                ExamplesUtils.DUMMY_MERCHANT_ACCESS_TOKEN, new BigDecimal("20"), mExcludedPaymentTypes);
    }
}
