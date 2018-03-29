package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Button;
import com.mercadopago.components.Component;
import com.mercadopago.components.Footer;
import com.mercadopago.components.NextAction;
import com.mercadopago.components.RecoverPaymentAction;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.components.ResultCodeAction;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.lite.model.Payment;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.paymentresult.PaymentResultProvider;
import com.mercadopago.preferences.PaymentResultScreenPreference;
import com.mercadopago.review_and_confirm.components.actions.ChangePaymentMethodAction;
import com.mercadopago.util.TextUtils;

public class FooterContainer extends Component<FooterContainer.Props, Void> {

    static {
        RendererFactory.register(FooterContainer.class, FooterContainerRenderer.class);
    }

    public PaymentResultProvider resourcesProvider;

    public FooterContainer(@NonNull final Props props,
                           @NonNull final ActionDispatcher dispatcher,
                           @NonNull final PaymentResultProvider provider) {
        super(props, dispatcher);
        resourcesProvider = provider;
    }

    @VisibleForTesting
    Footer getFooter() {
        return new Footer(getFooterProps(), getDispatcher());
    }

    @VisibleForTesting
    Footer.Props getFooterProps() {

        final PaymentResultScreenPreference preferences = CheckoutStore.getInstance()
                .getPaymentResultScreenPreference();

        Button.Props buttonAction = null;
        Button.Props linkAction = null;

        if (props.paymentResult.isStatusApproved()) {

            if (!preferences.isCongratsSecondaryExitButtonEnabled() ||
                    preferences.getSecondaryCongratsExitButtonTitle() == null
                    || preferences.getSecondaryCongratsExitResultCode() == null) {
                buttonAction = null;
            } else {
                buttonAction = new Button.Props(
                        preferences.getSecondaryCongratsExitButtonTitle(),
                        new ResultCodeAction(preferences.getSecondaryCongratsExitResultCode())
                );
            }

            if (TextUtils.isEmpty(preferences.getExitButtonTitle())) {
                linkAction = new Button.Props(resourcesProvider.getContinueShopping(), new NextAction());
            } else {
                linkAction = new Button.Props(preferences.getExitButtonTitle(), new NextAction());
            }

        } else if (props.paymentResult.isStatusPending() || props.paymentResult.isStatusInProcess()) {

            if (!preferences.isPendingSecondaryExitButtonEnabled() ||
                    preferences.getSecondaryPendingExitButtonTitle() == null
                    || preferences.getSecondaryPendingExitResultCode() == null) {
                buttonAction = null;
            } else {
                buttonAction = new Button.Props(
                        preferences.getSecondaryPendingExitButtonTitle(),
                        new ResultCodeAction(preferences.getSecondaryPendingExitResultCode())
                );
            }

            if (TextUtils.isEmpty(preferences.getExitButtonTitle())) {
                linkAction = new Button.Props(resourcesProvider.getContinueShopping(), new NextAction());
            } else {
                linkAction = new Button.Props(preferences.getExitButtonTitle(), new NextAction());
            }

        } else if (props.paymentResult.isStatusRejected()) {

            if (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE
                    .equals(props.paymentResult.getPaymentStatusDetail())) {

                buttonAction = new Button.Props(
                        resourcesProvider.getChangePaymentMethodLabel(),
                        new ChangePaymentMethodAction()
                );

                linkAction = new Button.Props(resourcesProvider.getCancelPayment(), new NextAction());

            } else if (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED
                    .equals(props.paymentResult.getPaymentStatusDetail())) {

                buttonAction = new Button.Props(
                        resourcesProvider.getCardEnabled(),
                        new RecoverPaymentAction()
                );

                linkAction = new Button.Props(resourcesProvider.getChangePaymentMethodLabel(),
                        new ChangePaymentMethodAction());

            } else if (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT
                    .equals(props.paymentResult.getPaymentStatusDetail())) {

                buttonAction = new Button.Props(
                        resourcesProvider.getChangePaymentMethodLabel(),
                        new ChangePaymentMethodAction()
                );

                linkAction = new Button.Props(resourcesProvider.getCancelPayment(), new NextAction());

            } else if (Payment.StatusDetail.isBadFilled(props.paymentResult.getPaymentStatusDetail())) {
                buttonAction = new Button.Props(
                        resourcesProvider.getRejectedBadFilledCardTitle(),
                        new RecoverPaymentAction()
                );

                linkAction = new Button.Props(resourcesProvider.getChangePaymentMethodLabel(),
                        new ChangePaymentMethodAction());

            } else if (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT
                    .equals(props.paymentResult.getPaymentStatusDetail())) {

                buttonAction = null;

                linkAction = new Button.Props(resourcesProvider.getContinueShopping(), new NextAction());

            } else {

                buttonAction = new Button.Props(
                        resourcesProvider.getChangePaymentMethodLabel(),
                        new ChangePaymentMethodAction()
                );

                linkAction = new Button.Props(resourcesProvider.getCancelPayment(), new NextAction());
            }

            // Remove the button by user preference
            if (!preferences.isRejectedSecondaryExitButtonEnabled()) {
                buttonAction = null;
            }
        }

        return new Footer.Props(
                buttonAction, linkAction
        );
    }

    public static class Props {

        public final PaymentResult paymentResult;

        public Props(PaymentResult paymentResult) {
            this.paymentResult = paymentResult;
        }
    }
}