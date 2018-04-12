package com.mercadopago.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.mercadopago.CheckoutActivity;
import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.lite.controllers.CustomServicesHandler;
import com.mercadopago.hooks.CheckoutHooks;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.plugins.DataInitializationTask;
import com.mercadopago.plugins.PaymentMethodPlugin;
import com.mercadopago.plugins.PaymentProcessor;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.FlowPreference;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.tracker.FlowHandler;
import com.mercadopago.uicontrollers.FontCache;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MercadoPagoCheckout {

    public static final int CHECKOUT_REQUEST_CODE = 5;
    public static final int PAYMENT_DATA_RESULT_CODE = 6;
    public static final int PAYMENT_RESULT_CODE = 7;
    public static final int TIMER_FINISHED_RESULT_CODE = 8;
    public static final int PAYMENT_METHOD_CHANGED_REQUESTED = 9;

    public static final String PAYMENT_PROCESSOR_KEY = "payment_processor";

    private Context context;
    private final Activity activity;
    private final String publicKey;
    private final CheckoutPreference checkoutPreference;
    private final ServicePreference servicePreference;
    private final FlowPreference flowPreference;
    private final PaymentResultScreenPreference paymentResultScreenPreference;
    private final PaymentData paymentData;
    private final PaymentResult paymentResult;
    private final Boolean binaryMode;
    private final Discount discount;
    private final String regularFontPath;
    private final String lightFontPath;
    public final List<PaymentMethodPlugin> paymentMethodPluginList;
    public final Map<String, PaymentProcessor> paymentPlugins;
    public final DataInitializationTask dataInitializationTask;
    public final CheckoutHooks checkoutHooks;

    private MercadoPagoCheckout(Builder builder) {
        activity = builder.activity;
        context = builder.context;
        publicKey = builder.publicKey;
        paymentData = builder.paymentData;
        checkoutPreference = builder.checkoutPreference;
        servicePreference = builder.servicePreference;
        flowPreference = builder.flowPreference;
        paymentResultScreenPreference = builder.paymentResultScreenPreference;
        paymentResult = builder.paymentResult;
        binaryMode = builder.binaryMode;
        discount = builder.discount;
        dataInitializationTask = builder.dataInitializationTask;
        paymentMethodPluginList = builder.paymentMethodPluginList;
        paymentPlugins = builder.paymentPlugins;
        checkoutHooks = builder.checkoutHooks;
        regularFontPath = builder.regularFontPath;
        lightFontPath = builder.lightFontPath;

        customizeServices(servicePreference);

        final CheckoutStore store = CheckoutStore.getInstance();
        store.reset();
        store.setReviewAndConfirmPreferences(builder.reviewAndConfirmPreferences);
        store.setPaymentResultScreenPreference(paymentResultScreenPreference);
        store.setPaymentMethodPluginList(builder.paymentMethodPluginList);
        store.setPaymentPlugins(builder.paymentPlugins);
        store.setCheckoutHooks(builder.checkoutHooks);
        store.setDataInitializationTask(builder.dataInitializationTask);
        store.setCheckoutPreference(builder.checkoutPreference);

        //Create flow identifier only for new checkouts
        if (paymentResult == null && paymentData == null) {
            FlowHandler.getInstance().generateFlowId();
        }
    }

    private void customizeServices(ServicePreference servicePreference) {
        CustomServicesHandler.getInstance().clear();
        CustomServicesHandler.getInstance().setServices(servicePreference);
    }

    private void validate(Integer resultCode) throws IllegalStateException {
        if (context == null && activity == null) {
            throw new IllegalStateException("activity not set");
        }
        if (TextUtil.isEmpty(publicKey)) {
            throw new IllegalStateException("public key not set");
        }
        if (checkoutPreference == null) {
            throw new IllegalStateException("Checkout preference required");
        }
        if (hasTwoDiscountsSet()) {
            throw new IllegalStateException("payment data discount and discount set");
        }
        if (isCheckoutTimerAvailable() && isPaymentDataIntegration(resultCode)) {
            throw new IllegalStateException("CheckoutTimer is not available with PaymentData integration");
        }
    }

    private boolean isCheckoutTimerAvailable() {
        return flowPreference != null && flowPreference.isCheckoutTimerEnabled();
    }

    private boolean isPaymentDataIntegration(int resultCode) {
        return resultCode == MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE;
    }

    private Boolean hasTwoDiscountsSet() {
        Boolean hasTwoDiscountsSet = false;

        if ((hasPaymentDataDiscount() || hasPaymentResultDiscount()) && hasDiscount()) {
            hasTwoDiscountsSet = true;
        }

        return hasTwoDiscountsSet;
    }

    private Boolean hasPaymentDataDiscount() {
        return paymentData != null && paymentData.getDiscount() != null;
    }

    private Boolean hasPaymentResultDiscount() {
        return paymentResult != null && paymentResult.getPaymentData() != null &&
                paymentResult.getPaymentData().getDiscount() != null;
    }

    private Boolean hasDiscount() {
        return discount != null;
    }

    private void startForResult(@NonNull Integer resultCode) {
        CallbackHolder.getInstance().clean();
        startCheckoutActivity(resultCode);
    }

    private void startCheckoutActivity(Integer resultCode) {
        validate(resultCode);
        startIntent(getIntent(resultCode));
    }

    private void startIntent(final Intent checkoutIntent) {
        if (context != null) {
            context.startActivity(checkoutIntent);
        } else {
            activity.startActivityForResult(checkoutIntent, MercadoPagoCheckout.CHECKOUT_REQUEST_CODE);
        }
    }

    @NonNull
    private Intent getIntent(final Integer resultCode) {
        Intent checkoutIntent;
        if (context != null) {
            checkoutIntent = new Intent(context, CheckoutActivity.class);
        } else {
            checkoutIntent = new Intent(activity, CheckoutActivity.class);
        }

        checkoutIntent.putExtra("merchantPublicKey", publicKey);
        checkoutIntent.putExtra("paymentData", JsonUtil.getInstance().toJson(paymentData));
        checkoutIntent.putExtra("checkoutPreference", JsonUtil.getInstance().toJson(checkoutPreference));
        checkoutIntent.putExtra("servicePreference", JsonUtil.getInstance().toJson(servicePreference));
        checkoutIntent.putExtra("flowPreference", JsonUtil.getInstance().toJson(flowPreference));
        checkoutIntent.putExtra("paymentResultScreenPreference", JsonUtil.getInstance().toJson(paymentResultScreenPreference));
        checkoutIntent.putExtra("paymentResult", JsonUtil.getInstance().toJson(paymentResult));
        checkoutIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        checkoutIntent.putExtra("binaryMode", binaryMode);
        checkoutIntent.putExtra("resultCode", resultCode);
        return checkoutIntent;
    }

    public static class Builder {
        private Activity activity;
        private String publicKey;
        private Boolean binaryMode = false;
        private CheckoutPreference checkoutPreference;
        private ServicePreference servicePreference;
        private FlowPreference flowPreference;
        private PaymentResultScreenPreference paymentResultScreenPreference;
        private PaymentData paymentData;
        private PaymentResult paymentResult;
        private Discount discount;
        private CheckoutHooks checkoutHooks;
        private final List<PaymentMethodPlugin> paymentMethodPluginList = new ArrayList<>();
        private final Map<String, PaymentProcessor> paymentPlugins = new HashMap<>();
        private DataInitializationTask dataInitializationTask;
        private String regularFontPath;
        private String lightFontPath;
        private String monoFontPath;
        private ReviewAndConfirmPreferences reviewAndConfirmPreferences;
        private Context context;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setPublicKey(String publicKey) {
            this.publicKey = publicKey.trim();
            return this;
        }

        public Builder setCheckoutPreference(CheckoutPreference checkoutPreference) {
            this.checkoutPreference = checkoutPreference;
            return this;
        }

        public Builder setDiscount(Discount discount) {
            this.discount = discount;
            return this;
        }

        public Builder setServicePreference(ServicePreference servicePreference) {
            this.servicePreference = servicePreference;
            return this;
        }

        public Builder setFlowPreference(FlowPreference flowPreference) {
            this.flowPreference = flowPreference;
            return this;
        }

        public Builder setPaymentResultScreenPreference(PaymentResultScreenPreference paymentResultScreenPreference) {
            this.paymentResultScreenPreference = paymentResultScreenPreference;
            return this;
        }

        public Builder setPaymentData(PaymentData paymentData) {
            this.paymentData = paymentData;
            return this;
        }

        public Builder enableBinaryMode() {
            binaryMode = true;
            return this;
        }

        public Builder setReviewAndConfirmPreferences(ReviewAndConfirmPreferences reviewAndConfirmPreferences) {
            this.reviewAndConfirmPreferences = reviewAndConfirmPreferences;
            return this;
        }

        public Builder setPaymentResult(PaymentResult paymentResult) {
            this.paymentResult = paymentResult;
            return this;
        }

        public Builder setCheckoutHooks(@NonNull final CheckoutHooks checkoutHooks) {
            this.checkoutHooks = checkoutHooks;
            return this;
        }

        public Builder addPaymentMethodPlugin(@NonNull final PaymentMethodPlugin paymentMethodPlugin,
                                              @NonNull final PaymentProcessor paymentProcessor) {
            paymentMethodPluginList.add(paymentMethodPlugin);
            paymentPlugins.put(paymentMethodPlugin.getId(), paymentProcessor);
            return this;
        }

        public Builder setPaymentProcessor(@NonNull final PaymentProcessor paymentProcessor) {
            paymentPlugins.put(PAYMENT_PROCESSOR_KEY, paymentProcessor);
            return this;
        }

        public Builder setDataInitializationTask(@NonNull final DataInitializationTask dataInitializationTask) {
            this.dataInitializationTask = dataInitializationTask;
            return this;
        }

        public void startForPaymentData() {
            MercadoPagoCheckout mercadoPagoCheckout = new MercadoPagoCheckout(this);
            mercadoPagoCheckout.startForResult(MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE);
        }

        public void startForPayment() {
            MercadoPagoCheckout mercadoPagoCheckout = new MercadoPagoCheckout(this);
            mercadoPagoCheckout.startForResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        }

        public Builder setCustomLightFont(String lightFontPath, Context context) {
            this.lightFontPath = lightFontPath;
            if (lightFontPath != null) {
                setCustomFont(context, FontCache.CUSTOM_LIGHT_FONT, this.lightFontPath);
            }
            return this;
        }

        public Builder setCustomRegularFont(String regularFontPath, Context context) {
            this.regularFontPath = regularFontPath;
            if (regularFontPath != null) {
                setCustomFont(context, FontCache.CUSTOM_REGULAR_FONT, this.regularFontPath);
            }
            return this;
        }

        public Builder setCustomMonoFont(String monoFontPath, Context context) {
            this.monoFontPath = monoFontPath;
            if (monoFontPath != null) {
                setCustomFont(context, FontCache.CUSTOM_MONO_FONT, this.monoFontPath);
            }
            return this;
        }

        private void setCustomFont(Context context, String fontType, String fontPath) {
            Typeface typeFace = null;
            if (!FontCache.hasTypeface(fontType)) {
                typeFace = Typeface.createFromAsset(context.getAssets(), fontPath);
                FontCache.setTypeface(fontType, typeFace);
            }
        }

    }
}