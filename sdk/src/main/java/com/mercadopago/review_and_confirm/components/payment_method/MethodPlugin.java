package com.mercadopago.review_and_confirm.components.payment_method;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.review_and_confirm.models.PaymentModel;
import com.mercadopago.util.ResourceUtil;

class MethodPlugin extends CompactComponent<MethodPlugin.Props, PaymentMethodComponent.Actions> {

    static class Props {

        private final String id;
        private final String paymentMethodName;
        private final boolean hasChangePaymentMethod;

        private Props(String id, String paymentMethodName, boolean hasChangePaymentMethod) {
            this.id = id;
            this.paymentMethodName = paymentMethodName;
            this.hasChangePaymentMethod = hasChangePaymentMethod;
        }

        static Props createFrom(PaymentModel props) {
            return new Props(props.paymentMethodId,
                    props.paymentMethodName,
                    props.moreThanOnePaymentMethod);
        }

    }

    MethodPlugin(final Props props, final PaymentMethodComponent.Actions actions) {
        super(props, actions);
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {

        View paymentView = inflate(parent, R.layout.mpsdk_payment_method_plugin);

        TextView title = paymentView.findViewById(R.id.title);
        title.setText(props.paymentMethodName);

        ImageView imageView = paymentView.findViewById(R.id.icon);
        imageView.setImageResource(ResourceUtil.getIconResource(imageView.getContext(), props.id));

        ViewStub stub = paymentView.findViewById(R.id.button);

        if (props.hasChangePaymentMethod) {
            TextView button = (TextView) stub.inflate();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (getActions() != null)
                        getActions().onPaymentMethodChangeClicked();
                }
            });
        }

        return paymentView;
    }
}
