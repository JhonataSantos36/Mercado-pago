package com.mercadopago.review_and_confirm.components.payment_method;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.review_and_confirm.models.PaymentModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by lbais on 1/3/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class MethodPluginTest {

    private static final String FAKE_LOCALIZED_VALUE = "fake_localized_value";
    private static final int ACCOUNT_MONEY_RES = R.drawable.mpsdk_account_money;

    @Mock
    PaymentModel paymentModel;

    @Mock
    PaymentMethodComponent.Actions actions;

    @Mock
    Context context;

    @Test
    public void when_account_money_type_then_return_localized_account_money_local_string() throws Exception {
        when(paymentModel.getPaymentType()).thenReturn(PaymentTypes.ACCOUNT_MONEY);
        when(context.getString(R.string.mpsdk_account_money)).thenReturn(FAKE_LOCALIZED_VALUE);
        MethodPlugin component = new MethodPlugin(MethodPlugin.Props.createFrom(paymentModel), actions);
        assertEquals(FAKE_LOCALIZED_VALUE, component.resolveTitleName(context));
    }

    @Test
    public void when_other_type_then_return_payment_name() throws Exception {
        String anyType = PaymentTypes.ATM;
        String anyName = "Jorge payment method";
        when(paymentModel.getPaymentType()).thenReturn(anyType);
        when(paymentModel.getPaymentMethodName()).thenReturn(anyName);
        MethodPlugin component = new MethodPlugin(MethodPlugin.Props.createFrom(paymentModel), actions);
        assertEquals(anyName, component.resolveTitleName(context));
    }

    public void when_account_money_type_then_return_account_money_resource() {
        when(paymentModel.getPaymentType()).thenReturn(PaymentTypes.ACCOUNT_MONEY);
        MethodPlugin component = new MethodPlugin(MethodPlugin.Props.createFrom(paymentModel), actions);
        assertEquals(ACCOUNT_MONEY_RES, component.resolveIcon());
    }


    public void when_other_type_then_return_icon_resource() {
        String anyType = PaymentTypes.ATM;
        int anyRes = R.drawable.mpsdk_amex;
        when(paymentModel.getPaymentType()).thenReturn(anyType);
        when(paymentModel.getIcon()).thenReturn(anyRes);
        MethodPlugin component = new MethodPlugin(MethodPlugin.Props.createFrom(paymentModel), actions);
        assertEquals(anyRes, component.resolveIcon());
    }


}