package com.mercadopago.paymentresult.components;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.paymentresult.PaymentMethodProvider;
import com.mercadopago.paymentresult.props.PaymentMethodProps;
import com.mercadopago.paymentresult.props.TotalAmountProps;

/**
 * Created by mromar on 11/22/17.
 */

public class PaymentMethod extends Component<PaymentMethodProps, Void> {

    private PaymentMethodProvider provider;

    public PaymentMethod(@NonNull final PaymentMethodProps props,
                         @NonNull final ActionDispatcher dispatcher,
                         @NonNull final PaymentMethodProvider provider) {
        super(props, dispatcher);

        this.provider = provider;
    }

    public Drawable getImage() {

        return provider.getImage(props.paymentMethod);
    }

    public String getDescription() {
        String description = "";

        if (isCardPaymentMethod() && isTokenValid()) {
            description = props.paymentMethod.getName() + " " + provider.getLastDigitsText() + " " + props.token.getLastFourDigits();
        } else if (isAccountMoneyPaymentMethod()) {
            description = provider.getAccountMoneyText();
        } else if (props.paymentMethod != null) {
            description = props.paymentMethod.getName();
        }

        return description;
    }

    public String getDetail() {
        String detail = "";

        if (props.issuer != null) {
            detail = props.issuer.getName();
        }

        return detail;
    }

    public String getDisclaimer() {
        String disclaimer = "";

        if (isCardPaymentMethod()) {
            disclaimer = provider.getDisclaimer(props.disclaimer);
        }

        return disclaimer;
    }

    private boolean isCardPaymentMethod() {
        return props.paymentMethod != null && props.paymentMethod.getPaymentTypeId() != null &&
                (props.paymentMethod.getPaymentTypeId().equals(PaymentTypes.CREDIT_CARD) ||
                        props.paymentMethod.getPaymentTypeId().equals(PaymentTypes.DEBIT_CARD) ||
                        props.paymentMethod.getPaymentTypeId().equals(PaymentTypes.PREPAID_CARD));
    }

    private boolean isTokenValid() {
        return props.token != null && props.token.getLastFourDigits() != null && !props.token.getLastFourDigits().isEmpty();
    }

    private boolean isAccountMoneyPaymentMethod() {
        return props.paymentMethod != null && props.paymentMethod.getPaymentTypeId() != null &&
                props.paymentMethod.getPaymentTypeId().equals(PaymentTypes.ACCOUNT_MONEY);
    }

    public TotalAmount getTotalAmountComponent() {
        final TotalAmountProps totalAmountProps = new TotalAmountProps(
                props.amountFormatter,
                props.payerCost,
                props.discount);

        return new TotalAmount(totalAmountProps, getDispatcher());
    }
}
