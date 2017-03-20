package com.mercadopago.examples.checkout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.PaymentCallback;
import com.mercadopago.callbacks.PaymentDataCallback;
import com.mercadopago.callbacks.PaymentResultCallback;
import com.mercadopago.callbacks.ReviewableCallback;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.Sites;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.examples.R;
import com.mercadopago.examples.reviewables.CellphoneReview;
import com.mercadopago.examples.reviewables.CongratsReview;
import com.mercadopago.examples.utils.ColorPickerDialog;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.Site;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.model.Payment;
import com.mercadopago.preferences.FlowPreference;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CheckoutExampleActivity extends AppCompatActivity {
    private static final int RESULT_CUSTOM_EXIT = 1321;
    private Activity mActivity;
    private ProgressBar mProgressBar;
    private View mRegularLayout;
    private boolean mAlreadyStartedRyC;
    private String mPublicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_example);
        mActivity = this;
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRegularLayout = findViewById(R.id.regularLayout);
        mPublicKey = ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY;
    }

    public void onContinueClicked(View view) {
        startMercadoPagoCheckout();
        mAlreadyStartedRyC = false;
    }

    private void startMercadoPagoCheckout() {
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("company_id", "movistar");
        additionalInfo.put("phone_number", "111111");

        String languageToLoad  = "pt"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        FlowPreference flowPreference = new FlowPreference.Builder()
//                .disableReviewAndConfirmScreen()
                .disableDiscount()
                .disableBankDeals()
                .disableInstallmentsReviewScreen()
                .build();

        new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setCheckoutPreference(getCheckoutPreference())
                .setFlowPreference(flowPreference)
                .startForPaymentData();
    }

    private void startRyC(PaymentData paymentData) {

        CellphoneReview cellphoneReview = new CellphoneReview(this, "15111111");

        ReviewScreenPreference reviewScreenPreference = new ReviewScreenPreference.Builder()
                .setTitle("Confirma tu recarga")
                .setConfirmText("Recargar")
                .setCancelText("Ir a Actividad")
                .setProductDetail("Recarga de celular")
                .addReviewable(cellphoneReview)
                .build();

        FlowPreference flowPreference = new FlowPreference.Builder()
                .disableReviewAndConfirmScreen()
                .disableBankDeals()
                .disableDiscount()
                .disableInstallmentsReviewScreen()
                .build();

        new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setReviewScreenPreference(reviewScreenPreference)
                .setPublicKey(mPublicKey)
                .setCheckoutPreference(getCheckoutPreference())
                .setFlowPreference(flowPreference)
                .setPaymentData(paymentData)
                .startForPaymentData();
    }

    private CheckoutPreference getCheckoutPreference() {
        return new CheckoutPreference.Builder()
                .addItem(new Item("Item", BigDecimal.TEN.multiply(BigDecimal.TEN)))
                .setSite(Sites.ARGENTINA)
                .addExcludedPaymentType(PaymentTypes.ATM)
                .addExcludedPaymentType(PaymentTypes.BANK_TRANSFER)
                .addExcludedPaymentType(PaymentTypes.DEBIT_CARD)
                .addExcludedPaymentType(PaymentTypes.DEBIT_CARD)
                .addExcludedPaymentType(PaymentTypes.TICKET)
                .enableAccountMoney()
                .setPayerAccessToken("APP_USR-6077407713835188-120612-9c010367e2aba8808865b227526f4ccc__LB_LD__-232134231")
                .build();
    }

    private String getSuccessMessage(PaymentData paymentData, boolean paymentMethodChanged) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Success! " + paymentData.getPaymentMethod().getId() + " selected. ");
        if(paymentMethodChanged) {
            stringBuilder.append("And it has changed!");
        }
        return stringBuilder.toString();
    }

    private void startWithPaymentResult(PaymentData paymentData) {

        CongratsReview congratsReview = new CongratsReview(this, "Hola!");

        PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentData(paymentData)
                .setPaymentStatus(Payment.StatusCodes.STATUS_APPROVED)
//                .setPaymentStatus(Payment.StatusCodes.STATUS_PENDING)
//                .setPaymentStatus(Payment.StatusCodes.STATUS_REJECTED)
//                .setPaymentStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER)
                .build();

        PaymentResultScreenPreference paymentResultScreenPreference = new PaymentResultScreenPreference.Builder()
                .setApprovedTitle("Recargaste!")
                .setApprovedSecondaryExitButton("Intentar nuevamente", RESULT_CUSTOM_EXIT)
                .addCongratsReviewable(congratsReview)
                .setExitButtonTitle("Ir a Actividad")
                .build();


        new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setCheckoutPreference(getCheckoutPreference())
                .setPaymentResult(paymentResult)
                .setPaymentResultScreenPreference(paymentResultScreenPreference)
                .startForPaymentData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LayoutUtil.showRegularLayout(this);

        if (requestCode == MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE) {
                PaymentData paymentData = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentData"), PaymentData.class);
                Boolean paymentMethodChanged = data.getBooleanExtra("paymentMethodChanged", false);
                Toast.makeText(mActivity, getSuccessMessage(paymentData, paymentMethodChanged), Toast.LENGTH_SHORT).show();
                if(!mAlreadyStartedRyC || paymentMethodChanged) {
                    mAlreadyStartedRyC = true;
                    startRyC(paymentData);
                } else {
                    startWithPaymentResult(paymentData);
                }


            } else if (resultCode == RESULT_CANCELED) {

                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    MercadoPagoError mercadoPagoError = JsonUtil.getInstance().fromJson(data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
                    Toast.makeText(mActivity, "Error: " + mercadoPagoError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Cancel", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == CellphoneReview.CELLPHONE_CHANGE) {
                Toast.makeText(mActivity, "Change cellphone!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == CongratsReview.CUSTOM_REVIEW) {
                Toast.makeText(mActivity, "Custom congrats view!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CUSTOM_EXIT) {
                Toast.makeText(mActivity, "Custom exit!", Toast.LENGTH_SHORT).show();
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
}