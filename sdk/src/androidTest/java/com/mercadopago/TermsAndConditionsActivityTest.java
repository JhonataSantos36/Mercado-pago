package com.mercadopago;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.mercadopago.constants.Sites;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ViewUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static org.junit.Assert.assertTrue;

/**
 * Created by mreverter on 21/7/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TermsAndConditionsActivityTest  {

    @Rule
    public ActivityTestRule<TermsAndConditionsActivity> mTestRule = new ActivityTestRule<>(TermsAndConditionsActivity.class, true, false);

    @Before
    public void initIntents() {
        Intents.init();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    //Validation

    @Test
    public void ifNeitherLegalsNorSiteReceivedStartErrorActivity() {
        Intent invalidStartIntent =  new Intent();
        mTestRule.launchActivity(invalidStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    //Bank deals legals

    @Test
    public void ifBankDealsLegalsReceivedShowThem() {
        String legals = StaticMock.getBankDeals().get(0).getLegals();

        Intent legalsIntent =  new Intent();
        legalsIntent.putExtra("bankDealLegals", legals);

        mTestRule.launchActivity(legalsIntent);

        assertTrue(legals.equals(mTestRule.getActivity().mBankDealsLegalsTextView.getText()));
    }

    //MercadoPagoTermsAndConditions

    @Test
    public void ifMLASiteReceivedShowMercadoPagoTermsAndConditions() {
        Intent mlaIntent = new Intent();
        mlaIntent.putExtra("siteId", Sites.ARGENTINA.getId());

        mTestRule.launchActivity(mlaIntent);

        assertTrue(mTestRule.getActivity().mTermsAndConditionsWebView.getVisibility() == View.VISIBLE);
    }

    @Test
    public void ifMLMSiteReceivedShowMercadoPagoTermsAndConditions() {
        Intent mlmIntent = new Intent();
        mlmIntent.putExtra("siteId", Sites.MEXICO.getId());

        mTestRule.launchActivity(mlmIntent);

        assertTrue(mTestRule.getActivity().mTermsAndConditionsWebView.getVisibility() == View.VISIBLE);
    }

    @Test
    public void ifNeitherMLANorMLMSiteReceivedFinishActivity() {
        Intent mlmIntent = new Intent();
        mlmIntent.putExtra("siteId", Sites.BRASIL.getId());

        mTestRule.launchActivity(mlmIntent);

        assertTrue(mTestRule.getActivity().isFinishing());
    }

    //On back pressed finish activity

    @Test (expected = NoActivityResumedException.class)
    public void onBackPressedFinishActivity () {
        String legals = StaticMock.getBankDeals().get(0).getLegals();

        Intent legalsIntent =  new Intent();
        legalsIntent.putExtra("bankDealLegals", legals);

        mTestRule.launchActivity(legalsIntent);

        pressBack();
    }

    @Test
    public void onNavigationArrowPressedFinishActivity () {
        String legals = StaticMock.getBankDeals().get(0).getLegals();

        Intent legalsIntent =  new Intent();
        legalsIntent.putExtra("bankDealLegals", legals);

        mTestRule.launchActivity(legalsIntent);

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        assertTrue(mTestRule.getActivity().isFinishing());
    }

    //Decoration

    @Test
    public void onDecorationPreferenceSetWithBaseColorAndDarkFontEnabledDecorateToolbar() {
        DecorationPreference decorationPreference = new DecorationPreference();
        decorationPreference.enableDarkFont();
        decorationPreference.setBaseColor(ContextCompat.getColor(InstrumentationRegistry.getContext(), R.color.mpsdk_color_light_grey));

        String legals = StaticMock.getBankDeals().get(0).getLegals();

        Intent legalsIntent =  new Intent();
        legalsIntent.putExtra("bankDealLegals", legals);
        legalsIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

        mTestRule.launchActivity(legalsIntent);

        Assert.assertTrue(ViewUtils.getBackgroundColor(mTestRule.getActivity().mToolbar) == decorationPreference.getBaseColor());
        Assert.assertTrue(mTestRule.getActivity().mTitle.getCurrentTextColor() == decorationPreference.getDarkFontColor(mTestRule.getActivity()));
    }

}
