package com.mercadopago.components;

import android.support.annotation.NonNull;

import com.mercadopago.model.PaymentMethod;

public class PaymentMethodComponent extends Component<PaymentMethodComponent.PaymentMethodProps, Void> {

    static {
        RendererFactory.register(PaymentMethodComponent.class, PaymentMethodRenderer.class);
    }

    public static class PaymentMethodProps {

        final PaymentMethod paymentMethod;
        final TotalAmount.TotalAmountProps totalAmountProps;
        final String lastFourDigits;
        final String disclaimer;

        public PaymentMethodProps(final com.mercadopago.model.PaymentMethod paymentMethod,
                                  final String lastFourDigits,
                                  final String disclaimer,
                                  final TotalAmount.TotalAmountProps totalAmountProps) {
            this.paymentMethod = paymentMethod;
            this.lastFourDigits = lastFourDigits;
            this.disclaimer = disclaimer;
            this.totalAmountProps = totalAmountProps;
        }
    }

    public PaymentMethodComponent(@NonNull final PaymentMethodProps props) {
        super(props);
    }
}
