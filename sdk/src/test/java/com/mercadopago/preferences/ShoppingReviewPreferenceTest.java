package com.mercadopago.preferences;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by mromar on 8/28/17.
 */

public class ShoppingReviewPreferenceTest {

    @Test
    public void testGetOneWordDescription() {
        ShoppingReviewPreference shoppingReviewPreference = new ShoppingReviewPreference.Builder()
                .setOneWordDescription("One word description")
                .build();

        assertTrue(shoppingReviewPreference.getOneWordDescription().equals("One"));
    }

    @Test
    public void testGetQuantityTitle() {
        ShoppingReviewPreference shoppingReviewPreference = new ShoppingReviewPreference.Builder()
                .setQuantityTitle("Quantity title")
                .build();

        assertTrue(shoppingReviewPreference.getQuantityTitle().equals("Quantity title"));
    }

    @Test
    public void testGetAmountTitle() {
        ShoppingReviewPreference shoppingReviewPreference = new ShoppingReviewPreference.Builder()
                .setAmountTitle("Amount title")
                .build();

        assertTrue(shoppingReviewPreference.getAmountTitle().equals("Amount title"));
    }

    @Test
    public void testShowQuantityRow() {
        ShoppingReviewPreference shoppingReviewPreference = new ShoppingReviewPreference.Builder()
                .showQuantityRow()
                .build();

        assertTrue(shoppingReviewPreference.shouldShowQuantityRow());
    }

    @Test
    public void testHideQuantityRow() {
        ShoppingReviewPreference shoppingReviewPreference = new ShoppingReviewPreference.Builder()
                .hideQuantityRow()
                .build();

        assertFalse(shoppingReviewPreference.shouldShowQuantityRow());
    }

    @Test
    public void testShowAmountTitle() {
        ShoppingReviewPreference shoppingReviewPreference = new ShoppingReviewPreference.Builder()
                .showAmountTitle()
                .build();

        assertTrue(shoppingReviewPreference.shouldShowAmountTitle());
    }

    @Test
    public void testHideAmountTitle() {
        ShoppingReviewPreference shoppingReviewPreference = new ShoppingReviewPreference.Builder()
                .hideAmountTitle()
                .build();

        assertFalse(shoppingReviewPreference.shouldShowAmountTitle());
    }
}
