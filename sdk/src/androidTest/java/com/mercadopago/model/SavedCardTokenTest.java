package com.mercadopago.model;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.mercadopago.CheckoutActivity;
import com.mercadopago.R;
import com.mercadopago.lite.exceptions.CardTokenException;
import com.mercadopago.exceptions.ExceptionHandler;
import com.mercadopago.lite.model.Card;
import com.mercadopago.lite.model.SavedCardToken;
import com.mercadopago.test.StaticMock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class SavedCardTokenTest {

    @Rule
    public ActivityTestRule<CheckoutActivity> mTestRule = new ActivityTestRule<>(CheckoutActivity.class, true, false);

    @Test
    public void testConstructor() {
        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        assertTrue(savedCardToken.getCardId().equals(StaticMock.DUMMY_CARD_ID));
        assertTrue(savedCardToken.getSecurityCode().equals(StaticMock.DUMMY_SECURITY_CODE));
    }

    @Test
    public void testValidate() {
        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        assertTrue(savedCardToken.validate());
    }

    // * Card id

    @Test
    public void testValidateNullCardId() {
        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setCardId(null);
        assertFalse(savedCardToken.validate());
    }

    @Test
    public void testValidateWrongCardId() {
        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setCardId("john");
        assertFalse(savedCardToken.validate());
    }

    // * Security code

    @Test
    public void testSecurityCode() {
        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        assertTrue(savedCardToken.validateSecurityCode());
    }

    @Test
    public void testSecurityCodeEmpty() {
        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setSecurityCode("");

        assertFalse(savedCardToken.validate());
        assertFalse(savedCardToken.validateSecurityCode());
    }

    @Test
    public void testSecurityCodeMinLength() {
        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setSecurityCode("4");

        assertFalse(savedCardToken.validate());
        assertFalse(savedCardToken.validateSecurityCode());
    }

    @Test
    public void testSecurityCodeMaxLength() {
        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setSecurityCode("44444");

        assertFalse(savedCardToken.validate());
        assertFalse(savedCardToken.validateSecurityCode());
    }

    @Test
    public void testSecurityCodeLengthZero() {
        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        Card card = StaticMock.getCard();

        savedCardToken.setSecurityCode(null);

        try {
            savedCardToken.validateSecurityCode(card);
            fail("Should have failed on security code length zero test");
        } catch (CardTokenException ex) {
            assertEquals(ex.getErrorCode(), CardTokenException.INVALID_FIELD);
            String message = ExceptionHandler.getErrorMessage(InstrumentationRegistry.getContext(), ex);
            String expectedMessage = InstrumentationRegistry.getContext().getString(R.string.mpsdk_invalid_field);
            assertEquals(message, expectedMessage);
        }

        savedCardToken.setSecurityCode("4444");

        try {
            savedCardToken.validateSecurityCode(card);
            fail("Should have failed on security code length zero test");
        } catch (CardTokenException ex) {
            assertEquals(ex.getErrorCode(), CardTokenException.INVALID_CVV_LENGTH);
            String message = ExceptionHandler.getErrorMessage(InstrumentationRegistry.getContext(), ex);
            String expectedMessage = InstrumentationRegistry.getContext().getString(R.string.mpsdk_invalid_cvv_length, String.valueOf(3));
            assertEquals(message, expectedMessage);
        }

        // Simulate a cards with security code not required
        savedCardToken.setSecurityCode(StaticMock.DUMMY_SECURITY_CODE);
        card.getSecurityCode().setLength(0);

        try {
            savedCardToken.validateSecurityCode(card);
        } catch (CardTokenException ex) {
            fail("Security code length zero test failed, cause: " + ex.getMessage());
        }

        card.setSecurityCode(null);

        try {
            savedCardToken.validateSecurityCode(card);
        } catch (CardTokenException ex) {
            fail("Security code length zero test failed, cause: " + ex.getMessage());
        }
    }
}
