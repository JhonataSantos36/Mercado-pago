package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.core.MercadoPagoUI;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mreverter on 2/2/17.
 */
public class ReviewAndConfirmProviderImpl implements ReviewAndConfirmProvider {
    private final Context context;

    public ReviewAndConfirmProviderImpl(Context context) {
        this.context = context;
    }

    @Override
    public Reviewable getSummaryReviewable(PaymentMethod paymentMethod, PayerCost payerCost, BigDecimal amount, Discount discount, Site site, DecorationPreference decorationPreference, OnConfirmPaymentCallback onConfirmPaymentCallback) {
        return new MercadoPagoUI.Views.SummaryViewBuilder()
                .setContext(context)
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setAmount(amount)
                .setDiscount(discount)
                .setCurrencyId(site.getCurrencyId())
                .setDecorationPreference(decorationPreference)
                .setConfirmPaymentCallback(onConfirmPaymentCallback)
                .build();
    }

    @Override
    public Reviewable getItemsReviewable(String currency, List<Item> items) {
        return new MercadoPagoUI.Views.ReviewItemsViewBuilder()
                .setContext(context)
                .setCurrencyId(currency)
                .addItems(items)
                .build();
    }

    @Override
    public Reviewable getPaymentMethodOnReviewable(PaymentMethod paymentMethod, PayerCost payerCost, CardInfo cardInfo, Site site, DecorationPreference decorationPreference, OnReviewChange onReviewChange) {
        return new MercadoPagoUI.Views.ReviewPaymentMethodOnBuilder()
                .setContext(context)
                .setCurrencyId(site.getCurrencyId())
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setCardInfo(cardInfo)
                .setDecorationPreference(decorationPreference)
                .setReviewChangeCallback(onReviewChange)
                .build();
    }

    @Override
    public Reviewable getPaymentMethodOffReviewable(PaymentMethod paymentMethod, String extraPaymentMethodInfo, BigDecimal amount, Site site, DecorationPreference decorationPreference, OnReviewChange onReviewChange) {
        return new MercadoPagoUI.Views.ReviewPaymentMethodOffBuilder()
                .setContext(context)
                .setPaymentMethod(paymentMethod)
                .setExtraPaymentMethodInfo(extraPaymentMethodInfo)
                .setAmount(amount)
                .setSite(site)
                .setDecorationPreference(decorationPreference)
                .setReviewChangeCallback(onReviewChange)
                .build();
    }
}
