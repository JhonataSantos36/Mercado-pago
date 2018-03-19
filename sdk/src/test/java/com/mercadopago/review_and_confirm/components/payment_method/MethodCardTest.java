package com.mercadopago.review_and_confirm.components.payment_method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class MethodCardTest {

    @Test
    public void when_issuer_name_equals_card_name_then_should_show_is_false() throws Exception {
        MethodCard.Props props = new MethodCard.Props("1234", "visa", "1235", "visa");
        MethodCard methodCard = new MethodCard(props);
        assertFalse(methodCard.shouldShowSubtitle());
    }

    @Test
    public void when_issuer_is_empty_then_should_show_is_false() throws Exception {
        MethodCard.Props props = new MethodCard.Props("1234", "visa", "1235", "");
        MethodCard methodCard = new MethodCard(props);
        assertFalse(methodCard.shouldShowSubtitle());
    }

    @Test
    public void when_issuer_different_than_card_name_then_should_show_is_true() throws Exception {
        MethodCard.Props props = new MethodCard.Props("1234", "visa", "1235", "banco visa");
        MethodCard methodCard = new MethodCard(props);
        assertTrue(methodCard.shouldShowSubtitle());
    }


}