package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.lite.util.CurrenciesUtil;
import com.mercadopago.paymentresult.props.TotalAmountProps;

import java.math.BigDecimal;
import java.util.Locale;

public class TotalAmount extends Component<TotalAmountProps, Void> {

    public TotalAmount(@NonNull final TotalAmountProps props,
                       @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public String getAmountTitle() {
        String amountTitle;

        if (hasPayerCostWithMultipleInstallments()) {
            String installmentsAmount = CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(props.currencyId, props.payerCost.getInstallmentAmount());
            amountTitle = String.format(Locale.getDefault(),
                    "%dx %s",
                    props.payerCost.getInstallments(),
                    installmentsAmount);
        } else {
            amountTitle = CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(props.currencyId, getAmount());
        }

        return amountTitle;
    }

    public String getAmountDetail() {
        String amountDetail = "";

        if (hasPayerCostWithMultipleInstallments()) {
            amountDetail = String.format(Locale.getDefault(),
                    "(%s)",
                    CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(props.currencyId, props.payerCost.getTotalAmount()));
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
            amount = props.amount;
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
        return props.amount.subtract(props.discount.getCouponAmount());
    }
}
