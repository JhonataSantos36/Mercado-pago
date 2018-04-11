package com.mercadopago.paymentresult;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.mocks.PaymentMethods;
import com.mercadopago.mocks.PaymentResults;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.paymentresult.components.Body;
import com.mercadopago.paymentresult.components.Header;
import com.mercadopago.paymentresult.components.PaymentResultContainer;
import com.mercadopago.paymentresult.model.Badge;
import com.mercadopago.paymentresult.props.HeaderProps;
import com.mercadopago.paymentresult.props.PaymentResultProps;
import com.mercadopago.preferences.PaymentResultScreenPreference;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by vaserber on 11/2/17.
 */

public class PaymentResultContainerTest {

    private final static String APPROVED_TITLE = "approved title";
    private final static String PENDING_TITLE = "pending title";
    private final static String REJECTED_OTHER_REASON_TITLE = "rejected other reason title";
    private final static String REJECTED_INSUFFICIENT_AMOUNT_TITLE = "rejected insufficient amount title";
    private final static String REJECTED_BAD_FILLED_TITLE = "rejected bad filled title";
    private final static String REJECTED_CALL_FOR_AUTH_TITLE = "rejected call for auth title";
    private final static String EMPTY_TITLE = "empty title";
    private final static String PENDING_LABEL = "pending label";
    private final static String REJECTION_LABEL = "rejection label";

    private ActionDispatcher dispatcher;
    private PaymentResultProvider paymentResultProvider;
    private PaymentMethodProvider paymentMethodProvider;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
        paymentResultProvider = mock(PaymentResultProvider.class);
        paymentMethodProvider = mock(PaymentMethodProvider.class);

        when(paymentResultProvider.getApprovedTitle()).thenReturn(APPROVED_TITLE);
        when(paymentResultProvider.getPendingTitle()).thenReturn(PENDING_TITLE);
        when(paymentResultProvider.getRejectedOtherReasonTitle("Mastercard")).thenReturn(REJECTED_OTHER_REASON_TITLE);
        when(paymentResultProvider.getRejectedInsufficientAmountTitle("Mastercard")).thenReturn(REJECTED_INSUFFICIENT_AMOUNT_TITLE);
        when(paymentResultProvider.getRejectedBadFilledCardTitle()).thenReturn(REJECTED_BAD_FILLED_TITLE);
        when(paymentResultProvider.getRejectedCallForAuthorizeTitle()).thenReturn(REJECTED_CALL_FOR_AUTH_TITLE);
        when(paymentResultProvider.getEmptyText()).thenReturn(EMPTY_TITLE);
        when(paymentResultProvider.getPendingLabel()).thenReturn(PENDING_LABEL);
        when(paymentResultProvider.getRejectionLabel()).thenReturn(REJECTION_LABEL);

        new PaymentResultScreenPreference.Builder().build();
    }

    @Test
    public void onApprovedPaymentThenShowGreenBackground() {

        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.background, PaymentResultContainer.GREEN_BACKGROUND_COLOR);
    }

    @Test
    public void onInProcessPaymentThenShowGreenBackground() {

        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.background, PaymentResultContainer.ORANGE_BACKGROUND_COLOR);
    }

    @Test
    public void onPaymentMethodOffPaymentThenShowGreenBackground() {

        final PaymentResult paymentResult = PaymentResults.getPaymentMethodOffPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.background, PaymentResultContainer.GREEN_BACKGROUND_COLOR);
    }

    @Test
    public void onRejectedOtherReasonPaymentThenShowRedBackground() {

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.background, PaymentResultContainer.RED_BACKGROUND_COLOR);
    }

    @Test
    public void onRejectedCallForAuthPaymentThenShowOrangeBackground() {

        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.background, PaymentResultContainer.ORANGE_BACKGROUND_COLOR);
    }

    @Test
    public void onRejectedInsufficientAmountPaymentThenShowOrangeBackground() {

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedInsufficientAmountPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.background, PaymentResultContainer.ORANGE_BACKGROUND_COLOR);
    }

    @Test
    public void onRejectedBadFilledSecuPaymentThenShowOrangeBackground() {

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledSecuPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.background, PaymentResultContainer.ORANGE_BACKGROUND_COLOR);
    }

    @Test
    public void onRejectedBadFilledDatePaymentThenShowOrangeBackground() {

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledDatePaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.background, PaymentResultContainer.ORANGE_BACKGROUND_COLOR);
    }

    @Test
    public void onRejectedBadFilledFormPaymentThenShowOrangeBackground() {

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.background, PaymentResultContainer.ORANGE_BACKGROUND_COLOR);
    }

    @Test
    public void onEmptyPaymentResultGetDefaultBackground() {
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(null);

        Assert.assertEquals(headerProps.background, PaymentResultContainer.DEFAULT_BACKGROUND_COLOR);
    }

    @Test
    public void onInvalidPaymentResultStatusGetDefaultBackground() {
        final PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        final PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus("")
                .setPaymentStatusDetail("")
                .setPaymentData(paymentData)
                .build();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.background, PaymentResultContainer.DEFAULT_BACKGROUND_COLOR);
    }

    @Test
    public void onAccreditedPaymentThenShowItemIcon() {
        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.iconImage, PaymentResultContainer.ITEM_ICON_IMAGE);
    }

    @Test
    public void onPaymentMethodOffPaymentThenShowItemIcon() {
        final PaymentResult paymentResult = PaymentResults.getPaymentMethodOffPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.iconImage, PaymentResultContainer.ITEM_ICON_IMAGE);
    }

    @Test
    public void onPaymentMethodOnInProcessThenShowCardIcon() {
        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.iconImage, PaymentResultContainer.CARD_ICON_IMAGE);
    }

    @Test
    public void onPaymentMethodOnRejectedBadFilledThenShowCardIcon() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.iconImage, PaymentResultContainer.CARD_ICON_IMAGE);
    }

    @Test
    public void onPaymentMethodOnRejectedInsufficientAmountThenShowCardIcon() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedInsufficientAmountPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.iconImage, PaymentResultContainer.CARD_ICON_IMAGE);
    }

    @Test
    public void onBoletoRejectedPaymentThenShowBoletoIcon() {
        final PaymentResult paymentResult = PaymentResults.getBoletoRejectedPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.iconImage, PaymentResultContainer.BOLETO_ICON_IMAGE);
    }

    @Test
    public void onBoletoApprovedPaymentThenShowItemIcon() {
        final PaymentResult paymentResult = PaymentResults.getBoletoApprovedPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.iconImage, PaymentResultContainer.ITEM_ICON_IMAGE);
    }

    @Test
    public void onPaymentMethodOnRejectedOtherThenShowCardIcon() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.iconImage, PaymentResultContainer.CARD_ICON_IMAGE);
    }

    @Test
    public void onEmptyPaymentResultGetDefaultIcon() {
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(null);

        Assert.assertEquals(headerProps.iconImage, PaymentResultContainer.DEFAULT_ICON_IMAGE);
    }

    @Test
    public void onInvalidPaymentResultStatusGetDefaultIcon() {
        final PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        final PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus("")
                .setPaymentStatusDetail("")
                .setPaymentData(paymentData)
                .build();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.iconImage, PaymentResultContainer.DEFAULT_ICON_IMAGE);
    }

    @Test
    public void onCustomizedIconOnApprovedStatusThenShowIt() {
        final int customizedIcon = 1;

        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setApprovedHeaderIcon(customizedIcon).build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.iconImage, customizedIcon);
    }

    @Test
    public void onCustomizedIconOnPaymentMethodOffThenShowIt() {
        final int customizedIcon = 2;

        final PaymentResult paymentResult = PaymentResults.getPaymentMethodOffPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setPendingHeaderIcon(customizedIcon).build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.iconImage, customizedIcon);
    }

    @Test
    public void onCustomizedIconOnRejectedStatusThenShowIt() {
        final int customizedIcon = 3;

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setRejectedHeaderIcon(customizedIcon).build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.iconImage, customizedIcon);
    }

    @Test
    public void onCustomizedIconWithOtherStatusThenDontShowIt() {
        final int customizedIcon = 4;

        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setRejectedHeaderIcon(customizedIcon).build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertNotSame(headerProps.iconImage, customizedIcon);
        Assert.assertEquals(headerProps.iconImage, PaymentResultContainer.ITEM_ICON_IMAGE);
    }

    @Test
    public void onApprovedPaymentThenShowCheckBadge() {
        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.CHECK_BADGE_IMAGE);
    }

    @Test
    public void onInProcessPaymentThenShowPendingBadge() {
        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.PENDING_BADGE_ORANGE_IMAGE);
    }

    @Test
    public void onPaymentMethodOffThenShowPendingBadge() {
        final PaymentResult paymentResult = PaymentResults.getPaymentMethodOffPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.PENDING_BADGE_GREEN_IMAGE);
    }

    @Test
    public void onStatusCallForAuthPaymentThenShowWarningBadge() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.WARNING_BADGE_IMAGE);
    }

    @Test
    public void onStatusBadFilledSecuPaymentThenShowWarningBadge() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledSecuPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.WARNING_BADGE_IMAGE);
    }

    @Test
    public void onStatusBadFilledDatePaymentThenShowWarningBadge() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledDatePaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.WARNING_BADGE_IMAGE);
    }

    @Test
    public void onStatusBadFilledFormPaymentThenShowWarningBadge() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.WARNING_BADGE_IMAGE);
    }

    @Test
    public void onStatusInsufficientAmountPaymentThenShowWarningBadge() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedInsufficientAmountPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.WARNING_BADGE_IMAGE);
    }

    @Test
    public void onStatusRejectedOtherReasonPaymentThenShowErrorBadge() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.ERROR_BADGE_IMAGE);
    }

    @Test
    public void onEmptyPaymentResultGetDefaultBadge() {
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(null);

        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.DEFAULT_BADGE_IMAGE);
    }

    @Test
    public void onInvalidPaymentResultStatusGetDefaultBadge() {
        final PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        final PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus("")
                .setPaymentStatusDetail("")
                .setPaymentData(paymentData)
                .build();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.DEFAULT_BADGE_IMAGE);
    }

    @Test
    public void onCustomizedBadgeOnApprovedStatusThenShowIt() {
        final String customizedBadge = Badge.PENDING_BADGE_IMAGE;
        final int badgeImage = PaymentResultContainer.PENDING_BADGE_GREEN_IMAGE;

        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setBadgeApproved(customizedBadge).build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.badgeImage, badgeImage);
    }

    @Test
    public void onInvalidCustomizedBadgeOnApprovedStatusThenDontShowIt() {
        final String customizedBadge = "";

        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setBadgeApproved(customizedBadge).build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.badgeImage, PaymentResultContainer.CHECK_BADGE_IMAGE);
    }

    @Test
    public void onApprovedPaymentThenShowApprovedTitle() {
        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.title, paymentResultProvider.getApprovedTitle());
    }

    @Test
    public void onInProcessPaymentThenShowPendingTitle() {
        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.title, paymentResultProvider.getPendingTitle());
    }

    @Test
    public void onRejectedOtherReasonPaymentThenShowTitle() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(paymentResult.getPaymentData().getPaymentMethod().getName(), "Mastercard");
        Assert.assertEquals(headerProps.title, paymentResultProvider.getRejectedOtherReasonTitle("Mastercard"));
    }

    @Test
    public void onRejectedInsufficientAmountPaymentThenShowTitle() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedInsufficientAmountPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(paymentResult.getPaymentData().getPaymentMethod().getName(), "Mastercard");
        Assert.assertEquals(headerProps.title, paymentResultProvider.getRejectedInsufficientAmountTitle("Mastercard"));
    }

    @Test
    public void onRejectedBadFilledSecuPaymentThenShowTitle() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledSecuPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.title, paymentResultProvider.getRejectedBadFilledCardTitle(""));
    }

    @Test
    public void onRejectedBadFilledDatePaymentThenShowTitle() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledDatePaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.title, paymentResultProvider.getRejectedBadFilledCardTitle(""));
    }

    @Test
    public void onRejectedBadFilledFormPaymentThenShowTitle() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.title, paymentResultProvider.getRejectedBadFilledCardTitle(""));
    }

    @Test
    public void onRejectedCallForAuthPaymentThenShowTitle() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.title, paymentResultProvider.getRejectedCallForAuthorizeTitle());
    }

    @Test
    public void onPaymentMethodOffPaymentThenShowInstructionsTitle() {
        final PaymentResult paymentResult = PaymentResults.getPaymentMethodOffPaymentResult();
        final Instruction instruction = com.mercadopago.mocks.Instructions.getRapipagoInstruction();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult, instruction);

        Assert.assertEquals(headerProps.title, instruction.getTitle());
    }

    @Test
    public void onEmptyPaymentResultGetEmptyTitle() {
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(null);

        Assert.assertEquals(headerProps.title, paymentResultProvider.getEmptyText());
    }

    @Test
    public void onPaymentMethodOffWithoutInstructionThenShowEmptyTitle() {
        final PaymentResult paymentResult = PaymentResults.getPaymentMethodOffPaymentResult();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.title, paymentResultProvider.getEmptyText());
    }

    @Test
    public void onCustomizedTitleOnApprovedStatusThenShowIt() {
        final String customizedTitle = "customized approved";

        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setApprovedTitle(customizedTitle).build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.title, customizedTitle);
    }

    @Test
    public void onCustomizedTitleOnInProcessStatusThenShowIt() {
        final String customizedTitle = "customized pending";

        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setPendingTitle(customizedTitle).build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.title, customizedTitle);
    }

    @Test
    public void onCustomizedTitleOnPaymentMethodOffThenDontShowIt() {
        final String customizedTitle = "customized instructions";

        final PaymentResult paymentResult = PaymentResults.getPaymentMethodOffPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setPendingTitle(customizedTitle).build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertNotSame(headerProps.title, customizedTitle);
    }

    @Test
    public void onCustomizedTitleOnRejectedStatusThenShowIt() {
        final String customizedTitle = "customized rejected";

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setRejectedTitle(customizedTitle).build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.title, customizedTitle);
    }

    @Test
    public void onApprovedPaymentThenShowEmptyLabel() {
        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.label, paymentResultProvider.getEmptyText());
    }

    @Test
    public void onInProcessPaymentThenShowEmptyLabel() {
        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.label, paymentResultProvider.getEmptyText());
    }

    @Test
    public void onPaymentMethodOffPaymentThenShowPendingLabel() {
        final PaymentResult paymentResult = PaymentResults.getPaymentMethodOffPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.label, paymentResultProvider.getPendingLabel());
    }

    @Test
    public void onRejectedBadFilledStatusPaymentThenShowRejectionLabel() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.label, paymentResultProvider.getRejectionLabel());
    }

    @Test
    public void onRejectedInsufficientAmountStatusPaymentThenShowRejectionLabel() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedInsufficientAmountPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.label, paymentResultProvider.getRejectionLabel());
    }

    @Test
    public void onRejectedOtherReasonStatusPaymentThenShowRejectionLabel() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.label, paymentResultProvider.getRejectionLabel());
    }

    @Test
    public void onRejectedPaymentOffStatusPaymentThenShowRejectionLabel() {
        final PaymentResult paymentResult = PaymentResults.getBoletoRejectedPaymentResult();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.label, paymentResultProvider.getRejectionLabel());
    }

    @Test
    public void onEmptyPaymentResultGetEmptyLabel() {
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(null);

        Assert.assertEquals(headerProps.label, paymentResultProvider.getEmptyText());
    }

    @Test
    public void onInvalidPaymentResultStatusGetEmptyLabel() {
        final PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        final PaymentResult paymentResult = new PaymentResult.Builder()
                .setPaymentStatus("")
                .setPaymentStatusDetail("")
                .setPaymentData(paymentData)
                .build();
        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);

        Assert.assertEquals(headerProps.label, paymentResultProvider.getEmptyText());
    }

    @Test
    public void onCustomizedLabelOnApprovedStatusThenShowIt() {
        final String customizedLabel = "customized approved label";

        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .setApprovedLabelText(customizedLabel).build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.label, customizedLabel);
    }

    @Test
    public void onCustomizedDisabledLabelOnRejectedStatusThenHideIt() {

        final PaymentResult paymentResult = PaymentResults.getStatusRejectedOtherPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .disableRejectedLabelText().build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.label, "");
    }

    @Test
    public void onCustomizedDisabledLabelOnInvalidStatusThenShowDefaultLabel() {

        final PaymentResult paymentResult = PaymentResults.getStatusInProcessContingencyPaymentResult();
        new PaymentResultScreenPreference.Builder()
                .disableRejectedLabelText().build();

        final HeaderProps headerProps = getHeaderPropsFromContainerWith(paymentResult);
        Assert.assertEquals(headerProps.label, paymentResultProvider.getEmptyText());
    }

    @Test
    public void testHasBodyComponentOnCallForAuth() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final PaymentResultContainer container = getContainer();

        final PaymentResultProps paymentResultProps = new PaymentResultProps.Builder()
                .setPaymentResult(paymentResult)
                .build();
        container.setProps(paymentResultProps);

        Assert.assertTrue(container.hasBodyComponent());
    }

    @Test
    public void testDoesntHaveBodyComponentOnBadFilledDate() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledDatePaymentResult();
        final PaymentResultContainer container = getContainer();

        final PaymentResultProps paymentResultProps = new PaymentResultProps.Builder()
                .setPaymentResult(paymentResult)
                .build();
        container.setProps(paymentResultProps);

        Assert.assertFalse(container.hasBodyComponent());
    }

    @Test
    public void testDoesntHaveBodyComponentOnBadFilledSecu() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledSecuPaymentResult();
        final PaymentResultContainer container = getContainer();

        final PaymentResultProps paymentResultProps = new PaymentResultProps.Builder()
                .setPaymentResult(paymentResult)
                .build();
        container.setProps(paymentResultProps);

        Assert.assertFalse(container.hasBodyComponent());
    }

    @Test
    public void testDoesntHaveBodyComponentOnBadFilledForm() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final PaymentResultContainer container = getContainer();

        final PaymentResultProps paymentResultProps = new PaymentResultProps.Builder()
                .setPaymentResult(paymentResult)
                .build();
        container.setProps(paymentResultProps);

        Assert.assertFalse(container.hasBodyComponent());
    }

    @Test
    public void testBodyComponentHasValidProps() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final PaymentResultContainer container = getContainer();

        final PaymentResultProps paymentResultProps = new PaymentResultProps.Builder()
                .setPaymentResult(paymentResult)
                .build();
        container.setProps(paymentResultProps);

        final Body body = container.getBodyComponent();
        Assert.assertEquals(body.props.paymentData, paymentResult.getPaymentData());
        Assert.assertEquals(body.props.status, paymentResult.getPaymentStatus());
        Assert.assertEquals(body.props.statusDetail, paymentResult.getPaymentStatusDetail());
    }

    @Test
    public void testHeaderWrapModeOnCallForAuth() {
        final PaymentResult paymentResult = PaymentResults.getStatusCallForAuthPaymentResult();
        final PaymentResultContainer container = getContainer();

        final PaymentResultProps paymentResultProps = new PaymentResultProps.Builder()
                .setPaymentResult(paymentResult)
                .build();
        container.setProps(paymentResultProps);

        Header header = container.getHeaderComponent();
        Assert.assertEquals(header.props.height, HeaderProps.HEADER_MODE_WRAP);
    }

    @Test
    public void testHeaderStretchOnBadFilledDate() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledDatePaymentResult();
        final PaymentResultContainer container = getContainer();

        final PaymentResultProps paymentResultProps = new PaymentResultProps.Builder()
                .setPaymentResult(paymentResult)
                .build();
        container.setProps(paymentResultProps);

        Header header = container.getHeaderComponent();
        Assert.assertEquals(header.props.height, HeaderProps.HEADER_MODE_STRETCH);
    }

    @Test
    public void testHeaderStretchOnBadFilledSecu() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledSecuPaymentResult();
        final PaymentResultContainer container = getContainer();

        final PaymentResultProps paymentResultProps = new PaymentResultProps.Builder()
                .setPaymentResult(paymentResult)
                .build();
        container.setProps(paymentResultProps);

        Header header = container.getHeaderComponent();
        Assert.assertEquals(header.props.height, HeaderProps.HEADER_MODE_STRETCH);
    }

    @Test
    public void testHeaderStretchOnBadFilledForm() {
        final PaymentResult paymentResult = PaymentResults.getStatusRejectedBadFilledFormPaymentResult();
        final PaymentResultContainer container = getContainer();

        final PaymentResultProps paymentResultProps = new PaymentResultProps.Builder()
                .setPaymentResult(paymentResult)
                .build();
        container.setProps(paymentResultProps);

        Header header = container.getHeaderComponent();
        Assert.assertEquals(header.props.height, HeaderProps.HEADER_MODE_STRETCH);
    }

    private HeaderProps getHeaderPropsFromContainerWith(PaymentResult paymentResult) {
        final PaymentResultContainer container = getContainer();

        final PaymentResultProps paymentResultProps = new PaymentResultProps.Builder()
                .setPaymentResult(paymentResult)
                .build();
        container.setProps(paymentResultProps);
        return container.getHeaderComponent().props;
    }

    private HeaderProps getHeaderPropsFromContainerWith(PaymentResult paymentResult, Instruction instruction) {
        final PaymentResultContainer container = getContainer();

        final PaymentResultProps paymentResultProps = new PaymentResultProps.Builder()
                .setPaymentResult(paymentResult)
                .setInstruction(instruction)
                .build();
        container.setProps(paymentResultProps);
        return container.getHeaderComponent().props;
    }

    private PaymentResultContainer getContainer() {
        return new PaymentResultContainer(dispatcher, paymentResultProvider, paymentMethodProvider);
    }


}
