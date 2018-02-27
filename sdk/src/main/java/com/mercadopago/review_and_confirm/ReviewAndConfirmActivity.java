package com.mercadopago.review_and_confirm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.ComponentManager;
import com.mercadopago.review_and_confirm.components.ReviewAndConfirmContainer;

public class ReviewAndConfirmActivity extends AppCompatActivity implements ReviewAndConfirmView, ActionDispatcher {

    private static final String EXTRA_TEST = "extra_test";
    private ReviewAndConfirmPresenter presenter;

    public static void start(final Context context, final String test) {
        Intent intent = new Intent(context, ReviewAndConfirmActivity.class);
        intent.putExtra(EXTRA_TEST, test);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String test = getActivityParameters();
        presenter = new ReviewAndConfirmPresenter(test);
        presenter.attachView(this);

        final ComponentManager manager = new ComponentManager(this);
        final ReviewAndConfirmContainer container = new ReviewAndConfirmContainer(new ReviewAndConfirmContainer.Props());

        container.setDispatcher(this);
        manager.render(container);

    }

    private String getActivityParameters() {
        return getIntent().getStringExtra(EXTRA_TEST);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void dispatch(Action action) {
        throw new UnsupportedOperationException();
    }
}
