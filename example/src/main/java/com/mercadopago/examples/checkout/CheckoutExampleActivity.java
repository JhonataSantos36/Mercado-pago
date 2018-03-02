package com.mercadopago.examples.checkout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.Sites;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.customviews.MPButton;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.hooks.ExampleHooks;
import com.mercadopago.model.Payment;
import com.mercadopago.plugins.DataInitializationTask;
import com.mercadopago.plugins.MainPaymentProcessor;
import com.mercadopago.plugins.SamplePaymentMethodPlugin;
import com.mercadopago.plugins.SamplePaymentProcessor;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.review_and_confirm.models.PaymentModel;
import com.mercadopago.review_and_confirm.ReviewAndConfirmActivity;
import com.mercadopago.review_and_confirm.models.TermsAndConditionsModel;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.util.HashMap;
import java.util.Map;

public class CheckoutExampleActivity extends AppCompatActivity {

    private Activity mActivity;
    private ProgressBar mProgressBar;
    private View mRegularLayout;
    private String mPublicKey;


    private CheckBox mHooksEnabled;

    private String mCheckoutPreferenceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_example);
        mActivity = this;
        mPublicKey = ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY;
        mCheckoutPreferenceId = ExamplesUtils.DUMMY_PREFERENCE_ID;

        mProgressBar = findViewById(R.id.progressBar);
        mRegularLayout = findViewById(R.id.regularLayout);

        mHooksEnabled = findViewById(R.id.hooks_enabled);

        TextView mJsonConfigButton = findViewById(R.id.jsonConfigButton);
        MPButton mContinueButton = findViewById(R.id.continueButton);

        mJsonConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJsonInput();
            }
        });
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContinueClicked();
            }
        });
    }

    private void onContinueClicked() {
        TermsAndConditionsModel termsAndConditionsModel = new TermsAndConditionsModel(Sites.ARGENTINA.getId(), true);
        PaymentModel paymentModel = new PaymentModel("debvisa",
                "1234",
                "MasterCard",
                PaymentTypes.BANK_TRANSFER,
                1000,
                "HSBC", true, R.drawable.mpsdk_account_money);
        ReviewAndConfirmActivity.start(this, termsAndConditionsModel, paymentModel);
        //startMercadoPagoCheckout();
    }

    private void startMercadoPagoCheckout() {

        final Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("amount", 120f);

        final MercadoPagoCheckout.Builder builder = new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setCheckoutPreference(getCheckoutPreference())
                .addPaymentMethodPlugin(
                        new SamplePaymentMethodPlugin(),
                        new SamplePaymentProcessor()
                )
                .setPaymentProcessor(new MainPaymentProcessor())
                .setDataInitializationTask(new DataInitializationTask(defaultData) {
                    @Override
                    public void onLoadData(@NonNull final Map<String, Object> data) {
                        data.put("user", "Nico");
                    }
                });

        if (mHooksEnabled.isChecked()) {
            builder.setCheckoutHooks(new ExampleHooks());
        }

        builder.startForPayment();
    }

    private CheckoutPreference getCheckoutPreference() {
        return new CheckoutPreference(mCheckoutPreferenceId);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        LayoutUtil.showRegularLayout(this);

        if (requestCode == MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {
                Payment payment = JsonUtil.getInstance().fromJson(data.getStringExtra("payment"), Payment.class);
                Toast.makeText(mActivity, "Pago con status: " + payment.getStatus(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    MercadoPagoError mercadoPagoError = JsonUtil.getInstance().fromJson(data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
                    Toast.makeText(mActivity, "Error: " + mercadoPagoError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Cancel", Toast.LENGTH_SHORT).show();
                }
            }
        }
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