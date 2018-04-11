package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.paymentresult.props.PaymentMethodProps;

public class PaymentMethod extends Component<PaymentMethodProps, Void> {

    static {
        RendererFactory.register(PaymentMethod.class, PaymentMethodRenderer.class);
    }

    public PaymentMethod(@NonNull final PaymentMethodProps props,
                         @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
