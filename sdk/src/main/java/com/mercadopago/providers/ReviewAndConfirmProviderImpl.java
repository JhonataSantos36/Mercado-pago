package com.mercadopago.providers;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnConfirmPaymentCallback;
import com.mercadopago.callbacks.OnReviewChange;
import com.mercadopago.controllers.CustomReviewablesHandler;
import com.mercadopago.core.MercadoPagoComponents;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Reviewable;
import com.mercadopago.model.Site;
import com.mercadopago.model.Summary;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.util.TextUtil;

import java.math.BigDecimal;
import java.util.List;

import static com.mercadopago.util.TextUtil.isEmpty;

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
    public Reviewable getSummaryReviewable(PaymentMethod paymentMethod, PayerCost payerCost, BigDecimal amount, Discount discount, Site site, Issuer issuer, OnConfirmPaymentCallback onConfirmPaymentCallback) {
        final SummaryHelper summaryHelper = new SummaryHelper(context, reviewScreenPreference, amount, payerCost, discount);
        final Summary summary = summaryHelper.getSummary();
        return new MercadoPagoComponents.Views.SummaryViewBuilder()
                .setContext(context)
                .setSummary(summary)
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setAmount(amount)
                .setDiscount(discount)
                .setCurrencyId(site.getCurrencyId())
                .setConfirmPaymentCallback(onConfirmPaymentCallback)
                .setIssuer(issuer)
                .setSite(site)
                .build();
    }

    @Override
    public Reviewable getItemsReviewable(String currency, List<Item> items) {
        if (CustomReviewablesHandler.getInstance().hasCustomItemsReviewable()) {
            return CustomReviewablesHandler.getInstance().getItemsReviewable();
        } else {
            return new MercadoPagoComponents.Views.ReviewItemsViewBuilder()
                    .setContext(context)
                    .setCurrencyId(currency)
                    .addItems(items)
                    .setReviewScreenPreference(reviewScreenPreference)
                    .build();
        }
    }

    @Override
    public Reviewable getPaymentMethodOnReviewable(PaymentMethod paymentMethod, PayerCost payerCost, CardInfo cardInfo, Site site, Boolean editionEnabled, OnReviewChange onReviewChange) {
        return new MercadoPagoComponents.Views.ReviewPaymentMethodOnBuilder()
                .setContext(context)
                .setCurrencyId(site.getCurrencyId())
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setCardInfo(cardInfo)
                .setReviewChangeCallback(onReviewChange)
                .setEditionEnabled(editionEnabled)
                .setSite(site)
                .build();
    }

    @Override
    public Reviewable getPaymentMethodOffReviewable(PaymentMethod paymentMethod, String paymentMethodCommentInfo, String paymentMethodDescriptionInfo, BigDecimal amount, Site site, Boolean editionEnabled, OnReviewChange onReviewChange) {
        return new MercadoPagoComponents.Views.ReviewPaymentMethodOffBuilder()
                .setContext(context)
                .setPaymentMethod(paymentMethod)
                .setPaymentMethodCommentInfo(paymentMethodCommentInfo)
                .setPaymentMethodDescriptionInfo(paymentMethodDescriptionInfo)
                .setAmount(amount)
                .setSite(site)
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
        if (this.reviewScreenPreference != null && !TextUtil.isEmpty(this.reviewScreenPreference.getConfirmText())) {
            return this.reviewScreenPreference.getConfirmText();
        }
        return context.getString(R.string.mpsdk_confirm_payment);
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


    private static class SummaryHelper {

        private Context context;
        private ReviewScreenPreference reviewScreenPreference;
        private BigDecimal amount;
        private PayerCost payerCost;
        private Discount discount;

        SummaryHelper(Context context, ReviewScreenPreference reviewScreenPreference, BigDecimal amount, PayerCost payerCost, Discount discount) {
            this.context = context;
            this.reviewScreenPreference = reviewScreenPreference;
            this.amount = amount;
            this.payerCost = payerCost;
            this.discount = discount;
        }

        public Summary getSummary() {
            Summary.Builder summaryBuilder = new Summary.Builder();

            if (reviewScreenPreference != null && isValidTotalAmount() && hasProductAmount()) {
                summaryBuilder.addSummaryProductDetail(reviewScreenPreference.getProductAmount(), getSummaryProductsTitle(), getDefaultTextColor())
                        .addSummaryShippingDetail(reviewScreenPreference.getShippingAmount(), getSummaryShippingTitle(), getDefaultTextColor())
                        .addSummaryArrearsDetail(reviewScreenPreference.getArrearsAmount(), getSummaryArrearTitle(), getDefaultTextColor())
                        .addSummaryTaxesDetail(reviewScreenPreference.getTaxesAmount(), getSummaryTaxesTitle(), getDefaultTextColor())
                        .addSummaryDiscountDetail(getDiscountAmount(), getSummaryDiscountsTitle(), getDiscountTextColor())
                        .setDisclaimerText(reviewScreenPreference.getDisclaimerText())
                        .setDisclaimerColor(getDisclaimerTextColor());

                if (getChargesAmount().compareTo(BigDecimal.ZERO) > 0) {
                    summaryBuilder.addSummaryChargeDetail(getChargesAmount(), getSummaryChargesTitle(), getDefaultTextColor());
                }

            } else {
                summaryBuilder.addSummaryProductDetail(amount, getSummaryProductsTitle(), getDefaultTextColor());

                if (payerCost != null && getPayerCostChargesAmount().compareTo(BigDecimal.ZERO) > 0) {
                    summaryBuilder.addSummaryChargeDetail(getPayerCostChargesAmount(), getSummaryChargesTitle(), getDefaultTextColor());
                }

                if (reviewScreenPreference != null && !isEmpty(reviewScreenPreference.getDisclaimerText())) {
                    summaryBuilder.setDisclaimerText(reviewScreenPreference.getDisclaimerText())
                            .setDisclaimerColor(getDisclaimerTextColor());
                }

                if (discount != null) {
                    summaryBuilder.addSummaryDiscountDetail(discount.getCouponAmount(), getSummaryDiscountsTitle(), getDiscountTextColor());
                }
            }

            return summaryBuilder.build();
        }

        private boolean hasProductAmount() {
            return reviewScreenPreference.hasProductAmount();
        }

        private int getDisclaimerTextColor() {
            int disclaimerColorText;

            if (isEmpty(reviewScreenPreference.getDisclaimerTextColor())) {
                disclaimerColorText = ContextCompat.getColor(context, R.color.mpsdk_default_disclaimer);
            } else {
                disclaimerColorText = Color.parseColor(reviewScreenPreference.getDisclaimerTextColor());
            }

            return disclaimerColorText;
        }

        private int getDefaultTextColor() {
            return ContextCompat.getColor(context, R.color.mpsdk_summary_text_color);
        }

        private int getDiscountTextColor() {
            return ContextCompat.getColor(context, R.color.mpsdk_summary_discount_color);
        }

        private boolean isValidTotalAmount() {
            BigDecimal totalAmountPreference = reviewScreenPreference.getTotalAmount();
            return totalAmountPreference.compareTo(amount) == 0;
        }

        private BigDecimal getDiscountAmount() {
            BigDecimal discountAmount = reviewScreenPreference.getDiscountAmount();

            if (discount != null && isValidAmount(discount.getCouponAmount())) {
                discountAmount = discountAmount.add(discount.getCouponAmount());
            }

            return discountAmount;
        }

        private BigDecimal getChargesAmount() {
            BigDecimal interestAmount = new BigDecimal(0);

            if (reviewScreenPreference.getChargeAmount() != null) {
                interestAmount = reviewScreenPreference.getChargeAmount();
            }

            if (payerCost != null && payerCost.getInstallments() > 1 && isValidAmount(payerCost.getTotalAmount())) {
                BigDecimal totalInterestsAmount = getPayerCostChargesAmount();
                interestAmount = interestAmount.add(totalInterestsAmount);
            }

            return interestAmount;
        }

        private BigDecimal getPayerCostChargesAmount() {
            BigDecimal totalInterestsAmount;

            if (discount != null && isValidAmount(discount.getCouponAmount())) {
                BigDecimal totalAmount = amount.subtract(discount.getCouponAmount());
                totalInterestsAmount = payerCost.getTotalAmount().subtract(totalAmount);
            } else {
                totalInterestsAmount = payerCost.getTotalAmount().subtract(amount);
            }

            return totalInterestsAmount;
        }

        private boolean isValidAmount(BigDecimal amount) {
            return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
        }

        private String getSummaryProductsTitle() {
            String summaryProductTitle;

            if (reviewScreenPreference != null && !isEmpty(reviewScreenPreference.getProductTitle())) {
                summaryProductTitle = reviewScreenPreference.getProductTitle();
            } else {
                summaryProductTitle = context.getString(R.string.mpsdk_review_summary_product);
            }

            return summaryProductTitle;
        }

        private String getSummaryDiscountsTitle() {
            return context.getString(R.string.mpsdk_review_summary_discounts);
        }

        private String getSummaryChargesTitle() {
            return context.getString(R.string.mpsdk_review_summary_charges);
        }

        private String getSummaryTaxesTitle() {
            return context.getString(R.string.mpsdk_review_summary_taxes);
        }

        private String getSummaryShippingTitle() {
            return context.getString(R.string.mpsdk_review_summary_shipping);
        }

        private String getSummaryArrearTitle() {
            return context.getString(R.string.mpsdk_review_summary_arrear);
        }

    }

}
