package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;

public class ReviewAndConfirmRenderer extends Renderer<ReviewAndConfirmContainer> {

    @Override
    protected View render(@NonNull final ReviewAndConfirmContainer component,
                          @NonNull final Context context,
                          @Nullable final ViewGroup parent) {

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        //TODO add views
        if (component.props.termsAndConditionsModel.isActive()) {
            addReviewAndConfirm(component, linearLayout);
        }

        //TODO add views

        parent.addView(linearLayout);

        return linearLayout;
    }

    private void addReviewAndConfirm(final @NonNull ReviewAndConfirmContainer component,
                                     final ViewGroup container) {
        Renderer termsAndConditions = RendererFactory.create(container.getContext(), component.createTermsAndConditions());
        termsAndConditions.render(container);
    }
}
