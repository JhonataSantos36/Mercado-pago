package com.mercadopago.review_and_confirm.components.payment_method;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.review_and_confirm.models.PaymentModel;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.ResourceUtil;

class MethodOff extends CompactComponent<MethodOff.Props, PaymentMethodComponent.Actions> {

    static class Props {

        final String id;
        final String title;
        final Integer time;
        final boolean hasChangePaymentMethod;

        private Props(String id, String title, Integer time, boolean hasChangePaymentMethod) {
            this.id = id;
            this.title = title;
            this.time = time;
            this.hasChangePaymentMethod = hasChangePaymentMethod;
        }

        static Props createFrom(final PaymentModel props) {
            return new Props(props.paymentMethodId,
                    props.getPaymentMethodName(),
                    props.accreditationTime,
                    props.moreThanOnePaymentMethod);
        }
    }

    MethodOff(final Props props, final PaymentMethodComponent.Actions actions) {
        super(props, actions);
    }

    @Override
    public View render(final ViewGroup parent) {
        View paymentView = inflate(parent, R.layout.mpsdk_payment_method_off);

        TextView time = paymentView.findViewById(R.id.time);
        time.setText(MercadoPagoUtil.getAccreditationTimeMessage(time.getContext(), props.time));

        TextView title = paymentView.findViewById(R.id.title);
        title.setText(props.title);

        ImageView imageView = paymentView.findViewById(R.id.icon);
        imageView.setImageResource(ResourceUtil.getIconResource(imageView.getContext(), props.id));

        ViewStub stub = paymentView.findViewById(R.id.button);

        if (props.hasChangePaymentMethod) {
            TextView button = (TextView) stub.inflate();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    getActions().onPaymentMethodChangeClicked();
                }
            });
        }

        return paymentView;
    }
}
