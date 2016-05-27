package com.mercadopago.examples.step7;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.examples.R;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentType;
import com.mercadopago.util.LayoutUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vaserber on 5/27/16.
 */
public class PaymentMethodTypeActivity  extends AppCompatActivity {

    private String publicKey = "TEST-ad365c37-8012-4014-84f5-6c895b3f8e0a";
    private String publicKeyMLX = "APP_USR-356d0014-6248-40fc-b4ac-0fe8c3b4c6fd";
    private MercadoPago mMercadoPago;
    private List<PaymentMethod> mPaymentMethods;
    private LinearLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method_type);
        mContainer = (LinearLayout) findViewById(R.id.buttonContainer);
        mContainer.setVisibility(View.GONE);
        mMercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(publicKey)
                .build();
        getPaymentMethodsAsync();
    }

    public void submitCredit(View view) {
        List<PaymentMethod> paymentMethods = filterPaymentMethods(PaymentType.CREDIT_CARD, mPaymentMethods);

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(publicKey)
                .setSupportedPaymentMethods(paymentMethods)
                .startGuessingCardActivity();

        LayoutUtil.showRegularLayout(this);
    }

    public void submitDebit(View view) {
//        final String publicKeyMLX = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";
        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(publicKeyMLX)
                .build();
        mercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods, Response response) {
                mContainer.setVisibility(View.VISIBLE);
                submitDebitMLX(paymentMethods);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    private void submitDebitMLX(List<PaymentMethod> list) {
        List<PaymentMethod> paymentMethods = filterPaymentMethods(PaymentType.DEBIT_CARD, list);
//        final String publicKeyMLX = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(publicKeyMLX)
                .setSupportedPaymentMethods(paymentMethods)
                .startGuessingCardActivity();

        LayoutUtil.showRegularLayout(this);
    }

    public void submitPrepaid(View view) {
//        final String publicKeyMLX = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";
        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(publicKeyMLX)
                .build();
        mercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods, Response response) {
                mContainer.setVisibility(View.VISIBLE);
                submitPrepaidMLX(paymentMethods);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void submitPrepaidMLX(List<PaymentMethod> list) {
        List<PaymentMethod> paymentMethods = filterPaymentMethods(PaymentType.PREPAID_CARD, list);
//        final String publicKeyMLX = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(publicKeyMLX)
                .setSupportedPaymentMethods(paymentMethods)
                .startGuessingCardActivity();

        LayoutUtil.showRegularLayout(this);
    }

    public List<PaymentMethod> filterPaymentMethods(String paymentType, List<PaymentMethod> pmlist) {
        List<PaymentMethod> list =  new ArrayList<>();
        for (PaymentMethod pm : pmlist) {
            if (pm.getPaymentTypeId().equals(paymentType)) {
                list.add(pm);
            }
        }
        return list;
    }

    protected void getPaymentMethodsAsync() {
        mMercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods, Response response) {
                mPaymentMethods = paymentMethods;
                mContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
