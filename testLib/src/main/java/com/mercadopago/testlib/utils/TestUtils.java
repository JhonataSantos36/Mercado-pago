package com.mercadopago.testlib.utils;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mercadopago.testlib.idlingresource.ViewVisibilityIdlingResource;
import com.mercadopago.testlib.espresso.CurrentActivityFetcher;
import com.mercadopago.testlib.matchers.RecyclerViewMatcher;
import java.lang.reflect.Method;
import java.util.Arrays;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Test Utils functions
 */

public class TestUtils {

    @Deprecated
    public static Activity getActivityInstance(Instrumentation instrumentation) {
        return CurrentActivityFetcher.fetch();
    }

    public static IdlingResource waitVisibility(@IdRes int viewId, int visibility) {
        IdlingResource idlingResource = new ViewVisibilityIdlingResource(getActivityInstance(InstrumentationRegistry.getInstrumentation()), viewId, visibility);
        Espresso.registerIdlingResources(idlingResource);
        return idlingResource;
    }

    public static void unregisterIdlingRes(IdlingResource idlingResource) {
        Espresso.unregisterIdlingResources(idlingResource);
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    public static void disableAnimation(Context context) {
        final String ANIMATION_PERMISSION = "android.permission.SET_ANIMATION_SCALE";
        final int permStatus = context.checkCallingOrSelfPermission(ANIMATION_PERMISSION);
        if (permStatus == PackageManager.PERMISSION_GRANTED) {
            if (reflectivelyDisableAnimation()) {
                Log.i("TestUtils", "Animations disabled.");
            } else {
                Log.i("TestUtils", "Could not disable animations.");
            }
        } else {
            Log.i("TestUtils", "Cannot disable animations due to lack of permission.");
        }
    }

    private static boolean reflectivelyDisableAnimation() {
        try {
            Class<?> windowManagerStubClazz = Class.forName("android.view.IWindowManager$Stub");
            Method asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", IBinder.class);
            Class<?> serviceManagerClazz = Class.forName("android.os.ServiceManager");
            Method getService = serviceManagerClazz.getDeclaredMethod("getService", String.class);
            Class<?> windowManagerClazz = Class.forName("android.view.IWindowManager");
            Method setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales",float[].class);
            Method getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales");

            IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
            Object windowManagerObj = asInterface.invoke(null, windowManagerBinder);
            float[] currentScales = (float[]) getAnimationScales.invoke(windowManagerObj);
            Arrays.fill(currentScales, 0.0f);
            setAnimationScales.invoke(windowManagerObj, currentScales);
            return true;
        } catch (Exception exception) {
            Log.w("TestUtils", "Cannot disable animations reflectively.", exception);
        }
        return false;
    }

    public static boolean deviceHasSimCard(final Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return tm.getSimState()!=TelephonyManager.SIM_STATE_ABSENT;
    }

    public static boolean deviceSimIsFromActiveSite(final Context context, final String siteID){
        final String expectedCountryCode;
        switch (siteID.toUpperCase()){
            case "MLA":
                expectedCountryCode = "ar";
                break;
            case "MLB":
                expectedCountryCode = "br";
                break;
            case "MLM":
                expectedCountryCode = "mx";
                break;
            case "MCO":
                expectedCountryCode = "co";
                break;
            case "MLV":
                expectedCountryCode = "ve";
                break;
            default:
                expectedCountryCode= "ar";
        }
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return tm.getSimCountryIso().equals(expectedCountryCode);
    }
}