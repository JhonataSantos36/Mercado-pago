package com.mercadopago.examples.checkout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ColorPickerDialog;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Payment;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

public class CheckoutExampleActivity extends AppCompatActivity {

    private Activity mActivity;
    private ProgressBar mProgressBar;
    private View mRegularLayout;
    private String mPublicKey;

    private Integer mDefaultColor;
    private CheckBox mDarkFontEnabled;
    private ImageView mColorSample;
    private Integer mSelectedColor;
    private CheckBox mVisaExcluded;
    private CheckBox mCashExcluded;
    private TextView mJsonConfigButton;
    private String mCheckoutPreferenceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_example);
        mActivity = this;
        mPublicKey = ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY;
        mCheckoutPreferenceId = ExamplesUtils.DUMMY_PREFERENCE_ID;
        mDefaultColor = ContextCompat.getColor(this, R.color.colorPrimary);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRegularLayout = findViewById(R.id.regularLayout);
        mDarkFontEnabled = (CheckBox) findViewById(R.id.darkFontEnabled);
        mColorSample = (ImageView) findViewById(R.id.colorSample);
        mVisaExcluded = (CheckBox) findViewById(R.id.visaExcluded);
        mCashExcluded = (CheckBox) findViewById(R.id.cashExcluded);
        mJsonConfigButton = (TextView) findViewById(R.id.jsonConfigButton);
        mJsonConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJsonInput();
            }
        });

        mSelectedColor = ContextCompat.getColor(this, R.color.mpsdk_colorPrimary);
    }

    public void onContinueClicked(View view) {
        startMercadoPagoCheckout();
    }

    private void startMercadoPagoCheckout() {
        new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setCheckoutPreference(getCheckoutPreference())
                .setDecorationPreference(getCurrentDecorationPreference())
                .startForPayment();
    }


    private CheckoutPreference getCheckoutPreference() {
        return new CheckoutPreference(mCheckoutPreferenceId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    public void changeColor(View view) {
        new ColorPickerDialog(this, mDefaultColor, color -> {
            mDarkFontEnabled.setEnabled(true);
            mColorSample.setBackgroundColor(color);
            mSelectedColor = color;
        }).show();
    }

    public void resetSelection(View view) {
        mSelectedColor = null;
        mColorSample.setBackgroundColor(mDefaultColor);
        mDarkFontEnabled.setChecked(false);
        mDarkFontEnabled.setEnabled(false);
        mVisaExcluded.setChecked(false);
        mCashExcluded.setChecked(false);
    }

    private DecorationPreference getCurrentDecorationPreference() {
        DecorationPreference.Builder decorationPreferenceBuilder =
                new DecorationPreference.Builder();
        if (mSelectedColor != null) {
            decorationPreferenceBuilder.setBaseColor(mSelectedColor);

            if (mDarkFontEnabled.isChecked()) {
                decorationPreferenceBuilder.enableDarkFont();
            }
        }
        return decorationPreferenceBuilder.build();
    }

    private void startJsonInput() {
        Intent intent = new Intent(this, JsonSetupActivity.class);
        startActivityForResult(intent, MercadoPagoCheckout.CHECKOUT_REQUEST_CODE);
    }
}