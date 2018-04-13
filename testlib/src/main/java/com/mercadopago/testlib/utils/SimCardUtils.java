package com.mercadopago.testlib.utils;


import android.content.Context;
import android.telephony.TelephonyManager;

import static android.content.Context.TELEPHONY_SERVICE;

public final class SimCardUtils {
    private SimCardUtils() {
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
                break;
        }
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return tm.getSimCountryIso().equals(expectedCountryCode);
    }
}
