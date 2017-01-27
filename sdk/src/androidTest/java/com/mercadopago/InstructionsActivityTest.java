package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.LinearLayout;

import com.mercadopago.model.InstructionActionInfo;
import com.mercadopago.model.InstructionReference;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ActivityResultUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
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
    private String mPaymentTypeId;
    private FakeAPI mFakeAPI;


    @Before
    public void createValidStartIntent() {
        mPayment = StaticMock.getPayment();
        mMerchantPublicKey = "1234";
        mPaymentTypeId = "ticket";

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentTypeId", mPaymentTypeId);
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
        PaymentResult instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        InstructionsActivity activity = mTestRule.launchActivity(validStartIntent);
        assertEquals(activity.mMerchantPublicKey, mMerchantPublicKey);
        assertEquals(activity.mPayment.getId(), mPayment.getId());
        assertEquals(activity.mPaymentTypeId, "ticket");
    }

    //Instructions data

    @Test
    public void showTitleOnCreate() {
        PaymentResult paymentResult = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(paymentResult, 200, "");
        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkTitle)).check(matches(withText(paymentResult.getInstructions().get(0).getTitle())));
    }

    @Test
    public void showAccreditationDateOnCreate() {
        PaymentResult paymentResult = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(paymentResult, 200, "");
        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkAccreditationMessage)).check(matches(withText(paymentResult.getInstructions().get(0).getAcreditationMessage())));
    }

    @Test
    public void showInfoOnCreate() {
        PaymentResult paymentResult = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(paymentResult, 200, "");
        mTestRule.launchActivity(validStartIntent);

        String infoFirstPhrase = paymentResult.getInstructions().get(0).getInfo().get(0);
        String infoSecondPhrase = paymentResult.getInstructions().get(0).getInfo().get(1);
        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(withText(containsString(infoFirstPhrase))));
        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(withText(containsString(infoSecondPhrase))));
    }

    @Test
    public void showSecondaryInfoOnCreate() {
        PaymentResult paymentResult = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(paymentResult, 200, "");
        mTestRule.launchActivity(validStartIntent);

        String infoFirstPhrase = paymentResult.getInstructions().get(0).getSecondaryInfo().get(0);
        String infoSecondPhrase = paymentResult.getInstructions().get(0).getSecondaryInfo().get(1);
        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(withText(containsString(infoFirstPhrase))));
        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(withText(containsString(infoSecondPhrase))));
    }

    @Test
    public void showTertiaryInfoOnCreate() {
        PaymentResult paymentResult = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(paymentResult, 200, "");
        mTestRule.launchActivity(validStartIntent);

        String infoFirstPhrase = paymentResult.getInstructions().get(0).getTertiaryInfo().get(0);
        String infoSecondPhrase = paymentResult.getInstructions().get(0).getTertiaryInfo().get(1);
        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(withText(containsString(infoFirstPhrase))));
        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(withText(containsString(infoSecondPhrase))));
    }

    @Test
    public void ifManyInstructionsFromServiceChooseByTypeAndShowIt() {
        PaymentResult paymentResult = StaticMock.getInstructions();
        mFakeAPI.addResponseToQueue(paymentResult, 200, "");
        mTestRule.launchActivity(validStartIntent);

        // Instruction for type "ticket" in position 0.
        String infoFirstPhrase = paymentResult.getInstructions().get(0).getInfo().get(0);
        String infoSecondPhrase = paymentResult.getInstructions().get(0).getInfo().get(1);
        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(withText(containsString(infoFirstPhrase))));
        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(withText(containsString(infoSecondPhrase))));
    }

    @Test
    public void ifNoPrimaryInfoDoNotDisplayInfoTextView() {
        PaymentResult instruction = StaticMock.getInstructionWithoutPrimaryInfo();
        mFakeAPI.addResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifPrimaryInfoDoNotDisplayInfoTextView() {
        PaymentResult instruction = StaticMock.getInstructionWithNullInfo();

        mFakeAPI.addResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifNoSecondaryInfoDoNotDisplayInfoTextView() {
        PaymentResult instruction = StaticMock.getInstructionWithoutSecondaryInfo();
        mFakeAPI.addResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifNoTertiaryInfoDoNotDisplayInfoTextView() {
        PaymentResult instruction = StaticMock.getInstructionWithoutTertiaryInfo();
        mFakeAPI.addResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test public void ifReferenceDoesNotHaveValueDoNotShowIt() {
        PaymentResult paymentResult = StaticMock.getInstructionWithInvalidReference();
        mFakeAPI.addResponseToQueue(paymentResult, 200, "");
        mTestRule.launchActivity(validStartIntent);

        InstructionReference firstReference = paymentResult.getInstructions().get(0).getReferences().get(0);
        InstructionReference secondReference = paymentResult.getInstructions().get(0).getReferences().get(1);

        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(not(withAnyChildText(firstReference.getLabel()))));
        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(not(withAnyChildText(secondReference.getLabel()))));
    }

    @Test
    public void ifInstructionsReceivedShowReferences() {
        PaymentResult paymentResult = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(paymentResult, 200, "");

        mTestRule.launchActivity(validStartIntent);

        InstructionReference firstReference = paymentResult.getInstructions().get(0).getReferences().get(0);
        InstructionReference secondReference = paymentResult.getInstructions().get(0).getReferences().get(1);

        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(firstReference.getFormattedReference())));
        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(secondReference.getFormattedReference())));
    }

    @Test
    public void showLabelsInLowerCase() {
        PaymentResult paymentResult = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(paymentResult, 200, "");

        mTestRule.launchActivity(validStartIntent);

        InstructionReference firstReference = paymentResult.getInstructions().get(0).getReferences().get(0);
        InstructionReference secondReference = paymentResult.getInstructions().get(0).getReferences().get(1);

        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(firstReference.getLabel())));
        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(secondReference.getLabel())));
    }

    @Test
    public void ifReferenceDoesNotHaveLabelDoNotShowLabel() {
        PaymentResult instructionWithoutActions = StaticMock.getInstructionWithoutLabels();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        LinearLayout referencesLayout = (LinearLayout) mTestRule.getActivity().findViewById(R.id.mpsdkReferencesLayout);

        //Check that only the reference values are shown
        assertTrue(referencesLayout.getChildCount() == 2);
    }

    @Test
    public void ifInstructionDoesNotHaveActionDoNotShowActionButton() {
        PaymentResult instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkActionButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifInstructionHasActionShowActionButton() {
        PaymentResult paymentResult = StaticMock.getInstructionWithAction();
        mFakeAPI.addResponseToQueue(paymentResult, 200, "");

        mTestRule.launchActivity(validStartIntent);

        InstructionActionInfo actionInfo = paymentResult.getInstructions().get(0).getActions().get(0);

        onView(withId(R.id.mpsdkActionButton)).check(matches(withText(actionInfo.getLabel())));
    }

    @Test
    public void ifInstructionHasActionButNullUrlDoNotShowActionButton() {
        PaymentResult instructionWithAction = StaticMock.getInstructionWithActionButNullUrl();
        mFakeAPI.addResponseToQueue(instructionWithAction, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkActionButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifInstructionHasActionButEmptyUrlDoNotShowActionButton() {
        PaymentResult instructionWithAction = StaticMock.getInstructionWithActionButEmptyUrl();
        mFakeAPI.addResponseToQueue(instructionWithAction, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkActionButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void onActionButtonClickIntentToUrl() {
        PaymentResult paymentResult = StaticMock.getInstructionWithAction();
        mFakeAPI.addResponseToQueue(paymentResult, 200, "");

        mTestRule.launchActivity(validStartIntent);

        InstructionActionInfo actionInfo = paymentResult.getInstructions().get(0).getActions().get(0);

        onView(withId(R.id.mpsdkActionButton)).perform(ViewActions.scrollTo(), click());

        intended(allOf(hasAction(Intent.ACTION_VIEW),hasData(actionInfo.getUrl())));
    }

    @Test
    public void ifActionIsNotLinkDoNotShowIt() {
        PaymentResult instruction = StaticMock.getInstructionWithInvalidAction();
        mFakeAPI.addResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkActionButton)).check(matches(not(isDisplayed())));
    }

    //Bad start

    @Test
    public void ifMerchantPublicKeyNotSetStartErrorActivity() {
        PaymentResult instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        Intent startWithoutPublicKey = new Intent(validStartIntent);
        startWithoutPublicKey.removeExtra("merchantPublicKey");
        mTestRule.launchActivity(startWithoutPublicKey);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifPaymentTypeIdNotSetStartErrorActivity() {
        PaymentResult instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        Intent startWithoutPaymentTypeId = new Intent(validStartIntent);
        startWithoutPaymentTypeId.removeExtra("paymentTypeId");
        mTestRule.launchActivity(startWithoutPaymentTypeId);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifPaymentNotSetStartErrorActivity() {
        PaymentResult instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        Intent startWithoutPayment = new Intent(validStartIntent);
        startWithoutPayment.removeExtra("payment");
        mTestRule.launchActivity(startWithoutPayment);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifCardPaymentTypeIdSetStartErrorActivity() {
        PaymentResult instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        mPaymentTypeId = "credit_card";

        Intent startWithCardPaymentMethod = new Intent(validStartIntent);
        startWithCardPaymentMethod.putExtra("paymentTypeId", mPaymentTypeId);

        mTestRule.launchActivity(startWithCardPaymentMethod);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    //Back

    @Test
    public void onPressedBackOnceDoNotFinish() {
        PaymentResult instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        pressBack();

        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(isDisplayed()));
    }

    @Test
    public void onPressedBackTwiceButWithDelayDoNotFinish() {
        PaymentResult instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        pressBack();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            //Do nothing
        }
        pressBack();

        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(isDisplayed()));
    }

    @Test (expected = NoActivityResumedException.class)
    public void onPressedBackTwiceFinish() {
        PaymentResult instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        pressBack();
        pressBack();
    }

    //API Fail (e.g timeout)

    @Test
    public void ifApiFailureShowErrorActivity() {
        mFakeAPI.addResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifApiFailsAndUserRetrySucceedsShowInstruction() {
        PaymentResult instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mFakeAPI.addResponseToQueue("", 401, "");
        mFakeAPI.addResponseToQueue(instructionWithoutActions, 200, "");

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkErrorRetry)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkErrorRetry)).perform(click());
        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(isDisplayed()));
    }

    @Test
    public void ifApiFailsAndUserCancelsFinishWithCancelResult() {
        mFakeAPI.addResponseToQueue("", 401, "");

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkExit)).perform(click());

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_CANCELED);
    }

    @Test
    public void ifNoInstructionsFromServiceShowErrorScreen() {
        PaymentResult emptyInstructionList = new PaymentResult();
        mFakeAPI.addResponseToQueue(emptyInstructionList, 200, "");
        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }


    @Test
    public void ifManyInstructionsFromServiceAndNoneMatchesTypeShowErrorScreen() {
        String paymentTypeId = "crazy_type";
        PaymentResult manyInstructions = StaticMock.getInstructions();
        mFakeAPI.addResponseToQueue(manyInstructions, 200, "");
        validStartIntent.putExtra("paymentTypeId", paymentTypeId);
        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }
}