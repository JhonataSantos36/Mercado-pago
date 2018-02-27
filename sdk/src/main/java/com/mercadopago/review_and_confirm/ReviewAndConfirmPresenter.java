package com.mercadopago.review_and_confirm;

import com.mercadopago.mvp.DefaultProvider;
import com.mercadopago.mvp.MvpPresenter;

/**
 * Created by lbais on 27/2/18.
 */

public class ReviewAndConfirmPresenter extends MvpPresenter<ReviewAndConfirmView, DefaultProvider> {

    private Object params;

    public ReviewAndConfirmPresenter(final String params) {
        this.params = params;
    }

}
