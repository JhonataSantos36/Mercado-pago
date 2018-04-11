package com.mercadopago;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.example.R;
import com.mercadopago.utils.CheckoutConfiguration;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.TextUtil;

public class JsonSetupActivity extends AppCompatActivity {

    private EditText mJsonInputEditText;
    private Button mStartCheckoutButton;
    private ImageView mStatusImageView;
    private CheckoutConfiguration mConfiguration;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_setup);
        initializeViews();
        setupJsonValidation();
        setupCheckoutStart();
    }

    private void initializeViews() {
        mJsonInputEditText = findViewById(R.id.jsonEditText);
        mStartCheckoutButton = findViewById(R.id.checkoutStartButton);
        mStatusImageView = findViewById(R.id.statusImage);

        mScrollView = findViewById(R.id.setupScrollView);
        mScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        mScrollView.setFocusable(true);
        mScrollView.setFocusableInTouchMode(true);
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
    }

    private void setupJsonValidation() {
        mJsonInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do something
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateJson();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do something
            }
        });
    }

    private void setupCheckoutStart() {
        mStartCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCheckout();
            }
        });
    }

    private void startCheckout() {
        MercadoPagoCheckout.Builder checkoutBuilder = new MercadoPagoCheckout.Builder();

        checkoutBuilder.setActivity(this);
        if (!TextUtil.isEmpty(mConfiguration.getPublicKey())) {
            checkoutBuilder.setPublicKey(mConfiguration.getPublicKey());
        }

        if (!TextUtil.isEmpty(mConfiguration.getPrefId())) {
            checkoutBuilder.setCheckoutPreference(new CheckoutPreference(mConfiguration.getPrefId()));
        } else {
            CheckoutPreference preference = createCheckoutPreference(mConfiguration);
            checkoutBuilder.setCheckoutPreference(preference);
        }

        if (mConfiguration.getServicePreference() != null) {
            completeServicePreferenceWithDefaultValues();
            checkoutBuilder.setServicePreference(mConfiguration.getServicePreference());
        }

        checkoutBuilder.setFlowPreference(mConfiguration.getFlowPreference());

        if (mConfiguration.paymentRequired()) {
            checkoutBuilder.startForPayment();
        } else if (mConfiguration.paymentDataRequired()) {
            checkoutBuilder.startForPaymentData();
        } else {
            Toast.makeText(this, R.string.start_for_wrong, Toast.LENGTH_SHORT).show();
        }
    }

    private void completeServicePreferenceWithDefaultValues() {
        if (mConfiguration.getServicePreference() != null) {
            ServicePreference servicePreference = mConfiguration.getServicePreference();
            if (servicePreference.getProcessingModeString() == null || servicePreference.getProcessingModeString().isEmpty()) {

                ServicePreference.Builder builder = new ServicePreference.Builder();
                if (servicePreference.hasGetCustomerURL()) {
                    builder.setGetCustomerURL(servicePreference.getGetCustomerURL(), servicePreference.getGetCustomerURI(), servicePreference.getGetCustomerAdditionalInfo());
                }
                if (servicePreference.hasCreatePaymentURL()) {
                    builder.setCreatePaymentURL(servicePreference.getCreatePaymentURL(), servicePreference.getCreatePaymentURI(), servicePreference.getCreatePaymentAdditionalInfo());
                }
                if (servicePreference.hasGetDiscountURL()) {
                    builder.setDiscountURL(servicePreference.getGetMerchantDiscountBaseURL(), servicePreference.getGetMerchantDiscountURI(), servicePreference.getGetDiscountAdditionalInfo());
                }
                if (servicePreference.hasCreateCheckoutPrefURL()) {
                    builder.setCreateCheckoutPreferenceURL(servicePreference.getCreateCheckoutPreferenceURL(), servicePreference.getCreateCheckoutPreferenceURI(), servicePreference.getCreateCheckoutPreferenceAdditionalInfo());
                }
                builder.setDefaultBaseURL(servicePreference.getDefaultBaseURL())
                        .setGatewayURL(servicePreference.getGatewayBaseURL());

                ServicePreference newServicePreference = builder.build();

                mConfiguration.setServicePreference(newServicePreference);
            }
        }
    }

    private CheckoutPreference createCheckoutPreference(CheckoutConfiguration checkoutConfiguration) {
        return new CheckoutPreference.Builder()
                .addItems(checkoutConfiguration.getItems())
                .setPayerEmail(checkoutConfiguration.getPayerEmail())
                .setSite(checkoutConfiguration.getSite())
                .build();
    }

    private void validateJson() {
        boolean isOk = false;
        String configsJson = mJsonInputEditText.getText().toString();
        try {
            mConfiguration = JsonUtil.getInstance().fromJson(configsJson, CheckoutConfiguration.class);
            if (!TextUtil.isEmpty(configsJson)) {
                isOk = true;
            }
        } catch (JsonSyntaxException exception) {
            //Do nothing
        }
        updateSetupStatus(isOk);
    }

    private void updateSetupStatus(boolean ok) {
        if (ok) {
            Drawable okImage = getResources().getDrawable(R.drawable.mpsdk_ok_sign);
            okImage.setColorFilter(ContextCompat.getColor(this, R.color.examples_green), PorterDuff.Mode.SRC_ATOP);
            mStatusImageView.setImageDrawable(okImage);
            mStartCheckoutButton.setEnabled(true);
        } else {
            mStatusImageView.setImageResource(R.drawable.mpsdk_icon_error);
            mStartCheckoutButton.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setResult(resultCode, data);
        finish();
    }
}
