package com.mercadopago.examples.services.step3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mercadopago.ExampleActivity;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.MercadoPago;
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

public class Step3Activity extends ExampleActivity {

    private MPButton mContinueButton;

    protected List<String> mExcludedPaymentTypes = new ArrayList<String>() {{
        add(PaymentTypes.TICKET);
        add(PaymentTypes.ATM);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);
        mContinueButton = findViewById(R.id.btnSimpleFlow);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSimpleForm();
            }
        });
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
                        issuerId, JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class), null);

            } else {

                if (data != null && data.getStringExtra("apiException") != null) {
                    ApiException apiException = JsonUtil.getInstance().fromJson(data.getStringExtra("apiException"), ApiException.class);
                    Toast.makeText(getApplicationContext(), apiException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == MercadoPago.PAYMENT_RESULT_REQUEST_CODE) {
            LayoutUtil.showRegularLayout(this);
        }
    }

    private void submitSimpleForm() {
        // Call final vault activity
        ExamplesUtils.startAdvancedVaultActivity(this, ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY_EXAMPLES_SERVICE,
                ExamplesUtils.DUMMY_MERCHANT_BASE_URL, ExamplesUtils.DUMMY_MERCHANT_GET_CUSTOMER_URI,
                ExamplesUtils.DUMMY_MERCHANT_ACCESS_TOKEN, new BigDecimal("20"), mExcludedPaymentTypes);
    }
}
