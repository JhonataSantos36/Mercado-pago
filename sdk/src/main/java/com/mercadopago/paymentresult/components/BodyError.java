package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.components.RecoverPaymentAction;
import com.mercadopago.model.Payment;
import com.mercadopago.paymentresult.PaymentResultProvider;
import com.mercadopago.paymentresult.props.BodyErrorProps;

/**
 * Created by vaserber on 27/11/2017.
 */

public class BodyError extends Component<BodyErrorProps, Void> {

    public PaymentResultProvider resourcesProvider;

    public BodyError(@NonNull BodyErrorProps props, @NonNull ActionDispatcher dispatcher, @NonNull final PaymentResultProvider provider) {
        super(props, dispatcher);
        resourcesProvider = provider;
    }

    public String getTitle() {
        String title = "";
        if (isRejectedWithTitle() || isPendingWithTitle()) {
            title = resourcesProvider.getErrorTitle();
        }
        return title;
    }

    public String getDescription() {
        String description = "";
        if (props.status.equals(Payment.StatusCodes.STATUS_PENDING) || props.status.equals(Payment.StatusCodes.STATUS_IN_PROCESS)) {
            if (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY)) {
                description = resourcesProvider.getPendingContingencyBodyErrorDescription();
            } else if (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_REVIEW_MANUAL)) {
                description = resourcesProvider.getPendingReviewManualBodyErrorDescription();
            }
        } else if (props.status.equals(Payment.StatusCodes.STATUS_REJECTED)) {
            if (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE)) {
                description = resourcesProvider.getRejectedCallForAuthBodyErrorDescription();
            } else if (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED)) {
                description = resourcesProvider.getRejectedCardDisabledBodyErrorDescription(props.paymentMethodName);
            } else if (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT)) {
                description = resourcesProvider.getRejectedInsufficientAmountBodyErrorDescription();
            } else if (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON)) {
                description = resourcesProvider.getRejectedOtherReasonBodyErrorDescription();
            } else if (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_BY_BANK)) {
                description = resourcesProvider.getRejectedByBankBodyErrorDescription();
            } else if (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA)) {
                description = resourcesProvider.getRejectedInsufficientDataBodyErrorDescription();
            } else if (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT)) {
                description = resourcesProvider.getRejectedDuplicatedPaymentBodyErrorDescription();
            } else if (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS)) {
                description = resourcesProvider.getRejectedMaxAttemptsBodyErrorDescription();
            } else if (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK)) {
                description = resourcesProvider.getRejectedHighRiskBodyErrorDescription();
            }
        }

        return description;
    }

    public String getSecondDescription() {
        String secondDescription = "";
        if (props.status.equals(Payment.StatusCodes.STATUS_REJECTED) &&
                props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT)) {
            secondDescription = resourcesProvider.getRejectedInsufficientAmountBodyErrorSecondDescription();
        }
        return secondDescription;
    }

    private boolean isRejectedWithTitle() {
        return (props.status.equals(Payment.StatusCodes.STATUS_REJECTED) &&
                (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_BY_BANK) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK)));
    }

    private boolean isPendingWithTitle() {
        return ((props.status.equals(Payment.StatusCodes.STATUS_IN_PROCESS) ||
                props.status.equals(Payment.StatusCodes.STATUS_PENDING)) &&
                (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_REVIEW_MANUAL)));
    }

    public String getActionText() {
        return resourcesProvider.getRejectedCallForAuthBodyActionText(props.paymentMethodName);
    }

    private boolean isCallForAuthorize() {
        return props.status.equals(Payment.StatusCodes.STATUS_REJECTED)
                && (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE));
    }

    public boolean hasActionForCallForAuth() {
        return isCallForAuthorize() && props.paymentMethodName != null && !props.paymentMethodName.isEmpty();
    }

    public String getSecondaryTitleForCallForAuth() {
        return resourcesProvider.getRejectedCallForAuthBodySecondaryTitle();
    }

    public void recoverPayment() {
        getDispatcher().dispatch(new RecoverPaymentAction());
    }
}
