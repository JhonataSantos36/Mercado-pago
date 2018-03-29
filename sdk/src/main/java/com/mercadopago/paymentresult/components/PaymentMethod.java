package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.lite.model.PaymentTypes;
import com.mercadopago.paymentresult.PaymentMethodProvider;
import com.mercadopago.paymentresult.props.PaymentMethodProps;
import com.mercadopago.paymentresult.props.TotalAmountProps;

import java.util.Locale;

public class PaymentMethod extends Component<PaymentMethodProps, Void> {

    private final PaymentMethodProvider provider;

    public PaymentMethod(@NonNull final PaymentMethodProps props,
                         @NonNull final ActionDispatcher dispatcher,
                         @NonNull final PaymentMethodProvider provider) {
        super(props, dispatcher);
        this.provider = provider;
    }

    int getIconResource() {
        return provider.getIconResource(props.paymentMethod);
    }

    public String getDescription() {
        if (props.paymentMethod != null) {
            if (isValidCreditCard()) {
                return formatCreditCardTitle();
            } else if (PaymentTypes.isAccountMoney(props.paymentMethod.getPaymentTypeId())) {
                return provider.getAccountMoneyText();
            } else {
                return props.paymentMethod.getName();
            }
        } else {
            return "";
        }
    }

    private String formatCreditCardTitle() {
        return String.format(Locale.getDefault(), "%s %s %s",
                props.paymentMethod.getName(),
                provider.getLastDigitsText(),
                props.token.getLastFourDigits());
    }

    private boolean isValidCreditCard() {
        return PaymentTypes.isCardPaymentMethod(props.paymentMethod.getPaymentTypeId())
                && props.token != null
                && props.token.isTokenValid();
    }

    public String getDisclaimer() {
        return isValidCreditCard() ? provider.getDisclaimer(props.disclaimer) : "";
    }

    TotalAmount getTotalAmountComponent() {
        final TotalAmountProps totalAmountProps = new TotalAmountProps(
                props.amountFormatter,
                props.payerCost,
                props.discount);

        return new TotalAmount(totalAmountProps, getDispatcher());
    }
}
