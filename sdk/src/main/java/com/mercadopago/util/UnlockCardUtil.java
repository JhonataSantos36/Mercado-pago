package com.mercadopago.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marlanti on 3/31/17.
 */

public class UnlockCardUtil {

    private static Map<String, String> mCardUnlockingLinks;


    public static String getCardUnlockingLink(String mSiteId, Long mIssuerId) {
        if (!hasUnlockingLinkParameters(mSiteId, mIssuerId)) {
            return null;
        }
        loadCardUnlockingLinks();
        String key = getCardUnlockingLinkKey(mSiteId, mIssuerId);

        return mCardUnlockingLinks.get(key);
    }

    public static boolean hasUnlockingLinkParameters(String mSiteId, Long mIssuerId) {
        return hasSite(mSiteId) && hasIssuer(mIssuerId);
    }


    private static String getCardUnlockingLinkKey(String mSiteId, Long mIssuerId) {

        if (!hasUnlockingLinkParameters(mSiteId, mIssuerId)) {
            return null;
        }

        return mSiteId + "_" + mIssuerId;

    }

    private static void loadCardUnlockingLinks() {
        if (mCardUnlockingLinks == null || mCardUnlockingLinks.isEmpty()) {
            mCardUnlockingLinks = new HashMap<>();
            mCardUnlockingLinks.put("MLV_1050", "https://www.provincial.com");
        }
    }

    public static boolean hasSite(String siteId) {
        return siteId != null && !siteId.trim().isEmpty();
    }

    private static boolean hasIssuer(Long mIssuerId) {
        return mIssuerId != null;
    }

}
