package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;

/**
 * Created by lbais on 27/2/18.
 */

public class ReviewAndConfirmRenderer extends Renderer<ReviewAndConfirmContainer> {

    @Override
    protected View render(@NonNull final ReviewAndConfirmContainer component,
                          @NonNull final Context context,
                          @Nullable final ViewGroup parent) {

        //TODO add views
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        //TODO add views
        if (component.props.termsAndConditionsModel.isActive()) {
            addReviewAndConfirm(component, mainLayout);
        }

        //TODO add views

        return mainLayout;
    }

    private void addReviewAndConfirm(final @NonNull ReviewAndConfirmContainer component,
                                     final ViewGroup container) {
        Renderer termsAndConditions = RendererFactory.create(container.getContext(), component.createTermsAndConditions());
        container.addView(termsAndConditions.render(container));
    }
}
