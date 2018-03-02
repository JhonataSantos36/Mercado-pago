package com.mercadopago.review_and_confirm.components.payment_method;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.review_and_confirm.models.PaymentModel;

class MethodPlugin extends CompactComponent<MethodPlugin.Props, PaymentMethodComponent.Actions> {

    static class Props {

        private final String id;
        private final String paymentMethodName;
        private final String paymentType;
        private final boolean hasChangePaymentMethod;
        private final int icon;

        private Props(String id, String paymentMethodName, String paymentType, int icon, boolean hasChangePaymentMethod) {
            this.id = id;
            this.paymentMethodName = paymentMethodName;
            this.paymentType = paymentType;
            this.icon = icon;
            this.hasChangePaymentMethod = hasChangePaymentMethod;
        }

        static Props createFrom(PaymentModel props) {
            return new Props(props.paymentMethodId,
                    props.getPaymentMethodName(),
                    props.getPaymentType(),
                    props.getIcon(),
                    props.moreThanOnePaymentMethod);
        }

    }

    MethodPlugin(final Props props, final PaymentMethodComponent.Actions actions) {
        super(props, actions);
    }

    @VisibleForTesting()
    int resolveIcon() {
        if (PaymentTypes.isAccountMoney(props.paymentType)) {
            return R.drawable.mpsdk_account_money;
        } else {
            return props.icon;
        }
    }

    @VisibleForTesting()
    String resolveTitleName(Context context) {
        if (PaymentTypes.isAccountMoney(props.paymentType)) {
            return context.getString(R.string.mpsdk_account_money);
        } else {
            return props.paymentMethodName;
        }
    }

    @Override
    public View render(final ViewGroup parent) {

        View paymentView = inflate(parent, R.layout.mpsdk_payment_method_plugin);

        TextView title = paymentView.findViewById(R.id.title);
        title.setText(resolveTitleName(title.getContext()));

        ImageView imageView = paymentView.findViewById(R.id.icon);
        imageView.setImageResource(resolveIcon());

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
