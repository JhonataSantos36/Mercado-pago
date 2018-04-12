package com.mercadopago.paymentresult.props;

import com.mercadopago.model.Discount;
import com.mercadopago.model.PayerCost;

import java.math.BigDecimal;

public class TotalAmountProps {

    public final PayerCost payerCost;
    public final Discount discount;
    public final String currencyId;
    public final BigDecimal amount;

    public TotalAmountProps(final String currencyId,
                            final BigDecimal amount,
                            final PayerCost payerCost,
                            final Discount discount) {
        this.payerCost = payerCost;
        this.discount = discount;
        this.currencyId = currencyId;
        this.amount = amount;
    }
}
