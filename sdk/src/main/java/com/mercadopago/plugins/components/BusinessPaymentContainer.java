package com.mercadopago.plugins.components;


import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.plugins.model.BusinessPayment;

public class BusinessPaymentContainer extends Component<BusinessPayment, Void> {

    static {
        RendererFactory.register(BusinessPaymentContainer.class, BusinessPaymentRenderer.class);
    }

    public BusinessPaymentContainer(@NonNull final BusinessPayment props,
                                    @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
