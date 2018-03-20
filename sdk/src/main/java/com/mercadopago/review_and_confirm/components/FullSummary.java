package com.mercadopago.review_and_confirm.components;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.model.Summary;
import com.mercadopago.model.SummaryDetail;
import com.mercadopago.review_and_confirm.SummaryProvider;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.review_and_confirm.props.AmountDescriptionProps;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.util.TextUtils.isEmpty;
import static com.mercadopago.util.TextUtils.isNotEmpty;

public class FullSummary extends Component<SummaryComponent.SummaryProps, Void> {

    private SummaryProvider provider;

    static {
        RendererFactory.register(FullSummary.class, FullSummaryRenderer.class);
    }

    FullSummary(@NonNull final SummaryComponent.SummaryProps props,
                @NonNull final SummaryProvider provider) {
        super(props);
        this.provider = provider;
    }

    @VisibleForTesting
    BigDecimal getTotalAmount() {
        BigDecimal totalAmount;

        if (isCardPaymentMethod()) {
            if (props.summaryModel.getInstallments() == 1) {
                if (props.summaryModel.getCouponAmount() != null && !isEmptySummaryDetails()) {
                    totalAmount = props.summaryModel.getPayerCostTotalAmount();
                } else {
                    totalAmount = props.summaryModel.getTotalAmount();
                }
            } else {
                totalAmount = props.summaryModel.getPayerCostTotalAmount();
            }
        } else if (hasDiscount() && !isEmptySummaryDetails()) {
            totalAmount = getSubtotal();
        } else {
            totalAmount = props.summaryModel.getTotalAmount();
        }
        return totalAmount;
    }

    @VisibleForTesting
    boolean hasToRenderPayerCost() {
        return isCardPaymentMethod() && props.summaryModel.getInstallments() > 1;
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

    @VisibleForTesting
    Summary getSummary() {
        ReviewAndConfirmPreferences reviewAndConfirmPreferences = props.reviewAndConfirmPreferences;
        Summary.Builder summaryBuilder = new com.mercadopago.model.Summary.Builder();

        if (isValidTotalAmount() && reviewAndConfirmPreferences.hasProductAmount()) {
            summaryBuilder.addSummaryProductDetail(reviewAndConfirmPreferences.getProductAmount(), getItemTitle(),
                    provider.getDefaultTextColor())
                    .addSummaryShippingDetail(reviewAndConfirmPreferences.getShippingAmount(),
                            provider.getSummaryShippingTitle(), provider.getDefaultTextColor())
                    .addSummaryArrearsDetail(reviewAndConfirmPreferences.getArrearsAmount(), provider.getSummaryArrearTitle(),
                            provider.getDefaultTextColor())
                    .addSummaryTaxesDetail(reviewAndConfirmPreferences.getTaxesAmount(), provider.getSummaryTaxesTitle(),
                            provider.getDefaultTextColor())
                    .addSummaryDiscountDetail(getDiscountAmount(), provider.getSummaryDiscountsTitle(),
                            provider.getDiscountTextColor())
                    .setDisclaimerText(reviewAndConfirmPreferences.getDisclaimerText())
                    .setDisclaimerColor(provider.getDisclaimerTextColor());

            if (getChargesAmount().compareTo(BigDecimal.ZERO) > 0) {
                summaryBuilder.addSummaryChargeDetail(getChargesAmount(), provider.getSummaryChargesTitle(),
                        provider.getDefaultTextColor());
            }
        } else {
            summaryBuilder.addSummaryProductDetail(props.summaryModel.getTotalAmount(), getItemTitle(),
                    provider.getDefaultTextColor());

            if (isValidAmount(props.summaryModel.getPayerCostTotalAmount()) &&
                    getPayerCostChargesAmount().compareTo(BigDecimal.ZERO) > 0) {
                summaryBuilder.addSummaryChargeDetail(getPayerCostChargesAmount(), provider.getSummaryChargesTitle(),
                        provider.getDefaultTextColor());
            }

            if (!isEmpty(reviewAndConfirmPreferences.getDisclaimerText())) {
                summaryBuilder.setDisclaimerText(reviewAndConfirmPreferences.getDisclaimerText())
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

    private String getItemTitle() {
        String title = props.summaryModel.title;

        if (isNotEmpty(props.reviewAndConfirmPreferences.getProductTitle())) {
            title = props.reviewAndConfirmPreferences.getProductTitle();
        }

        return title;
    }

    @VisibleForTesting
    BigDecimal getChargesAmount() {
        ReviewAndConfirmPreferences reviewScreenPreference = props.reviewAndConfirmPreferences;
        BigDecimal interestAmount = new BigDecimal(0);

        if (reviewScreenPreference.getChargeAmount() != null) {
            interestAmount = reviewScreenPreference.getChargeAmount();
        }

        if (props.summaryModel.getInstallments() != null && props.summaryModel.getInstallments() > 1 &&
                isValidAmount(props.summaryModel.getPayerCostTotalAmount())) {
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
            totalInterestsAmount =
                    props.summaryModel.getPayerCostTotalAmount().subtract(props.summaryModel.getTotalAmount());
        }

        return totalInterestsAmount;
    }

    private BigDecimal getDiscountAmount() {
        ReviewAndConfirmPreferences reviewScreenPreference = props.reviewAndConfirmPreferences;
        BigDecimal discountAmount = reviewScreenPreference.getDiscountAmount();

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
        BigDecimal totalAmountPreference = reviewScreenPreference.getTotalAmount();
        return totalAmountPreference.compareTo(props.summaryModel.getTotalAmount()) == 0;
    }

    private boolean isEmptySummaryDetails() {
        return getSummary() != null && getSummary().getSummaryDetails() != null &&
                getSummary().getSummaryDetails().size() < 2;
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

    public DisclaimerComponent getDisclaimerComponent(String disclaimer) {
        DisclaimerComponent.Props props = new DisclaimerComponent.Props(disclaimer);
        return new DisclaimerComponent(props);
    }
}
