package com.mercadopago.preferences;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by vaserber on 6/13/17.
 */

public class FlowPreferenceTest {

    @Test
    public void testDisableReviewAndConfirmScreen() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .disableReviewAndConfirmScreen()
                .build();
        assertFalse(flowPreference.isReviewAndConfirmScreenEnabled());
    }

    @Test
    public void testDisablePaymentResultScreen() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .disablePaymentResultScreen()
                .build();
        assertFalse(flowPreference.isPaymentResultScreenEnabled());
    }

    @Test
    public void testDisablePaymentApprovedScreen() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .disablePaymentApprovedScreen()
                .build();
        assertFalse(flowPreference.isPaymentApprovedScreenEnabled());
    }

    @Test
    public void testDisablePaymentRejectedScreen() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .disablePaymentRejectedScreen()
                .build();
        assertFalse(flowPreference.isPaymentRejectedScreenEnabled());
    }

    @Test
    public void testDisablePaymentPendingScreen() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .disablePaymentPendingScreen()
                .build();
        assertFalse(flowPreference.isPaymentPendingScreenEnabled());
    }

    @Test
    public void testDisableBankDeals() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .disableBankDeals()
                .build();
        assertFalse(flowPreference.isBankDealsEnabled());
    }

    @Test
    public void testDisableDiscounts() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .disableDiscount()
                .build();
        assertFalse(flowPreference.isDiscountEnabled());
    }

    @Test
    public void testDisableInstallmentsReviewScreen() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .disableInstallmentsReviewScreen()
                .build();
        assertFalse(flowPreference.isInstallmentsReviewScreenEnabled());
    }

    @Test
    public void testDefaultMaxSavedCards() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .build();
        assertEquals(FlowPreference.DEFAULT_MAX_SAVED_CARDS_TO_SHOW, flowPreference.getMaxSavedCardsToShow());
        assertFalse(flowPreference.isShowAllSavedCardsEnabled());
    }

    @Test
    public void testSetMaxSavedCards() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .setMaxSavedCardsToShow(5)
                .build();
        assertEquals(5, flowPreference.getMaxSavedCardsToShow());
        assertFalse(flowPreference.isShowAllSavedCardsEnabled());
    }

    @Test
    public void testSetMaxSavedCardsWithInvalidInt() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .setMaxSavedCardsToShow(0)
                .build();
        assertEquals(FlowPreference.DEFAULT_MAX_SAVED_CARDS_TO_SHOW, flowPreference.getMaxSavedCardsToShow());
        assertFalse(flowPreference.isShowAllSavedCardsEnabled());
    }

    @Test
    public void testSetMaxSavedCardsWithNegativeInt() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .setMaxSavedCardsToShow(-1)
                .build();
        assertEquals(FlowPreference.DEFAULT_MAX_SAVED_CARDS_TO_SHOW, flowPreference.getMaxSavedCardsToShow());
        assertFalse(flowPreference.isShowAllSavedCardsEnabled());
    }

    @Test
    public void testSetMaxSavedCardsWithNullInt() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .setMaxSavedCardsToShow(null)
                .build();
        assertEquals(FlowPreference.DEFAULT_MAX_SAVED_CARDS_TO_SHOW, flowPreference.getMaxSavedCardsToShow());
        assertFalse(flowPreference.isShowAllSavedCardsEnabled());
    }

    @Test
    public void testSetMaxSavedCardsWithEmptyString() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .setMaxSavedCardsToShow("")
                .build();
        assertEquals(FlowPreference.DEFAULT_MAX_SAVED_CARDS_TO_SHOW, flowPreference.getMaxSavedCardsToShow());
        assertFalse(flowPreference.isShowAllSavedCardsEnabled());
    }

    @Test
    public void testSetMaxSavedCardsWithInvalidString() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .setMaxSavedCardsToShow("invalid")
                .build();
        assertEquals(FlowPreference.DEFAULT_MAX_SAVED_CARDS_TO_SHOW, flowPreference.getMaxSavedCardsToShow());
        assertFalse(flowPreference.isShowAllSavedCardsEnabled());
    }

    @Test
    public void testSetMaxSavedCardsToShowAll() {
        FlowPreference flowPreference = new FlowPreference.Builder()
                .setMaxSavedCardsToShow(FlowPreference.SHOW_ALL_SAVED_CARDS_CODE)
                .build();
        assertTrue(flowPreference.isShowAllSavedCardsEnabled());
    }

}
