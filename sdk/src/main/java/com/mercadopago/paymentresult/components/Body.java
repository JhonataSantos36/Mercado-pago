package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.components.CustomComponent;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.model.Payment;
import com.mercadopago.paymentresult.PaymentMethodProvider;
import com.mercadopago.paymentresult.PaymentResultProvider;
import com.mercadopago.paymentresult.props.BodyErrorProps;
import com.mercadopago.paymentresult.props.InstructionsProps;
import com.mercadopago.paymentresult.props.PaymentMethodProps;
import com.mercadopago.paymentresult.props.PaymentResultBodyProps;
import com.mercadopago.paymentresult.props.ReceiptProps;

public class Body extends Component<PaymentResultBodyProps, Void> {

    private final PaymentMethodProvider paymentMethodProvider;
    public PaymentResultProvider paymentResultProvider;

    public Body(@NonNull final PaymentResultBodyProps props,
                @NonNull final ActionDispatcher dispatcher,
                @NonNull final PaymentResultProvider paymentResultProvider,
                @NonNull final PaymentMethodProvider paymentMethodProvider) {
        super(props, dispatcher);
        this.paymentResultProvider = paymentResultProvider;
        this.paymentMethodProvider = paymentMethodProvider;
    }

    public boolean hasInstructions() {
        return props.instruction != null;
    }

    public Instructions getInstructionsComponent() {
        final InstructionsProps instructionsProps = new InstructionsProps.Builder()
                .setInstruction(props.instruction)
                .setProcessingMode(props.processingMode)
                .build();
        return new Instructions(instructionsProps, getDispatcher());
    }

    public boolean hasPaymentMethodDescription() {
        return props.paymentData != null && isStatusApproved() &&
                isPaymentTypeOn(props.paymentData.getPaymentMethod());
    }

    private boolean isPaymentTypeOn(final com.mercadopago.model.PaymentMethod paymentMethod) {
        return isCardType(paymentMethod)
                || isAccountMoney(paymentMethod)
                || isPluginType(paymentMethod);
    }

    private boolean isCardType(final com.mercadopago.model.PaymentMethod paymentMethod) {
        return paymentMethod != null && paymentMethod.getPaymentTypeId() != null &&
                paymentMethod.getPaymentTypeId().equals(PaymentTypes.CREDIT_CARD) ||
                paymentMethod.getPaymentTypeId().equals(PaymentTypes.DEBIT_CARD) ||
                paymentMethod.getPaymentTypeId().equals(PaymentTypes.PREPAID_CARD);
    }

    private boolean isAccountMoney(final com.mercadopago.model.PaymentMethod paymentMethod) {
        return paymentMethod != null && paymentMethod.getPaymentTypeId() != null
                && paymentMethod.getPaymentTypeId().equals(PaymentTypes.ACCOUNT_MONEY);
    }

    private boolean isPluginType(final com.mercadopago.model.PaymentMethod paymentMethod) {
        return PaymentTypes.PLUGIN.equalsIgnoreCase(paymentMethod.getPaymentTypeId());
    }

    public PaymentMethod getPaymentMethodComponent() {
        final PaymentMethodProps paymentMethodProps = new PaymentMethodProps.Builder()
                .setPaymentMethod(props.paymentData.getPaymentMethod())
                .setPayerCost(props.paymentData.getPayerCost())
                .setToken(props.paymentData.getToken())
                .setIssuer(props.paymentData.getIssuer())
                .setDisclaimer(props.disclaimer)
                .setAmountFormatter(props.bodyAmountFormatter)
                .setDiscount(props.paymentData.getDiscount())
                .build();
        return new PaymentMethod(paymentMethodProps, getDispatcher(), paymentMethodProvider);
    }

    public boolean hasBodyError() {
        return props.status != null && props.statusDetail != null &&
                (isPendingWithBody() || isRejectedWithBody());
    }

    private boolean isPendingWithBody() {
        return (props.status.equals(Payment.StatusCodes.STATUS_PENDING) || props.status.equals(Payment.StatusCodes.STATUS_IN_PROCESS)) &&
                (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_REVIEW_MANUAL));
    }

    private boolean isRejectedWithBody() {
        return (props.status.equals(Payment.StatusCodes.STATUS_REJECTED) &&
                (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_BY_BANK) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED) ||
                        props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT)));
    }

    private boolean isStatusApproved() {
        return Payment.StatusCodes.STATUS_APPROVED.equals(props.status);
    }

    public BodyError getBodyErrorComponent() {
        final BodyErrorProps bodyErrorProps = new BodyErrorProps.Builder()
                .setStatus(props.status)
                .setStatusDetail(props.statusDetail)
                .setPaymentMethodName(props.paymentData.getPaymentMethod().getName())
                .build();
        return new BodyError(bodyErrorProps, getDispatcher(), paymentResultProvider);
    }

    public boolean hasReceipt() {
        final com.mercadopago.model.PaymentMethod paymentMethod = props.paymentData.getPaymentMethod();
        return props.paymentId != null && props.isReceiptEnabled() && props.paymentData != null
                && isStatusApproved() && isPaymentTypeOn(paymentMethod);
    }

    public Receipt getReceiptComponent() {
        final ReceiptProps receiptProps = new ReceiptProps.Builder()
                .setReceiptId(props.paymentId)
                .build();
        return new Receipt(receiptProps, getDispatcher(), paymentResultProvider);
    }

    public boolean hasTopCustomComponent() {
        return hasPaymentResultScreenPreference() && CheckoutStore.getInstance()
                .getPaymentResultScreenPreference().hasApprovedTopCustomComponent();
    }

    public boolean hasBottomCustomComponent() {
        return hasPaymentResultScreenPreference() && CheckoutStore.getInstance()
                .getPaymentResultScreenPreference().hasApprovedBottomCustomComponent();
    }

    private boolean hasPaymentResultScreenPreference() {
        return CheckoutStore.getInstance().getPaymentResultScreenPreference() != null;
    }

    public CustomComponent getApprovedTopCustomComponent() {
        final CheckoutStore store = CheckoutStore.getInstance();
        final CustomComponent.Props props =
                new CustomComponent.Props(
                        store.getData(),
                        store.getCheckoutPreference()
                );
        return store.getPaymentResultScreenPreference()
                .getApprovedTopCustomComponentFactory()
                .create(props);
    }

    public CustomComponent getApprovedBottomCustomComponent() {
        final CheckoutStore store = CheckoutStore.getInstance();
        final CustomComponent.Props props =
                new CustomComponent.Props(
                        store.getData(),
                        store.getCheckoutPreference()
                );
        return store.getPaymentResultScreenPreference()
                .getApprovedBottomCustomComponentFactory()
                .create(props);
    }
}
