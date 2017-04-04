package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.controllers.CustomReviewablesHandler;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.core.MercadoPagoUI;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.util.TextUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mreverter on 2/2/17.
 */
public class ReviewAndConfirmProviderImpl implements ReviewAndConfirmProvider {
    private final Context context;
    private final ReviewScreenPreference reviewScreenPreference;

    public ReviewAndConfirmProviderImpl(Context context, ReviewScreenPreference reviewScreenPreference) {
        this.context = context;
        this.reviewScreenPreference = reviewScreenPreference;
    }

    @Override
    public Reviewable getSummaryReviewable(PaymentMethod paymentMethod, PayerCost payerCost, BigDecimal amount, Discount discount, Site site, DecorationPreference decorationPreference, OnConfirmPaymentCallback onConfirmPaymentCallback) {
        String confirmationMessage;
        if (this.reviewScreenPreference != null && !TextUtil.isEmpty(this.reviewScreenPreference.getConfirmText())) {
            confirmationMessage = reviewScreenPreference.getConfirmText();
        } else {
            confirmationMessage = context.getString(R.string.mpsdk_confirm);
        }
        String productDetailText = getProductDetailText();
        String discountDetailText = getDiscountDetailText(discount);
        String customDescriptionText = getCustomDescriptionText();
        Integer customTextColor = getCustomTextColor();
        BigDecimal customAmount = getCustomAmount();

        return new MercadoPagoComponents.Views.SummaryViewBuilder()
                .setContext(context)
                .setConfirmationMessage(confirmationMessage)
                .setProductDetailText(productDetailText)
                .setDiscountDetailText(discountDetailText)
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setAmount(amount)
                .setDiscount(discount)
                .setCurrencyId(site.getCurrencyId())
                .setCustomDescriptionText(customDescriptionText)
                .setCustomAmount(customAmount)
                .setCustomTextColor(customTextColor)
                .setDecorationPreference(decorationPreference)
                .setConfirmPaymentCallback(onConfirmPaymentCallback)
                .build();
    }

    @Override
    public Reviewable getItemsReviewable(String currency, List<Item> items, DecorationPreference decorationPreference) {
        return new MercadoPagoComponents.Views.ReviewItemsViewBuilder()
                .setContext(context)
                .setCurrencyId(currency)
                .addItems(items)
                .setDecorationPreference(decorationPreference)
                .build();
    }

    @Override
    public Reviewable getPaymentMethodOnReviewable(PaymentMethod paymentMethod, PayerCost payerCost, CardInfo cardInfo, Site site, DecorationPreference decorationPreference, Boolean editionEnabled, OnReviewChange onReviewChange) {
        return new MercadoPagoComponents.Views.ReviewPaymentMethodOnBuilder()
                .setContext(context)
                .setCurrencyId(site.getCurrencyId())
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setCardInfo(cardInfo)
                .setDecorationPreference(decorationPreference)
                .setReviewChangeCallback(onReviewChange)
                .setEditionEnabled(editionEnabled)
                .build();
    }

    @Override
    public Reviewable getPaymentMethodOffReviewable(PaymentMethod paymentMethod, String paymentMethodCommentInfo, String paymentMethodDescriptionInfo, BigDecimal amount, Site site, DecorationPreference decorationPreference, Boolean editionEnabled, OnReviewChange onReviewChange) {
        return new MercadoPagoComponents.Views.ReviewPaymentMethodOffBuilder()
                .setContext(context)
                .setPaymentMethod(paymentMethod)
                .setPaymentMethodCommentInfo(paymentMethodCommentInfo)
                .setPaymentMethodDescriptionInfo(paymentMethodDescriptionInfo)
                .setAmount(amount)
                .setSite(site)
                .setDecorationPreference(decorationPreference)
                .setReviewChangeCallback(onReviewChange)
                .setEditionEnabled(editionEnabled)
                .build();
    }

    @Override
    public String getReviewTitle() {
        String title;
        if (this.reviewScreenPreference != null && !TextUtil.isEmpty(this.reviewScreenPreference.getReviewTitle())) {
            title = reviewScreenPreference.getReviewTitle();
        } else {
            title = context.getString(R.string.mpsdk_activity_checkout_title);
        }
        return title;
    }

    @Override
    public String getConfirmationMessage() {
        return context.getString(R.string.mpsdk_confirm);
    }

    @Override
    public String getCancelMessage() {
        String confirmationMessage;
        if (this.reviewScreenPreference != null && !TextUtil.isEmpty(this.reviewScreenPreference.getCancelText())) {
            confirmationMessage = reviewScreenPreference.getCancelText();
        } else {
            confirmationMessage = context.getString(R.string.mpsdk_cancel_payment);
        }
        return confirmationMessage;
    }

    private String getProductDetailText() {
        String productDetail;
        if (this.reviewScreenPreference != null && !TextUtil.isEmpty(this.reviewScreenPreference.getProductDetail())) {
            productDetail = reviewScreenPreference.getProductDetail();
        } else {
            productDetail = context.getString(R.string.mpsdk_review_summary_products);
        }
        return productDetail;
    }

    private String getDiscountDetailText(Discount discount) {
        String discountDetail = "";
        if (this.reviewScreenPreference != null && !TextUtil.isEmpty(this.reviewScreenPreference.getDiscountDetail())) {
            discountDetail = reviewScreenPreference.getDiscountDetail();
        } else if(discount != null){
            if (discount.hasPercentOff()) {
                discountDetail = context.getResources().getString(R.string.mpsdk_review_summary_discount_with_percent_off,
                        String.valueOf(discount.getPercentOff()));
            } else {
                discountDetail = context.getResources().getString(R.string.mpsdk_review_summary_discount_with_amount_off);
            }
        }
        return discountDetail;
    }

    private String getCustomDescriptionText() {
        String customDescription = "";
        if (this.reviewScreenPreference != null && !TextUtil.isEmpty(this.reviewScreenPreference.getSummaryCustomDescription())) {
            customDescription = reviewScreenPreference.getSummaryCustomDescription();
        }
        return customDescription;
    }

    private Integer getCustomTextColor() {
        Integer customTextColor = null;
        if (this.reviewScreenPreference != null && this.reviewScreenPreference.getSummaryCustomTextColor() != null) {
            customTextColor = reviewScreenPreference.getSummaryCustomTextColor();
        }
        return customTextColor;
    }

    private BigDecimal getCustomAmount() {
        BigDecimal customAmount = null;
        if (this.reviewScreenPreference != null && isCustomRowAmountValid()) {
            customAmount = reviewScreenPreference.getSummaryCustomAmount();
        }
        return customAmount;
    }

    private boolean isCustomRowAmountValid() {
        return reviewScreenPreference.getSummaryCustomAmount() != null && reviewScreenPreference.getSummaryCustomAmount().compareTo(BigDecimal.ZERO) >= 0;
    }
}
