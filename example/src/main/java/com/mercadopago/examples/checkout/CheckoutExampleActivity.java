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
    private ImageView mColorSample;
    private CheckBox mDarkFontEnabled;
    private ProgressBar mProgressBar;
    private View mRegularLayout;

    private CheckoutPreference mCheckoutPreference;
    private String mPublicKey;
    private Integer mDefaultColor;
    private Integer mSelectedColor;

    //Result
    private PaymentData mPaymentData;
    private boolean mAlreadyStartedRyC = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_example);
        mActivity = this;
        mColorSample = (ImageView) findViewById(R.id.colorSample);
        mDarkFontEnabled = (CheckBox) findViewById(R.id.darkFontEnabled);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRegularLayout = findViewById(R.id.regularLayout);
        mPublicKey = ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY;
        mDefaultColor = ContextCompat.getColor(this, R.color.colorPrimary);
    }

    public void changeColor(View view) {
        new ColorPickerDialog(this, mDefaultColor, new ColorPickerDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mDarkFontEnabled.setEnabled(true);
                mColorSample.setBackgroundColor(color);
                mSelectedColor = color;
            }
        }).show();
    }

    public void onContinueClicked(View view) {
        showProgressLayout();
        Map<String, Object> map = new HashMap<>();
        map.put("item_id", "1");
        map.put("amount", new BigDecimal(300));

        MerchantServer.createPreference(this, "http://private-4d9654-mercadopagoexamples.apiary-mock.com/",
                "merchantUri/create_preference", map, new Callback<CheckoutPreference>() {
                    @Override
                    public void success(CheckoutPreference checkoutPreference) {
                        mCheckoutPreference = checkoutPreference;
                        startMercadoPagoCheckout();
                    }

                    @Override
                    public void failure(ApiException error) {
                        showRegularLayout();
                        Toast.makeText(mActivity, getString(R.string.preference_creation_failed), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void startMercadoPagoCheckout() {
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("company_id", "movistar");
        additionalInfo.put("phone_number", "111111");

        String languageToLoad  = "en"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        CellphoneReview cellphoneReview = new CellphoneReview(this, "15111111");

        ReviewScreenPreference reviewScreenPreference = new ReviewScreenPreference.Builder()
                .setTitle("Confirma tu recarga")
                .setConfirmText("Recargar")
                .setCancelText("Ir a Actividad")
                .setProductDetail("Recarga de celular")
                .addReviewable(cellphoneReview)
                .build();

        CongratsReview congratsReview = new CongratsReview(this, "Hola!");

        DecorationPreference decorationPreference = new DecorationPreference.Builder()
//                .setCustomLightFont("fonts/Pacifico-Light.ttf")
//                .setCustomRegularFont("fonts/Merriweather-Light.ttf")
                .build();

        PaymentResultScreenPreference paymentResultScreenPreference = new PaymentResultScreenPreference.Builder()
                //.addCongratsReviewable(congratsReview)
                .setPendingContentTitle("Ahora acredita tu carga y sigue viajando")
                .disablePendingContentText()
                .disablePendingContentTitle()
                .setApprovedSecondaryExitButton("lala", new PaymentResultCallback() {
                    @Override
                    public void onResult(PaymentResult paymentResult) {
                        Log.d("log", "on result");
                    }
                })
                .build();

        FlowPreference flowPreference = new FlowPreference.Builder()
                .disableReviewAndConfirmScreen()
                .disableInstallmentsReviewScreen()
//                .disableDiscount()
                .disableBankDeals()
                .build();

        mCheckoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("Item", new BigDecimal(10000)))
                .setSite(Sites.ARGENTINA)
//                .addExcludedPaymentType(PaymentTypes.ATM)
//                .addExcludedPaymentType(PaymentTypes.BANK_TRANSFER)
//                .addExcludedPaymentType(PaymentTypes.CREDIT_CARD)
//                .addExcludedPaymentType(PaymentTypes.DEBIT_CARD)
//                .addExcludedPaymentType(PaymentTypes.TICKET)
                .enableAccountMoney()
                .setPayerAccessToken("TEST-7176766875549918-111008-fa5660d2d0aa37532716eb2bf2f9089b__LB_LC__-192992930")
                .build();

        new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setCheckoutPreference(mCheckoutPreference)
                .setFlowPreference(flowPreference)
                .start(new PaymentDataCallback() {
                    @Override
                    public void onSuccess(PaymentData paymentData, boolean paymentMethodChanged) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Success! " + paymentData.getPaymentMethod().getId() + " selected. ");
                        if(paymentMethodChanged) {
                            stringBuilder.append("And it has changed!");
                        }
                        Toast.makeText(mActivity, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                        startRyC(paymentData);
                    }
                    @Override
                    public void onCancel() {
                        Toast.makeText(mActivity, "Cancel callback", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(MercadoPagoError error) {
                        Log.d("log", "failure");
                    }
                });
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
                .setCheckoutPreference(mCheckoutPreference)
                .setFlowPreference(flowPreference)
                .setPaymentData(paymentData)
                .start(new PaymentDataCallback() {
                    @Override
                    public void onSuccess(PaymentData paymentData, boolean paymentMethodChanged) {
                        if (paymentMethodChanged) {
                            startRyC(paymentData);
                        } else {
                            startWithPaymentResult(paymentData);
                        }
                    }
                    @Override
                    public void onCancel() {
                        Toast.makeText(mActivity, "Cancel callback", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(MercadoPagoError error) {
                        Toast.makeText(mActivity, "Error callback: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private CheckoutPreference getCheckoutPreference() {
        return new CheckoutPreference.Builder()
                .addItem(new Item("Item", BigDecimal.TEN.multiply(BigDecimal.TEN)))
                .setSite(Sites.ARGENTINA)
//                .addExcludedPaymentType(PaymentTypes.ATM)
//                .addExcludedPaymentType(PaymentTypes.BANK_TRANSFER)
//                .addExcludedPaymentType(PaymentTypes.DEBIT_CARD)
//                .addExcludedPaymentType(PaymentTypes.DEBIT_CARD)
//                .addExcludedPaymentType(PaymentTypes.TICKET)
                .enableAccountMoney()
                .setPayerAccessToken("APP_USR-6077407713835188-120612-9c010367e2aba8808865b227526f4ccc__LB_LD__-232134231")
                .build();
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
                .setApprovedSecondaryExitButton("Secondary", RESULT_CUSTOM_EXIT)
                .addPendingReviewable(congratsReview)
                .addCongratsReviewable(congratsReview)
                .setExitButtonTitle("Ir a Actividad")
                .build();

        new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setCheckoutPreference(mCheckoutPreference)
                .setPaymentResult(paymentResult)
                .setDiscount(paymentData.getDiscount())
                .setPaymentResultScreenPreference(paymentResultScreenPreference)
                .start(new PaymentDataCallback() {
                    @Override
                    public void onSuccess(PaymentData paymentData, boolean paymentMethodChanged) {
                        Log.d("log", "en success 3");
                        Log.d("log", "payment method changed: " + paymentMethodChanged);
                    }

                    @Override
                    public void onCancel() {
                        Log.d("log", "en cancel 3");
                    }

                    @Override
                    public void onFailure(MercadoPagoError error) {
                        Log.d("log", "en failure 3");
                    }
                });
//                .startForPaymentData();
    }

    private DecorationPreference.Builder getCurrentDecorationPreferenceBuilder() {
        DecorationPreference.Builder builder = new DecorationPreference.Builder();
        if (mSelectedColor != null) {
            builder.setBaseColor(mSelectedColor);
            if (mDarkFontEnabled.isChecked()) {
                builder.enableDarkFont();
            }
        }
        return builder;
    }

    private void doSomething() {
        //After doing something, we start MercadoPagoCheckout with PaymentData
        Toast.makeText(this, "Restarting!", Toast.LENGTH_SHORT).show();
        startMercadoPagoCheckoutWithPaymentData();
    }

    private void startMercadoPagoCheckoutWithPaymentData() {

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("company_id", "movistar");
        additionalInfo.put("phone_number", "111111");

        DecorationPreference decorationPreference = new DecorationPreference.Builder()
//                .setCustomLightFont("fonts/Pacifico-Light.ttf")
//                .setCustomRegularFont("fonts/Merriweather-Light.ttf")
                .build();

        ServicePreference servicePreference = new ServicePreference.Builder()
                .setCreatePaymentURL("http://private-4d9654-mercadopagoexamples.apiary-mock.com", "create_payment", additionalInfo)
                .build();

        CellphoneReview cellphoneReview = new CellphoneReview(this, "15999999");
        cellphoneReview.setReviewableCallback(new ReviewableCallback() {
            @Override
            public void onChangeRequired(PaymentData paymentData) {
                Toast.makeText(getBaseContext(), "Change button clicked!", Toast.LENGTH_SHORT).show();
                mPaymentData = paymentData;
                doSomething();
            }
        });

        ReviewScreenPreference reviewScreenPreference = new ReviewScreenPreference.Builder()
                .setTitle("Confirma tu recarga")
                .setConfirmText("Recargar")
                .setCancelText("Ir a Actividad")
                .setProductDetail("Recarga")
                .addReviewable(cellphoneReview)
                .build();

        new MercadoPagoCheckout.Builder()
                .setContext(this)
                .setPublicKey(mPublicKey)
                .setCheckoutPreference(mCheckoutPreference)
                .setDecorationPreference(decorationPreference)
                .setReviewScreenPreference(reviewScreenPreference)
                .setPaymentData(mPaymentData)
                .start(new PaymentCallback() {
                    @Override
                    public void onSuccess(Payment payment) {
                        //Done!
                        Log.d("log", "success");
                        Log.d("log", payment.getStatus());
                    }

                    @Override
                    public void onCancel() {
                        //User canceled
                        Log.d("log", "cancel");
                    }

                    @Override
                    public void onFailure(MercadoPagoError exception) {
                        //Failure in checkout
                        Log.d("log", "failure");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LayoutUtil.showRegularLayout(this);

        if (requestCode == MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE) {
                PaymentData paymentData = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentData"), PaymentData.class);
                if (mAlreadyStartedRyC) {
                    Toast.makeText(mActivity, "Restart en Resultados", Toast.LENGTH_SHORT).show();
                    startWithPaymentResult(paymentData);
                } else {
                    mAlreadyStartedRyC = true;
                    Toast.makeText(mActivity, "Restart en RyC", Toast.LENGTH_SHORT).show();
                    startRyC(paymentData);
                }

            } else if (resultCode == RESULT_CANCELED) {

                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    MercadoPagoError mercadoPagoError = JsonUtil.getInstance().fromJson(data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
                    Log.d("log", "RESULT_CANCELED - " + mercadoPagoError.getMessage());
                } else {
                    Log.d("log", "RESULT_CANCELED");
                }

            } else if (resultCode == CellphoneReview.CELLPHONE_CHANGE) {
                mPaymentData = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentData"), PaymentData.class);

            } else if (resultCode == CongratsReview.CUSTOM_REVIEW) {
                Payment payment = JsonUtil.getInstance().fromJson(data.getStringExtra("payment"), Payment.class);
                PaymentResult paymentResult = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentResult"), PaymentResult.class);
            } else if (resultCode == RESULT_CUSTOM_EXIT) {
                Toast.makeText(mActivity, "Hola custom exit!", Toast.LENGTH_SHORT).show();
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

    private void showProgressLayout() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRegularLayout.setVisibility(View.GONE);
    }

    public void resetSelection(View view) {
        mSelectedColor = null;
        mColorSample.setBackgroundColor(mDefaultColor);
        mDarkFontEnabled.setChecked(false);
        mDarkFontEnabled.setEnabled(false);
    }
}