package com.mercadopago.review_and_confirm.components.payment_method;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.review_and_confirm.models.PaymentModel;

public class PaymentMethodComponent extends CompactComponent<PaymentModel, PaymentMethodComponent.Actions> {

    public interface Actions {
        void onPaymentMethodChangeClicked();
    }

    public PaymentMethodComponent(PaymentModel props, PaymentMethodComponent.Actions actions) {
        super(props, actions);
    }

    @VisibleForTesting()
    CompactComponent resolveComponent() {
        if (PaymentTypes.isCardPaymentMethod(props.getPaymentType())) {
            return new MethodCard(MethodCard.Props.createFrom(props), getActions());
        } else if (PaymentTypes.isPlugin(props.getPaymentType())) {
            return new MethodPlugin(MethodPlugin.Props.createFrom(props), getActions());
        } else {
            return new MethodOff(MethodOff.Props.createFrom(props), getActions());
        }
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        return resolveComponent().render(parent);
    }
}
