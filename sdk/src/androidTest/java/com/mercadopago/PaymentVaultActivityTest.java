package com.mercadopago;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.mercadopago.lite.model.Sites;
import com.mercadopago.model.Customer;
import com.mercadopago.lite.model.Discount;
import com.mercadopago.lite.model.PaymentMethodSearch;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PaymentVaultActivityTest {
    @Rule
    public ActivityTestRule<PaymentVaultActivity> mTestRule = new ActivityTestRule<>(PaymentVaultActivity.class, true, false);

    private Intent validStartIntent;
    private FakeAPI mFakeAPI;

    private BigDecimal transactionAmount = new BigDecimal(100);

    @Before
    public void setupStartIntent() {
        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", "1234");
        validStartIntent.putExtra("amount", JsonUtil.getInstance().toJson(transactionAmount));
        validStartIntent.putExtra("purchaseTitle", "test item");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
    }

    @Before
    public void startFakeAPI() {
        mFakeAPI = new FakeAPI();
        mFakeAPI.start();
    }

    @Before
    public void initIntentsRecording() {
        Intents.init();
    }

    @After
    public void stopFakeAPI() {
        mFakeAPI.stop();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void ifOnlyUniqueSearchItemAvailableThenSelectIt() {
        PaymentMethodSearch paymentMethodSearch = StaticMock.getPaymentMethodSearchWithUniqueItemCreditCard();
        Customer customer = StaticMock.getCustomer(3);
        mFakeAPI.addResponseToQueue(customer, 200, "");
        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(CardVaultActivity.class.getName()));
    }

    private Discount getDirectDiscount() {
        Discount discount = new Discount();
        discount.setCouponAmount(new BigDecimal("100"));
        discount.setId(123L);
        discount.setAmountOff(new BigDecimal("100"));
        return discount;
    }
}
