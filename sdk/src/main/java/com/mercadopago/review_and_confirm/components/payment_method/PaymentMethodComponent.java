package com.mercadopago.review_and_confirm.components.payment_method;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Action;
import com.mercadopago.components.Button;
import com.mercadopago.components.ButtonLink;
import com.mercadopago.components.CompactComponent;
import com.mercadopago.lite.model.PaymentTypes;
import com.mercadopago.review_and_confirm.models.PaymentModel;

public class PaymentMethodComponent extends CompactComponent<PaymentModel, PaymentMethodComponent.Actions> {

    public interface Actions {
        void onPaymentMethodChangeClicked();
    }

    public PaymentMethodComponent(PaymentModel props, Actions actions) {
        super(props, actions);
    }

    @VisibleForTesting()
    CompactComponent resolveComponent() {
        if (PaymentTypes.isCardPaymentMethod(props.getPaymentType())) {
            return new MethodCard(MethodCard.Props.createFrom(props));
        } else if (PaymentTypes.isPlugin(props.getPaymentType())) {
            return new MethodPlugin(MethodPlugin.Props.createFrom(props));
        } else {
            return new MethodOff(MethodOff.Props.createFrom(props));
        }
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {

        ViewGroup paymentMethodView = (ViewGroup) resolveComponent().render(parent);

        if (props.moreThanOnePaymentMethod) {
            String changeLabel = parent.getContext().getString(R.string.mpsdk_change_payment);
            ButtonLink buttonLink = new ButtonLink(new Button.Props(changeLabel, null), new Button.Actions() {
                @Override
                public void onClick(final Action action) {
                    if (getActions() != null)
                        getActions().onPaymentMethodChangeClicked();
                }
            });

            compose(paymentMethodView, buttonLink.render(paymentMethodView));
        }

        return paymentMethodView;
    }
}
