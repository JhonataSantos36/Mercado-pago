package com.mercadopago.util;

import com.mercadopago.constants.Sites;
import com.mercadopago.model.Site;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by marlanti on 4/6/17.
 */

public class InstallmentsUtil {

    private static Set<String> bankInterestsNotIncludedInInstallmentsSites;

    public static boolean shouldWarnAboutBankInterests(Site site) {
        boolean shouldWarn = false;
        if (site != null && getSitesWithBankInterestsNotIncluded().contains(site.getId())) {
            shouldWarn = true;
        }
        return shouldWarn;
    }

    private static Collection<String> getSitesWithBankInterestsNotIncluded() {
        if (bankInterestsNotIncludedInInstallmentsSites == null) {
            bankInterestsNotIncludedInInstallmentsSites = new HashSet<>();
            bankInterestsNotIncludedInInstallmentsSites.add(Sites.COLOMBIA.getId());
        }
        return bankInterestsNotIncludedInInstallmentsSites;
    }
}
