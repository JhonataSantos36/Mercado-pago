package com.mercadopago.examples.services.step4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mercadopago.ExampleActivity;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.customviews.MPButton;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Step4Activity extends ExampleActivity {

    private MPButton mContinueButton;

    protected List<String> mExcludedPaymentTypes = new ArrayList<String>(){{
        add(PaymentTypes.DEBIT_CARD);
        add(PaymentTypes.CREDIT_CARD);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step4);
        mContinueButton = findViewById(R.id.continueButton);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ExamplesUtils.FINAL_VAULT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Set issuer id
                Long issuerId = (data.getStringExtra("issuerId") != null)
                        ? Long.parseLong(data.getStringExtra("issuerId")) : null;

                // Set installments
                Integer installments = (data.getStringExtra("installments") != null)
                        ? Integer.parseInt(data.getStringExtra("installments")) : null;

                // Create payment
                ExamplesUtils.createPayment(this, data.getStringExtra("token"),
                        installments, issuerId,
                        JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class), null);

            } else {

                if (data != null && data.getStringExtra("apiException") != null) {
                    ApiException apiException = JsonUtil.getInstance().fromJson(data.getStringExtra("apiException"), ApiException.class);
                    Toast.makeText(getApplicationContext(), apiException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            LayoutUtil.showRegularLayout(this);
        }
    }

    public void submitForm() {

        // Call final vault activity
        ExamplesUtils.startFinalVaultActivity(this, ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY,
                ExamplesUtils.DUMMY_MERCHANT_BASE_URL, ExamplesUtils.DUMMY_MERCHANT_GET_CUSTOMER_URI,
                ExamplesUtils.DUMMY_MERCHANT_ACCESS_TOKEN, new BigDecimal("20"), mExcludedPaymentTypes);
    }
}
