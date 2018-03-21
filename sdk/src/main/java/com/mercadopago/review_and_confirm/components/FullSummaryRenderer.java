package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.Summary;
import com.mercadopago.model.SummaryDetail;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.review_and_confirm.props.AmountDescriptionProps;
import com.mercadopago.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.util.TextUtils.isEmpty;
import static com.mercadopago.util.TextUtils.isNotEmpty;

/**
 * Created by mromar on 2/28/18.
 */

public class FullSummaryRenderer extends Renderer<FullSummary> {

    @Override
    public View render(@NonNull final FullSummary component, @NonNull final Context context, final ViewGroup parent) {
        final View summaryView = inflate(R.layout.mpsdk_full_summary_component, parent);
        final MPTextView totalAmountTextView = summaryView.findViewById(R.id.mpsdkReviewSummaryTotalText);
        final FrameLayout payerCostContainer = summaryView.findViewById(R.id.mpsdkReviewSummaryPayerCostContainer);
        final MPTextView disclaimerTextView = summaryView.findViewById(R.id.mpsdkDisclaimer);
        final LinearLayout summaryDetailsContainer = summaryView.findViewById(R.id.mpsdkSummaryDetails);
        final LinearLayout reviewSummaryPayContainer = summaryView.findViewById(R.id.mpsdkReviewSummaryPay);
        final View firstSeparator = summaryView.findViewById(R.id.mpsdkFirstSeparator);
        final LinearLayout totalAmountContainer = summaryView.findViewById(R.id.mpsdkReviewSummaryTotal);
        final View secondSeparator = summaryView.findViewById(R.id.mpsdkSecondSeparator);
        final LinearLayout disclaimerLinearLayout = summaryView.findViewById(R.id.disclaimer);

        //summaryDetails list
        for (AmountDescription amountDescription : getAmountDescriptionComponents(component, context)) {
            final Renderer amountDescriptionRenderer = RendererFactory.create(context, amountDescription);
            final View amountView = amountDescriptionRenderer.render();
            summaryDetailsContainer.addView(amountView);
        }

        if (hasToRenderPayerCost(component)) {
            //payer cost
            PayerCostColumn payerCostColumn = new PayerCostColumn(context, component.props.summaryModel.currencyId,
                    component.props.summaryModel.siteId, component.props.summaryModel.getInstallmentsRate(),
                    component.props.summaryModel.getInstallments(), component.props.summaryModel.getPayerCostTotalAmount(),
                    component.props.summaryModel.getInstallmentAmount());
            payerCostColumn.inflateInParent(payerCostContainer, true);
            payerCostColumn.initializeControls();
            payerCostColumn.drawPayerCostWithoutTotal();
        } else {
            reviewSummaryPayContainer.setVisibility(View.GONE);
            firstSeparator.setVisibility(View.GONE);
        }

        //disclaimer
        if (!isEmpty(component.props.summaryModel.cftPercent)) {
            String disclaimer = getDisclaimer(component, context);
            final Renderer disclaimerRenderer = RendererFactory.create(context, getDisclaimerComponent(disclaimer));
            final View disclaimerView = disclaimerRenderer.render();
            disclaimerLinearLayout.addView(disclaimerView);
        }

        //total
        setText(totalAmountTextView, getFormattedAmount(getTotalAmount(component, context), component.props.summaryModel.currencyId));
        totalAmountContainer.setVisibility(getTotalAmount(component, context) == null ? View.GONE : View.VISIBLE);
        secondSeparator.setVisibility(getTotalAmount(component, context) == null ? View.GONE : View.VISIBLE);

        //disclaimer
        setText(disclaimerTextView, getSummary(component, context).getDisclaimerText());
        disclaimerTextView.setTextColor(getSummary(component, context).getDisclaimerColor());

        return summaryView;
    }

    private Summary getSummary(FullSummary component, Context context) {
        ReviewAndConfirmPreferences reviewAndConfirmPreferences = component.props.reviewAndConfirmPreferences;
        Summary.Builder summaryBuilder = new com.mercadopago.model.Summary.Builder();

        if (isValidTotalAmount(component) && reviewAndConfirmPreferences.hasProductAmount()) {
            summaryBuilder.addSummaryProductDetail(reviewAndConfirmPreferences.getProductAmount(), getItemTitle(component), getDefaultTextColor(context))
                    .addSummaryShippingDetail(reviewAndConfirmPreferences.getShippingAmount(), getSummaryShippingTitle(context), getDefaultTextColor(context))
                    .addSummaryArrearsDetail(reviewAndConfirmPreferences.getArrearsAmount(), getSummaryArrearTitle(context), getDefaultTextColor(context))
                    .addSummaryTaxesDetail(reviewAndConfirmPreferences.getTaxesAmount(), getSummaryTaxesTitle(context), getDefaultTextColor(context))
                    .addSummaryDiscountDetail(getDiscountAmount(component), getSummaryDiscountsTitle(context), getDiscountTextColor(context))
                    .setDisclaimerText(reviewAndConfirmPreferences.getDisclaimerText())
                    .setDisclaimerColor(getDisclaimerTextColor(component,context));

            if (getChargesAmount(component).compareTo(BigDecimal.ZERO) > 0) {
                summaryBuilder.addSummaryChargeDetail(getChargesAmount(component), getSummaryChargesTitle(context), getDefaultTextColor(context));
            }
        } else {
            summaryBuilder.addSummaryProductDetail(component.props.summaryModel.getTotalAmount(), getItemTitle(component), getDefaultTextColor(context));

            if (isValidAmount(component.props.summaryModel.getPayerCostTotalAmount()) && getPayerCostChargesAmount(component).compareTo(BigDecimal.ZERO) > 0) {
                summaryBuilder.addSummaryChargeDetail(getPayerCostChargesAmount(component), getSummaryChargesTitle(context), getDefaultTextColor(context));
            }

            if (!isEmpty(reviewAndConfirmPreferences.getDisclaimerText())) {
                summaryBuilder.setDisclaimerText(reviewAndConfirmPreferences.getDisclaimerText()).setDisclaimerColor(getDisclaimerTextColor(component, context));
            }

            if (isValidAmount(component.props.summaryModel.getCouponAmount())) {
                summaryBuilder.addSummaryDiscountDetail(component.props.summaryModel.getCouponAmount(), getSummaryDiscountsTitle(context), getDiscountTextColor(context));
            }
        }

        return summaryBuilder.build();
    }

    private String getSummaryDiscountsTitle(Context context) {
        return context.getString(R.string.mpsdk_review_summary_discounts);
    }

    private String getSummaryTaxesTitle(Context context) {
        return context.getString(R.string.mpsdk_review_summary_taxes);
    }

    private String getSummaryArrearTitle(Context context) {
        return context.getString(R.string.mpsdk_review_summary_arrear);
    }

    private String getSummaryShippingTitle(Context context) {
        return context.getString(R.string.mpsdk_review_summary_shipping);
    }

    private int getDefaultTextColor(Context context) {
        return ContextCompat.getColor(context, R.color.mpsdk_summary_text_color);
    }

    private int getDiscountTextColor(Context context) {
        return ContextCompat.getColor(context, R.color.mpsdk_summary_discount_color);
    }

    private boolean isValidTotalAmount(FullSummary component) {
        ReviewAndConfirmPreferences reviewScreenPreference = component.props.reviewAndConfirmPreferences;
        BigDecimal totalAmountPreference = reviewScreenPreference.getTotalAmount();
        return totalAmountPreference.compareTo(component.props.summaryModel.getTotalAmount()) == 0;
    }

    private BigDecimal getChargesAmount(FullSummary component) {
        ReviewAndConfirmPreferences reviewScreenPreference = component.props.reviewAndConfirmPreferences;
        BigDecimal interestAmount = new BigDecimal(0);

        if (reviewScreenPreference.getChargeAmount() != null) {
            interestAmount = reviewScreenPreference.getChargeAmount();
        }

        if (component.props.summaryModel.getInstallments() != null && component.props.summaryModel.getInstallments() > 1 &&
                isValidAmount(component.props.summaryModel.getPayerCostTotalAmount())) {
            BigDecimal totalInterestsAmount = getPayerCostChargesAmount(component);
            interestAmount = interestAmount.add(totalInterestsAmount);
        }

        return interestAmount;
    }

    private BigDecimal getPayerCostChargesAmount(FullSummary component) {
        BigDecimal totalInterestsAmount;

        if (isValidAmount(component.props.summaryModel.getCouponAmount())) {
            BigDecimal totalAmount = component.props.summaryModel.getTotalAmount().subtract(component.props.summaryModel.getCouponAmount());
            totalInterestsAmount = component.props.summaryModel.getPayerCostTotalAmount().subtract(totalAmount);
        } else {
            totalInterestsAmount =
                    component.props.summaryModel.getPayerCostTotalAmount().subtract(component.props.summaryModel.getTotalAmount());
        }

        return totalInterestsAmount;
    }


    private BigDecimal getDiscountAmount(FullSummary component) {
        ReviewAndConfirmPreferences reviewScreenPreference = component.props.reviewAndConfirmPreferences;
        BigDecimal discountAmount = reviewScreenPreference.getDiscountAmount();

        if (isValidAmount(component.props.summaryModel.getCouponAmount())) {
            discountAmount = discountAmount.add(component.props.summaryModel.getCouponAmount());
        }

        return discountAmount;
    }

    private String getSummaryChargesTitle(Context context) {
        return context.getString(R.string.mpsdk_review_summary_charges);
    }

    private boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private String getItemTitle(FullSummary component) {
        String title = component.props.summaryModel.title;

        if (isNotEmpty(component.props.reviewAndConfirmPreferences.getProductTitle())) {
            title = component.props.reviewAndConfirmPreferences.getProductTitle();
        }

        return title;
    }

    private List<AmountDescription> getAmountDescriptionComponents(FullSummary component, Context context) {
        List<AmountDescription> amountDescriptionList = new ArrayList<>();

        for (SummaryDetail summaryDetail : getSummary(component, context).getSummaryDetails()) {
            final AmountDescriptionProps amountDescriptionProps = new AmountDescriptionProps(
                    summaryDetail.getTotalAmount(),
                    summaryDetail.getTitle(),
                    component.props.summaryModel.currencyId,
                    summaryDetail.getTextColor());

            amountDescriptionList.add(new AmountDescription(amountDescriptionProps));
        }

        return amountDescriptionList;
    }

    private int getDisclaimerTextColor(FullSummary component, Context context) {
        int disclaimerTextColor;

        if (isEmpty(component.props.reviewAndConfirmPreferences.getDisclaimerTextColor())) {
            disclaimerTextColor = ContextCompat.getColor(context, R.color.mpsdk_default_disclaimer);
        } else {
            disclaimerTextColor = Color.parseColor(component.props.reviewAndConfirmPreferences.getDisclaimerTextColor());
        }

        return disclaimerTextColor;
    }

    private Spanned getFormattedAmount(BigDecimal amount, String currencyId) {
        return amount != null && !isEmpty(currencyId) ? CurrenciesUtil.getFormattedAmount(amount, currencyId) : null;
    }

    private String getDisclaimer(FullSummary component, Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        if (!isEmpty(component.props.summaryModel.cftPercent)) {
            stringBuilder.append(context.getString(R.string.mpsdk_installments_cft));
            stringBuilder.append(" ");
            stringBuilder.append(component.props.summaryModel.cftPercent);
        }

        return stringBuilder.toString();
    }

    private BigDecimal getTotalAmount(FullSummary component, Context context) {
        BigDecimal totalAmount;

        if (isCardPaymentMethod(component)) {
            if (component.props.summaryModel.getInstallments() == 1) {
                if (component.props.summaryModel.getCouponAmount() != null && !isEmptySummaryDetails(component, context)) {
                    totalAmount = component.props.summaryModel.getPayerCostTotalAmount();
                } else {
                    totalAmount = component.props.summaryModel.getTotalAmount();
                }
            } else {
                totalAmount = component.props.summaryModel.getPayerCostTotalAmount();
            }
        } else if (hasDiscount(component) && !isEmptySummaryDetails(component, context)) {
            totalAmount = getSubtotal(component);
        } else {
            totalAmount = component.props.summaryModel.getTotalAmount();
        }
        return totalAmount;
    }

    private boolean isCardPaymentMethod(FullSummary component) {
        return component.props.summaryModel.paymentTypeId != null && isCard(component.props.summaryModel.paymentTypeId);
    }

    private boolean hasDiscount(FullSummary component) {
        return component.props.summaryModel.currencyId != null && component.props.summaryModel.getCouponAmount() != null;
    }

    private boolean isCard(String paymentTypeId) {
        boolean isCard = false;

        if ((paymentTypeId != null) && (paymentTypeId.equals("credit_card") ||
                paymentTypeId.equals("debit_card") || paymentTypeId.equals("prepaid_card"))) {
            isCard = true;
        }

        return isCard;
    }

    private boolean isEmptySummaryDetails(FullSummary component, Context context) {
        return getSummary(component, context) != null && getSummary(component, context).getSummaryDetails() != null &&
                getSummary(component, context).getSummaryDetails().size() < 2;
    }

    private boolean hasToRenderPayerCost(FullSummary component) {
        return isCardPaymentMethod(component) && component.props.summaryModel.getInstallments() > 1;
    }

    private DisclaimerComponent getDisclaimerComponent(String disclaimer) {
        DisclaimerComponent.Props props = new DisclaimerComponent.Props(disclaimer);
        return new DisclaimerComponent(props);
    }

    private BigDecimal getSubtotal(FullSummary component) {
        BigDecimal ans = component.props.summaryModel.getTotalAmount();
        if (hasDiscount(component)) {
            ans = component.props.summaryModel.getTotalAmount().subtract(component.props.summaryModel.getCouponAmount());
        }
        return ans;
    }

    private String getSummaryProductsTitle(FullSummary component, Context context) {
        String summaryProductTitle;

        if (!isEmpty(component.props.reviewAndConfirmPreferences.getProductTitle())) {
            summaryProductTitle = component.props.reviewAndConfirmPreferences.getProductTitle();
        } else {
            summaryProductTitle = context.getString(R.string.mpsdk_review_summary_product);
        }

        return summaryProductTitle;
    }

}
