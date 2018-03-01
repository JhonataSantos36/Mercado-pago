package com.mercadopago.review_and_confirm.actions;

import com.mercadopago.components.Action;

/**
 * Created by lbais on 1/3/18.
 */

public class TermsAndConditionAction extends Action {

    private final String siteId;

    public TermsAndConditionAction(final String siteId) {
        this.siteId = siteId;
    }

    public String getSiteId() {
        return siteId;
    }
}
