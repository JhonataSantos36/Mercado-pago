package com.mercadopago.controllers;

import android.support.annotation.LayoutRes;

/**
 * Created by mreverter on 11/25/16.
 */

public class CheckoutErrorHandler {

    private static CheckoutErrorHandler checkoutErrorHandler;
    private Integer customErrorLayout;

    public static CheckoutErrorHandler getInstance() {
        if (checkoutErrorHandler == null) {
            checkoutErrorHandler = new CheckoutErrorHandler();
        }
        return checkoutErrorHandler;
    }

    /**
     *
     * @param layout
     * Add a layout to show when an error occurs.
     * The layout MUST have the following views:
     *   TextView with ID: mpsdkErrorMessage - Message shown to the user.
     *   View with ID: mpsdkErrorRetry - Any view giving the user the chance to retry.
     *   View with ID: mpsdkExit - Any view that will cancel the action in progress.
     */
    public void setCustomErrorLayout(@LayoutRes int layout) {
        customErrorLayout = layout;
    }

    public boolean hasCustomErrorLayout() {
        return customErrorLayout != null;
    }

    public Integer getCustomErrorLayout() {
        return customErrorLayout;
    }
}
