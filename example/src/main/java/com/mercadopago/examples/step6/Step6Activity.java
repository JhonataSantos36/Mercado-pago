package com.mercadopago.examples.step6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mercadopago.ExampleActivity;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CheckoutIntent;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Item;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Step6Activity extends ExampleActivity {

    private CheckoutPreference mCheckoutPreference;
    private String mMerchantPublicKey;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step6);
        mMerchantPublicKey = ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY;
        mActivity = this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LayoutUtil.showRegularLayout(this);

        if (requestCode == MercadoPago.CHECKOUT_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                // Set message
                /*Payment payment = (Payment) data.getSerializableExtra("payment");

                new AlertDialog.Builder(this)
                        .setTitle("Test")
                        .setMessage("Payment Id:" + payment.getId())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show(); */
            } else {
                if ((data != null) && (data.getSerializableExtra("mpException") != null)) {
                    MPException mpException = (MPException) data.getSerializableExtra("mpException");
                    Toast.makeText(getApplicationContext(), mpException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void submitForm(View view) {

        LayoutUtil.showProgressLayout(this);
        Map<String, Object> map = new HashMap<>();
        map.put("item_id", "1");
        map.put("amount", new BigDecimal(300));
        MerchantServer.createPreference(this, "http://private-9376e-paymentmethodsmla.apiary-mock.com/",
                "merchantUri/merchant_preference", map, new Callback<CheckoutPreference>() {
            @Override
            public void success(CheckoutPreference checkoutPreference, Response response) {
                mCheckoutPreference = checkoutPreference;
                startCheckoutActivity(mMerchantPublicKey);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(mActivity, "Preference creation failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void startCheckoutActivity(String publicKey)
    {
        //PREF CON SOLO CARGAVIRTUAL: 150216849-b7fb60e9-aee2-40af-a3de-b5b2e57e4e61
        //PREF CON SOLO TC: 150216849-db0ef449-0f5c-49e9-83c6-087f5edfc2d3
        //PREF SIN EXCLUSIONES: 150216849-53df0831-8142-4b7c-b7ce-af51fa48dffa
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(publicKey)
                .setCheckoutPreferenceId(mCheckoutPreference.getId())
                .startCheckoutActivity();
    }

    public void submitFormTest(View view) {

        LayoutUtil.showProgressLayout(this);

        Map<String, Object> map = new HashMap<>();
        map.put("item_id", "1");
        map.put("amount", new BigDecimal(300));
        MerchantServer.createPreference(this, "http://private-9376e-paymentmethodsmla.apiary-mock.com/",
            "merchantUri/merchant_preference", map, new Callback<CheckoutPreference>() {
            @Override
            public void success(CheckoutPreference checkoutPreference, Response response) {
                mCheckoutPreference = checkoutPreference;
                startCheckoutActivity("TEST-ad365c37-8012-4014-84f5-6c895b3f8e0a");
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(mActivity, "Preference creation failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
