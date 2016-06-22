package com.mercadopago.examples.services.step5;

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
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.util.LayoutUtil;

import java.util.ArrayList;
import java.util.List;

public class Step5Activity extends ExampleActivity {


    protected List<String> mExcludedPaymentTypesIds = new ArrayList<String>(){{

    }};
    protected List<String> mExcludedPaymentMethodIds = new ArrayList<String>(){{
//        add("visa");
    }};
    protected PaymentPreference mPaymentPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step5);
        createPaymentPreference();
    }

    private void createPaymentPreference() {
        mPaymentPreference = new PaymentPreference();
        mPaymentPreference.setDefaultInstallments(ExamplesUtils.DUMMY_DEFAULT_INSTALLMENTS);
        mPaymentPreference.setExcludedPaymentMethodIds(mExcludedPaymentMethodIds);
        mPaymentPreference.setExcludedPaymentTypeIds(mExcludedPaymentTypesIds);
        mPaymentPreference.setMaxAcceptedInstallments(ExamplesUtils.DUMMY_MAX_INSTALLMENTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
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
                        (PaymentMethod) data.getSerializableExtra("paymentMethod"), null);

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
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(mExcludedPaymentTypesIds);
        paymentPreference.setExcludedPaymentMethodIds(mExcludedPaymentMethodIds);
        paymentPreference.setMaxAcceptedInstallments(ExamplesUtils.DUMMY_MAX_INSTALLMENTS);
        // Call final vault activity
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY)
                .setMerchantBaseUrl(ExamplesUtils.DUMMY_MERCHANT_BASE_URL)
                .setMerchantGetCustomerUri(ExamplesUtils.DUMMY_MERCHANT_GET_CUSTOMER_URI)
                .setMerchantAccessToken(ExamplesUtils.DUMMY_MERCHANT_ACCESS_TOKEN)
                .setAmount(ExamplesUtils.DUMMY_ITEM_UNIT_PRICE)
                .setPaymentPreference(mPaymentPreference)
                .setShowBankDeals(true)
                .startPaymentVaultActivity();
    }

    public void submitGuessingForm(View view){
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(mExcludedPaymentTypesIds);
        paymentPreference.setExcludedPaymentMethodIds(mExcludedPaymentMethodIds);
        paymentPreference.setMaxAcceptedInstallments(ExamplesUtils.DUMMY_MAX_INSTALLMENTS);
        paymentPreference.setDefaultInstallments(ExamplesUtils.DUMMY_DEFAULT_INSTALLMENTS);
        // Call final vault activity
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY)
                .setMerchantBaseUrl(ExamplesUtils.DUMMY_MERCHANT_BASE_URL)
                .setMerchantGetCustomerUri(ExamplesUtils.DUMMY_MERCHANT_GET_CUSTOMER_URI)
                .setMerchantAccessToken(ExamplesUtils.DUMMY_MERCHANT_ACCESS_TOKEN)
                .setAmount(ExamplesUtils.DUMMY_ITEM_UNIT_PRICE)
                .setPaymentPreference(mPaymentPreference)
                .setShowBankDeals(true)
                .startPaymentVaultActivity();
    }
}
