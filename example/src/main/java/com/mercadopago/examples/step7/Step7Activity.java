package com.mercadopago.examples.step7;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentType;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vaserber on 5/2/16.
 */
public class Step7Activity extends AppCompatActivity {


    private PaymentMethod mPaymentMethod;
    private Token mToken;
    private Issuer mIssuer;
    private PayerCost mPayerCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step7);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            resolveGuessingCardRequest(resultCode, data);
        } else if(requestCode == MercadoPago.GUESSING_CARD_REQUEST_CODE) {
            resolveGuessingCardRequest(resultCode, data);
        } else if(requestCode == MercadoPago.CARD_VAULT_REQUEST_CODE) {
            resolveGuessingCardRequest(resultCode, data);
        }
    }

    protected void resolveGuessingCardRequest(int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            mPaymentMethod = (PaymentMethod) data.getSerializableExtra("paymentMethod");
            mToken = (Token) data.getSerializableExtra("token");
            mIssuer = (Issuer) data.getSerializableExtra("issuer");

            LayoutUtil.showProgressLayout(this);

        } else if ((data != null) && (data.getSerializableExtra("apiException") != null)) {
            finish();
        }
    }

    public void submitFlowCard(View view) {
        startFullFlowActivity();
    }

    private void startFullFlowActivity() {

        String publicKeyMLX = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";
        String publicKeyMLA = "444a9ef5-8a6b-429f-abdf-587639155d88";

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultPaymentTypeId(PaymentType.CREDIT_CARD);
//        paymentPreference.setMaxAcceptedInstallments(6);
//        paymentPreference.setDefaultInstallments(6);
//
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(publicKeyMLA)
                .setAmount(ExamplesUtils.DUMMY_ITEM_UNIT_PRICE)
                .setPaymentPreference(paymentPreference)
                .startCardVaultActivity();

    }

    public void submitCardForm(View view) {
        startNewFormActivity();
    }

    private void startNewFormActivity() {
        final String publicKeyMLX = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";
        final String publicKeyMLA = "444a9ef5-8a6b-429f-abdf-587639155d88";

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(publicKeyMLA)
                .build();

        //ARG
        CardToken cardTokenArg = new CardToken("5031755734530604", 12, 19, "123", "pepe", null, "35900841");
        //MX
        CardToken cardTokenMx = new CardToken("5031753134311717", 12, 19, "123", "pepe", null, null);

        mercadoPago.createToken(cardTokenArg, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                mToken = token;
                callNewFormActivity(publicKeyMLA);
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(getParent(), error);
            }
        });

        LayoutUtil.showRegularLayout(this);
    }

    private void callNewFormActivity(String publicKey) {

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultPaymentTypeId(PaymentType.CREDIT_CARD);

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(publicKey)
                .setPaymentPreference(paymentPreference)
//                .setToken(mToken)
                .startGuessingCardActivity();

        LayoutUtil.showRegularLayout(this);
    }


    public void submitInstallments(View view) {
        startInstallmentsActivity();
    }

    private void startInstallmentsActivity() {
        final String publicKeyMLX = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";
        final String publicKeyMLA = "444a9ef5-8a6b-429f-abdf-587639155d88";

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(publicKeyMLA)
                .build();

        CardToken cardToken = new CardToken("5031755734530604", 12, 19, "123", "pepe", null, "35900841" );


        mercadoPago.createToken(cardToken, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                mToken = token;
                callInstallmentsActivity(publicKeyMLA);
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(getParent(), error);
            }
        });

        LayoutUtil.showRegularLayout(this);
    }

    private void callInstallmentsActivity(String publicKey) {
        final PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultPaymentMethodId("master");



        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(publicKey)
                .setAmount(ExamplesUtils.DUMMY_ITEM_UNIT_PRICE)
                .setPaymentPreference(paymentPreference)
//                .setIssuer(issuer)
//                .setPaymentMethod(paymentMethod)
                .setToken(mToken)
//                .setPayerCosts(payerCosts)  //opcional
                .startCardInstallmentsActivity();

    }

    public void submitIssuers(View view) {
        startIssuersActivity();
    }

    private void startIssuersActivity() {
        final String publicKeyMLX = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";
        final String publicKeyMLA = "444a9ef5-8a6b-429f-abdf-587639155d88";

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(publicKeyMLA)
                .build();

        CardToken cardToken = new CardToken("5031755734530604", 12, 19, "123", "pepe", null, "35900841" );


        mercadoPago.createToken(cardToken, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                mToken = token;
                callIssuersActivity(publicKeyMLA);
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(getParent(), error);
            }
        });

        LayoutUtil.showRegularLayout(this);
    }

    private void callIssuersActivity(String publicKey) {

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(publicKey)
//                .setPaymentMethod(mCurrentPaymentMethod)
                .setToken(mToken)
//                .setIssuers(issuers)  //opcional
                .startCardIssuersActivity();
    }


}
