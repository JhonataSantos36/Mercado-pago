package com.mercadopago.review_and_confirm.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.model.Summary;
import com.mercadopago.model.SummaryDetail;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.review_and_confirm.SummaryProvider;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.review_and_confirm.models.SummaryModel;
import com.mercadopago.review_and_confirm.props.AmountDescriptionProps;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.util.TextUtils.isEmpty;

/**
 * Created by mromar on 2/28/18.
 */

public class FullSummary extends Component<SummaryComponent.SummaryProps, Void> {

    private SummaryProvider provider;

    public static final String CFT = "CFT ";

    static {
        RendererFactory.register(FullSummary.class, FullSummaryRenderer.class);
    }

    public FullSummary(@NonNull final SummaryComponent.SummaryProps props,
                       @NonNull final SummaryProvider provider) {
        super(props);
        this.provider = provider;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = null;

        if (isCardPaymentMethod()) {
            if (props.summaryModel.getInstallments() == 1) {
                if (props.summaryModel.getCouponAmount() != null && !isEmptySummaryDetails()) {
                    totalAmount = props.summaryModel.getPayerCostTotalAmount();
                }
            } else {
                totalAmount = props.summaryModel.getPayerCostTotalAmount();
            }
        } else if (hasDiscount() && !isEmptySummaryDetails()) {
            totalAmount = getSubtotal();
        }
        return totalAmount;
    }

    public boolean hasToRenderPayerCost() {
        return isCardPaymentMethod() && props.summaryModel.getInstallments() > 1;
    }

    public String getFinance() {
        StringBuilder stringBuilder = new StringBuilder();

        if (!isEmpty(props.summaryModel.cftPercent)) {
            stringBuilder.append(CFT);
            stringBuilder.append(props.summaryModel.cftPercent);
        }

        return stringBuilder.toString();
    }

    public List<AmountDescription> getAmountDescriptionComponents() {
        List<AmountDescription> amountDescriptionList = new ArrayList<>();

        for (SummaryDetail summaryDetail : getSummary().getSummaryDetails()) {
            final AmountDescriptionProps amountDescriptionProps = new AmountDescriptionProps(
                    summaryDetail.getTotalAmount(),
                    summaryDetail.getTitle(),
                    props.summaryModel.currencyId,
                    summaryDetail.getTextColor());

            amountDescriptionList.add(new AmountDescription(amountDescriptionProps));
        }

        return amountDescriptionList;
    }

    public Summary getSummary() {
        ReviewAndConfirmPreferences reviewScreenPreference = props.reviewAndConfirmPreferences;
        Summary.Builder summaryBuilder = new com.mercadopago.model.Summary.Builder();

        if (isValidTotalAmount() && reviewScreenPreference.hasProductAmount()) {
            summaryBuilder.addSummaryProductDetail(reviewScreenPreference.productAmount, props.summaryModel.title, provider.getDefaultTextColor())
                    .addSummaryShippingDetail(reviewScreenPreference.shippingAmount, provider.getSummaryShippingTitle(), provider.getDefaultTextColor())
                    .addSummaryArrearsDetail(reviewScreenPreference.arrearsAmount, provider.getSummaryArrearTitle(), provider.getDefaultTextColor())
                    .addSummaryTaxesDetail(reviewScreenPreference.taxesAmount, provider.getSummaryTaxesTitle(), provider.getDefaultTextColor())
                    .addSummaryDiscountDetail(getDiscountAmount(), provider.getSummaryDiscountsTitle(), provider.getDiscountTextColor())
                    .setDisclaimerText(reviewScreenPreference.disclaimerText)
                    .setDisclaimerColor(provider.getDisclaimerTextColor());

            if (getChargesAmount().compareTo(BigDecimal.ZERO) > 0) {
                summaryBuilder.addSummaryChargeDetail(getChargesAmount(), provider.getSummaryChargesTitle(), provider.getDefaultTextColor());
            }

        } else {
            summaryBuilder.addSummaryProductDetail(props.summaryModel.getTotalAmount(), props.summaryModel.title, provider.getDefaultTextColor());

            if (isValidAmount(props.summaryModel.getPayerCostTotalAmount()) && getPayerCostChargesAmount().compareTo(BigDecimal.ZERO) > 0) {
                summaryBuilder.addSummaryChargeDetail(getPayerCostChargesAmount(), provider.getSummaryChargesTitle(), provider.getDefaultTextColor());
            }

            if (!isEmpty(reviewScreenPreference.disclaimerText)) {
                summaryBuilder.setDisclaimerText(reviewScreenPreference.disclaimerText)
                        .setDisclaimerColor(provider.getDisclaimerTextColor());
            }

            if (isValidAmount(props.summaryModel.getCouponAmount())) {
                summaryBuilder.addSummaryDiscountDetail(props.summaryModel.getCouponAmount(),
                        provider.getSummaryDiscountsTitle(),
                        provider.getDiscountTextColor());
            }
        }

        return summaryBuilder.build();
    }

    private BigDecimal getChargesAmount() {
        ReviewAndConfirmPreferences reviewScreenPreference = props.reviewAndConfirmPreferences;
        BigDecimal interestAmount = new BigDecimal(0);

        if (reviewScreenPreference.chargeAmount != null) {
            interestAmount = reviewScreenPreference.chargeAmount;
        }

        if (props.summaryModel.getInstallments() != null && props.summaryModel.getInstallments() > 1 && isValidAmount(props.summaryModel.getPayerCostTotalAmount())) {
            BigDecimal totalInterestsAmount = getPayerCostChargesAmount();
            interestAmount = interestAmount.add(totalInterestsAmount);
        }

        return interestAmount;
    }

    private BigDecimal getPayerCostChargesAmount() {
        BigDecimal totalInterestsAmount;

        if (isValidAmount(props.summaryModel.getCouponAmount())) {
            BigDecimal totalAmount = props.summaryModel.getTotalAmount().subtract(props.summaryModel.getCouponAmount());
            totalInterestsAmount = props.summaryModel.getPayerCostTotalAmount().subtract(totalAmount);
        } else {
            totalInterestsAmount = props.summaryModel.getPayerCostTotalAmount().subtract(props.summaryModel.getTotalAmount());
        }

        return totalInterestsAmount;
    }

    private BigDecimal getDiscountAmount() {
        ReviewAndConfirmPreferences reviewScreenPreference = props.reviewAndConfirmPreferences;
        BigDecimal discountAmount = reviewScreenPreference.discountAmount;

        if (isValidAmount(props.summaryModel.getCouponAmount())) {
            discountAmount = discountAmount.add(props.summaryModel.getCouponAmount());
        }

        return discountAmount;
    }

    private boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isValidTotalAmount() {
        ReviewAndConfirmPreferences reviewScreenPreference = props.reviewAndConfirmPreferences;
        BigDecimal totalAmountPreference = reviewScreenPreference.totalAmount;
        return totalAmountPreference.compareTo(props.summaryModel.getTotalAmount()) == 0;
    }

    private boolean isEmptySummaryDetails() {
        return getSummary() != null && getSummary().getSummaryDetails() != null && getSummary().getSummaryDetails().size() < 2;
    }

    private BigDecimal getSubtotal() {
        BigDecimal ans = props.summaryModel.getTotalAmount();
        if (hasDiscount()) {
            ans = props.summaryModel.getTotalAmount().subtract(props.summaryModel.getCouponAmount());
        }
        return ans;
    }

    private boolean hasDiscount() {
        return props.summaryModel.currencyId != null && props.summaryModel.getCouponAmount() != null;
    }

    private boolean isCardPaymentMethod() {
        return props.summaryModel.paymentTypeId != null && isCard(props.summaryModel.paymentTypeId);
    }

    private boolean isCard(String paymentTypeId) {
        boolean isCard = false;

        if ((paymentTypeId != null) && (paymentTypeId.equals("credit_card") ||
                paymentTypeId.equals("debit_card") || paymentTypeId.equals("prepaid_card"))) {
            isCard = true;
        }

        return isCard;
    }
}
