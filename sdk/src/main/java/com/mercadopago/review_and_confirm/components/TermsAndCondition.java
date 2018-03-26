package com.mercadopago.review_and_confirm.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.review_and_confirm.models.TermsAndConditionsModel;

class TermsAndCondition extends Component<TermsAndConditionsModel, Void> {

    static {
        RendererFactory.register(TermsAndCondition.class, TermsAndConditionRenderer.class);
    }

    public TermsAndCondition(@NonNull final TermsAndConditionsModel props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
