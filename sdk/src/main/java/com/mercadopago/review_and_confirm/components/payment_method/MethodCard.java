package com.mercadopago.review_and_confirm.components.payment_method;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.components.CompactComponent;
import com.mercadopago.review_and_confirm.models.PaymentModel;
import com.mercadopago.util.ResourceUtil;
import com.mercadopago.util.TextUtils;

import java.util.Locale;

class MethodCard extends CompactComponent<MethodCard.Props, Void> {

    static class Props {

        private final String id;
        private final String cardName;
        private final String lastFourDigits;
        private final String bankName;

        Props(String id,
              String cardName,
              String lastFourDigits,
              String bankName) {
            this.id = id;
            this.cardName = cardName;
            this.lastFourDigits = lastFourDigits;
            this.bankName = bankName;
        }

        static Props createFrom(final PaymentModel props) {
            return new Props(props.paymentMethodId,
                    props.paymentMethodName,
                    props.lastFourDigits,
                    props.issuerName);
        }
    }


    MethodCard(final Props props) {
        super(props);
    }


    @Override
    public View render(@NonNull final ViewGroup parent) {

        View paymentView = inflate(parent, R.layout.mpsdk_payment_method_card);

        TextView title = paymentView.findViewById(R.id.title);
        title.setText(formatTitle(title.getContext()));

        TextView subtitle = paymentView.findViewById(R.id.subtitle);
        subtitle.setText(props.bankName);

        subtitle.setVisibility(shouldShowSubtitle() ? View.VISIBLE : View.GONE);

        ImageView imageView = paymentView.findViewById(R.id.icon);
        imageView.setImageResource(ResourceUtil.getIconResource(imageView.getContext(), props.id));

        return paymentView;
    }

    @VisibleForTesting
    boolean shouldShowSubtitle() {
        return TextUtils.isNotEmpty(props.bankName) && !props.bankName.equals(props.cardName);
    }

    private String formatTitle(Context context) {
        String ending = context.getString(R.string.mpsdk_ending_in);
        return String.format(Locale.getDefault(), "%s %s %s",
                props.cardName,
                ending,
                props.lastFourDigits);
    }
}
