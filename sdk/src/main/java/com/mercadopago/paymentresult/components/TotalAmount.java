package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.model.Currency;
import com.mercadopago.paymentresult.props.TotalAmountProps;
import com.mercadopago.util.CurrenciesUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

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

        if (hasPayerCost() && props.payerCost.getInstallments() > 1) {
            amountTitle = props.payerCost.getInstallments() + "x " + props.amountFormatter.formatNumber(props.payerCost.getInstallmentAmount(), props.amountFormatter.getCurrencyId());
        } else {
            amountTitle = props.amountFormatter.formatNumber(getAmount(), props.amountFormatter.getCurrencyId());
        }

        return amountTitle;
    }

    public String getAmountDetail() {
        String amountDetail = "";

        if (hasPayerCost() && props.payerCost.getInstallments() > 1) {
            amountDetail = "(" + props.amountFormatter.formatNumber(props.payerCost.getTotalAmount(), props.amountFormatter.getCurrencyId()) + ")";
        }

        return amountDetail;
    }

    private boolean hasPayerCost() {
        return props.payerCost != null;
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
