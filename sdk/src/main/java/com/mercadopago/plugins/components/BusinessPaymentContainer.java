package com.mercadopago.plugins.components;


import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.components.PaymentMethodComponent;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.plugins.model.BusinessPayment;

public class BusinessPaymentContainer extends Component<BusinessPaymentContainer.Props, Void> {

    static {
        RendererFactory.register(BusinessPaymentContainer.class, BusinessPaymentRenderer.class);
    }

    public static class Props {

        final BusinessPayment payment;
        final PaymentMethodComponent.PaymentMethodProps paymentMethod;

        public Props(@NonNull final BusinessPayment payment,
                     @NonNull final PaymentMethodComponent.PaymentMethodProps paymentMethod) {
            this.payment = payment;
            this.paymentMethod = paymentMethod;
        }
    }

    public BusinessPaymentContainer(@NonNull final BusinessPaymentContainer.Props props,
                                    @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
