package com.mercadopago.core;

import android.support.annotation.NonNull;

import com.mercadopago.hooks.CheckoutHooks;
import com.mercadopago.hooks.Hook;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.plugins.PaymentMethodPlugin;
import com.mercadopago.plugins.PaymentPlugin;
import com.mercadopago.plugins.model.PaymentMethodInfo;
import com.mercadopago.preferences.DecorationPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutStore {

    private static final CheckoutStore INSTANCE = new CheckoutStore();

    //Read only data
    private DecorationPreference decorationPreference;
    private List<PaymentMethodPlugin> paymentMethodPluginList = new ArrayList<>();
    private Map<String, PaymentPlugin> paymentPlugins = new HashMap<>();
    private CheckoutHooks checkoutHooks;
    private Hook hook;

    //App state
    private Map<String, Object> data = new HashMap();
    private PaymentMethodInfo selectedPaymentMethod;

    //Payment
    private PaymentResult paymentResult;
    private PaymentData paymentData;
    private Payment payment;


    private CheckoutStore() {
    }

    public static CheckoutStore getInstance() {
        return INSTANCE;
    }

    public DecorationPreference getDecorationPreference() {
        return decorationPreference;
    }

    public void setDecorationPreference(@NonNull final DecorationPreference decorationPreference) {
        this.decorationPreference = decorationPreference;
    }

    public List<PaymentMethodPlugin> getPaymentMethodPluginList() {
        return paymentMethodPluginList;
    }

    public PaymentMethodPlugin getPaymentMethodPluginById(@NonNull final String id) {
        for (PaymentMethodPlugin plugin : paymentMethodPluginList) {
            if (plugin.getPaymentMethodInfo().id.equalsIgnoreCase(id)) {
                return plugin;
            }
        }
        return null;
    }

    public PaymentMethodInfo getPaymentMethodPluginInfoById(@NonNull final String id) {
        for (PaymentMethodPlugin plugin : paymentMethodPluginList) {
            final PaymentMethodInfo info = plugin.getPaymentMethodInfo();
            if (info.id.equalsIgnoreCase(id)) {
                return info;
            }
        }
        return null;
    }

    public void setPaymentMethodPluginList(@NonNull final List<PaymentMethodPlugin> paymentMethodPluginList) {
        this.paymentMethodPluginList = paymentMethodPluginList;
    }

    public PaymentMethodInfo getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public void setPaymentPlugins(Map<String, PaymentPlugin> paymentPlugins) {
        this.paymentPlugins = paymentPlugins;
    }

    public void setSelectedPaymentMethod(PaymentMethodInfo selectedPaymentMethod) {
        this.selectedPaymentMethod = selectedPaymentMethod;
    }

    public Hook getHook() {
        return hook;
    }

    public void setHook(Hook hook) {
        this.hook = hook;
    }

    public CheckoutHooks getCheckoutHooks() {
        return checkoutHooks;
    }

    public void setCheckoutHooks(CheckoutHooks checkoutHooks) {
        this.checkoutHooks = checkoutHooks;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public PaymentPlugin getPaymentPluginByMethod(@NonNull final String paymentMethod) {
        return paymentPlugins.get(paymentMethod);
    }

    public void addPaymentPlugins(@NonNull final PaymentPlugin paymentPlugin, @NonNull final String paymentMethod) {
        this.paymentPlugins.put(paymentMethod, paymentPlugin);
    }

    public PaymentData getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(PaymentData paymentData) {
        this.paymentData = paymentData;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public void reset() {
        data.clear();
        selectedPaymentMethod = null;
        paymentResult= null;
        paymentData= null;
        payment= null;
    }
}