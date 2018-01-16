package com.mercadopago.views;

import com.mercadopago.model.ReviewSubscriber;
import com.mercadopago.model.Reviewable;
import com.mercadopago.mvp.MvpView;

import java.util.List;

/**
 * Created by mreverter on 2/2/17.
 */
public interface ReviewAndConfirmView extends MvpView {
    void showError(String message);

    void showReviewables(List<Reviewable> reviewables);

    void changePaymentMethod();

    void confirmPayment();

    void cancelPayment();

    void showTitle(String title);

    void showConfirmationMessage(String message);

    void showCancelMessage(String message);

    void showTermsAndConditions();

    ReviewSubscriber getReviewSubscriber();

    void trackScreen();
}
