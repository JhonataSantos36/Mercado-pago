package com.mercadopago.examples.step1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mercadopago.CongratsActivity;
import com.mercadopago.ExampleActivity;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Token;
import com.mercadopago.model.TransactionDetails;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Step1Activity extends ExampleActivity {

    protected List<String> mExcludedPaymentTypeIds = new ArrayList<String>(){{
        add("atm");
        add("ticket");
        add("digital_currency");
    }};

    protected List<String> mExcludedPaymentMethodIds = new ArrayList<String>(){{
        add("visa");
    }};
    protected Activity mActivity;
    protected PaymentPreference mPaymentPreference;
    protected BigDecimal mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);
        createPaymentPreference();
        mActivity = this;
        mPaymentPreference = new PaymentPreference();
        mPaymentPreference.setExcludedPaymentTypeIds(mExcludedPaymentTypeIds);
        mPaymentPreference.setExcludedPaymentMethodIds(mExcludedPaymentMethodIds);
        mAmount = ExamplesUtils.DUMMY_ITEM_UNIT_PRICE;

        //TODO borrar, test congrats
        Payment payment = new Payment();
        payment.setStatus("approved");
        payment.setStatusDetail("cc_rejected_other_reason");
        Intent intent = new Intent(this, CongratsActivity.class);
        intent.putExtra("payment", payment);
        new MercadoPago.StartActivityBuilder()
                .setPublicKey(ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY)
                .setActivity(mActivity)
                .setPayment(payment)
                .startCongratsActivity();

    }

    private void createPaymentPreference() {
        mPaymentPreference = new PaymentPreference();
        mPaymentPreference.setExcludedPaymentMethodIds(mExcludedPaymentMethodIds);
        mPaymentPreference.setExcludedPaymentTypeIds(mExcludedPaymentTypeIds);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Set payment method
                PaymentMethod paymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");

                // Call new cards activity
                ExamplesUtils.startCardActivity(this, ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY, paymentMethod);
            } else {

                if ((data != null) && (data.getSerializableExtra("apiException") != null)) {
                    ApiException apiException = (ApiException) data.getSerializableExtra("apiException");
                    Toast.makeText(getApplicationContext(), apiException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == ExamplesUtils.CARD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Create payment
                ExamplesUtils.createPayment(this, data.getStringExtra("token"),
                        1, null, (PaymentMethod) data.getSerializableExtra("paymentMethod"), null);

            } else {

                if (data != null) {
                    if (data.getSerializableExtra("apiException") != null) {
                        ApiException apiException = (ApiException) data.getSerializableExtra("apiException");
                        Toast.makeText(getApplicationContext(), apiException.getMessage(), Toast.LENGTH_LONG).show();

                    } else if (data.getBooleanExtra("backButtonPressed", false)) {

                        PaymentPreference paymentPreference = new PaymentPreference();
                        paymentPreference.setExcludedPaymentTypeIds(mExcludedPaymentTypeIds);

                        new MercadoPago.StartActivityBuilder()
                                .setActivity(this)
                                .setPublicKey(ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY)
                                .setPaymentPreference(mPaymentPreference)
                                .startPaymentMethodsActivity();
                    }
                }
            }
        } else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {

            LayoutUtil.showRegularLayout(this);
        } else if (requestCode == MercadoPago.GUESSING_CARD_REQUEST_CODE){

            if(resultCode == RESULT_OK){
                PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
                Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
                CardToken cardToken = JsonUtil.getInstance().fromJson(data.getStringExtra("cardToken"), CardToken.class);

                createTokenAsyncAndPay(paymentMethod, issuer, cardToken);

            } else {

                if ((data != null) && (data.getStringExtra("apiException") != null)) {
                    Toast.makeText(getApplicationContext(), data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();
                }
            }

        }

    }
    private void createTokenAsyncAndPay(final PaymentMethod paymentMethod, final Issuer issuer, CardToken cardToken) {
        LayoutUtil.showProgressLayout(this);
        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY)
                .build();

        mercadoPago.createToken(cardToken, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                LayoutUtil.showRegularLayout(mActivity);
                Long issuerId = null;
                if(issuer != null)
                    issuerId = issuer.getId();

                ExamplesUtils.createPayment(mActivity, token.getId(),
                        1, issuerId, paymentMethod, null);
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(mActivity, error);
            }
        });
    }


    public void submitForm(View view) {

        // Call payment methods activity
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY)
                .setPaymentPreference(mPaymentPreference)
                .startPaymentMethodsActivity();
    }

    public void submitSimpleForm(View view) {

        // Call payment methods activity
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY)
                .setPaymentPreference(mPaymentPreference)
                .startPaymentMethodsActivity();
    }

    public void submitGuessingForm(View view) {
        ExamplesUtils.startGuessingCardActivity(this, ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY, true,
                mPaymentPreference, mAmount);
    }

}