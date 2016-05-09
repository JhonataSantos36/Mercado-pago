package com.mercadopago.examples.step6;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mercadopago.ExampleActivity;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CheckoutIntent;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Item;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

public class Step6Activity extends ExampleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step6);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPago.CHECKOUT_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                // Set message
                Payment payment = (Payment) data.getSerializableExtra("payment");

                new AlertDialog.Builder(this)
                        .setTitle("Test")
                        .setMessage("Payment Id:" + payment.getId())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                if ((data != null) && (data.getSerializableExtra("mpException") != null)) {
                    MPException mpException = (MPException) data.getSerializableExtra("mpException");
                    Toast.makeText(getApplicationContext(), mpException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void submitForm(View view) {
        startCheckoutActivity("TEST-b130744e-3dc5-4809-b027-599109307f1e");
    }

    private void startCheckoutActivity(String publicKey)
    {

        // Set item
        Item item = new Item(ExamplesUtils.DUMMY_ITEM_ID, ExamplesUtils.DUMMY_ITEM_QUANTITY);

        // Set checkout intent
        CheckoutIntent checkoutIntent = new CheckoutIntent(ExamplesUtils.DUMMY_MERCHANT_ACCESS_TOKEN, item);

        CheckoutPreference mockPreference = JsonUtil.getInstance().fromJson(ExamplesUtils.getFile(this, "mocks/preference_with_exclusions.json"), CheckoutPreference.class);
        // Call final vault activity
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(publicKey)
                .setCheckoutPreference(mockPreference)
                .setShowBankDeals(true)
                .startCheckoutActivity();

        LayoutUtil.showRegularLayout(this);
    }

}
