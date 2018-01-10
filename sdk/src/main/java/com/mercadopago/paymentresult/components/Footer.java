package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.components.NextAction;

public class Footer extends Component<Footer.Props, Void> {

    public Footer(@NonNull final Props props,
                  @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public static class Props {

        public final FooterAction buttonAction;
        public final FooterAction linkAction;

        public Props(@NonNull final FooterAction buttonAction,
                     @NonNull final FooterAction linkAction) {
            this.buttonAction = buttonAction;
            this.linkAction = linkAction;
        }
    }

    public static class FooterAction {
        public final String label;
        public final Action action;

        public FooterAction(final String label) {
            this(label, new NextAction());
        }

        public FooterAction(final String label, final Action action) {
            this.label = label;
            this.action = action;
        }
    }
}