package com.mercadopago.utils;

import android.app.Activity;
import android.content.Intent;

import com.mercadopago.test.ActivityResult;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertTrue;

/**
 * Created by mreverter on 7/7/16.
 */
public class ActivityResultUtil {

    public static void assertFinishCalledWithResult(Activity activity, int resultCode) {

        assertTrue(activity.isFinishing());
        try {
            Field field = Activity.class.getDeclaredField("mResultCode");
            field.setAccessible(true);
            int actualResultCode = (Integer) field.get(activity);
            assertTrue(actualResultCode == resultCode);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Looks like the Android Activity class has changed it's private fields for mResultCode or mResultData.Time to update the reflection code.", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ActivityResult getActivityResult(Activity activity) {

        try {
            ActivityResult activityResult = new ActivityResult();
            // Result code
            Field field = Activity.class.getDeclaredField("mResultCode");
            field.setAccessible(true);
            activityResult.setResultCode((Integer) field.get(activity));
            // Extras
            field = Activity.class.getDeclaredField("mResultData");
            field.setAccessible(true);
            Intent resultData = (Intent) field.get(activity);
            if (resultData != null) {
                activityResult.setExtras(resultData.getExtras());
            }
            // Return
            return activityResult;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Looks like the Android Activity class has changed it's private fields for mResultCode or mResultData.Time to update the reflection code.", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
