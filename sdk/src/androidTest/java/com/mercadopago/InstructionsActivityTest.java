package com.mercadopago;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.test.StaticMock;
import com.mercadopago.test.rules.MockedApiTestRule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by mreverter on 28/3/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class InstructionsActivityTest {
    public MockedApiTestRule<InstructionsActivity> mTestRule = new MockedApiTestRule<>(InstructionsActivity.class, true, false);
    public Intent validStartIntent;

    @Before
    public void validStartIntent() {
        Payment payment = StaticMock.getPayment();
        String merchantPublicKey = "1234";
        PaymentMethod paymentMethod = getOfflinePaymentMethod();

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", merchantPublicKey);
        validStartIntent.putExtra("paymentMethod", paymentMethod);
        validStartIntent.putExtra("payment", payment);
    }

    @Test
    public void getActivityParametersOnCreate() {
        String cashInstructionsJson = StaticMock.getCashInstructionsJson();
        mTestRule.addApiResponseToQueue(cashInstructionsJson, 200, "");
        mTestRule.launchActivity(validStartIntent);
    }

    private PaymentMethod getOfflinePaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("oxxo");
        paymentMethod.setName("Oxxo");
        paymentMethod.setPaymentTypeId("ticket");
        return paymentMethod;
    }


}