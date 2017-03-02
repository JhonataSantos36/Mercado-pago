package com.mercadopago.core;

import android.content.Context;
import android.content.Intent;

import com.mercadopago.CheckoutActivity;
import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.callbacks.PaymentCallback;
import com.mercadopago.callbacks.PaymentDataCallback;
import com.mercadopago.controllers.CustomReviewablesHandler;
import com.mercadopago.controllers.CustomServicesHandler;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.FlowPreference;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.services.CustomService;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.TextUtil;


/**
 * Created by mreverter on 1/17/17.
 */

public class MercadoPagoCheckout {

    private final ReviewScreenPreference reviewScreenPreference;
    private Context context;
    private String publicKey;
    private CheckoutPreference checkoutPreference;
    private DecorationPreference decorationPreference;
    private ServicePreference servicePreference;
    private FlowPreference flowPreference;
    private PaymentResultScreenPreference paymentResultScreenPreference;
    private PaymentData paymentData;
    private PaymentResult paymentResult;
    private Boolean binaryMode;
    private Integer maxSavedCards;
    private Discount discount;

    private MercadoPagoCheckout(Builder builder) {
        this.context = builder.context;
        this.publicKey = builder.publicKey;
        this.paymentData = builder.paymentData;
        this.checkoutPreference = builder.checkoutPreference;
        this.decorationPreference = builder.decorationPreference;
        this.servicePreference = builder.servicePreference;
        this.flowPreference = builder.flowPreference;
        this.paymentResultScreenPreference = builder.paymentResultScreenPreference;
        this.paymentResult = builder.paymentResult;
        this.reviewScreenPreference = builder.reviewScreenPreference;
        this.binaryMode = builder.binaryMode;
        this.maxSavedCards = builder.maxSavedCards;
        this.discount = builder.discount;

        customizeServices(servicePreference);

        CustomReviewablesHandler.getInstance().clear();
        customizeCheckoutReview(reviewScreenPreference);
        customizePaymentResultReview(paymentResultScreenPreference);
    }

    private void customizeServices(ServicePreference servicePreference) {
        CustomServicesHandler.getInstance().clear();
        CustomServicesHandler.getInstance().setServices(servicePreference);
    }

    private void customizeCheckoutReview(ReviewScreenPreference reviewScreenPreference) {

        CustomReviewablesHandler.getInstance().clear();
        if (reviewScreenPreference != null && reviewScreenPreference.hasCustomReviewables()) {
            CustomReviewablesHandler.getInstance().setItemsReview(reviewScreenPreference.getItemsReviewable());
            CustomReviewablesHandler.getInstance().add(reviewScreenPreference.getCustomReviewables());
        }
    }

    private void customizePaymentResultReview(PaymentResultScreenPreference paymentResultScreenPreference) {
        if (paymentResultScreenPreference != null && paymentResultScreenPreference.hasCustomCongratsReviewables()) {
            CustomReviewablesHandler.getInstance().addCongratsReviewables(paymentResultScreenPreference.getCongratsReviewables());
        }
        if (paymentResultScreenPreference != null && paymentResultScreenPreference.hasCustomPendingReviewables()) {
            CustomReviewablesHandler.getInstance().addPendingReviewables(paymentResultScreenPreference.getPendingReviewables());
        }
    }

    private void validate() throws IllegalStateException {
        if (context == null) {
            throw new IllegalStateException("context not set");
        }
        if (TextUtil.isEmpty(publicKey)) {
            throw new IllegalStateException("public key not set");
        }
        if (checkoutPreference == null) {
            throw new IllegalStateException("Checkout preference required");
        }
        if (CallbackHolder.getInstance().hasPaymentCallback()
                && !this.checkoutPreference.hasId()
                && (this.servicePreference == null || !this.servicePreference.hasCreatePaymentURL())) {
            throw new IllegalStateException("Payment service or preference created with private key required to create a payment");
        }
        if (!CallbackHolder.getInstance().hasPaymentCallback()
                && !CallbackHolder.getInstance().hasPaymentDataCallback()) {
            throw new IllegalStateException("Callback is null");
        }
    }

    private void start(PaymentCallback paymentCallback) {
        CallbackHolder.getInstance().clean();
        attachCheckoutCallback(paymentCallback);
        startCheckoutActivity();
    }

    private void start(PaymentDataCallback paymentDataCallback) {
        CallbackHolder.getInstance().clean();
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
        checkoutIntent.putExtra("paymentData", JsonUtil.getInstance().toJson(paymentData));
        checkoutIntent.putExtra("checkoutPreference", JsonUtil.getInstance().toJson(checkoutPreference));
        checkoutIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
        checkoutIntent.putExtra("servicePreference", JsonUtil.getInstance().toJson(servicePreference));
        checkoutIntent.putExtra("flowPreference", JsonUtil.getInstance().toJson(flowPreference));
        checkoutIntent.putExtra("paymentResultScreenPreference", JsonUtil.getInstance().toJson(paymentResultScreenPreference));
        checkoutIntent.putExtra("paymentResult", JsonUtil.getInstance().toJson(paymentResult));
        checkoutIntent.putExtra("reviewScreenPreference", JsonUtil.getInstance().toJson(reviewScreenPreference));
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
        private PaymentResultScreenPreference paymentResultScreenPreference;
        private ReviewScreenPreference reviewScreenPreference;
        private PaymentData paymentData;
        private PaymentResult paymentResult;
        private Discount discount;

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

        public Builder setDecorationPreference(DecorationPreference decorationPreference) {
            this.decorationPreference = decorationPreference;
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

        public Builder setMaxSavedCards(Integer maxSavedCards) {
            this.maxSavedCards = maxSavedCards;
            return this;
        }

        public Builder enableBinaryMode() {
            this.binaryMode = true;
            return this;
        }

        public Builder setReviewScreenPreference(ReviewScreenPreference reviewScreenPreference) {
            this.reviewScreenPreference = reviewScreenPreference;
            return this;
        }

        public Builder setPaymentResult(PaymentResult paymentResult) {
            this.paymentResult = paymentResult;
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
