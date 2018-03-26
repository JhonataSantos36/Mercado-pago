package com.mercadopago.review_and_confirm.components.payment_method;

import com.mercadopago.components.CompactComponent;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.review_and_confirm.models.PaymentModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodComponentTest {

    @Mock
    PaymentModel model;

    @Mock
    PaymentMethodComponent.Actions actions;

    private PaymentMethodComponent component;

    @Before
    public void setUp() {
        component = new PaymentMethodComponent(model, actions);
    }

    @Test
    public void when_payment_model_has_card_property_then_return_MethodCardInstance() throws Exception {
        when(model.getPaymentType()).thenReturn(PaymentTypes.CREDIT_CARD);
        CompactComponent instance = component.resolveComponent();
        assertTrue(instance instanceof MethodCard);
    }

    @Test
    public void when_payment_model_has_off_property_then_return_MethodOffInstance() throws Exception {
        when(model.getPaymentType()).thenReturn(PaymentTypes.BANK_TRANSFER);
        CompactComponent instance = component.resolveComponent();
        assertTrue(instance instanceof MethodOff);
    }

    @Test
    public void when_payment_model_has_account_money_property_then_return_MethodPluginInstance() throws Exception {
        when(model.getPaymentType()).thenReturn(PaymentTypes.ACCOUNT_MONEY);
        CompactComponent instance = component.resolveComponent();
        assertTrue(instance instanceof MethodPlugin);
    }

    @Test
    public void render() throws Exception {
        //TODO test with integration test.
    }

}