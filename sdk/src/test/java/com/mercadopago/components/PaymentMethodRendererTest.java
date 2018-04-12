package com.mercadopago.components;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.model.PaymentTypes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodRendererTest {

    private static final String STUB_R_STRING_TEXT_STATE = "Algo con %s algo";
    private static final String STUB_R_STRING_ENDING = "ending";
    private static final String EMPTY_STRING = "";

    @Mock
    private Context context;

    private PaymentMethodRenderer renderer;

    @Before
    public void setUp() throws Exception {
        renderer = new PaymentMethodRenderer();
        when(context.getString(R.string.mpsdk_text_state_account_activity_congrats)).thenReturn(STUB_R_STRING_TEXT_STATE);
        when(context.getString(R.string.mpsdk_ending_in)).thenReturn(STUB_R_STRING_ENDING);
    }

    @Test
    public void whenTextIsNotEmptyAndIsCardThenDisclaimerIsEmpty() {
        String stubDisclaimer = "stub";
        String disclaimer = renderer.getDisclaimer(PaymentTypes.CREDIT_CARD, stubDisclaimer, context);
        assertEquals(String.format(STUB_R_STRING_TEXT_STATE, stubDisclaimer), disclaimer);
    }

    @Test
    public void whenTextIsEmptyAndIsCardThenDisclaimerIsEmpty() {
        String disclaimer = renderer.getDisclaimer(PaymentTypes.CREDIT_CARD, "", context);
        assertTrue(disclaimer.equals(EMPTY_STRING));
    }

    @Test
    public void whenTextIsNullDisclaimerAndIsCardThenDisclaimerIsEmpty() {
        String disclaimer = renderer.getDisclaimer(PaymentTypes.CREDIT_CARD, null, context);
        assertTrue(disclaimer.equals(EMPTY_STRING));
    }

    @Test
    public void whenTextIsNullThenDisclaimerIsEmpty() {
        String disclaimer = renderer.getDisclaimer(PaymentTypes.CREDIT_CARD, null, context);
        assertTrue(disclaimer.equals(EMPTY_STRING));
    }

    @Test
    public void whenTextIsNotEmptyAndIsNotCardThenDisclaimerIsEmpty() {
        String disclaimer = renderer.getDisclaimer(PaymentTypes.PLUGIN, null, context);
        assertTrue(disclaimer.equals(EMPTY_STRING));
    }

    @Test
    public void whenPaymentTypeIsCardDescriptionIsCardFormatted() {
        String stubName = "stubName";
        String stub4Digits = "1234";
        String description = renderer.getDescription(stubName, PaymentTypes.CREDIT_CARD, stub4Digits, context);
        assertEquals(String.format(Locale.getDefault(), "%s %s %s", stubName, STUB_R_STRING_ENDING, stub4Digits), description);
    }

    @Test
    public void whenPaymentTypeIsNotCardDescriptionIsPaymentMethodName() {
        String stubName = "stubName";
        String description = renderer.getDescription(stubName, PaymentTypes.PLUGIN, null, context);
        assertEquals(stubName, description);
    }


}