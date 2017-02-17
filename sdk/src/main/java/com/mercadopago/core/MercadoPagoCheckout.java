package com.mercadopago.core;

import android.content.Context;
import android.content.Intent;

import com.mercadopago.CheckoutActivity;
import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.callbacks.PaymentCallback;
import com.mercadopago.callbacks.PaymentDataCallback;
import com.mercadopago.controllers.CustomReviewablesHandler;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.Reviewable;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.FlowPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 1/17/17.
 */

public class MercadoPagoCheckout {

    private Context context;
    private String publicKey;
    private CheckoutPreference checkoutPreference;
    private DecorationPreference decorationPreference;
    private ServicePreference servicePreference;
    private FlowPreference flowPreference;
    private Boolean binaryMode;
    private Integer maxSavedCards;
    private PaymentData paymentData;
    private Discount discount;

    private MercadoPagoCheckout(Builder builder) {
        this.context = builder.context;
        this.publicKey = builder.publicKey;
        this.checkoutPreference = builder.checkoutPreference;
        this.decorationPreference = builder.decorationPreference;
        this.servicePreference = builder.servicePreference;
        this.flowPreference = builder.flowPreference;
        this.paymentData = builder.paymentData;
        this.binaryMode = builder.binaryMode;
        this.maxSavedCards = builder.maxSavedCards;
        CustomReviewablesHandler.getInstance().clear();
        CustomReviewablesHandler.getInstance().add(builder.reviewables);
    }

    private void validate() throws IllegalStateException {
        if(context == null) {
            throw new IllegalStateException("context not set");
        }
        if(TextUtil.isEmpty(publicKey)) {
            throw new IllegalStateException("public key not set");
        }
        if(checkoutPreference == null) {
            throw new IllegalStateException("Checkout preference required");
        }
        if(CallbackHolder.getInstance().hasPaymentCallback()
                && !this.checkoutPreference.hasId()
                && (this.servicePreference == null || !this.servicePreference.hasCreatePaymentURL())) {
            throw new IllegalStateException("Payment service or preference created with private key required to create a payment");
        }
        if(!CallbackHolder.getInstance().hasPaymentCallback()
                && !CallbackHolder.getInstance().hasPaymentDataCallback()) {
            throw new IllegalStateException("Callback is null");
        }
    }

    private void start(PaymentCallback paymentCallback) {
        attachCheckoutCallback(paymentCallback);
        startCheckoutActivity();
    }

    private void start(PaymentDataCallback paymentDataCallback) {
        attachCheckoutCallback(paymentDataCallback);
        startCheckoutActivity();
    }

    private void attachCheckoutCallback(PaymentCallback paymentCallback) {
        CallbackHolder.getInstance().setPaymentCallback(paymentCallback);
    }

    private void attachCheckoutCallback(PaymentDataCallback paymentDataCallback) {
        CallbackHolder.getInstance().setPaymentDataCallback(paymentDataCallback);
    }

    private void startCheckoutActivity() {
        validate();
        Intent checkoutIntent = new Intent(context, CheckoutActivity.class);
        checkoutIntent.putExtra("merchantPublicKey", publicKey);
        checkoutIntent.putExtra("checkoutPreference", JsonUtil.getInstance().toJson(checkoutPreference));
        checkoutIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        checkoutIntent.putExtra("servicePreference", JsonUtil.getInstance().toJson(servicePreference));
        checkoutIntent.putExtra("flowPreference", JsonUtil.getInstance().toJson(flowPreference));
        checkoutIntent.putExtra("paymentData", JsonUtil.getInstance().toJson(paymentData));
        checkoutIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        checkoutIntent.putExtra("binaryMode", binaryMode);
        checkoutIntent.putExtra("maxSavedCards", maxSavedCards);
        context.startActivity(checkoutIntent);
    }

    public static class Builder {
        private Context context;
        private String publicKey;
        private Boolean binaryMode = false;
        private Integer maxSavedCards;
        private CheckoutPreference checkoutPreference;
        private DecorationPreference decorationPreference;
        private ServicePreference servicePreference;
        private FlowPreference flowPreference;
        private List<Reviewable> reviewables;
        private PaymentData paymentData;

        public Builder setContext(Context context) {
            this.reviewables = new ArrayList<>();
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

        public Builder setDecorationPreference(DecorationPreference decorationPreference) {
            this.decorationPreference = decorationPreference;
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

        public Builder setMaxSavedCards(Integer maxSavedCards) {
            this.maxSavedCards = maxSavedCards;
            return this;
        }

        public Builder enableBinaryMode() {
            this.binaryMode = true;
            return this;
        }

        public Builder addReviewable(Reviewable customReviewable) {
            this.reviewables.add(customReviewable);
            return this;
        }

        public Builder setPaymentData(PaymentData paymentData) {
            this.paymentData = paymentData;
            return this;
        }

        public void start(PaymentCallback paymentCallback) {
            MercadoPagoCheckout mercadoPagoCheckout = new MercadoPagoCheckout(this);
            mercadoPagoCheckout.start(paymentCallback);

        }

        public void start(PaymentDataCallback paymentDataCallback) {
            MercadoPagoCheckout mercadoPagoCheckout = new MercadoPagoCheckout(this);
            mercadoPagoCheckout.start(paymentDataCallback);
        }
    }
}
