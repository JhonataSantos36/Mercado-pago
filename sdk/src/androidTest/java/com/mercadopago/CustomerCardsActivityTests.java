package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.gson.Gson;
import com.mercadopago.model.Card;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ActivityResultUtil;
import com.mercadopago.utils.ViewUtils;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CustomerCardsActivityTests {

    public ActivityTestRule<CustomerCardsActivity> mTestRule = new ActivityTestRule<>(CustomerCardsActivity.class, true, false);

    @Before
    public void initIntents() {
        Intents.init();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void ifCardsReceivedShowThem() {

        List<Card> cards = StaticMock.getCards();

        Intent intent = new Intent();
        intent.putExtra("cards", new Gson().toJson(cards));

        mTestRule.launchActivity(intent);

        RecyclerView list = (RecyclerView) mTestRule.getActivity().findViewById(R.id.mpsdkCustomerCardsList);
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Customer cards test failed, no items found");
        }
    }

    @Test
    public void ifCardsNotSetStartErrorActivity() {
        Intent intent = new Intent();
        mTestRule.launchActivity(intent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test (expected = NoActivityResumedException.class)
    public void onBackPressedFinishActivity() {
        List<Card> cards = StaticMock.getCards();
        Intent intent = new Intent();
        intent.putExtra("cards", new Gson().toJson(cards));
        mTestRule.launchActivity(intent);

        pressBack();
    }

    @Test
    public void ifDecorationColorSetAndDarkFontEnabledDecorateToolbar() {

        DecorationPreference decorationPreference = new DecorationPreference();
        decorationPreference.enableDarkFont();
        decorationPreference.setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_light_grey));

        List<Card> cards = StaticMock.getCards();

        Intent intent = new Intent();
        intent.putExtra("cards", new Gson().toJson(cards));
        intent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        mTestRule.launchActivity(intent);

        Assert.assertTrue(mTestRule.getActivity().mTitle.getCurrentTextColor() == decorationPreference.getDarkFontColor(mTestRule.getActivity()));
    }

    @Test
    public void onCardSelectedFinishWithCardResult() {
        List<Card> cards = StaticMock.getCards();
        assert cards != null;

        Intent intent = new Intent();
        Gson gson = new Gson();
        intent.putExtra("cards", gson.toJson(cards));

        mTestRule.launchActivity(intent);

        onView(withId(R.id.mpsdkCustomerCardsList)).perform(
                actionOnItemAtPosition(0, click()));

        ActivityResult result = ActivityResultUtil.getActivityResult(mTestRule.getActivity());
        String selectedCardJson = JsonUtil.getInstance().toJson(cards.get(0));

        assertTrue(result.getResultCode() == Activity.RESULT_OK);
        assertTrue(selectedCardJson.equals(result.getExtras().getString("card")));
        assertTrue(mTestRule.getActivity().isFinishing());
    }

}


