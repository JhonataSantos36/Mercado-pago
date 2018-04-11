package com.mercadopago.paymentresult.props;

import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;

public class PaymentMethodProps {

    public final PaymentMethod paymentMethod;
    public final Issuer issuer;
    public final Token token;
    public final String disclaimer;
    public final TotalAmountProps totalAmountProps;

    public PaymentMethodProps(final PaymentMethod paymentMethod,
                              final Token token, final Issuer issuer, final String disclaimer,
                              final TotalAmountProps totalAmountProps) {
        this.paymentMethod = paymentMethod;
        this.token = token;
        this.issuer = issuer;
        this.disclaimer = disclaimer;
        this.totalAmountProps = totalAmountProps;
    }
}
