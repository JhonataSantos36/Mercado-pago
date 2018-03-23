package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.paymentresult.props.TotalAmountProps;
import com.mercadopago.util.ResourceUtil;

import java.util.Locale;

/**
 * Created by mromar on 11/22/17.
 */

public class PaymentMethodRenderer extends Renderer<PaymentMethod> {
    @Override
    public View render(@NonNull final PaymentMethod component, @NonNull final Context context, final ViewGroup parent) {
        final View paymentMethodView = inflate(R.layout.mpsdk_payment_method_component, parent);
        final ViewGroup paymentMethodViewGroup = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodContainer);
        final ImageView imageView = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodIcon);
        final MPTextView descriptionTextView = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodDescription);
        final MPTextView statementDescriptionTextView = paymentMethodView.findViewById(R.id.mpsdkStatementDescription);
        final FrameLayout totalAmountContainer = paymentMethodView.findViewById(R.id.mpsdkTotalAmountContainer);

        imageView.setImageDrawable(ContextCompat.getDrawable(context, getIconResource(component.props.paymentMethod, context)));

        RendererFactory.create(context, getTotalAmountComponent(component)).render(totalAmountContainer);

        setText(descriptionTextView, getDescription(component, context));
        setText(statementDescriptionTextView, getDisclaimer(component, context));

        stretchHeight(paymentMethodViewGroup);
        return paymentMethodView;
    }

    private int getIconResource(com.mercadopago.model.PaymentMethod paymentMethod, Context context) {
        return ResourceUtil.getIconResource(context, paymentMethod.getId());
    }

    private String getDescription(PaymentMethod component, Context context) {
        if (component.props.paymentMethod != null) {
            if (isValidCreditCard(component)) {
                return formatCreditCardTitle(component, context);
            } else if (PaymentTypes.isAccountMoney(component.props.paymentMethod.getPaymentTypeId())) {
                return getAccountMoneyText(context);
            } else {
                return component.props.paymentMethod.getName();
            }
        } else {
            return "";
        }
    }

    private boolean isValidCreditCard(PaymentMethod component) {
        return PaymentTypes.isCardPaymentMethod(component.props.paymentMethod.getPaymentTypeId())
                && component.props.token != null
                && component.props.token.isTokenValid();
    }


    private String getAccountMoneyText(Context context) {
        return context.getString(R.string.mpsdk_account_money);
    }

    private String formatCreditCardTitle(PaymentMethod component, Context context) {
        return String.format(Locale.getDefault(), "%s %s %s",
                component.props.paymentMethod.getName(),
                getLastDigitsText(context),
                component.props.token.getLastFourDigits());
    }

    private String getLastDigitsText(Context context) {
        return context.getString(R.string.mpsdk_ending_in);
    }

    private TotalAmount getTotalAmountComponent(PaymentMethod component) {
        final TotalAmountProps totalAmountProps = new TotalAmountProps(
                component.props.amountFormatter,
                component.props.payerCost,
                component.props.discount);

        return new TotalAmount(totalAmountProps, component.getDispatcher());
    }

    private String getDisclaimer(PaymentMethod component, Context context) {
        String disclaimer = "";

        if (isValidCreditCard(component)) {
            if (component.props.disclaimer != null && !component.props.disclaimer.isEmpty()) {
                disclaimer = String.format(context.getString(R.string.mpsdk_text_state_account_activity_congrats), component.props.disclaimer);
            }
        }

        return disclaimer;
    }
}
