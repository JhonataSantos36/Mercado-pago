package com.mercadopago.views;

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
}
