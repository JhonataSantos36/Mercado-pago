package com.mercadopago.review_and_confirm.components.payment_method;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.components.CompactComponent;
import com.mercadopago.review_and_confirm.models.PaymentModel;
import com.mercadopago.util.ResourceUtil;

class MethodPlugin extends CompactComponent<MethodPlugin.Props, Void> {

    static class Props {

        private final String id;
        private final String paymentMethodName;

        private Props(String id, String paymentMethodName) {
            this.id = id;
            this.paymentMethodName = paymentMethodName;
        }

        static Props createFrom(PaymentModel props) {
            return new Props(props.paymentMethodId,
                    props.paymentMethodName);
        }

    }

    MethodPlugin(final Props props) {
        super(props);
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {

        View paymentView = inflate(parent, R.layout.mpsdk_payment_method_plugin);

        TextView title = paymentView.findViewById(R.id.title);
        title.setText(props.paymentMethodName);

        ImageView imageView = paymentView.findViewById(R.id.icon);
        imageView.setImageResource(ResourceUtil.getIconResource(imageView.getContext(), props.id));

        return paymentView;
    }
}
