package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.R;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.components.LoadingComponent;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.constants.PaymentMethods;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.PaymentTypes;
import com.mercadopago.paymentresult.PaymentResultProvider;
import com.mercadopago.paymentresult.formatter.HeaderTitleFormatter;
import com.mercadopago.paymentresult.model.Badge;
import com.mercadopago.paymentresult.props.HeaderProps;
import com.mercadopago.paymentresult.props.PaymentResultBodyProps;
import com.mercadopago.paymentresult.props.PaymentResultProps;

public class PaymentResultContainer extends Component<PaymentResultProps, Void> {

    static {
        RendererFactory.register(PaymentResultContainer.class, PaymentResultRenderer.class);
    }

    public static final int DEFAULT_BACKGROUND_COLOR = R.color.mpsdk_blue_MP;
    public static final int GREEN_BACKGROUND_COLOR = R.color.mpsdk_green_payment_result_background;
    public static final int RED_BACKGROUND_COLOR = R.color.mpsdk_red_payment_result_background;
    public static final int ORANGE_BACKGROUND_COLOR = R.color.mpsdk_orange_payment_result_background;

    private static final int DEFAULT_STATUS_BAR_COLOR = R.color.mpsdk_blue_status_MP;
    private static final int GREEN_STATUS_BAR_COLOR = R.color.mpsdk_green_status_MP;
    private static final int RED_STATUS_BAR_COLOR = R.color.mpsdk_red_status_MP;
    private static final int ORANGE_STATUS_BAR_COLOR = R.color.mpsdk_orange_status_MP;

    public static final int DEFAULT_ICON_IMAGE = R.drawable.mpsdk_icon_default;
    public static final int ITEM_ICON_IMAGE = R.drawable.mpsdk_icon_product;
    public static final int CARD_ICON_IMAGE = R.drawable.mpsdk_icon_card;
    public static final int BOLETO_ICON_IMAGE = R.drawable.mpsdk_icon_boleto;

    //armar componente Badge que va como hijo
    public static final int DEFAULT_BADGE_IMAGE = 0;
    public static final int CHECK_BADGE_IMAGE = R.drawable.mpsdk_badge_check;
    public static final int PENDING_BADGE_GREEN_IMAGE = R.drawable.mpsdk_badge_pending;
    public static final int PENDING_BADGE_ORANGE_IMAGE = R.drawable.mpsdk_badge_pending_orange;
    public static final int ERROR_BADGE_IMAGE = R.drawable.mpsdk_badge_error;
    public static final int WARNING_BADGE_IMAGE = R.drawable.mpsdk_badge_warning;

    public PaymentResultProvider paymentResultProvider;

    public PaymentResultContainer(@NonNull final ActionDispatcher dispatcher,
                                  @NonNull final PaymentResultProvider paymentResultProvider) {
        super(new PaymentResultProps.Builder().build(), dispatcher);
        this.paymentResultProvider = paymentResultProvider;
    }

    public boolean isLoading() {
        return props.loading;
    }

    public LoadingComponent getLoadingComponent() {
        return new LoadingComponent();
    }

    public Header getHeaderComponent() {

        final HeaderProps headerProps = new HeaderProps.Builder()
                .setHeight(getHeaderMode())
                .setBackground(getBackground(props.paymentResult))
                .setStatusBarColor(getStatusBarColor(props.paymentResult))
                .setIconImage(getIconImage(props))
                .setIconUrl(getIconUrl(props))
                .setBadgeImage(getBadgeImage(props))
                .setTitle(getTitle(props))
                .setLabel(getLabel(props))
                .build();

        return new Header(headerProps, getDispatcher());
    }

    public boolean hasBodyComponent() {
        boolean hasBody = true;
        if (props.paymentResult != null) {
            String status = props.paymentResult.getPaymentStatus();
            String statusDetail = props.paymentResult.getPaymentStatusDetail();

            if (Payment.StatusCodes.STATUS_REJECTED.equals(status)
                    && Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM.equals(statusDetail)) {
                hasBody = false;
            } else if (status != null && statusDetail != null && status.equals(Payment.StatusCodes.STATUS_REJECTED) &&
                    (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER) ||
                            statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE) ||
                            statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE) ||
                            statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER))) {
                hasBody = false;
            }
        }
        return hasBody;
    }

    public Body getBodyComponent() {
        Body body = null;
        if (props.paymentResult != null) {
            final PaymentResultBodyProps bodyProps = new PaymentResultBodyProps.Builder()
                    .setStatus(props.paymentResult.getPaymentStatus())
                    .setStatusDetail(props.paymentResult.getPaymentStatusDetail())
                    .setPaymentData(props.paymentResult.getPaymentData())
                    .setDisclaimer(props.paymentResult.getStatementDescription())
                    .setPaymentId(props.paymentResult.getPaymentId())
                    .setInstruction(props.instruction)
                    .setCurrencyId(props.currencyId)
                    .setProcessingMode(props.processingMode)
                    .build();
            body = new Body(bodyProps, getDispatcher(), paymentResultProvider);
        }
        return body;
    }

    public FooterContainer getFooterContainer() {
        return new FooterContainer(new FooterContainer.Props(
                props.paymentResult),
                getDispatcher(),
                paymentResultProvider
        );
    }

    private String getHeaderMode() {
        String headerMode;
        if (hasBodyComponent()) {
            headerMode = props.headerMode;
        } else {
            headerMode = HeaderProps.HEADER_MODE_STRETCH;
        }
        return headerMode;
    }

    private int getBackground(@NonNull final PaymentResult paymentResult) {
        if (paymentResult == null) {
            return DEFAULT_BACKGROUND_COLOR;
        } else if (isGreenBackground(paymentResult)) {
            return GREEN_BACKGROUND_COLOR;
        } else if (isRedBackground(paymentResult)) {
            return RED_BACKGROUND_COLOR;
        } else if (isOrangeBackground(paymentResult)) {
            return ORANGE_BACKGROUND_COLOR;
        } else {
            return DEFAULT_BACKGROUND_COLOR;
        }
    }

    private int getStatusBarColor(@NonNull final PaymentResult paymentResult) {
        if (paymentResult == null) {
            return DEFAULT_STATUS_BAR_COLOR;
        } else if (isGreenBackground(paymentResult)) {
            return GREEN_STATUS_BAR_COLOR;
        } else if (isRedBackground(paymentResult)) {
            return RED_STATUS_BAR_COLOR;
        } else if (isOrangeBackground(paymentResult)) {
            return ORANGE_STATUS_BAR_COLOR;
        } else {
            return DEFAULT_STATUS_BAR_COLOR;
        }
    }

    private boolean isGreenBackground(@NonNull final PaymentResult paymentResult) {
        return (paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_APPROVED) ||
                ((paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS) ||
                        paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_PENDING)) &&
                        paymentResult.getPaymentStatusDetail().equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT)));
    }

    private boolean isRedBackground(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return Payment.StatusCodes.STATUS_REJECTED.equals(status) &&
                (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON.equals(statusDetail) ||
                        Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM.equals(statusDetail) ||
                        Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_BY_BANK.equals(statusDetail) ||
                        Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA.equals(statusDetail) ||
                        Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT.equals(statusDetail) ||
                        Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS.equals(statusDetail) ||
                        Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_HIGH_RISK.equals(statusDetail) ||
                        Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK.equals(statusDetail));
    }

    private boolean isOrangeBackground(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return ((status.equals(Payment.StatusCodes.STATUS_PENDING) ||
                status.equals(Payment.StatusCodes.STATUS_IN_PROCESS)) &&
                (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY) ||
                        statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_REVIEW_MANUAL))) ||
                (status.equals(Payment.StatusCodes.STATUS_REJECTED) &&
                        (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC) ||
                                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE) ||
                                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER) ||
                                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE) ||
                                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE) ||
                                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER) ||
                                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED) ||
                                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT)));

    }

    private int getIconImage(@NonNull final PaymentResultProps props) {
        if (props.hasCustomizedImageIcon()) {
            return props.getPreferenceIcon();
        } else if (props.paymentResult == null) {
            return DEFAULT_ICON_IMAGE;
        } else if (isItemIconImage(props.paymentResult)) {
            return ITEM_ICON_IMAGE;
        } else if (isCardIconImage(props.paymentResult)) {
            return CARD_ICON_IMAGE;
        } else if (isBoletoIconImage(props.paymentResult)) {
            return BOLETO_ICON_IMAGE;
        } else {
            return DEFAULT_ICON_IMAGE;
        }
    }

    private String getIconUrl(@NonNull final PaymentResultProps props) {
        if (props.hasCustomizedUrlIcon()) {
            return props.getPreferenceUrlIcon();
        }
        return null;
    }

    private boolean isItemIconImage(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return status.equals(Payment.StatusCodes.STATUS_APPROVED) ||
                (status.equals(Payment.StatusCodes.STATUS_PENDING) &&
                        statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT));
    }

    private boolean isCardIconImage(@NonNull final PaymentResult paymentResult) {
        if (isPaymentMethodIconImage(paymentResult)) {
            final String paymentTypeId = paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId();
            return paymentTypeId.equals(PaymentTypes.PREPAID_CARD) || paymentTypeId.equals(PaymentTypes.DEBIT_CARD) ||
                    paymentTypeId.equals(PaymentTypes.CREDIT_CARD);
        }
        return false;
    }

    private boolean isBoletoIconImage(@NonNull final PaymentResult paymentResult) {
        if (isPaymentMethodIconImage(paymentResult)) {
            final String paymentMethodId = paymentResult.getPaymentData().getPaymentMethod().getId();
            return paymentMethodId.equals(PaymentMethods.BRASIL.BOLBRADESCO);
        }
        return false;
    }

    private boolean isPaymentMethodIconImage(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return ((status.equals(Payment.StatusCodes.STATUS_PENDING) && !statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT)) ||
                status.equals(Payment.StatusCodes.STATUS_IN_PROCESS) ||
                status.equals(Payment.StatusCodes.STATUS_REJECTED));
    }

    private int getBadgeImage(@NonNull final PaymentResultProps props) {
        if (props.isPluginPaymentResult(props.paymentResult)) {
            if (props.paymentResult != null && props.paymentResult.isStatusApproved()) {
                return CHECK_BADGE_IMAGE;
            } else {
                return ERROR_BADGE_IMAGE;
            }
        } else if (props.hasCustomizedBadge()) {
            final String badge = props.getPreferenceBadge();
            if (badge.equals(Badge.CHECK_BADGE_IMAGE)) {
                return CHECK_BADGE_IMAGE;
            } else if (badge.equals(Badge.PENDING_BADGE_IMAGE)) {
                return PENDING_BADGE_GREEN_IMAGE;
            } else {
                return DEFAULT_BADGE_IMAGE;
            }
        } else if (props.paymentResult == null) {
            return DEFAULT_BADGE_IMAGE;
        } else if (isCheckBagdeImage(props.paymentResult)) {
            return CHECK_BADGE_IMAGE;
        } else if (isPendingGreenBadgeImage(props.paymentResult)) {
            return PENDING_BADGE_GREEN_IMAGE;
        } else if (isPendingOrangeBadgeImage(props.paymentResult)) {
            return PENDING_BADGE_ORANGE_IMAGE;
        } else if (isWarningBadgeImage(props.paymentResult)) {
            return WARNING_BADGE_IMAGE;
        } else if (isErrorBadgeImage(props.paymentResult)) {
            return ERROR_BADGE_IMAGE;
        } else {
            return DEFAULT_BADGE_IMAGE;
        }
    }

    private boolean isCheckBagdeImage(@NonNull final PaymentResult paymentResult) {
        return paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_APPROVED);
    }

    private boolean isPendingGreenBadgeImage(@NonNull final PaymentResult paymentResult) {
        return (paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_PENDING) ||
                paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS)) &&
                paymentResult.getPaymentStatusDetail().equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT);
    }

    private boolean isPendingOrangeBadgeImage(@NonNull final PaymentResult paymentResult) {
        return (paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_PENDING) ||
                paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_IN_PROCESS)) &&
                (paymentResult.getPaymentStatusDetail().equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY) ||
                        paymentResult.getPaymentStatusDetail().equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_REVIEW_MANUAL));
    }

    private boolean isWarningBadgeImage(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return status.equals(Payment.StatusCodes.STATUS_REJECTED) && (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED) ||
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT));
    }

    private boolean isErrorBadgeImage(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return status.equals(Payment.StatusCodes.STATUS_REJECTED) && (
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON) ||
                        statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM) ||
                        statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_BY_BANK) ||
                        statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA) ||
                        statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT) ||
                        statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS) ||
                        statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK));
    }

    private CharSequence getTitle(@NonNull final PaymentResultProps props) {
        if (props.hasCustomizedTitle()) {
            return props.getPreferenceTitle();
        } else if (props.hasInstructions()) {
            return props.getInstructionsTitle();
        } else if (props.paymentResult == null) { // TODO REMOVE THIS, is only used in mocks
            return paymentResultProvider.getEmptyText();
        } else if (isPaymentMethodOff(props.paymentResult)) {
            return paymentResultProvider.getEmptyText();
        } else {
            final String paymentMethodName = props.paymentResult.getPaymentData().getPaymentMethod().getName();
            final String status = props.paymentResult.getPaymentStatus();
            final String statusDetail = props.paymentResult.getPaymentStatusDetail();

            if (status.equals(Payment.StatusCodes.STATUS_APPROVED)) {
                return paymentResultProvider.getApprovedTitle();
            } else if (status.equals(Payment.StatusCodes.STATUS_IN_PROCESS) || status.equals(Payment.StatusCodes.STATUS_PENDING)) {
                return paymentResultProvider.getPendingTitle();
            } else if (status.equals(Payment.StatusCodes.STATUS_REJECTED)) {

                if (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON.equals(statusDetail)
                        || Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM.equals(statusDetail)) {
                    return paymentResultProvider.getRejectedOtherReasonTitle(paymentMethodName);
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT)) {
                    return paymentResultProvider.getRejectedInsufficientAmountTitle(paymentMethodName);
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT)) {
                    return paymentResultProvider.getRejectedDuplicatedPaymentTitle(paymentMethodName);
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED)) {
                    return paymentResultProvider.getRejectedCardDisabledTitle(paymentMethodName);
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK)) {
                    return paymentResultProvider.getRejectedHighRiskTitle();
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS)) {
                    return paymentResultProvider.getRejectedMaxAttemptsTitle();
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER) ||
                        statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER) ||
                        statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE) ||
                        statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE)) {
                    return paymentResultProvider.getRejectedBadFilledCardTitle(paymentMethodName);
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_BY_BANK)
                        || statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA)) {
                    return paymentResultProvider.getRejectedInsufficientDataTitle();
                } else if (props.paymentResult.isCallForAuthorize()) {
                    return getCallForAuthFormattedTitle(props);
                } else {
                    return paymentResultProvider.getRejectedBadFilledOther();
                }
            }
        }

        return paymentResultProvider.getEmptyText();
    }

    private CharSequence getCallForAuthFormattedTitle(final @NonNull PaymentResultProps props) {
        String rejectedCallForAuthorizeTitle = paymentResultProvider.getRejectedCallForAuthorizeTitle();
        HeaderTitleFormatter headerTitleFormatter = new HeaderTitleFormatter(props.currencyId,
                props.paymentResult.getPaymentData().getTransactionAmount(),
                props.paymentResult.getPaymentData().getPaymentMethod().getName());
        return headerTitleFormatter.formatTextWithAmount(rejectedCallForAuthorizeTitle);
    }

    private String getLabel(@NonNull final PaymentResultProps props) {
        if (!props.isPluginPaymentResult(props.paymentResult) && props.hasCustomizedLabel()) {
            return props.getPreferenceLabel();
        } else if (props.paymentResult == null) {
            return paymentResultProvider.getEmptyText();
        } else {
            if (isLabelEmpty(props.paymentResult)) {
                return paymentResultProvider.getEmptyText();
            } else if (isLabelPending(props.paymentResult)) {
                return paymentResultProvider.getPendingLabel();
            } else if (isLabelError(props.paymentResult)) {
                return paymentResultProvider.getRejectionLabel();
            }
        }
        return paymentResultProvider.getEmptyText();
    }

    private boolean isLabelEmpty(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return status.equals(Payment.StatusCodes.STATUS_APPROVED) || status.equals(Payment.StatusCodes.STATUS_IN_PROCESS) ||
                (status.equals(Payment.StatusCodes.STATUS_PENDING)
                        && !statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT));
    }

    private boolean isLabelPending(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return status.equals(Payment.StatusCodes.STATUS_PENDING)
                && statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT);
    }

    private boolean isLabelError(@NonNull final PaymentResult paymentResult) {
        return paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_REJECTED);
    }

    private boolean isPaymentMethodOff(@NonNull final PaymentResult paymentResult) {
        final String paymentTypeId = paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId();
        return paymentTypeId.equals(PaymentTypes.TICKET) || paymentTypeId.equals(PaymentTypes.ATM) || paymentTypeId.equals(PaymentTypes.BANK_TRANSFER);
    }
}