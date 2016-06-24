package com.mercadopago.examples.services.step4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mercadopago.examples.R;
import com.mercadopago.examples.services.step3.AdvancedVaultActivity;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

public class FinalVaultActivity extends AdvancedVaultActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    protected void setContentView() {

        setContentView(R.layout.activity_final_vault);
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    protected void resolvePaymentMethodsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mTempIssuer = null;
            mTempPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");

            if (MercadoPagoUtil.isCardPaymentType(mTempPaymentMethod.getPaymentTypeId())) {  // Card-like methods

                if (mTempPaymentMethod.isIssuerRequired()) {

                    // Call issuer activity
                    startIssuersActivity();

                } else {

                    // Call new cards activity
                    startCardActivity();
                }
            } else {  // Off-line methods

                // Set selection status
                mPayerCosts = null;
                mToken = null;
                mSelectedPaymentMethodRow = null;
                mSelectedPayerCost = null;
                mSelectedPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
                mSelectedIssuer = null;

                // Set customer method selection
                mCustomerMethodsText.setText(mSelectedPaymentMethod.getName());
                mPaymentMethodImage.setImageResource(MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId()));

                // Set security cards visibility
                mSecurityCodeCard.setVisibility(View.GONE);

                // Set installments visibility
                mInstallmentsCard.setVisibility(View.GONE);

                // Set button visibility
                mSubmitButton.setEnabled(true);
            }
        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            } else if ((mSelectedPaymentMethodRow == null) && (mToken == null)) {
                // if nothing is selected
                finish();
            }
        }
    }

    @Override
    public void submitForm(View view) {

        LayoutUtil.hideKeyboard(mActivity);

        // Validate installments
        if (((mSelectedPaymentMethodRow != null) || (mToken != null)) && mSelectedPayerCost == null) {
            return;
        }

        // Create token
        if (mSelectedPaymentMethodRow != null) {

            createSavedCardToken();

        }
        else {  // Off-line methods

            // Return payment method id
            LayoutUtil.showRegularLayout(mActivity);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("paymentMethod", mSelectedPaymentMethod);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }
}
