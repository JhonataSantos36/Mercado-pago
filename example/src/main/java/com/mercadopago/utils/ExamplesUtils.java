package com.mercadopago.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.mercadopago.components.CustomComponent;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.core.MercadoPagoCheckout.Builder;
import com.mercadopago.example.R;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Payment;
import com.mercadopago.model.Discount;
import com.mercadopago.plugins.DataInitializationTask;
import com.mercadopago.plugins.MainPaymentProcessor;
import com.mercadopago.plugins.components.SampleCustomComponent;
import com.mercadopago.plugins.model.BusinessPayment;
import com.mercadopago.plugins.model.ExitAction;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;

public class ExamplesUtils {

    private static final String REQUESTED_CODE_MESSAGE = "Requested code: ";
    private static final String PAYMENT_WITH_STATUS_MESSAGE = "Payment with status: ";
    private static final String BUTTON_PRIMARY_NAME = "ButtonPrimaryName";
    private static final String BUTTON_SECONDARY_NAME = "ButtonSecondaryName";
    private static final String RESULT_CODE_MESSAGE = " Result code: ";
    private static final String DUMMY_PREFERENCE_ID = "243962506-0bb62e22-5c7b-425e-a0a6-c22d0f4758a9";
    private static final String DUMMY_PREFERENCE_ID_WITH_DECIMALS = "243962506-ad5df092-f5a2-4b99-bcc4-7578d6e71849";
    private static final String DUMMY_MERCHANT_PUBLIC_KEY = "TEST-c6d9b1f9-71ff-4e05-9327-3c62468a23ee";

    /*
    public static final String DUMMY_PREFERENCE_ID = "243962506-e9464aff-30dd-43e0-a6fa-37e3a54b884c";

    public static final String DUMMY_MERCHANT_PUBLIC_KEY_EXAMPLES_SERVICE = "444a9ef5-8a6b-429f-abdf-587639155d88";
    public static final String DUMMY_MERCHANT_PUBLIC_KEY_AR = "444a9ef5-8a6b-429f-abdf-587639155d88";
    public static final String DUMMY_MERCHANT_PUBLIC_KEY_BR = "APP_USR-f163b2d7-7462-4e7b-9bd5-9eae4a7f99c3";
    public static final String DUMMY_MERCHANT_PUBLIC_KEY_MX = "6c0d81bc-99c1-4de8-9976-c8d1d62cd4f2";
    public static final String DUMMY_MERCHANT_PUBLIC_KEY_VZ = "2b66598b-8b0f-4588-bd2f-c80ca21c6d18";

    public static final String DUMMY_MERCHANT_PUBLIC_KEY_CO = "aa371283-ad00-4d5d-af5d-ed9f58e139f1";
    // * Merchant server vars
    public static final String DUMMY_MERCHANT_BASE_URL = "https://www.mercadopago.com";
    public static final String DUMMY_MERCHANT_GET_CUSTOMER_URI = "/checkout/examples/getCustomer";
    public static final String DUMMY_MERCHANT_CREATE_PAYMENT_URI = "/checkout/examples/doPayment";


    public static final String DUMMY_MERCHANT_GET_DISCOUNT_URI = "/checkout/examples/getDiscounts";
    // * Merchant access token
    public static final String DUMMY_MERCHANT_ACCESS_TOKEN = "mla-cards-data";
    public static final String DUMMY_MERCHANT_ACCESS_TOKEN_AR = "mla-cards-data";
    public static final String DUMMY_MERCHANT_ACCESS_TOKEN_BR = "mlb-cards-data";
    public static final String DUMMY_MERCHANT_ACCESS_TOKEN_MX = "mlm-cards-data";
    //    public static final String DUMMY_MERCHANT_ACCESS_TOKEN_VZ = "mlv-cards-data";
    public static final String DUMMY_MERCHANT_ACCESS_TOKEN_VZ = "mco-cards-data";

    public static final String DUMMY_MERCHANT_ACCESS_TOKEN_NO_CCV = "mla-cards-data-tarshop";
    // * Payment item
    public static final String DUMMY_ITEM_ID = "id1";
    public static final Integer DUMMY_ITEM_QUANTITY = 1;
    public static final BigDecimal DUMMY_ITEM_UNIT_PRICE = new BigDecimal("1000");



    public static PaymentMethod getDummyPaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("visa");
        paymentMethod.setName("Visa");
        paymentMethod.setPaymentTypeId("credit_card");
        return paymentMethod;
    }

    public static Issuer getDummyIssuer() {
        Issuer issuer = new Issuer();
        issuer.setId(338L);
        return issuer;
    }

    */

    public static void resolveCheckoutResult(final Activity context, final int requestCode, final int resultCode, final Intent data) {
        LayoutUtil.showRegularLayout(context);

        if (requestCode == MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {
                Payment payment = JsonUtil.getInstance().fromJson(data.getStringExtra("payment"), Payment.class);
                Toast.makeText(context, new StringBuilder()
                        .append(PAYMENT_WITH_STATUS_MESSAGE)
                        .append(payment.getStatus()), Toast.LENGTH_LONG)
                        .show();

            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    MercadoPagoError mercadoPagoError = JsonUtil.getInstance().fromJson(data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
                    Toast.makeText(context, "Error: " + mercadoPagoError.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, new StringBuilder()
                            .append("Cancel - ")
                            .append(REQUESTED_CODE_MESSAGE)
                            .append(requestCode)
                            .append(RESULT_CODE_MESSAGE)
                            .append(resultCode), Toast.LENGTH_LONG)
                            .show();
                }
            } else {

                Toast.makeText(context, new StringBuilder()
                        .append(REQUESTED_CODE_MESSAGE)
                        .append(requestCode)
                        .append(RESULT_CODE_MESSAGE)
                        .append(resultCode), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public static List<Pair<String, Builder>> getOptions(Activity activity) {
        List<Pair<String, Builder>> options = new ArrayList<>();

        options.add(new Pair<>("Discount", discountSample(activity)));
        options.add(new Pair<>("Review and Confirm - Custom exit", customExitReviewAndConfirm(activity)));
        options.add(new Pair<>("Business - Complete - Rejected", startCompleteRejectedBusiness(activity)));
        options.add(new Pair<>("Business - Secondary And Help - Approved", startCompleteApprovedBusiness(activity)));
        options.add(new Pair<>("Business - Primary And Help - Pending", startCompletePendingBusiness(activity)));
        options.add(new Pair<>("Business - No help - Pending", startPendingBusinessNoHelp(activity)));

        return options;

    }

    private static Builder startCompleteRejectedBusiness(Activity activity) {
        BusinessPayment payment = new BusinessPayment.Builder(BusinessPayment.Status.REJECTED, R.drawable.mpsdk_icon_card, "Title")
                .setHelp("Help description!")
                .setPrimaryButton(new ExitAction(BUTTON_PRIMARY_NAME, 23))
                .setSecondaryButton(new ExitAction(BUTTON_SECONDARY_NAME, 34))
                .build();

        return customBusinessPayment(activity, payment);
    }

    private static Builder startCompleteApprovedBusiness(Activity activity) {
        BusinessPayment payment = new BusinessPayment.Builder(BusinessPayment.Status.APPROVED, R.drawable.mpsdk_icon_card, "Title")
                .setHelp("Help description!")
                .setSecondaryButton(new ExitAction(BUTTON_SECONDARY_NAME, 34))
                .build();

        return customBusinessPayment(activity, payment);
    }

    private static Builder startCompletePendingBusiness(Activity activity) {
        BusinessPayment payment = new BusinessPayment.Builder(BusinessPayment.Status.PENDING, R.drawable.mpsdk_icon_card, "Title")
                .setHelp("Help description!")
                .setPrimaryButton(new ExitAction(BUTTON_PRIMARY_NAME, 23))
                .build();

        return customBusinessPayment(activity, payment);
    }

    private static Builder startPendingBusinessNoHelp(Activity activity) {
        BusinessPayment payment = new BusinessPayment.Builder(BusinessPayment.Status.PENDING, R.drawable.mpsdk_icon_card, "Title")
                .setPrimaryButton(new ExitAction(BUTTON_PRIMARY_NAME, 23))
                .setSecondaryButton(new ExitAction(BUTTON_SECONDARY_NAME, 34))
                .build();

        return customBusinessPayment(activity, payment);
    }


    private static Builder customBusinessPayment(Activity activity, BusinessPayment businessPayment) {
        return createBase(activity).setPaymentProcessor(new MainPaymentProcessor(businessPayment));
    }

    private static Builder customExitReviewAndConfirm(Activity activity) {
        CustomComponent.Props props = new CustomComponent.Props(new HashMap<String, Object>(), null);
        ReviewAndConfirmPreferences preferences = new ReviewAndConfirmPreferences.Builder()
                .setTopComponent(new SampleCustomComponent(props)).build();
        return createBaseWithDecimals(activity).setReviewAndConfirmPreferences(preferences);
    }

    private static Builder discountSample(Activity activity) {
        Discount discount = new Discount();
        discount.setCurrencyId("ARS");
        discount.setId("77123");
        discount.setCouponAmount(new BigDecimal(20));
        discount.setPercentOff(new BigDecimal(20));
        return createBase(activity).setDiscount(discount);
    }

    public static Builder createBase(final Activity activity) {
        final Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("amount", 120f);

        return new Builder()
                .setActivity(activity)
                .setPublicKey(DUMMY_MERCHANT_PUBLIC_KEY)
                .setCheckoutPreference(new CheckoutPreference(DUMMY_PREFERENCE_ID))
                .setDataInitializationTask(new DataInitializationTask(defaultData) {
                    @Override
                    public void onLoadData(@NonNull final Map<String, Object> data) {
                        data.put("user", "Nico");
                    }
                });
    }

    public static Builder createBaseWithDecimals(final Activity activity) {
        final Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("amount", 120f);

        return new Builder()
                .setActivity(activity)
                .setPublicKey(DUMMY_MERCHANT_PUBLIC_KEY)
                .setCheckoutPreference(new CheckoutPreference(DUMMY_PREFERENCE_ID_WITH_DECIMALS))
                .setDataInitializationTask(new DataInitializationTask(defaultData) {
                    @Override
                    public void onLoadData(@NonNull final Map<String, Object> data) {
                        data.put("user", "Nico");
                    }
                });
    }
}