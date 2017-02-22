package com.mercadopago.views;

import com.mercadopago.mvp.MvpView;

/**
 * Created by mromar on 11/29/16.
 */

public interface DiscountsActivityView extends MvpView {

    void drawSummary();

    void requestDiscountCode();

    void finishWithResult();

    void finishWithCancelResult();

    void showCodeInputError(String message);

    void clearErrorView();

    void showProgressBar();

    void hideProgressBar();

    void showEmptyDiscountCodeError();

    void hideKeyboard();

    void setSoftInputModeSummary();

    void hideDiscountSummary();
}
