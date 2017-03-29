package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Looper;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.text.Spanned;

import com.mercadopago.model.ApiException;
import com.mercadopago.model.Currency;
import com.mercadopago.model.Discount;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.JsonUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by mromar on 1/13/17.
 */

public class DiscountsActivityTests {

    @Rule
    public ActivityTestRule<DiscountsActivity> mTestRule = new ActivityTestRule<>(DiscountsActivity.class, true, false);
    public Intent validStartIntent;

    private String mMerchantPublicKey;
    private BigDecimal mAmount;

    private FakeAPI mFakeAPI;

    @BeforeClass
    static public void initialize(){
        Looper.prepare();
    }

    @Before
    public void createValidStartIntent() {
        mMerchantPublicKey = StaticMock.DUMMY_TEST_PUBLIC_KEY;//"TEST-bbc4bfb5-b57b-48cc-9cc5-a3e3d5f1f5e1";//StaticMock.DUMMY_TEST_PUBLIC_KEY;
        mAmount = new BigDecimal(1000);

        validStartIntent = new Intent();
        validStartIntent.putExtra("amount", JsonUtil.getInstance().toJson(mAmount));
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
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
    public void showDiscountSummaryAndFinishWithDiscountWhenHasDirectDiscount() {
        Discount discount = StaticMock.getPercentOffDiscount();

        Spanned productAmount = getFormattedAmount(mAmount, discount.getCurrencyId());
        Spanned discountAmount = getDiscountAmount(discount);
        Spanned totalAmount = getFormattedAmount(discount.getAmountWithDiscount(mAmount), discount.getCurrencyId());

        mFakeAPI.addResponseToQueue(discount, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkReviewSummaryTitle)).check(matches(withText(getTitle(discount))));
        onView(withId(R.id.mpsdkReviewSummaryProductsAmount)).check(matches(withText(productAmount.toString())));
        onView(withId(R.id.mpsdkReviewSummaryDiscountsAmount)).check(matches(withText(discountAmount.toString())));
        onView(withId(R.id.mpsdkReviewSummaryTotalAmount)).check(matches(withText(totalAmount.toString())));

        onView(withId(R.id.mpsdkCloseImage)).perform(click());

        Intent discountResultIntent = new Intent();
        discountResultIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, discountResultIntent);
        intending(hasComponent(DiscountsActivity.class.getName())).respondWith(result);
    }

    @Test
    public void requestDiscountCodeAndShowSummaryWhenDirectDiscountIsDisabled() {
        Discount discount = StaticMock.getPercentOffDiscount();

        Spanned productAmount = getFormattedAmount(mAmount, discount.getCurrencyId());
        Spanned discountAmount = getDiscountAmount(discount);
        Spanned totalAmount = getFormattedAmount(discount.getAmountWithDiscount(mAmount), discount.getCurrencyId());

        validStartIntent.putExtra("directDiscountEnabled", false);

        mFakeAPI.addResponseToQueue(discount, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkDiscountCode)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkDiscountCode)).perform(typeText(StaticMock.DUMMY_DISCOUNT_CODE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        onView(withId(R.id.mpsdkReviewSummaryTitle)).check(matches(withText(getTitle(discount))));
        onView(withId(R.id.mpsdkReviewSummaryProductsAmount)).check(matches(withText(productAmount.toString())));
        onView(withId(R.id.mpsdkReviewSummaryDiscountsAmount)).check(matches(withText(discountAmount.toString())));
        onView(withId(R.id.mpsdkReviewSummaryTotalAmount)).check(matches(withText(totalAmount.toString())));

        onView(withId(R.id.mpsdkCloseImage)).perform(click());

        Intent discountResultIntent = new Intent();
        discountResultIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, discountResultIntent);
        intending(hasComponent(DiscountsActivity.class.getName())).respondWith(result);
    }

    @Test
    public void requestDiscountCodeAndShowSummaryWhenHasNotDirectDiscount() {
        Discount discount = StaticMock.getPercentOffDiscount();

        Spanned productAmount = getFormattedAmount(mAmount, discount.getCurrencyId());
        Spanned discountAmount = getDiscountAmount(discount);
        Spanned totalAmount = getFormattedAmount(discount.getAmountWithDiscount(mAmount), discount.getCurrencyId());

        mFakeAPI.addResponseToQueue("", 404, "");
        mFakeAPI.addResponseToQueue(discount, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkDiscountCode)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkDiscountCode)).perform(typeText(StaticMock.DUMMY_DISCOUNT_CODE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        onView(withId(R.id.mpsdkReviewSummaryTitle)).check(matches(withText(getTitle(discount))));
        onView(withId(R.id.mpsdkReviewSummaryProductsAmount)).check(matches(withText(productAmount.toString())));
        onView(withId(R.id.mpsdkReviewSummaryDiscountsAmount)).check(matches(withText(discountAmount.toString())));
        onView(withId(R.id.mpsdkReviewSummaryTotalAmount)).check(matches(withText(totalAmount.toString())));

        onView(withId(R.id.mpsdkCloseImage)).perform(click());

        Intent discountResultIntent = new Intent();
        discountResultIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, discountResultIntent);
        intending(hasComponent(DiscountsActivity.class.getName())).respondWith(result);
    }

    @Test
    public void showErrorWhenDiscountCodeIsInvalid() {
        ApiException apiException = StaticMock.getApiExceptionDiscountCodeNotMatch();

        mFakeAPI.addResponseToQueue("", 404, "");
        mFakeAPI.addResponseToQueue(apiException, 404, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkDiscountCode)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkDiscountCode)).perform(typeText(StaticMock.DUMMY_DISCOUNT_CODE));
        onView(withId(R.id.mpsdkNextButton)).perform(click());

        onView(withId(R.id.mpsdkErrorTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void showDiscountSummaryWhenStartDiscountActivityWithDiscount() {
        Discount discount = StaticMock.getAmountOffDiscount();

        Spanned productAmount = getFormattedAmount(mAmount, discount.getCurrencyId());
        Spanned discountAmount = getDiscountAmount(discount);
        Spanned totalAmount = getFormattedAmount(discount.getAmountWithDiscount(mAmount), discount.getCurrencyId());

        validStartIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkReviewSummaryTitle)).check(matches(withText(getTitle(discount))));
        onView(withId(R.id.mpsdkReviewSummaryProductsAmount)).check(matches(withText(productAmount.toString())));
        onView(withId(R.id.mpsdkReviewSummaryDiscountsAmount)).check(matches(withText(discountAmount.toString())));
        onView(withId(R.id.mpsdkReviewSummaryTotalAmount)).check(matches(withText(totalAmount.toString())));

        onView(withId(R.id.mpsdkCloseImage)).perform(click());

        Intent discountResultIntent = new Intent();
        discountResultIntent.putExtra("discount", JsonUtil.getInstance().toJson(discount));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, discountResultIntent);
        intending(hasComponent(DiscountsActivity.class.getName())).respondWith(result);
    }

    private String getTitle(Discount discount) {
        if (discount.getAmountOff().equals(new BigDecimal(0))) {
            return discount.getPercentOff() + mTestRule.getActivity().getString(R.string.mpsdk_percent_of_discount);
        } else {
            Currency currency = CurrenciesUtil.getCurrency(discount.getCurrencyId());
            String amount = currency.getSymbol() + discount.getAmountOff();

            return amount + " " + mTestRule.getActivity().getString(R.string.mpsdk_of_discount);
        }
    }

    private Spanned getFormattedAmount(BigDecimal amount, String currencyId) {
        String originalNumber = CurrenciesUtil.formatNumber(amount, currencyId);
        Spanned amountText = CurrenciesUtil.formatCurrencyInText(amount, currencyId, originalNumber, false, true);
        return amountText;
    }

    private Spanned getDiscountAmount(Discount discount) {
        StringBuilder discountAmountBuilder = new StringBuilder();
        Spanned discountAmount;

        discountAmountBuilder.append("-");
        discountAmountBuilder.append(CurrenciesUtil.formatNumber(discount.getCouponAmount(), discount.getCurrencyId()));
        discountAmount = CurrenciesUtil.formatCurrencyInText(discount.getCouponAmount(), discount.getCurrencyId(), discountAmountBuilder.toString(), false, true);

        return discountAmount;
    }
}
