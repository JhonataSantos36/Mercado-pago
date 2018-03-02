package com.mercadopago.review_and_confirm.components.payment_method;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.review_and_confirm.models.PaymentModel;
import com.mercadopago.util.ResourceUtil;

import java.util.Locale;

class MethodCard extends CompactComponent<MethodCard.Props, PaymentMethodComponent.Actions> {

    static class Props {

        private final String id;
        private final String cardName;
        private final String lastFourDigits;
        private final String bankName;
        private final boolean hasChangePaymentMethod;

        private Props(String id,
                      String cardName,
                      String lastFourDigits,
                      String bankName,
                      boolean hasChangePaymentMethod) {
            this.id = id;
            this.cardName = cardName;
            this.lastFourDigits = lastFourDigits;
            this.bankName = bankName;
            this.hasChangePaymentMethod = hasChangePaymentMethod;
        }

        static Props createFrom(final PaymentModel props) {
            return new Props(props.paymentMethodId,
                    props.getPaymentMethodName(),
                    props.lastFourDigits,
                    props.issuerName,
                    props.moreThanOnePaymentMethod);
        }
    }


    MethodCard(final Props props, final PaymentMethodComponent.Actions actions) {
        super(props, actions);
    }


    @Override
    public View render(final ViewGroup parent) {

        View paymentView = inflate(parent, R.layout.mpsdk_payment_method_card);

        TextView title = paymentView.findViewById(R.id.title);
        title.setText(formatTitle(title.getContext()));

        TextView time = paymentView.findViewById(R.id.subtitle);
        time.setText(props.bankName);

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

    private String formatTitle(Context context) {
        String ending = context.getString(R.string.mpsdk_ending_in);
        return String.format(Locale.getDefault(), "%s %s %s",
                props.cardName,
                ending,
                props.lastFourDigits);
    }
}
