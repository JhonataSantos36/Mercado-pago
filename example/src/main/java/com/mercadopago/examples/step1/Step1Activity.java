package com.mercadopago.examples.step1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mercadopago.CallForAuthorizeActivity;
import com.mercadopago.CongratsActivity;
import com.mercadopago.ExampleActivity;
import com.mercadopago.OldCongratsActivity;
import com.mercadopago.RejectionActivity;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
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

    protected List<String> mExcludedPaymentTypes = new ArrayList<String>(){{
        add("atm");
        add("ticket");
        add("digital_currency");
    }};

    protected List<String> mExcludedPaymentIds = new ArrayList<String>(){{
        add("visa");
    }};
    protected Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);
        mActivity = this;

        //TODO borrar, era para probar congrats
        Payment payment = new Payment();
        Payer payer = new Payer();
        Card card = new Card();
        PaymentMethod paymentMethod = new PaymentMethod();

        payment.setId(123456789L);
        payment.setStatus("in_process");
        payment.setStatusDetail("pending_contingency");
        payer.setEmail("juan-carlos@email.com");
        payment.setPayer(payer);
        card.setLastFourDigits("5676");
        paymentMethod.setId("master");
        paymentMethod.setName("Master");
        card.setPaymentMethod(paymentMethod);
        payment.setCard(card);
        payment.setInstallments(6);
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setInstallmentAmount(new BigDecimal(173.33));
        transactionDetails.setTotalPaidAmount(new BigDecimal(1038));
        payment.setTransactionDetails(transactionDetails);
        payment.setCurrencyId("MXN");



        Intent intent = new Intent(this, CongratsActivity.class);
        intent.putExtra("payment", payment);
        startActivity(intent);
        ////////////////////////////////////////////////
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

                        new MercadoPago.StartActivityBuilder()
                                .setActivity(this)
                                .setPublicKey(ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY)
                                .setExcludedPaymentTypes(mExcludedPaymentTypes)
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
                .setExcludedPaymentTypes(mExcludedPaymentTypes)
                .startPaymentMethodsActivity();
    }

    public void submitSimpleForm(View view) {

        // Call payment methods activity
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY)
                .setExcludedPaymentTypes(mExcludedPaymentTypes)
                .setExcludedPaymentTypes(mExcludedPaymentTypes)
                .setExcludedPaymentMethodIds(mExcludedPaymentIds)
                .startPaymentMethodsActivity();
    }

    public void submitGuessingForm(View view) {
        ExamplesUtils.startGuessingCardActivity(this, ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY, true);
    }

}