package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ActivityResultUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by mreverter on 21/7/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ErrorActivityTest {
    @Rule
    public ActivityTestRule<ErrorActivity> mTestRule = new ActivityTestRule<>(ErrorActivity.class, true, false);

    @Test
    public void ifMPExceptionNotReceivedFinishActivityWithCancelResult() {
        Intent intent = new Intent();
        mTestRule.launchActivity(intent);
        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_CANCELED);
    }

    @Test
    public void ifMPExceptionWithNonRecoverableErrorDoNotShowRetryMessage() {
        Intent intent = new Intent();
        MPException mpException = new MPException("Some message", false);
        intent.putExtra("mpException", JsonUtil.getInstance().toJson(mpException));
        mTestRule.launchActivity(intent);

        onView(withText(mTestRule.getActivity().getString(R.string.mpsdk_refresh_message))).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifMPExceptionWithRecoverableErrorShowRetryMessage() {
        Intent intent = new Intent();
        MPException mpException = new MPException("Some message", true);
        intent.putExtra("mpException", JsonUtil.getInstance().toJson(mpException));
        mTestRule.launchActivity(intent);

        onView(withText(mTestRule.getActivity().getString(R.string.mpsdk_refresh_message))).check(matches(isDisplayed()));
    }

    @Test
    public void ifMPExceptionWithMessageSetShowMessage() {
        Intent intent = new Intent();
        MPException mpException = new MPException("Some message", true);
        intent.putExtra("mpException", JsonUtil.getInstance().toJson(mpException));
        mTestRule.launchActivity(intent);

        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText("Some message")));
    }

    @Test
    public void ifMPExceptionHasApiExceptionShowApiExceptionMessage() {
        ApiException apiException = StaticMock.getApiExceptionNotFound();

        Intent intent = new Intent();
        MPException mpException = new MPException(apiException);
        String json = JsonUtil.getInstance().toJson(mpException);
        intent.putExtra("mpException", json);
        mTestRule.launchActivity(intent);

        String apiExceptionMessage = ApiUtil.getApiExceptionMessage(mTestRule.getActivity(), apiException);
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(apiExceptionMessage)));
    }

    @Test
    public void ifMPExceptionHasRecoverableApiExceptionShowRetryMessage() {
        ApiException apiException = StaticMock.getApiExceptionWithoutStatus();
        apiException.setStatus(415);
        apiException.setCause(null);

        Intent intent = new Intent();
        MPException mpException = new MPException(apiException);
        intent.putExtra("mpException", JsonUtil.getInstance().toJson(mpException));
        mTestRule.launchActivity(intent);

        onView(withText(mTestRule.getActivity().getString(R.string.mpsdk_refresh_message))).check(matches(isDisplayed()));
    }

    @Test
    public void ifMPExceptionHasNonRecoverableApiExceptionDoNotShowRetryMessage() {
        ApiException apiException = StaticMock.getApiExceptionNotFound();

        Intent intent = new Intent();
        MPException mpException = new MPException(apiException);
        intent.putExtra("mpException", JsonUtil.getInstance().toJson(mpException));
        mTestRule.launchActivity(intent);

        onView(withText(mTestRule.getActivity().getString(R.string.mpsdk_refresh_message))).check(matches(not(isDisplayed())));
    }
}
