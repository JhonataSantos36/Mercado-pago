package com.mercadopago;

import android.content.Intent;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mercadopago.model.Instruction;
import com.mercadopago.model.InstructionActionInfo;
import com.mercadopago.model.InstructionReference;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.test.rules.MockedApiTestRule;
import com.mercadopago.util.JsonUtil;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
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
    public MockedApiTestRule<InstructionsActivity> mTestRule = new MockedApiTestRule<>(InstructionsActivity.class, true, false);
    public Intent validStartIntent;

    private Payment mPayment;
    private String mMerchantPublicKey;
    private PaymentMethod mPaymentMethod;


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
        FakeAPI.getInstance().start();
    }

    @After
    public void stopFakeAPI() {
        FakeAPI.getInstance().shutDown();
    }

    @Test
    public void getActivityParametersOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mTestRule.addApiResponseToQueue(instructionWithoutActions, 200, "");
        InstructionsActivity activity = mTestRule.launchActivity(validStartIntent);
        assertEquals(activity.mMerchantPublicKey, mMerchantPublicKey);
        assertEquals(activity.mPayment.getId(), mPayment.getId());
        assertEquals(activity.mPaymentMethod.getId(), mPaymentMethod.getId());
    }

    @Test
    public void showTitleOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mTestRule.addApiResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkTitle)).check(matches(withText(instructionWithoutActions.getTitle())));
    }

    @Test
    public void showAccreditationDateOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mTestRule.addApiResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkAccreditationMessage)).check(matches(withText(instructionWithoutActions.getAcreditationMessage())));
    }

    @Test
    public void showInfoOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mTestRule.addApiResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        String infoFirstPhrase = instructionWithoutActions.getInfo().get(0);
        String infoSecondPhrase = instructionWithoutActions.getInfo().get(1);
        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(withText(containsString(infoFirstPhrase))));
        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(withText(containsString(infoSecondPhrase))));
    }

    @Test
    public void showSecondaryInfoOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mTestRule.addApiResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        String infoFirstPhrase = instructionWithoutActions.getSecondaryInfo().get(0);
        String infoSecondPhrase = instructionWithoutActions.getSecondaryInfo().get(1);
        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(withText(containsString(infoFirstPhrase))));
        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(withText(containsString(infoSecondPhrase))));
    }

    @Test
    public void showTertiaryInfoOnCreate() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mTestRule.addApiResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        String infoFirstPhrase = instructionWithoutActions.getTertiaryInfo().get(0);
        String infoSecondPhrase = instructionWithoutActions.getTertiaryInfo().get(1);
        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(withText(containsString(infoFirstPhrase))));
        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(withText(containsString(infoSecondPhrase))));
    }

    @Test
    public void ifNoPrimaryInfoDoNotDisplayInfoTextView() {
        Instruction instruction = StaticMock.getInstructionWithoutPrimaryInfo();
        mTestRule.addApiResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifPrimaryInfoDoNotDisplayInfoTextView() {
        Instruction instruction = StaticMock.getInstructionWithNullInfo();

        mTestRule.addApiResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkPrimaryInfo)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(not(isDisplayed())));
        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifNoSecondaryInfoDoNotDisplayInfoTextView() {
        Instruction instruction = StaticMock.getInstructionWithoutSecondaryInfo();
        mTestRule.addApiResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkSecondaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifNoTertiaryInfoDoNotDisplayInfoTextView() {
        Instruction instruction = StaticMock.getInstructionWithoutTertiaryInfo();
        mTestRule.addApiResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkTertiaryInfo)).check(matches(not(isDisplayed())));
    }

    @Test public void ifReferenceDoesNotHaveValueDoNotShowIt() {
        Instruction instruction = StaticMock.getInstructionWithInvalidReference();
        mTestRule.addApiResponseToQueue(instruction, 200, "");
        mTestRule.launchActivity(validStartIntent);

        InstructionReference firstReference = instruction.getReferences().get(0);
        InstructionReference secondReference = instruction.getReferences().get(1);

        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(not(withAnyChildText(firstReference.getLabel()))));
        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(not(withAnyChildText(secondReference.getLabel()))));
    }

    @Test
    public void showReferences() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mTestRule.addApiResponseToQueue(instructionWithoutActions, 200, "");

        mTestRule.launchActivity(validStartIntent);

        InstructionReference firstReference = instructionWithoutActions.getReferences().get(0);
        InstructionReference secondReference = instructionWithoutActions.getReferences().get(1);

        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(firstReference.getFormattedReference())));
        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(secondReference.getFormattedReference())));
    }

    @Test
    public void showLabelsInUpperCase() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mTestRule.addApiResponseToQueue(instructionWithoutActions, 200, "");

        mTestRule.launchActivity(validStartIntent);

        InstructionReference firstReference = instructionWithoutActions.getReferences().get(0);
        InstructionReference secondReference = instructionWithoutActions.getReferences().get(1);

        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(firstReference.getLabel().toUpperCase())));
        onView(withId(R.id.mpsdkReferencesLayout)).check(matches(withAnyChildText(secondReference.getLabel().toUpperCase())));
    }

    @Test
    public void ifReferenceDoesNotHaveLabelDoNotShowLabel() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutLabels();
        mTestRule.addApiResponseToQueue(instructionWithoutActions, 200, "");
        mTestRule.launchActivity(validStartIntent);

        LinearLayout referencesLayout = (LinearLayout) mTestRule.getActivity().findViewById(R.id.mpsdkReferencesLayout);

        //Check that only the reference values are shown
        assertTrue(referencesLayout.getChildCount() == 2);
    }

    @Test
    public void ifInstructionDoesNotHaveActionDoNotShowActionButton() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithoutActions();
        mTestRule.addApiResponseToQueue(instructionWithoutActions, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkActionButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifInstructionHasActionShowActionButton() {
        Instruction instructionWithAction = StaticMock.getInstructionWithAction();
        mTestRule.addApiResponseToQueue(instructionWithAction, 200, "");

        mTestRule.launchActivity(validStartIntent);

        InstructionActionInfo actionInfo = instructionWithAction.getActions().get(0);

        onView(withId(R.id.mpsdkActionButton)).check(matches(withText(actionInfo.getLabel())));
    }

    @Test
    public void ifInstructionHasActionButNullUinstrlDoNotShowActionButton() {
        Instruction instructionWithAction = StaticMock.getInstructionWithActionButNullUrl();
        mTestRule.addApiResponseToQueue(instructionWithAction, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkActionButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifInstructionHasActionButEmptyUrlDoNotShowActionButton() {
        Instruction instructionWithAction = StaticMock.getInstructionWithActionButEmptyUrl();
        mTestRule.addApiResponseToQueue(instructionWithAction, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkActionButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void onActionButtonClickIntentToUrl() {
        Instruction instructionWithoutActions = StaticMock.getInstructionWithAction();
        mTestRule.addApiResponseToQueue(instructionWithoutActions, 200, "");

        mTestRule.initIntentsRecording();
        mTestRule.launchActivity(validStartIntent);

        InstructionActionInfo actionInfo = instructionWithoutActions.getActions().get(0);

        onView(withId(R.id.mpsdkActionButton)).perform(click());

        intended(allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(actionInfo.getUrl())));
    }

    @Test
    public void ifApiFailureShowErrorActivity() {
        mTestRule.initIntentsRecording();
        mTestRule.addApiResponseToQueue("", 401, "");
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

    static Matcher<View> withAnyChildText(final String text) {
        return new BoundedMatcher<View, LinearLayout>(LinearLayout.class) {
            @Override
            public boolean matchesSafely(LinearLayout view) {
                return anyChildMatches(view, text);
            }

            private boolean anyChildMatches(LinearLayout view, String text) {
                for(int i=0; i < view.getChildCount(); i++) {
                    View child = view.getChildAt(i);
                    if (child != null && child instanceof TextView) {
                        if(((TextView) child).getText().toString().equals(text)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with child text: ");
            }
        };
    }
}