package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.PaymentTypes;
import com.mercadopago.paymentresult.props.PaymentMethodProps;
import com.mercadopago.paymentresult.props.TotalAmountProps;
import com.mercadopago.util.ResourceUtil;
import com.mercadopago.util.TextUtils;

import java.util.Locale;

public class PaymentMethodRenderer extends Renderer<PaymentMethod> {


    @Override
    public View render(@NonNull final PaymentMethod component, @NonNull final Context context, final ViewGroup parent) {

        final View paymentMethodView = inflate(R.layout.mpsdk_payment_method_component, parent);
        final ViewGroup paymentMethodViewGroup = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodContainer);
        final ImageView imageView = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodIcon);
        final MPTextView descriptionTextView = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodDescription);
        final MPTextView statementDescriptionTextView = paymentMethodView.findViewById(R.id.mpsdkStatementDescription);
        final FrameLayout totalAmountContainer = paymentMethodView.findViewById(R.id.mpsdkTotalAmountContainer);

        PaymentMethodProps props = component.props;

        imageView.setImageDrawable(ContextCompat.getDrawable(context, ResourceUtil.getIconResource(context, props.paymentMethod.getId())));
        RendererFactory.create(context, getTotalAmountComponent(component.props.totalAmountProps, component.getDispatcher())).render(totalAmountContainer);
        setText(descriptionTextView, getDescription(props.paymentMethod.getName(), props.paymentMethod.getPaymentTypeId(),
                props.token.getLastFourDigits(), context));
        setText(statementDescriptionTextView, getDisclaimer(props.paymentMethod.getPaymentTypeId(),
                props.disclaimer, context));

        stretchHeight(paymentMethodViewGroup);
        return paymentMethodView;
    }

    private Component getTotalAmountComponent(final TotalAmountProps totalAmountProps,
                                              final ActionDispatcher dispatcher) {
        return new TotalAmount(totalAmountProps, dispatcher);
    }

    @VisibleForTesting
    String getDisclaimer(final String paymentMethodTypeId, final String disclaimer, final Context context) {
        if (PaymentTypes.isCardPaymentMethod(paymentMethodTypeId) && TextUtils.isNotEmpty(disclaimer)) {
            return String.format(context.getString(R.string.mpsdk_text_state_account_activity_congrats), disclaimer);
        }
        return "";
    }

    @VisibleForTesting
    String getDescription(final String paymentMethodName,
                          final String paymentMethodType,
                          final String lastFourDigits,
                          final Context context) {
        if (PaymentTypes.isCardPaymentMethod(paymentMethodType)) {
            return String.format(Locale.getDefault(), "%s %s %s",
                    paymentMethodName,
                    context.getString(R.string.mpsdk_ending_in),
                    lastFourDigits);
        } else {
            return paymentMethodName;
        }
    }
}
