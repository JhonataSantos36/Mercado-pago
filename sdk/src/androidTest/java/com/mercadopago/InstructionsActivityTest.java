package com.mercadopago;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.LinearLayout;

import com.mercadopago.model.Instruction;
import com.mercadopago.model.InstructionActionInfo;
import com.mercadopago.model.InstructionReference;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mercadopago.utils.CustomMatchers.withAnyChildText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mreverter on 28/3/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class InstructionsActivityTest {

    @Rule
    public ActivityTestRule<InstructionsActivity> mTestRule = new ActivityTestRule<>(InstructionsActivity.class, true, false);
    public Intent validStartIntent;

    private Payment mPayment;
    private String mMerchantPublicKey;
    private PaymentMethod mPaymentMethod;
    private FakeAPI mFakeAPI;


    @Before
    public void createValidStartIntent() {
        mPayment = StaticMock.getPayment();
        mMerchantPublicKey = "1234";
        mPaymentMethod = getOfflinePaymentMethod();

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
        validStartIntent.putExtra("payment", JsonUtil.getInstance().toJson(mPayment));
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
    public void getActivityParametersOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        InstructionsActivity activity = mTestRule.launchActivity(validStartIntent);
        assertEquals(activity.mMerchantPublicKey, mMerchantPublicKey);
        assertEquals(activity.mPayment.getId(), mPayment.getId());
        assertEquals(activity.mPaymentMethod.getId(), mPaymentMethod.getId());
    }

    @Test
    public void showTitleOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkTitle)).check(matches(withText(instructionWithoutActions.getTitle())));
    }

    @Test
    public void showAccreditationDateOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkAccreditationMessage)).check(matches(withText(instructionWithoutActions.getAcreditationMessage())));
    }

    @Test
    public void showInfoOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        String infoFirstPhrase = instructionWithoutActions.getInfo().get(0);
        String infoSecondPhrase = instructionWithoutActions.getInfo().get(1);
        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(withText(containsString(infoFirstPhrase))));
        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(withText(containsString(infoSecondPhrase))));
    }

    @Test
    public void showSecondaryInfoOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        String infoFirstPhrase = instructionWithoutActions.getSecondaryInfo().get(0);
        String infoSecondPhrase = instructionWithoutActions.getSecondaryInfo().get(1);
        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(withText(containsString(infoFirstPhrase))));
        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(withText(containsString(infoSecondPhrase))));
    }

    @Test
    public void showTertiaryInfoOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        String infoFirstPhrase = instructionWithoutActions.getTertiaryInfo().get(0);
        String infoSecondPhrase = instructionWithoutActions.getTertiaryInfo().get(1);
        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(withText(containsString(infoFirstPhrase))));
        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(withText(containsString(infoSecondPhrase))));
    }

    @Test
    public void ifNoPrimaryInfoDoNotDisplayInfoTextView() {
        Instruction instruction = StaticMock.getInstructionWithoutPrimaryInfo();
        mFakeAPI.addResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifPrimaryInfoDoNotDisplayInfoTextView() {
        Instruction instruction = StaticMock.getInstructionWithNullInfo();

        mFakeAPI.addResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifNoSecondaryInfoDoNotDisplayInfoTextView() {
        Instruction instruction = StaticMock.getInstructionWithoutSecondaryInfo();
        mFakeAPI.addResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifNoTertiaryInfoDoNotDisplayInfoTextView() {
        Instruction instruction = StaticMock.getInstructionWithoutTertiaryInfo();
        mFakeAPI.addResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test public void ifReferenceDoesNotHaveValueDoNotShowIt() {
        Instruction instruction = StaticMock.getInstructionWithInvalidReference();
        mFakeAPI.addResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        InstructionReference firstReference = instruction.getReferences().get(0);
        InstructionReference secondReference = instruction.getReferences().get(1);

        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(not(withAnyChildText(firstReference.getLabel()))));
        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(not(withAnyChildText(secondReference.getLabel()))));
    }

    @Test
    public void showReferences() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");

        mTestRule.launchActivity(validStartIntent);

        InstructionReference firstReference = instructionWithoutActions.getReferences().get(0);
        InstructionReference secondReference = instructionWithoutActions.getReferences().get(1);

        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(firstReference.getFormattedReference())));
        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(secondReference.getFormattedReference())));
    }

    @Test
    public void showLabelsInUpperCase() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");

        mTestRule.launchActivity(validStartIntent);

        InstructionReference firstReference = instructionWithoutActions.getReferences().get(0);
        InstructionReference secondReference = instructionWithoutActions.getReferences().get(1);

        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(firstReference.getLabel().toUpperCase())));
        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(secondReference.getLabel().toUpperCase())));
    }

    @Test
    public void ifReferenceDoesNotHaveLabelDoNotShowLabel() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutLabels();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        LinearLayout referencesLayout = (LinearLayout) mTestRule.getActivity().findViewById(R.id.mpsdkReferencesLayout);

        //Check that only the reference values are shown
        assertTrue(referencesLayout.getChildCount() == 2);
    }

    @Test
    public void ifInstructionDoesNotHaveActionDoNotShowActionButton() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkActionButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifInstructionHasActionShowActionButton() {
        Instruction instructionWithAction = StaticMock.getInstructionWithAction();
        mFakeAPI.addResponseToQueue(instructionWithAction, 200, "");

        mTestRule.launchActivity(validStartIntent);

        InstructionActionInfo actionInfo = instructionWithAction.getActions().get(0);

        onView(withId(R.id.mpsdkActionButton)).check(matches(withText(actionInfo.getLabel())));
    }

    @Test
    public void ifInstructionHasActionButNullUinstrlDoNotShowActionButton() {
        Instruction instructionWithAction = StaticMock.getInstructionWithActionButNullUrl();
        mFakeAPI.addResponseToQueue(instructionWithAction, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkActionButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifInstructionHasActionButEmptyUrlDoNotShowActionButton() {
        Instruction instructionWithAction = StaticMock.getInstructionWithActionButEmptyUrl();
        mFakeAPI.addResponseToQueue(instructionWithAction, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkActionButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void onActionButtonClickIntentToUrl() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithAction();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");

        mTestRule.launchActivity(validStartIntent);

        InstructionActionInfo actionInfo = instructionWithoutActions.getActions().get(0);

        onView(withId(R.id.mpsdkActionButton)).perform(click());

        intended(allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(actionInfo.getUrl())));
    }

    @Test
    public void ifApiFailureShowErrorActivity() {
        mFakeAPI.addResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    private PaymentMethod getOfflinePaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("oxxo");
        paymentMethod.setName("Oxxo");
        paymentMethod.setPaymentTypeId("ticket");
        return paymentMethod;
    }
}