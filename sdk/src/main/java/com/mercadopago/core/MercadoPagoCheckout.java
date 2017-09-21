package com.mercadopago.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.mercadopago.CheckoutActivity;
import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.constants.ContentLocation;
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
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.TextUtil;


/**
 * Created by mreverter on 1/17/17.
 */

public class MercadoPagoCheckout {

    public static final Integer CHECKOUT_REQUEST_CODE = 5;
    public static final Integer PAYMENT_DATA_RESULT_CODE = 6;
    public static final Integer PAYMENT_RESULT_CODE = 7;
    public static final Integer TIMER_FINISHED_RESULT_CODE = 8;
    public static final Integer PAYMENT_METHOD_CHANGED_REQUESTED = 9;

    private final ReviewScreenPreference reviewScreenPreference;
    private Context context;
    private Activity activity;
    private String publicKey;
    private CheckoutPreference checkoutPreference;
    private DecorationPreference decorationPreference;
    private ServicePreference servicePreference;
    private FlowPreference flowPreference;
    private PaymentResultScreenPreference paymentResultScreenPreference;
    private PaymentData paymentData;
    private PaymentResult paymentResult;
    private Boolean binaryMode;
    private Discount discount;

    private MercadoPagoCheckout(Builder builder) {
        this.activity = builder.activity;
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
            CustomReviewablesHandler.getInstance().addCongratsReviewables(paymentResultScreenPreference.getCongratsReviewables(ContentLocation.BOTTOM), ContentLocation.BOTTOM);
            CustomReviewablesHandler.getInstance().addCongratsReviewables(paymentResultScreenPreference.getCongratsReviewables(ContentLocation.TOP), ContentLocation.TOP);
        }
        if (paymentResultScreenPreference != null && paymentResultScreenPreference.hasCustomPendingReviewables()) {
            CustomReviewablesHandler.getInstance().addPendingReviewables(paymentResultScreenPreference.getPendingReviewables());
        }
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
        if ((CallbackHolder.getInstance().hasPaymentCallback() || resultCode.equals(MercadoPagoCheckout.PAYMENT_RESULT_CODE))
                && !this.checkoutPreference.hasId()
                && (this.servicePreference == null || !this.servicePreference.hasCreatePaymentURL())) {
            //TODO revisar
//            throw new IllegalStateException("Payment service or preference created with private key required to create a payment");
        }
        if (hasTwoDiscountsSet()) {
            throw new IllegalStateException("payment data discount and discount set");
        }
        if (isCheckoutTimerAvailable(resultCode) && isPaymentDataIntegration(resultCode)) {
            throw new IllegalStateException("CheckoutTimer is not available with PaymentData integration");
        }
    }

    private boolean isCheckoutTimerAvailable(int resultCode) {
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
        return this.paymentData != null && this.paymentData.getDiscount() != null;
    }

    private Boolean hasPaymentResultDiscount() {
        return this.paymentResult != null && this.paymentResult.getPaymentData() != null && this.paymentResult.getPaymentData().getDiscount() != null;
    }

    private Boolean hasDiscount() {
        return this.discount != null;
    }

    private void startForResult(@NonNull Integer resultCode) {
        CallbackHolder.getInstance().clean();
        startCheckoutActivity(resultCode);
    }

    private void startCheckoutActivity(Integer resultCode) {
        validate(resultCode);
        Intent checkoutIntent;
        if (context != null) {
            checkoutIntent = new Intent(context, CheckoutActivity.class);
        } else {
            checkoutIntent = new Intent(activity, CheckoutActivity.class);
        }
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
        checkoutIntent.putExtra("resultCode", resultCode);

        if (context != null) {
            context.startActivity(checkoutIntent);
        } else {
            activity.startActivityForResult(checkoutIntent, MercadoPagoCheckout.CHECKOUT_REQUEST_CODE);
        }
    }

    public static class Builder {
        private Context context;
        private Activity activity;
        private String publicKey;
        private Boolean binaryMode = false;
        private CheckoutPreference checkoutPreference;
        private DecorationPreference decorationPreference;
        private ServicePreference servicePreference;
        private FlowPreference flowPreference;
        private PaymentResultScreenPreference paymentResultScreenPreference;
        private ReviewScreenPreference reviewScreenPreference;
        private PaymentData paymentData;
        private PaymentResult paymentResult;
        private Discount discount;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
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

        public void startForPaymentData() {
            MercadoPagoCheckout mercadoPagoCheckout = new MercadoPagoCheckout(this);
            mercadoPagoCheckout.startForResult(MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE);
        }

        public void startForPayment() {
            MercadoPagoCheckout mercadoPagoCheckout = new MercadoPagoCheckout(this);
            mercadoPagoCheckout.startForResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        }
    }
}