package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.paymentresult.props.TotalAmountProps;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Created by mromar on 11/28/17.
 */

public class TotalAmount extends Component<TotalAmountProps, Void> {

    public TotalAmount(@NonNull final TotalAmountProps props,
                       @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public String getAmountTitle() {
        String amountTitle;

        if (hasPayerCostWithMultipleInstallments()) {
            amountTitle = String.format(Locale.getDefault(),
                    "%dx %s",
                    props.payerCost.getInstallments(),
                    props.amountFormatter.formatNumber(props.payerCost.getInstallmentAmount()));
        } else {
            amountTitle = props.amountFormatter.formatNumber(getAmount());
        }

        return amountTitle;
    }

    public String getAmountDetail() {
        String amountDetail = "";

        if (hasPayerCostWithMultipleInstallments()) {
            amountDetail = String.format(Locale.getDefault(),
                    "(%s)",
                    props.amountFormatter.formatNumber(props.payerCost.getTotalAmount()));
        }

        return amountDetail;
    }

    private boolean hasPayerCostWithMultipleInstallments() {
        return props.payerCost != null && props.payerCost.hasMultipleInstallments();
    }

    private BigDecimal getAmount() {
        BigDecimal amount;

        if (isValidDiscount()) {
            amount = getAmountWithDiscount();
        } else {
            amount = props.amountFormatter.getAmount();
        }

        return amount;
    }

    private boolean isValidDiscount() {
        boolean isValidDiscount = false;

        if (props.discount != null && isValidCouponAmount()) {
            isValidDiscount = true;
        }

        return isValidDiscount;
    }

    private boolean isValidCouponAmount() {
        return props.discount.getCouponAmount() != null && props.discount.getCouponAmount().compareTo(BigDecimal.ZERO) >= 0;
    }

    private BigDecimal getAmountWithDiscount() {
        return props.amountFormatter.getAmount().subtract(props.discount.getCouponAmount());
    }
}
