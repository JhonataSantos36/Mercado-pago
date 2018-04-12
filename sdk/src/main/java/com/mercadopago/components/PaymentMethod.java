package com.mercadopago.components;

import android.support.annotation.NonNull;

public class PaymentMethod extends Component<PaymentMethod.PaymentMethodProps, Void> {

    static {
        RendererFactory.register(PaymentMethod.class, PaymentMethodRenderer.class);
    }

    public PaymentMethod(@NonNull final PaymentMethodProps props) {
        super(props);
    }

    public static class PaymentMethodProps {

        public final com.mercadopago.model.PaymentMethod paymentMethod;
        public final String lastFourDigits;
        public final String disclaimer;
        public final TotalAmount.TotalAmountProps totalAmountProps;

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
}
